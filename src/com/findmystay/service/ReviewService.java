package com.findmystay.service;

import com.findmystay.dao.ReviewDAO;
import com.findmystay.model.Review;

import java.util.List;

/**
 * Business service for reviews and ratings.
 */
public class ReviewService {

    private final ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
    }

    public boolean addReview(int propertyId, int userId, int rating, String comment) {
        if (rating < 1 || rating > 5 || comment == null || comment.trim().isEmpty()) {
            return false;
        }
        Review r = new Review(propertyId, userId, rating, comment.trim());
        return reviewDAO.addReview(r);
    }

    public List<Review> getPropertyReviews(int propertyId) {
        return reviewDAO.findByPropertyId(propertyId);
    }

    public List<Review> getAllReviews() {
        return reviewDAO.findAllReviews();
    }

    public boolean deleteReview(int reviewId) {
        return reviewDAO.deleteReview(reviewId);
    }
}

