package com.learnerplatform.backend.service;

import com.learnerplatform.backend.dto.DashboardStats;
import com.learnerplatform.backend.repository.LearnerRepository;
import com.learnerplatform.backend.repository.PlacementApplicationRepository;
import com.learnerplatform.backend.repository.PlacementDriveRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private LearnerRepository learnerRepository;

    @Mock
    private PlacementDriveRepository driveRepository;

    @Mock
    private PlacementApplicationRepository applicationRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldGenerateDashboardStats() {

        when(learnerRepository.count()).thenReturn(100L);
        when(learnerRepository.countByStatus("ACTIVE")).thenReturn(80L);

        when(driveRepository.count()).thenReturn(10L);
        when(driveRepository.countByStatus("UPCOMING")).thenReturn(4L);

        when(applicationRepository.count()).thenReturn(250L);
        when(applicationRepository.countByResult("PLACED")).thenReturn(40L);

        when(learnerRepository.findAverageGpa()).thenReturn(8.25);

        List<Object[]> courseData = new ArrayList<>();
        courseData.add(new Object[]{"Computer Science", 60L});
        courseData.add(new Object[]{"Information Technology", 40L});

        when(learnerRepository.countByCourse())
                .thenReturn(courseData);

        List<Object[]> placementTrend = new ArrayList<>();
        placementTrend.add(new Object[]{"2025-01", 10L});
        placementTrend.add(new Object[]{"2025-02", 15L});

        when(applicationRepository.countByMonth())
                .thenReturn(placementTrend);

        DashboardStats stats = dashboardService.getStats();

        assertEquals(100L, stats.getTotalLearners());
        assertEquals(80L, stats.getActiveLearners());

        assertEquals(10L, stats.getTotalDrives());
        assertEquals(4L, stats.getUpcomingDrives());

        assertEquals(250L, stats.getTotalApplications());
        assertEquals(40L, stats.getPlacedCount());

        assertEquals(8.25, stats.getAverageGpa());

        assertEquals(
                60L,
                stats.getCourseDistribution()
                        .get("Computer Science")
        );

        assertEquals(
                15L,
                stats.getPlacementTrend()
                        .get("2025-02")
        );
    }
}