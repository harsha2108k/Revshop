package com.revshop.service;

import com.revshop.dao.FavoriteDAO;
import com.revshop.model.Product;

import java.util.List;

public class FavoriteService {
    private FavoriteDAO favoriteDAO;

    public FavoriteService() {
        this.favoriteDAO = new FavoriteDAO();
    }

    public boolean addFavorite(int userId, int productId) {
        if (favoriteDAO.isFavorite(userId, productId)) {
            return false; // Already created
        }
        return favoriteDAO.addFavorite(userId, productId);
    }

    public boolean removeFavorite(int userId, int productId) {
        return favoriteDAO.removeFavorite(userId, productId);
    }

    public List<Product> getFavorites(int userId) {
        return favoriteDAO.getFavoritesByUserId(userId);
    }
    
    public boolean isFavorite(int userId, int productId) {
        return favoriteDAO.isFavorite(userId, productId);
    }
}
