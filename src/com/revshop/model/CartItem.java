package com.revshop.model;

import java.sql.Date;

public class CartItem {
    private int cartId;
    private int buyerId;
    private int productId;
    private int quantity;
    private Date addedAt;

    public CartItem() {}

    public CartItem(int cartId, int buyerId, int productId, int quantity, Date addedAt) {
        this.cartId = cartId;
        this.buyerId = buyerId;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Date getAddedAt() { return addedAt; }
    public void setAddedAt(Date addedAt) { this.addedAt = addedAt; }
}
