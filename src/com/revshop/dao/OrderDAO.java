package com.revshop.dao;

import com.revshop.model.Order;
import com.revshop.model.OrderItem;
import com.revshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int createOrder(Order order) {
        String sql = "INSERT INTO ORDERS (order_id, buyer_id, total_amount, order_status, order_date, shipping_address, payment_method) " +
                     "VALUES (ORDER_SEQ.NEXTVAL, ?, ?, 'PENDING', SYSDATE, ?, ?)";
        // Need to return the generated ID
        String[] generatedColumns = {"order_id"};

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, generatedColumns)) {
            
            pstmt.setInt(1, order.getBuyerId());
            pstmt.setDouble(2, order.getTotalAmount());
            pstmt.setString(3, order.getShippingAddress());
            pstmt.setString(4, order.getPaymentMethod());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to create order for buyer " + order.getBuyerId() + 
                    " (amount=" + order.getTotalAmount() + "): " + e.getMessage());
        }
        return -1; // Fail
    }

    public boolean createOrderItem(OrderItem item) {
        String sql = "INSERT INTO ORDER_ITEMS (order_item_id, order_id, product_id, quantity, price_per_unit) " +
                     "VALUES (ORDER_ITEM_SEQ.NEXTVAL, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, item.getOrderId());
            pstmt.setInt(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getPricePerUnit());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to create order item (order=" + item.getOrderId() + 
                     ", product=" + item.getProductId() + "): " + e.getMessage());
            return false;
        }
    }

    public List<Order> getOrdersByBuyer(int buyerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM ORDERS WHERE buyer_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, buyerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setBuyerId(rs.getInt("buyer_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orders.add(order);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to fetch orders for buyer " + buyerId + ": " + e.getMessage());
        }
        return orders;
    }

    public List<Order> getOrdersBySeller(int sellerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT DISTINCT o.* FROM ORDERS o " +
                     "JOIN ORDER_ITEMS oi ON o.order_id = oi.order_id " +
                     "JOIN PRODUCTS p ON oi.product_id = p.product_id " +
                     "WHERE p.seller_id = ? " +
                     "ORDER BY o.order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setBuyerId(rs.getInt("buyer_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orders.add(order);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to fetch seller orders for seller " + sellerId + ": " + e.getMessage());
        }
        return orders;
    }

    public List<OrderItem> getOrderItemsBySeller(int orderId, int sellerId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.* FROM ORDER_ITEMS oi " +
                     "JOIN PRODUCTS p ON oi.product_id = p.product_id " +
                     "WHERE oi.order_id = ? AND p.seller_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, sellerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPricePerUnit(rs.getDouble("price_per_unit"));
                items.add(item);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to fetch order items (order=" + orderId + ", seller=" + sellerId + "): " + e.getMessage());
        }
        return items;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM ORDERS WHERE order_id = ?";
        Order order = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setBuyerId(rs.getInt("buyer_id"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setOrderDate(rs.getDate("order_date"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to fetch order " + orderId + ": " + e.getMessage());
        }
        return order;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE ORDERS SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to update order " + orderId + " status to '" + status + "': " + e.getMessage());
            return false;
        }
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM ORDER_ITEMS WHERE order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPricePerUnit(rs.getDouble("price_per_unit"));
                items.add(item);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	System.err.println("Failed to fetch order items for order " + orderId + ": " + e.getMessage());
        }
        return items;
    }
    public boolean hasUserPurchasedProduct(int userId, int productId) {
        String sql = "SELECT 1 FROM ORDERS o " +
                     "JOIN ORDER_ITEMS oi ON o.order_id = oi.order_id " +
                     "WHERE o.buyer_id = ? AND oi.product_id = ? " +
                     "AND o.order_status IN ('SHIPPED', 'DELIVERED')";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        	 System.err.println("Failed to check purchase (user=" + userId + ", product=" + productId + "): " + e.getMessage());
            return false;
        }
    }
}
