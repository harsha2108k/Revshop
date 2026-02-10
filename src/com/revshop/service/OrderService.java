package com.revshop.service;

import com.revshop.dao.CartDAO;
import com.revshop.dao.OrderDAO;
import com.revshop.dao.ProductDAO;
import com.revshop.model.CartItem;
import com.revshop.model.Order;
import com.revshop.model.OrderItem;
import com.revshop.model.Product;
import com.revshop.service.NotificationService;

import java.util.List;

public class OrderService {

    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private NotificationService notificationService;

    public OrderService() {
        this.cartDAO = new CartDAO();
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.notificationService = new NotificationService();
    }

    public boolean addToCart(int buyerId, int productId, int quantity) {
        Product product = productDAO.getProductById(productId);
        if (product == null || product.getStockQuantity() < quantity) {
            return false;
        }
        CartItem item = new CartItem();
        item.setBuyerId(buyerId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        return cartDAO.addToCart(item);
    }

    public List<CartItem> getCartItems(int buyerId) {
        return cartDAO.getCartItems(buyerId);
    }
    
    public boolean removeFromCart(int buyerId, int productId) {
        return cartDAO.removeFromCart(buyerId, productId);
    }

    public boolean placeOrder(int buyerId, String shippingAddress, String paymentMethod) {
        List<CartItem> cartItems = cartDAO.getCartItems(buyerId);
        if (cartItems.isEmpty()) {
            return false;
        }

        // Calculate Total and Validate Stock again
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            Product p = productDAO.getProductById(item.getProductId());
            if (p.getStockQuantity() < item.getQuantity()) {
                System.out.println("Insufficient stock for product: " + p.getName());
                return false;
            }
            totalAmount += p.getPrice() * item.getQuantity();
        }

        // Create Order
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);

        int orderId = orderDAO.createOrder(order);
        if (orderId != -1) {
            // Create Order Items and Update Stock
            // Create Order Items (Stock update moved to Acceptance)
            for (CartItem item : cartItems) {
                Product p = productDAO.getProductById(item.getProductId());
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPricePerUnit(p.getPrice());
                
                orderDAO.createOrderItem(orderItem);
                // productDAO.updateStock(p.getProductId(), item.getQuantity()); // Moved to Seller Acceptance
            }

            // Clear Cart
            cartDAO.clearCart(buyerId);
            
            // Notify Buyer
            notificationService.sendNotification(buyerId, "Order #" + orderId + " placed successfully. Total: " + totalAmount);
            
            // Notify Sellers
            for (CartItem item : cartItems) {
                Product p = productDAO.getProductById(item.getProductId());
                if (p != null) {
                    String updateMsg = "New Order #" + orderId + " received for product: " + p.getName() + " (Qty: " + item.getQuantity() + ")";
                    // We need to notify the seller of this product.
                    // IMPORTANT: We need sellerId from product.
                    notificationService.sendNotification(p.getSellerId(), updateMsg);
                }
            }
            
            return true;
        }

        return false;
    }

    public List<Order> getOrderHistory(int buyerId) {
        return orderDAO.getOrdersByBuyer(buyerId);
    }
    
    public List<Order> getOrdersBySeller(int sellerId) {
        return orderDAO.getOrdersBySeller(sellerId);
    }

    public List<OrderItem> getOrderItemsForSeller(int orderId, int sellerId) {
        return orderDAO.getOrderItemsBySeller(orderId, sellerId);
    }

    public boolean updateOrderStatus(int orderId, String status) {
        if ("SHIPPED".equals(status)) {
            // Deduct stock on acceptance
            List<OrderItem> items = orderDAO.getOrderItems(orderId);
            boolean stockUpdated = true;
            for (OrderItem item : items) {
                if (!productDAO.updateStock(item.getProductId(), item.getQuantity())) {
                    System.out.println("Failed to update stock for Product ID: " + item.getProductId() + " (Insufficient Stock?)");
                    stockUpdated = false; 
                    // ideally we should rollback previous items here or check beforehand
                }
            }
            if (!stockUpdated) return false;
        }
        
        boolean updated = orderDAO.updateOrderStatus(orderId, status);
        if (updated) {
            // Notify Buyer about status update
            Order order = orderDAO.getOrderById(orderId);
            if (order != null) {
                notificationService.sendNotification(order.getBuyerId(), "Order #" + orderId + " is now " + status);
            }
        }
        return updated;
    }
    public boolean hasUserPurchasedProduct(int userId, int productId) {
        return orderDAO.hasUserPurchasedProduct(userId, productId);
    }
}
