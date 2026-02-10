package com.revshop.dao;

import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDAO {

	public int getCategoryIdByName(String categoryName) {
		String sql = "SELECT category_id FROM CATEGORIES WHERE UPPER(category_name) = UPPER(?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, categoryName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("category_id");
				}
			}
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Failed to get category ID for '" + categoryName + "': " + e.getMessage());
		}
		return -1;
	}

	public int createCategory(String categoryName) {
		String sql = "INSERT INTO CATEGORIES (category_id, category_name) VALUES (CATEGORY_SEQ.NEXTVAL, ?)";
		String[] generatedColumns = { "category_id" };

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {

			pstmt.setString(1, categoryName);
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Failed to create category '" + categoryName + "': " + e.getMessage());
		}
		return -1;
	}
}
