package com.revshop.model;

import java.sql.Date;

public class Favorite {
    private int favoriteId;
    private int userId;
    private int productId;
    private Date addedAt;

    public Favorite() {}

    public Favorite(int favoriteId, int userId, int productId, Date addedAt) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.productId = productId;
        this.addedAt = addedAt;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }
}
