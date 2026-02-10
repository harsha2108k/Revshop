package com.revshop.dao;

import com.revshop.model.Review;
import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public boolean addReview(Review review) {
        String sql = "INSERT INTO REVIEWS (review_id, product_id, buyer_id, rating, review_text, review_date) " +
                     "VALUES (REVIEW_SEQ.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, review.getProductId());
            pstmt.setInt(2, review.getBuyerId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getReviewText());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to add review for product " + review.getProductId() + 
                    " by buyer " + review.getBuyerId() + ": " + e.getMessage());
            return false;
        }
    }

    public List<Review> getReviewsByProduct(int productId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM REVIEWS WHERE product_id = ? ORDER BY review_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setProductId(rs.getInt("product_id"));
                review.setBuyerId(rs.getInt("buyer_id"));
                review.setRating(rs.getInt("rating"));
                review.setReviewText(rs.getString("review_text"));
                review.setReviewDate(rs.getDate("review_date"));
                reviews.add(review);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to fetch reviews for product " + productId + ": " + e.getMessage());
        }
        return reviews;
    }
}
