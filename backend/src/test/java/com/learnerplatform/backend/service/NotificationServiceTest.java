package com.learnerplatform.backend.service;

import com.learnerplatform.backend.exception.ResourceNotFoundException;
import com.learnerplatform.backend.model.Notification;
import com.learnerplatform.backend.repository.NotificationRepository;
import com.learnerplatform.backend.repository.UserRepository;
import com.learnerplatform.backend.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldMarkNotificationAsRead() {

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setRead(false);

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertTrue(notification.getRead());

        verify(notificationRepository, times(1))
                .save(notification);
    }

    @Test
    void shouldThrowExceptionWhenNotificationNotFound() {

        when(notificationRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAsRead(100L)
        );
    }
}