package com.learnerplatform.backend.service;

import com.learnerplatform.backend.dto.AuthResponse;
import com.learnerplatform.backend.dto.LoginRequest;
import com.learnerplatform.backend.dto.RegisterRequest;
import com.learnerplatform.backend.exception.BadRequestException;
import com.learnerplatform.backend.model.Learner;
import com.learnerplatform.backend.model.Role;
import com.learnerplatform.backend.model.User;
import com.learnerplatform.backend.repository.LearnerRepository;
import com.learnerplatform.backend.repository.UserRepository;
import com.learnerplatform.backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final LearnerRepository learnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, LearnerRepository learnerRepository,
                       PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.learnerRepository = learnerRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        if (request.getEmail() == null ||
        	    request.getEmail().trim().isEmpty()) {

        	    throw new BadRequestException(
        	            "Email cannot be empty"
        	    );
        	}
        if (request.getName() == null ||
        	    request.getName().trim().isEmpty()) {

        	    throw new BadRequestException(
        	            "Name cannot be empty"
        	    );
        	}

        	if (request.getPassword() == null ||
        	    request.getPassword().trim().isEmpty()) {

        	    throw new BadRequestException(
        	            "Password cannot be empty"
        	    );
        	}

        Role role = Role.LEARNER;
        if (request.getRole() != null) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid role: " + request.getRole());
            }
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Auto-create a Learner profile for LEARNER role users
        if (role == Role.LEARNER) {
            String studentId = "USR-" + user.getId();
            if (!learnerRepository.existsByStudentId(studentId)) {
            	Learner learner = new Learner();

            	learner.setUser(user);
            	learner.setStudentId(studentId);
            	learner.setName(user.getName());
            	learner.setEmail(user.getEmail());
            	learner.setStatus("ACTIVE");
                learnerRepository.save(learner);
                log.info("Auto-created Learner profile for user: {} with studentId: {}", user.getEmail(), studentId);
            }
        }

        String token = tokenProvider.generateTokenFromEmail(user.getEmail());

        AuthResponse response = new AuthResponse();

        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());

        return response;
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        log.info("User logged in successfully: {}", user.getEmail());

        AuthResponse response = new AuthResponse();

        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());

        return response;
    }
}
