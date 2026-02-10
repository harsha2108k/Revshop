-- RevShop Database Schema for Oracle 11g

-- cleanup
-- DROP TABLE REVIEWS;
-- DROP TABLE ORDER_ITEMS;
-- DROP TABLE ORDERS;
-- DROP TABLE CART;
-- DROP TABLE PRODUCTS;
-- DROP TABLE CATEGORIES;
-- DROP TABLE USERS;
-- DROP SEQUENCE USER_SEQ;
-- DROP SEQUENCE CATEGORY_SEQ;
-- DROP SEQUENCE PRODUCT_SEQ;
-- DROP SEQUENCE CART_SEQ;
-- DROP SEQUENCE ORDER_SEQ;
-- DROP SEQUENCE ORDER_ITEM_SEQ;
-- DROP SEQUENCE REVIEW_SEQ;

-- Users Table
CREATE TABLE USERS (
    user_id NUMBER PRIMARY KEY,
    email VARCHAR2(100) UNIQUE NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    role VARCHAR2(20) CHECK (role IN ('BUYER', 'SELLER', 'ADMIN')),
    name VARCHAR2(100),
    phone VARCHAR2(20),
    address VARCHAR2(255),
    security_question VARCHAR2(255),
    security_answer VARCHAR2(255),
    registration_date DATE DEFAULT SYSDATE
);

CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;

-- Categories Table
CREATE TABLE CATEGORIES (
    category_id NUMBER PRIMARY KEY,
    category_name VARCHAR2(100) UNIQUE NOT NULL
);

CREATE SEQUENCE CATEGORY_SEQ START WITH 1 INCREMENT BY 1;

-- Products Table
CREATE TABLE PRODUCTS (
    product_id NUMBER PRIMARY KEY,
    seller_id NUMBER REFERENCES USERS(user_id),
    category_id NUMBER REFERENCES CATEGORIES(category_id),
    name VARCHAR2(100) NOT NULL,
    description VARCHAR2(500),
    price NUMBER(10, 2) NOT NULL,
    mrp NUMBER(10, 2), -- Maximum Retail Price
    stock_quantity NUMBER DEFAULT 0,
    image_url VARCHAR2(255),
    is_active NUMBER(1) DEFAULT 1, -- 1 for active, 0 for inactive
    created_at DATE DEFAULT SYSDATE
);

CREATE SEQUENCE PRODUCT_SEQ START WITH 1 INCREMENT BY 1;

-- Cart Table
CREATE TABLE CART (
    cart_id NUMBER PRIMARY KEY,
    buyer_id NUMBER REFERENCES USERS(user_id),
    product_id NUMBER REFERENCES PRODUCTS(product_id),
    quantity NUMBER DEFAULT 1,
    added_at DATE DEFAULT SYSDATE,
    CONSTRAINT unique_cart_item UNIQUE (buyer_id, product_id)
);

CREATE SEQUENCE CART_SEQ START WITH 1 INCREMENT BY 1;

-- Orders Table
CREATE TABLE ORDERS (
    order_id NUMBER PRIMARY KEY,
    buyer_id NUMBER REFERENCES USERS(user_id),
    total_amount NUMBER(10, 2),
    order_status VARCHAR2(20) DEFAULT 'PENDING' CHECK (order_status IN ('PENDING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    order_date DATE DEFAULT SYSDATE,
    shipping_address VARCHAR2(255),
    payment_method VARCHAR2(50)
);

CREATE SEQUENCE ORDER_SEQ START WITH 1 INCREMENT BY 1;

-- Order Items Table
CREATE TABLE ORDER_ITEMS (
    order_item_id NUMBER PRIMARY KEY,
    order_id NUMBER REFERENCES ORDERS(order_id),
    product_id NUMBER REFERENCES PRODUCTS(product_id),
    quantity NUMBER,
    price_per_unit NUMBER(10, 2)
);

CREATE SEQUENCE ORDER_ITEM_SEQ START WITH 1 INCREMENT BY 1;

-- Reviews Table
CREATE TABLE REVIEWS (
    review_id NUMBER PRIMARY KEY,
    product_id NUMBER REFERENCES PRODUCTS(product_id),
    buyer_id NUMBER REFERENCES USERS(user_id),
    rating NUMBER(1) CHECK (rating BETWEEN 1 AND 5),
    review_text VARCHAR2(500),
    review_date DATE DEFAULT SYSDATE
);

CREATE SEQUENCE REVIEW_SEQ START WITH 1 INCREMENT BY 1;

-- Favorites Table
CREATE TABLE FAVORITES (
    favorite_id NUMBER PRIMARY KEY,
    user_id NUMBER REFERENCES USERS(user_id),
    product_id NUMBER REFERENCES PRODUCTS(product_id),
    added_at DATE DEFAULT SYSDATE,
    CONSTRAINT unique_favorite UNIQUE (user_id, product_id)
);

CREATE SEQUENCE FAVORITE_SEQ START WITH 1 INCREMENT BY 1;

-- Notifications Table
CREATE TABLE NOTIFICATIONS (
    notification_id NUMBER PRIMARY KEY,
    user_id NUMBER REFERENCES USERS(user_id),
    message VARCHAR2(500),
    is_read NUMBER(1) DEFAULT 0, -- 0 for unread, 1 for read
    created_at DATE DEFAULT SYSDATE
);

CREATE SEQUENCE NOTIFICATION_SEQ START WITH 1 INCREMENT BY 1;

-- Initial Categories
INSERT INTO CATEGORIES (category_id, category_name) VALUES (CATEGORY_SEQ.NEXTVAL, 'Electronics');
INSERT INTO CATEGORIES (category_id, category_name) VALUES (CATEGORY_SEQ.NEXTVAL, 'Fashion');
INSERT INTO CATEGORIES (category_id, category_name) VALUES (CATEGORY_SEQ.NEXTVAL, 'Books');
INSERT INTO CATEGORIES (category_id, category_name) VALUES (CATEGORY_SEQ.NEXTVAL, 'Home & Kitchen');

COMMIT;
