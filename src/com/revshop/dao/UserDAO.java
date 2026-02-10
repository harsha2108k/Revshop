package com.revshop.dao;

import com.revshop.model.User;
import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO USERS (user_id, email, password_hash, role, name, phone, address, security_question, security_answer, registration_date) " +
                     "VALUES (USER_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setString(7, user.getSecurityQuestion());
            pstmt.setString(8, user.getSecurityAnswer());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to register user '" + user.getEmail() + "': " + e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM USERS WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to fetch user '" + email + "': " + e.getMessage());
        }
        return null; // User not found
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT 1 FROM USERS WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to check email '" + email + "': " + e.getMessage());
        }
        return false;
    }
    
    public boolean updatePassword(String email, String newPasswordHash) {
         String sql = "UPDATE USERS SET password_hash = ? WHERE email = ?";
         try (Connection conn = DBConnection.getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
             pstmt.setString(1, newPasswordHash);
             pstmt.setString(2, email);
             
             int rowsAffected = pstmt.executeUpdate();
             return rowsAffected > 0;
         } catch (SQLException e) {
//             e.printStackTrace();
        	 System.err.println("Failed to update password for '" + email + "': " + e.getMessage());
             return false;
         }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setName(rs.getString("name"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswer(rs.getString("security_answer"));
        user.setRegistrationDate(rs.getDate("registration_date"));
        return user;
    }
}
