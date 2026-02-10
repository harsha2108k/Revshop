package com.revshop.dao;

import com.revshop.model.Notification;
import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

	public boolean addNotification(Notification notification) {
		String sql = "INSERT INTO NOTIFICATIONS (notification_id, user_id, message) VALUES (NOTIFICATION_SEQ.NEXTVAL, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, notification.getUserId());
			pstmt.setString(2, notification.getMessage());

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Failed to add notification for user " + notification.getUserId() + " ('"
					+ notification.getMessage() + "'): " + e.getMessage());
			return false;
		}
	}

	public List<Notification> getNotificationsByUserId(int userId) {
		List<Notification> notifications = new ArrayList<>();
		String sql = "SELECT * FROM NOTIFICATIONS WHERE user_id = ? ORDER BY created_at DESC";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, userId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Notification n = new Notification();
					n.setNotificationId(rs.getInt("notification_id"));
					n.setUserId(rs.getInt("user_id"));
					n.setMessage(rs.getString("message"));
					n.setRead(rs.getInt("is_read") == 1);
					n.setCreatedAt(rs.getDate("created_at"));
					notifications.add(n);
				}
			}
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Failed to fetch notifications for user " + userId + ": " + e.getMessage());
		}
		return notifications;
	}

	public boolean markAsRead(int notificationId) {
		String sql = "UPDATE NOTIFICATIONS SET is_read = 1 WHERE notification_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, notificationId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Failed to mark notification " + notificationId + " as read: " + e.getMessage());
			return false;
		}
	}
}
