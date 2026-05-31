package com.learnerplatform.backend.service;

import com.learnerplatform.backend.model.Learner;
import com.learnerplatform.backend.model.PlacementApplication;
import com.learnerplatform.backend.model.PlacementDrive;
import com.learnerplatform.backend.repository.LearnerRepository;
import com.learnerplatform.backend.repository.PlacementApplicationRepository;
import com.learnerplatform.backend.repository.PlacementDriveRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlacementServiceTest {

    @Mock
    private PlacementDriveRepository driveRepository;

    @Mock
    private PlacementApplicationRepository applicationRepository;

    @Mock
    private LearnerRepository learnerRepository;

    @InjectMocks
    private PlacementService placementService;

    @Test
    void shouldApplyToDriveSuccessfully() {

        Learner learner = new Learner();
        learner.setId(1L);
        learner.setName("Rahul");

        PlacementDrive drive = new PlacementDrive();
        drive.setId(10L);
        drive.setCompanyName("Google");

        when(learnerRepository.findById(1L))
                .thenReturn(Optional.of(learner));

        when(driveRepository.findById(10L))
                .thenReturn(Optional.of(drive));

        when(applicationRepository.save(any(PlacementApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlacementApplication application =
                placementService.applyToDrive(1L, 10L);

        assertNotNull(application);
        assertEquals("APPLIED", application.getStatus());
        assertEquals(learner, application.getLearner());
        assertEquals(drive, application.getDrive());

        verify(applicationRepository, times(1))
                .save(any(PlacementApplication.class));
    }

    @Test
    void shouldUpdateApplicationStatus() {

        PlacementApplication application = new PlacementApplication();
        application.setId(1L);
        application.setStatus("APPLIED");

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(application));

        when(applicationRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlacementApplication updated =
                placementService.updateApplicationStatus(
                        1L,
                        "INTERVIEW",
                        "SHORTLISTED"
                );

        assertEquals("INTERVIEW", updated.getStatus());
        assertEquals("SHORTLISTED", updated.getResult());

        verify(applicationRepository).save(application);
    }
    
    @Test
    void shouldNotAllowDuplicateApplications() {

        PlacementApplication existingApplication =
                new PlacementApplication();

        when(applicationRepository
                .findByLearnerIdAndDriveId(1L, 10L))
                .thenReturn(Optional.of(existingApplication));

        assertThrows(
                IllegalStateException.class,
                () -> placementService.applyToDrive(1L, 10L)
        );
    }
    
    @Test
    void shouldNotMovePlacedCandidateBackToApplied() {

        PlacementApplication application =
                new PlacementApplication();

        application.setStatus("PLACED");

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> placementService.updateApplicationStatus(
                        1L,
                        "APPLIED",
                        null
                )
        );
    }
}