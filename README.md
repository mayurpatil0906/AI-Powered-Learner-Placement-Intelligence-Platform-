# AI-Powered-Learner-Placement-Intelligence-Platform-

# nodejsBackend

This part contains a small Node.js notification service that uses Express and Socket.IO. It provides REST endpoints for sending notifications and maintains real-time socket connections for delivering messages to users and roles.

## Overview

The service is designed to:
- accept notification requests via REST
- create notifications through an external Spring backend
- emit notifications over WebSocket to connected users
- broadcast notifications to all clients
- log activity using Winston

## Files

### `src/index.js`

The main application entry point.

Responsibilities:
- loads environment variables with `dotenv`
- initializes Express, HTTP server, and Socket.IO
- configures security and middleware:
  - `helmet` for HTTP headers
  - `cors` with origins from `CORS_ALLOWED_ORIGINS` or default local ports
  - `express.json()` for JSON body parsing
  - `morgan` to route HTTP logs into Winston
- authenticates socket clients using JWT from `socket.handshake.auth.token` or query string
- sets up socket event handlers by calling `setupSocketHandlers(io)`
- exposes REST endpoints:
  - `GET /health` — service health and client count
  - `POST /notify` — send a notification to a specific user
  - `POST /notify/broadcast` — broadcast a notification to all connected clients
  - `GET /status` — current connected clients and socket rooms
- starts the server on `PORT` (default `5000`)

Important exports:
- `{ app, server, io }`

### `src/logger.js`

A Winston logger configured with:
- JSON file logging via `winston-daily-rotate-file`
- console output with colored formatting
- timestamped messages and stack traces

Default logging settings:
- `LOG_LEVEL` environment variable controls level
- daily rotating logs saved under `logs/notification-%DATE%.log`
- retains logs for 30 days with a maximum size of 20MB per file

### `src/notificationService.js`

Notification orchestration and delivery logic.

Functions:
- `createAndEmit(io, userId, message, type = 'INFO')`
  - sends a POST to `SPRING_BACKEND_URL/api/notifications`
  - on success, emits a notification to a user room using `sendToUser`
  - on failure, logs the error and still emits a fallback notification to the user
- `broadcastNotification(io, message, type = 'INFO')`
  - emits a notification to all connected clients via `broadcast`

Notes:
- `SPRING_BACKEND_URL` defaults to `http://spring-backend:8080`
- the external Spring backend is expected to return notification data including `id`, `message`, `type`, and `createdAt`

### `src/socketHandler.js`

Socket.IO event and room management.

Core behavior:
- listens for new socket connections
- handles `join-room` events so sockets can join:
  - `user-${userId}` room for user-targeted notifications
  - `role-${role}` room for role-targeted notifications
- handles optional `leave-room`
- logs disconnects and socket errors

Exported helpers:
- `setupSocketHandlers(io)` — initialize socket event listeners
- `sendToUser(io, userId, notification)` — emit to a specific user room
- `broadcast(io, notification)` — emit to all connected sockets
- `sendToRole(io, role, notification)` — emit to all sockets in a role room

## Environment Variables

Required or useful variables:

- `PORT` — server listening port (default `5000`)
- `JWT_SECRET` — secret for Socket.IO JWT validation
- `CORS_ALLOWED_ORIGINS` — comma-separated list of allowed origins
- `SPRING_BACKEND_URL` — URL for the external Spring notification API
- `LOG_LEVEL` — Winston log level (default `info`)

## How it works

1. A client connects to Socket.IO.
2. The client may optionally provide a JWT token for authentication.
3. The client joins rooms using `join-room` with `userId` and optional `role`.
4. REST calls to `/notify` or `/notify/broadcast` trigger notification emission.
5. `notificationService` forwards user-targeted notifications to the Spring backend,
   then emits events back to connected sockets.

## Running the service

From the `nodejsBackend` folder, install dependencies and run the service with Node.js. Example commands:

```bash
npm install
node src/index.js
```

If the project does not have a dedicated `package.json` in `nodejsBackend`, run this service from the workspace root or add a package manifest locally.

## Notes

- Socket authentication is permissive: if JWT verification fails or is missing, the connection is still allowed but without authenticated user context.
- The app uses room-based Socket.IO broadcasting to target users and roles.
- Logs are written to both console and daily rotating log files.
