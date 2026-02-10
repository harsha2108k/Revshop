package com.revshop.dao;

import com.revshop.model.CartItem;
import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

	public boolean addToCart(CartItem item) {
		// Check if item exists, if so update quantity
		String checkSql = "SELECT quantity FROM CART WHERE buyer_id = ? AND product_id = ?";
		String updateSql = "UPDATE CART SET quantity = quantity + ? WHERE buyer_id = ? AND product_id = ?";
		String insertSql = "INSERT INTO CART (cart_id, buyer_id, product_id, quantity, added_at) VALUES (CART_SEQ.NEXTVAL, ?, ?, ?, SYSDATE)";

		try (Connection conn = DBConnection.getConnection()) {
			// Check existence
			try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
				checkStmt.setInt(1, item.getBuyerId());
				checkStmt.setInt(2, item.getProductId());
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next()) {
					// Update
					try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
						updateStmt.setInt(1, item.getQuantity());
						updateStmt.setInt(2, item.getBuyerId());
						updateStmt.setInt(3, item.getProductId());
						return updateStmt.executeUpdate() > 0;
					}
				}
			}

			// Insert
			try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
				insertStmt.setInt(1, item.getBuyerId());
				insertStmt.setInt(2, item.getProductId());
				insertStmt.setInt(3, item.getQuantity());
				return insertStmt.executeUpdate() > 0;
			}

		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Database error adding to cart: " + e.getMessage());
			return false;
		}
	}

	public List<CartItem> getCartItems(int buyerId) {
		List<CartItem> items = new ArrayList<>();
		String sql = "SELECT * FROM CART WHERE buyer_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, buyerId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				CartItem item = new CartItem();
				item.setCartId(rs.getInt("cart_id"));
				item.setBuyerId(rs.getInt("buyer_id"));
				item.setProductId(rs.getInt("product_id"));
				item.setQuantity(rs.getInt("quantity"));
				item.setAddedAt(rs.getDate("added_at"));
				items.add(item);
			}
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Failed to fetch cart for buyer " + buyerId + ": " + e.getMessage());
		}
		return items;
	}

	public boolean removeFromCart(int buyerId, int productId) {
		String sql = "DELETE FROM CART WHERE buyer_id = ? AND product_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, buyerId);
			pstmt.setInt(2, productId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
//            e.printStackTrace();
			System.err.println("Failed to remove item from cart (buyer=" + buyerId + ", product=" + productId + "): "
					+ e.getMessage());
			return false;
		}
	}

	public boolean clearCart(int buyerId) {
		String sql = "DELETE FROM CART WHERE buyer_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, buyerId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
//			e.printStackTrace();
			System.err.println("Failed to clear cart for buyer " + buyerId + ": " + e.getMessage());
			return false;
		}
	}
}
