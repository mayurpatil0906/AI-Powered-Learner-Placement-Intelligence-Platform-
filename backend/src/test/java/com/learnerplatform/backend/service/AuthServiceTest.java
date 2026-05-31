package com.learnerplatform.backend.service;

import com.learnerplatform.backend.dto.AuthResponse;
import com.learnerplatform.backend.dto.RegisterRequest;
import com.learnerplatform.backend.model.User;
import com.learnerplatform.backend.repository.LearnerRepository;
import com.learnerplatform.backend.repository.UserRepository;
import com.learnerplatform.backend.security.JwtTokenProvider;
import com.learnerplatform.backend.exception.BadRequestException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LearnerRepository learnerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();

        request.setName("Abhinav");
        request.setEmail("abhinav@test.com");
        request.setPassword("password");

        when(userRepository.existsByEmail("abhinav@test.com"))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();

        request.setName("Abhinav");
        request.setEmail("abhinav@test.com");
        request.setPassword("password");

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(tokenProvider.generateTokenFromEmail(anyString()))
                .thenReturn("jwt-token");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Abhinav");
        savedUser.setEmail("abhinav@test.com");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        AuthResponse response =
                authService.register(request);

        assertNotNull(response);

        assertEquals(
                "jwt-token",
                response.getToken()
        );

        assertEquals(
                "abhinav@test.com",
                response.getEmail()
        );

        verify(userRepository, times(1))
                .save(any(User.class));
    }
    
    @Test
    void shouldRejectBlankEmail() {

        RegisterRequest request = new RegisterRequest();

        request.setName("Abhinav");
        request.setEmail("");
        request.setPassword("password");

        when(userRepository.existsByEmail(""))
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> authService.register(request)
        );
    }
}