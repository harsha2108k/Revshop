package com.revshop.service;

import com.revshop.dao.CategoryDAO;

import com.revshop.dao.ProductDAO;
import com.revshop.model.Product;

import java.util.List;
import java.util.stream.Collectors;


public class ProductService {

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public boolean addProduct(Product product) {
        // The original price check was removed as per the provided "Code Edit"
        return productDAO.addProduct(product);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> getProductsBySeller(int sellerId) {
        return productDAO.getProductsBySeller(sellerId);
    }
    
    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> allProducts = productDAO.getAllProducts();
        return allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()) || 
                             p.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Product> getProductsByCategory(int categoryId) {
         List<Product> allProducts = productDAO.getAllProducts();
         return allProducts.stream()
                 .filter(p -> p.getCategoryId() == categoryId)
                 .collect(Collectors.toList());
    }

    public int getOrCreateCategory(String categoryName) {
        int catId = categoryDAO.getCategoryIdByName(categoryName);
        if (catId == -1) {
            catId = categoryDAO.createCategory(categoryName);
        }
        return catId;
    }
    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);
    }
}
