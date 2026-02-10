package com.revshop.dao;

import com.revshop.model.Product;
import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO PRODUCTS (product_id, seller_id, category_id, name, description, price, mrp, stock_quantity, image_url, is_active, created_at) " +
                     "VALUES (PRODUCT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, product.getSellerId());
            pstmt.setInt(2, product.getCategoryId());
            pstmt.setString(3, product.getName());
            pstmt.setString(4, product.getDescription());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setDouble(6, product.getMrp());
            pstmt.setInt(7, product.getStockQuantity());
            pstmt.setString(8, product.getImageUrl());
            pstmt.setInt(9, product.isActive() ? 1 : 0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to add product by seller " + product.getSellerId() + 
                    " ('" + product.getName() + "'): " + e.getMessage());
            return false;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS WHERE is_active = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to fetch all products: " + e.getMessage());
        }
        return products;
    }

    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS WHERE seller_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to fetch products for seller " + sellerId + ": " + e.getMessage());
        }
        return products;
    }
    
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM PRODUCTS WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to fetch product " + productId + ": " + e.getMessage());
        }
        return null;
    }

    public boolean updateStock(int productId, int quantitySold) {
        String sql = "UPDATE PRODUCTS SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantitySold);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantitySold); // Ensure stock doesn't go negative
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to update stock for product " + productId + 
                    " (quantity=" + quantitySold + "): " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE PRODUCTS SET price = ?, mrp = ?, stock_quantity = ? WHERE product_id = ? AND seller_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, product.getPrice());
            pstmt.setDouble(2, product.getMrp());
            pstmt.setInt(3, product.getStockQuantity());
            pstmt.setInt(4, product.getProductId());
            pstmt.setInt(5, product.getSellerId()); // Ensure seller owns product
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to update product " + product.getProductId() + 
                     " for seller " + product.getSellerId() + ": " + e.getMessage());
            return false;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setSellerId(rs.getInt("seller_id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setMrp(rs.getDouble("mrp"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setImageUrl(rs.getString("image_url"));
        p.setActive(rs.getInt("is_active") == 1);
        p.setCreatedAt(rs.getDate("created_at"));
        return p;
    }

    public boolean restoreStock(int productId, int quantity) {
        String sql = "UPDATE PRODUCTS SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to restore stock for product " + productId + 
                     " (quantity=" + quantity + "): " + e.getMessage());
            return false;
        }
    }
}
