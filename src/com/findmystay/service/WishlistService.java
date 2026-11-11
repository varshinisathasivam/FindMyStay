package com.findmystay.service;

import com.findmystay.dao.WishlistDAO;
import com.findmystay.model.Wishlist;

import java.util.List;

/**
 * Business service for customer property wishlists.
 */
public class WishlistService {

    private final WishlistDAO wishlistDAO;

    public WishlistService() {
        this.wishlistDAO = new WishlistDAO();
    }

    public boolean addToWishlist(int userId, int propertyId) {
        return wishlistDAO.addWishlist(userId, propertyId);
    }

    public boolean removeFromWishlist(int userId, int propertyId) {
        return wishlistDAO.removeWishlist(userId, propertyId);
    }

    public boolean isWishlisted(int userId, int propertyId) {
        return wishlistDAO.isWishlisted(userId, propertyId);
    }

    public List<Wishlist> getUserWishlist(int userId) {
        return wishlistDAO.findByUserId(userId);
    }

    public int getWishlistCount(int userId) {
        return wishlistDAO.countByUserId(userId);
    }
}

