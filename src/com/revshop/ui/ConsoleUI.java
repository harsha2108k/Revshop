package com.revshop.ui;

import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.model.Order;
import com.revshop.model.CartItem;
import com.revshop.model.Review;
import com.revshop.model.OrderItem;
import com.revshop.service.AuthService;
import com.revshop.service.OrderService;
import com.revshop.service.ProductService;
import com.revshop.service.ReviewService;
import com.revshop.service.FavoriteService;
import com.revshop.service.NotificationService;
import com.revshop.model.Notification;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;
    private AuthService authService;
    private ProductService productService;
    private OrderService orderService;
    private ReviewService reviewService;
    private FavoriteService favoriteService;
    private NotificationService notificationService;
    private User currentUser;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthService();
        this.productService = new ProductService();
        this.orderService = new OrderService();
        this.reviewService = new ReviewService();
        this.favoriteService = new FavoriteService();
        this.notificationService = new NotificationService();
    }

    public void start() {
        System.out.println("Welcome to RevShop - Your Trusted E-Commerce Platform");
        
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                if ("SELLER".equalsIgnoreCase(currentUser.getRole())) {
                    showSellerMenu();
                } else {
                    showBuyerMenu();
                }
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Forgot Password");
        System.out.println("4. Exit");
        System.out.print("Enter choice: ");
        
        int choice = readInt();
        switch (choice) {
            case 1: register(); break;
            case 2: login(); break;
            case 3: forgotPassword(); break;
            case 4: 
                System.out.println("Goodbye!"); 
                System.exit(0);
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void register() {
        System.out.println("\n--- Register ---");
        String email;
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.nextLine();
            if (email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
                break;
            }
            System.out.println("Invalid Email. Please enter a valid email address (e.g., user@example.com).");
        }

        String password;
        while (true) {
            System.out.println("Enter Password (at least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 symbol): ");
            password = scanner.nextLine();
            
            if (password.length() < 8) {
                System.out.println("Password must be at least 8 characters long.");
                continue;
            }
            if (!password.matches(".*[A-Z].*")) {
                System.out.println("Password must contain at least one uppercase letter.");
                continue;
            }
            if (!password.matches(".*[a-z].*")) {
                System.out.println("Password must contain at least one lowercase letter.");
                continue;
            }
            if (!password.matches(".*\\d.*")) {
                System.out.println("Password must contain at least one digit.");
                continue;
            }
            if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                System.out.println("Password must contain at least one symbol (!@#$%^&* etc).");
                continue;
            }
            break;
        }

        String name;
        while (true) {
            System.out.print("Enter Name: ");
            name = scanner.nextLine();
            if (name.matches("^[a-zA-Z\\s]+$")) {
                break;
            }
            System.out.println("Invalid Name. Name should not contain numbers. Please try again.");
        }

        System.out.print("Enter Role (BUYER/SELLER): ");
        String role = scanner.nextLine().toUpperCase();

        String phone;
        while (true) {
            System.out.print("Enter Phone: ");
            phone = scanner.nextLine();
            if (phone.matches("^\\d{10}$")) {
                break;
            }
            System.out.println("Invalid Phone Number. Must be exactly 10 digits and contain no letters.");
        }
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Security Question: ");
        String question = scanner.nextLine();
        System.out.print("Security Answer: ");
        String answer = scanner.nextLine();

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(password); // Will be hashed in service
        user.setName(name);
        user.setRole(role);
        user.setPhone(phone);
        user.setAddress(address);
        user.setSecurityQuestion(question);
        user.setSecurityAnswer(answer);

        if (authService.registerUser(user)) {
            System.out.println("Registration Successful! Please login.");
        } else {
            System.out.println("Registration Failed. Email might already exist.");
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = authService.login(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login Successful! Welcome, " + user.getName());
        } else {
            System.out.println("Invalid Credentials.");
        }
    }
    
    private void forgotPassword() {
        System.out.println("\n--- Forgot Password ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        User user = authService.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        
        System.out.println("Security Question: " + user.getSecurityQuestion());
        System.out.print("Enter Answer: ");
        String answer = scanner.nextLine();
        
        System.out.print("Enter New Password: ");
        String newPass = scanner.nextLine();
        
        if (authService.resetPassword(email, answer, newPass)) {
            System.out.println("Password reset successfully. Please login.");
        } else {
            System.out.println("Incorrect Request.");
        }
    }

    private void showSellerMenu() {
        System.out.println("\n--- Seller Dashboard ---");
        System.out.println("1. Add Product");
        System.out.println("2. View My Products");
        System.out.println("3. View Orders (Sold Items)"); 
        System.out.println("4. View Notifications");
        System.out.println("5. Update Product");
        System.out.println("6. Logout");
        System.out.print("Enter choice: ");
        
        int choice = readInt();
        switch (choice) {
            case 1: addProduct(); break;
            case 2: viewMyProducts(); break;

            case 3: viewSellerOrders(); break;
            case 4: viewNotifications(); break;
            case 5: updateProduct(); break;
            case 6: logout(); break;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void addProduct() {
        System.out.println("\n--- Add Product ---");
        System.out.print("Category Name: ");
        String catName = scanner.nextLine();
        
        // Get or Create Category
        int catId = productService.getOrCreateCategory(catName);
        
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Price: ");
        double price = readDouble();
        System.out.print("MRP: ");
        double mrp = readDouble();
        System.out.print("Stock Quantity: ");
        int stock = readInt();
        
        Product p = new Product();
        p.setSellerId(currentUser.getUserId());
        p.setCategoryId(catId);
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setMrp(mrp);
        p.setStockQuantity(stock);
        p.setImageUrl("N/A");
        p.setActive(true);
        
        if (productService.addProduct(p)) {
            System.out.println("Product Added Successfully!");
        } else {
            System.out.println("Failed to add product.");
        }
    }
    
    private void viewMyProducts() {
        System.out.println("\n--- My Products ---");
        List<Product> products = productService.getProductsBySeller(currentUser.getUserId());
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            for (Product p : products) {
                System.out.printf("ID: %d | Name: %s | Price: %.2f | Stock: %d\n", 
                        p.getProductId(), p.getName(), p.getPrice(), p.getStockQuantity());
            }
        }
    }

    private void viewSellerOrders() {
        System.out.println("\n--- Orders Containing Your Products ---");
        List<Order> orders = orderService.getOrdersBySeller(currentUser.getUserId());
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        for (Order o : orders) {
            System.out.printf("Order ID: %d | Date: %s | Status: %s | Total: %.2f\n",
                    o.getOrderId(), o.getOrderDate(), o.getOrderStatus(), o.getTotalAmount());
        }

        System.out.println("\nEnter Order ID to view details/process, or 0 to back:");
        int orderId = readInt();
        if (orderId > 0) {
            // Check if this order actually belongs to the list shown
            boolean exists = orders.stream().anyMatch(o -> o.getOrderId() == orderId);
            if (exists) {
                Order selectedOrder = orders.stream().filter(o -> o.getOrderId() == orderId).findFirst().orElse(null);
                if (selectedOrder != null) {
                    processSellerOrder(selectedOrder);
                }
            } else {
                System.out.println("Invalid Order ID from the list.");
            }
        }
    }

    private void processSellerOrder(Order order) {
        if (!"PENDING".equalsIgnoreCase(order.getOrderStatus())) {
             System.out.println("\nOrder #" + order.getOrderId() + " is already " + order.getOrderStatus());
             // Still show items but maybe not allow actions? Or allow generic actions if requirements change.
        } else {
             System.out.println("\nProcessing Order #" + order.getOrderId());
        }
        
        System.out.println("Buyer ID: " + order.getBuyerId());
        System.out.println("Shipping Address: " + order.getShippingAddress());
        
        System.out.println("\n--- Items ---");
        List<OrderItem> items = orderService.getOrderItemsForSeller(order.getOrderId(), currentUser.getUserId());
        for (OrderItem item : items) {
             Product p = productService.getProductById(item.getProductId());
             System.out.printf("- %s (ID: %d) x %d @ %.2f\n", p.getName(), p.getProductId(), item.getQuantity(), item.getPricePerUnit());
        }

        if ("PENDING".equalsIgnoreCase(order.getOrderStatus())) {
            System.out.println("\n1. Accept Order");
            System.out.println("2. Reject Order");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");
            int choice = readInt();
            
            if (choice == 1) {
                if (orderService.updateOrderStatus(order.getOrderId(), "SHIPPED")) {
                    System.out.println("Order Accepted (Status: SHIPPED)!");
                } else {
                    System.out.println("Failed to update status.");
                }
            } else if (choice == 2) {
                if (orderService.updateOrderStatus(order.getOrderId(), "CANCELLED")) {
                    System.out.println("Order Rejected (Status: CANCELLED)!");
                } else {
                    System.out.println("Failed to update status.");
                }
            }
        } else {
            System.out.println("\n[Status is " + order.getOrderStatus() + " - No actions available]");
            System.out.println("Press Enter to go back...");
            scanner.nextLine();
        }
    }

    private void showBuyerMenu() {
        System.out.println("\n--- Buyer Dashboard ---");
        System.out.println("1. Browse Products");
        System.out.println("2. Search Products");
        System.out.println("3. View Cart");
        System.out.println("4. View Order History");
        System.out.println("5. View Favorites");
        System.out.println("6. View Notifications");
        System.out.println("7. Logout");
        System.out.print("Enter choice: ");
        
        int choice = readInt();
        switch (choice) {
            case 1: browseProducts(); break;
            case 2: searchProducts(); break;
            case 3: viewCart(); break;
            case 4: viewOrderHistory(); break;
            case 5: viewFavorites(); break;
            case 6: viewNotifications(); break;
            case 7: logout(); break;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void browseProducts() {
        List<Product> products = productService.getAllProducts();
        displayProductList(products);
    }
    
    private void searchProducts() {
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine();
        List<Product> products = productService.searchProducts(keyword);
        displayProductList(products);
    }
    
    private void displayProductList(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        System.out.println("\n--- Products ---");
        for (Product p : products) {
            System.out.printf("ID: %d | %s | %.2f | Stock: %d\n", 
                    p.getProductId(), p.getName(), p.getPrice(), p.getStockQuantity());
        }
        System.out.println("----------------");
        System.out.println("Enter Product ID to view details/add to cart, or 0 to go back:");
        int pid = readInt();
        if (pid > 0) {
            Product p = productService.getProductById(pid);
            if (p != null) {
                showProductDetails(p);
            }
        }
    }
    
    private void showProductDetails(Product p) {
        System.out.println("\n--- Product Details ---");
        System.out.println("Name: " + p.getName());
        System.out.println("Description: " + p.getDescription());
        System.out.println("Price: " + p.getPrice());
        System.out.println("MRP: " + p.getMrp());
        System.out.println("Stock: " + p.getStockQuantity());
        
        // Show Reviews
        List<Review> reviews = reviewService.getReviewsForProduct(p.getProductId());
        if (!reviews.isEmpty()) {
            System.out.println("Reviews:");
            for (Review r : reviews) {
                System.out.println("- " + r.getRating() + "/5: " + r.getReviewText());
            }
        }
        
        System.out.println("\n1. Add to Cart");
        
        boolean hasPurchased = orderService.hasUserPurchasedProduct(currentUser.getUserId(), p.getProductId());
        if (hasPurchased) {
            System.out.println("2. Write Review");
        } else {
            System.out.println("2. Write Review (Locked - Purchase Required)");
        }
        
        System.out.println("3. Add to Favorites");
        System.out.println("4. Back");
        int subChoice = readInt();
        if (subChoice == 1) {
            System.out.print("Enter Quantity: ");
            int qty = readInt();
            if (orderService.addToCart(currentUser.getUserId(), p.getProductId(), qty)) {
                System.out.println("Added to cart!");
            } else {
                System.out.println("Failed to add to cart (Check stock).");
            }
        } else if (subChoice == 2) {
             if (!hasPurchased) {
                 System.out.println("You must purchase and receive this product before verifying.");
             } else {
                 System.out.print("Rating (1-5): ");
                 int rating = readInt();
                 System.out.print("Review: ");
                 String text = scanner.nextLine();
                 Review r = new Review();
                 r.setBuyerId(currentUser.getUserId());
                 r.setProductId(p.getProductId());
                 r.setRating(rating);
                 r.setReviewText(text);
                 if (reviewService.addReview(r)) {
                     System.out.println("Review added!");
                 }
             }
        } else if (subChoice == 3) {
            if (favoriteService.addFavorite(currentUser.getUserId(), p.getProductId())) {
                System.out.println("Added to Favorites!");
            } else {
                System.out.println("Already in Favorites.");
            }
        }
    }

    private void viewFavorites() {
        System.out.println("\n--- Your Favorites ---");
        List<Product> favorites = favoriteService.getFavorites(currentUser.getUserId());
        if (favorites.isEmpty()) {
            System.out.println("No favorites found.");
            return;
        }

        for (Product p : favorites) {
            System.out.printf("ID: %d | %s | %.2f\n", p.getProductId(), p.getName(), p.getPrice());
        }

        System.out.println("\n1. Remove from Favorites");
        System.out.println("2. Add to Cart");
        System.out.println("3. Back");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 1) {
            System.out.print("Enter Product ID to remove: ");
            int pid = readInt();
            if (favoriteService.removeFavorite(currentUser.getUserId(), pid)) {
                System.out.println("Removed from Favorites.");
                viewFavorites(); // Refresh
            } else {
                System.out.println("Failed to remove. Check Product ID.");
            }
        } else if (choice == 2) {
            System.out.print("Enter Product ID to add to cart: ");
            int pid = readInt();
            // Verify if it's in the favorites list
            Product selectedFav = favorites.stream()
                    .filter(p -> p.getProductId() == pid)
                    .findFirst()
                    .orElse(null);
            
            if (selectedFav == null) {
                System.out.println("Product ID not found in your favorites list.");
            } else {
                System.out.println("Available Stock: " + selectedFav.getStockQuantity());
                System.out.print("Enter Quantity: ");
                int qty = readInt();
                if (orderService.addToCart(currentUser.getUserId(), pid, qty)) {
                     System.out.println("Added to cart!");
                } else {
                     System.out.println("Failed to add to cart (Check stock).");
                }
            }
        }
    }
    
    private void viewCart() {
        System.out.println("\n--- Your Cart ---");
        List<CartItem> items = orderService.getCartItems(currentUser.getUserId());
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        double total = 0;
        for (CartItem item : items) {
            Product p = productService.getProductById(item.getProductId());
            double subtotal = p.getPrice() * item.getQuantity();
            total += subtotal;
            System.out.printf("[%d] %s x %d = %.2f\n", 
                    p.getProductId(), p.getName(), item.getQuantity(), subtotal);
        }
        System.out.printf("Total: %.2f\n", total);
        
        System.out.println("1. Checkout");
        System.out.println("2. Remove Product");
        System.out.println("3. Go Back");
        int choice = readInt();
        if (choice == 1) {
            System.out.print("Enter Shipping Address: ");
            String address = scanner.nextLine();
            
            String payment = "";
            while (true) {
                System.out.print("Payment Method (Card/UPI/COD): ");
                payment = scanner.nextLine();
                if (payment.equalsIgnoreCase("Card") || payment.equalsIgnoreCase("UPI") || payment.equalsIgnoreCase("COD")) {
                    break;
                }
                System.out.println("Invalid payment method. Please enter Card, UPI, or COD.");
            }
            
            if (orderService.placeOrder(currentUser.getUserId(), address, payment)) {
                System.out.println("Order Placed Successfully!");
            } else {
                System.out.println("Order failed. Stock might have run out.");
            }
        } else if (choice == 2) {
            System.out.print("Enter Product ID to remove: ");
            int pid = readInt();
            if (orderService.removeFromCart(currentUser.getUserId(), pid)) {
                System.out.println("Product removed from cart.");
                viewCart(); // Refresh cart view
            } else {
                System.out.println("Failed to remove product. Check Product ID.");
            }
        }
    }
    
    private void viewOrderHistory() {
         System.out.println("\n--- Order History ---");
         List<Order> orders = orderService.getOrderHistory(currentUser.getUserId());
         if (orders.isEmpty()) {
             System.out.println("No orders found.");
         } else {
             for (Order o : orders) {
                 System.out.printf("Order #%d | Date: %s | Status: %s | Total: %.2f\n", 
                         o.getOrderId(), o.getOrderDate(), o.getOrderStatus(), o.getTotalAmount());
             }
         }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private int readInt() {
        try {
            int i = Integer.parseInt(scanner.nextLine());
            return i;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void viewNotifications() {
        System.out.println("\n--- Notifications ---");
        List<Notification> notifications = notificationService.getNotifications(currentUser.getUserId());
        if (notifications.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }
        for (Notification n : notifications) {
            System.out.printf("[%s] %s %s\n", 
                    n.getCreatedAt(), 
                    n.getMessage(), 
                    n.isRead() ? "(Read)" : "(New)");
            if (!n.isRead()) {
                notificationService.markAsRead(n.getNotificationId());
            }
        }
    }


    private void updateProduct() {
        System.out.println("\n--- Update Product ---");
        System.out.print("Enter Product ID to update: ");
        int productId = readInt();

        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        // Verify ownership (assuming current seller must own the product)
        if (product.getSellerId() != currentUser.getUserId()) {
            System.out.println("You are not authorized to update this product.");
            return;
        }

        System.out.println("Updating Product: " + product.getName());
        System.out.println("Press Enter to keep current value.");

        System.out.print("Enter New Price (Current: " + product.getPrice() + "): ");
        String priceInput = scanner.nextLine();
        if (!priceInput.isEmpty()) {
            try {
                product.setPrice(Double.parseDouble(priceInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid Price. Keeping current.");
            }
        }

        System.out.print("Enter New MRP (Current: " + product.getMrp() + "): ");
        String mrpInput = scanner.nextLine();
        if (!mrpInput.isEmpty()) {
             try {
                product.setMrp(Double.parseDouble(mrpInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid MRP. Keeping current.");
            }
        }

        System.out.print("Enter New Stock Quantity (Current: " + product.getStockQuantity() + "): ");
        String stockInput = scanner.nextLine();
        if (!stockInput.isEmpty()) {
             try {
                product.setStockQuantity(Integer.parseInt(stockInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid Stock. Keeping current.");
            }
        }

        if (productService.updateProduct(product)) {
            System.out.println("Product updated successfully!");
        } else {
            System.out.println("Failed to update product.");
        }
    }
    
    private double readDouble() {
        try {
            double d = Double.parseDouble(scanner.nextLine());
            return d;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
