package com.revshop.service;

import com.revshop.dao.ReviewDAO;
import com.revshop.model.Review;

import java.util.List;

public class ReviewService {

    private ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
    }

    public boolean addReview(Review review) {
        return reviewDAO.addReview(review);
    }

    public List<Review> getReviewsForProduct(int productId) {
        return reviewDAO.getReviewsByProduct(productId);
    }
}
