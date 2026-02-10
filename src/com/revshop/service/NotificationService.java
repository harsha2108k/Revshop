package com.revshop.service;

import com.revshop.dao.NotificationDAO;
import com.revshop.model.Notification;

import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public boolean sendNotification(int userId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        return notificationDAO.addNotification(n);
    }

    public List<Notification> getNotifications(int userId) {
        return notificationDAO.getNotificationsByUserId(userId);
    }

    public boolean markAsRead(int notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }
}
