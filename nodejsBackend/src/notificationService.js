const axios = require('axios');
const logger = require('./logger');

const SPRING_BACKEND_URL = process.env.SPRING_BACKEND_URL || 'http://spring-backend:8080';

async function createAndEmit(io, userId, message, type = 'INFO') {
  try {
    logger.info(`Creating notification for user ${userId}: ${message}`);

    const response = await axios.post(`${SPRING_BACKEND_URL}/api/notifications`, {
      userId,
      message,
      type,
    });

    const notification = response.data;

    const { sendToUser } = require('./socketHandler');
    sendToUser(io, userId, {
      id: notification.id,
      message: notification.message,
      type: notification.type,
      read: false,
      createdAt: notification.createdAt,
    });

    logger.info(`Notification created and emitted for user ${userId}: id=${notification.id}`);
    return notification;
  } catch (error) {
    logger.error(`Failed to create notification for user ${userId}: ${error.message}`);

    const { sendToUser } = require('./socketHandler');
    sendToUser(io, userId, {
      id: null,
      message,
      type,
      read: false,
      createdAt: new Date().toISOString(),
    });

    return { userId, message, type, error: error.message };
  }
}

async function broadcastNotification(io, message, type = 'INFO') {
  try {
    logger.info(`Broadcasting notification: ${message}`);

    const { broadcast } = require('./socketHandler');
    broadcast(io, {
      id: null,
      message,
      type,
      read: false,
      createdAt: new Date().toISOString(),
    });

    logger.info('Broadcast notification sent successfully');
  } catch (error) {
    logger.error(`Failed to broadcast notification: ${error.message}`);
  }
}

module.exports = {
  createAndEmit,
  broadcastNotification,
};