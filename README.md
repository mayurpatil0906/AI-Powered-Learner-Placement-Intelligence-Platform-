# AI Learning Platform - Spring Boot Backend

## Overview

The AI Learning Platform Backend is a Spring Boot application designed to manage learners, assessments, placement drives, notifications, authentication, and machine learning-based placement predictions.

The system provides REST APIs for learner management, placement tracking, assessment recording, dashboard analytics, notification delivery, and integration with an external machine learning prediction service.

The backend follows a layered architecture consisting of controllers, services, repositories, DTOs, security components, and domain models.

---

## Features

### Authentication & Authorization
- JWT-based authentication
- User registration and login
- Role-based access control
- Password encryption using Spring Security

### Learner Management
- Create, update, retrieve, and delete learners
- Bulk learner import using CSV files
- Track academic and professional information
- Mentor assignment support

### Assessment Management
- Record learner assessments
- Store scores, feedback, and evaluator information
- Update assessment records
- Automatic learner notifications on assessment updates

### Placement Management
- Manage placement drives
- Apply learners to placement drives
- Track application statuses
- Monitor placement outcomes

### Dashboard Analytics
- Learner statistics
- Placement statistics
- GPA analytics
- Course distribution reports
- Placement trend visualization

### Notification System
- User-specific notifications
- Unread notification tracking
- Mark notifications as read
- Real-time notification integration

### Machine Learning Integration
- Placement prediction support
- External ML service integration
- Prediction history tracking
- Fallback prediction mechanism when ML service is unavailable

---

## Architecture

The backend follows a layered architecture:

```text
Controller Layer
        ↓
Service Layer
        ↓
Repository Layer
        ↓
Database
```

### Controller Layer
Handles incoming HTTP requests and returns API responses.

### Service Layer
Contains business logic and application workflows.

### Repository Layer
Provides data access using Spring Data JPA.

### Security Layer
Handles:
- Authentication
- JWT validation
- Authorization

### External Services
- Machine Learning Prediction Service
- Notification Service

---

## Technology Stack

### Backend Framework
- Spring Boot

### Security
- Spring Security
- JWT Authentication

### Database
- MySQL
- Spring Data JPA
- Hibernate

### Build Tool
- Maven

### Testing
- JUnit 5
- Mockito

### Logging
- SLF4J
- Logback

---

## Major Modules

### Authentication Module

Responsible for:

- User registration
- User login
- JWT token generation
- Password encryption
- Role assignment

### Learner Module

Responsible for:

- Learner profile management
- CSV import processing
- Mentor assignment
- Academic data tracking

### Assessment Module

Responsible for:

- Assessment creation
- Score management
- Feedback recording
- Learner performance tracking

### Placement Module

Responsible for:

- Placement drive management
- Application processing
- Placement status updates
- Placement analytics

### Dashboard Module

Responsible for:

- System-wide statistics
- Placement trends
- Learner distribution reports
- KPI generation

### Notification Module

Responsible for:

- Notification creation
- Notification retrieval
- Notification tracking
- Push notification integration

### Machine Learning Module

Responsible for:

- Placement prediction requests
- ML service communication
- Prediction storage
- Fallback prediction generation

---

## Security Features

### JWT Authentication

Each authenticated user receives a JWT token which must be supplied with subsequent API requests.

### Password Encryption

Passwords are securely stored using encrypted hashes.

### Role-Based Authorization

Supported roles include:

- ADMIN
- MENTOR
- LEARNER

Access to APIs is controlled using Spring Security.

---

## Database Design

The system manages the following primary entities:

### User
Stores authentication and authorization data.

### Learner
Stores learner academic and profile information.

### Assessment
Stores learner assessment results.

### PlacementDrive
Stores company placement opportunities.

### PlacementApplication
Tracks learner applications to placement drives.

### Notification
Stores user notifications.

### MLPrediction
Stores machine learning prediction results.

---

## External Integrations

### Machine Learning Service

The backend communicates with an external Python-based machine learning service to obtain placement predictions.

Prediction requests include:

- GPA
- Skills
- Experience
- Course
- Semester

Returned predictions include:

- Placement probability
- Placeability status
- Key influencing factors

### Notification Service

The backend can communicate with an external notification service for real-time user updates.

---

## Testing

The backend includes unit testing using:

- JUnit 5
- Mockito

Test coverage focuses on:

- Authentication workflows
- Placement workflows
- Notification management
- Dashboard analytics

Example tested scenarios:

- Successful placement application
- Notification read status updates
- Dashboard statistic generation
- User registration validation
- Duplicate placement application prevention

---

## Logging

The application uses structured logging throughout the service layer.

Examples include:

- User registration events
- Login attempts
- Placement applications
- Assessment updates
- Notification delivery
- ML prediction requests

This helps support debugging, monitoring, and operational visibility.

---

## Future Enhancements

- Real-time WebSocket notifications
- Email notification support
- Advanced placement recommendation engine
- Resume analysis integration
- ML model retraining pipeline
- Interview scheduling module
- Role-specific dashboards
- Audit logging and activity tracking

---

## Project Goal

The goal of the AI Learning Platform is to provide a centralized system that combines learner management, placement tracking, assessment monitoring, notification delivery, and machine learning-based career insights into a single platform that supports educational institutions and placement departments.
