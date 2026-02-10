package com.revshop.dao;

import com.revshop.model.Product;
import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO {

	public boolean addFavorite(int userId, int productId) {
		String sql = "INSERT INTO FAVORITES (favorite_id, user_id, product_id) VALUES (FAVORITE_SEQ.NEXTVAL, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			pstmt.setInt(2, productId);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println(
					"Failed to add favorite (user=" + userId + ", product=" + productId + "): " + e.getMessage());
			return false;
		}
	}

	public boolean removeFavorite(int userId, int productId) {
		String sql = "DELETE FROM FAVORITES WHERE user_id = ? AND product_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			pstmt.setInt(2, productId);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println(
					"Failed to remove favorite (user=" + userId + ", product=" + productId + "): " + e.getMessage());
			return false;
		}
	}

	public boolean isFavorite(int userId, int productId) {
		String sql = "SELECT 1 FROM FAVORITES WHERE user_id = ? AND product_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			pstmt.setInt(2, productId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println(
					"Failed to check favorite (user=" + userId + ", product=" + productId + "): " + e.getMessage());
			return false;
		}
	}

	public List<Product> getFavoritesByUserId(int userId) {
		List<Product> favorites = new ArrayList<>();
		String sql = "SELECT p.* FROM PRODUCTS p JOIN FAVORITES f ON p.product_id = f.product_id WHERE f.user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Product product = new Product();
					product.setProductId(rs.getInt("product_id"));
					product.setSellerId(rs.getInt("seller_id"));
					product.setCategoryId(rs.getInt("category_id"));
					product.setName(rs.getString("name"));
					product.setDescription(rs.getString("description"));
					product.setPrice(rs.getDouble("price"));
					product.setMrp(rs.getDouble("mrp"));
					product.setStockQuantity(rs.getInt("stock_quantity"));
					product.setImageUrl(rs.getString("image_url"));
					product.setActive(rs.getBoolean("is_active"));
					product.setCreatedAt(rs.getDate("created_at"));
					favorites.add(product);
				}
			}
		} catch (SQLException e) {
//			e.printStackTrace();
			 System.err.println("Failed to fetch favorites for user " + userId + ": " + e.getMessage());
		}
		return favorites;
	}
}
