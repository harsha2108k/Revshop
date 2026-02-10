\# RevShop 🛒



RevShop is a \*\*console-based eCommerce application\*\* developed using \*\*Java\*\*, \*\*JDBC\*\*, and \*\*Oracle 11g\*\*.  

It simulates the core functionality of an online shopping platform with role-based users and persistent data storage.



---



\## 🚀 Features



\### 👤 User Management

\- User registration and login

\- Role-based access (Buyer, Seller, Admin)

\- Security questions for account recovery



\### 🛍 Product \& Category Management

\- Category-wise product listing

\- Seller-managed products

\- Stock and price management

\- Active / inactive product support



\### 🛒 Cart Management

\- Add products to cart

\- Update quantity

\- Remove items

\- Prevent duplicate cart entries



\### 📦 Order Management

\- Place orders from cart

\- Order status tracking:

&nbsp; - PENDING

&nbsp; - SHIPPED

&nbsp; - DELIVERED

&nbsp; - CANCELLED

\- Order history support



\### ⭐ Reviews \& Favorites

\- Product ratings (1–5)

\- Text reviews

\- Favorite products list



\### 🔔 Notifications

\- Persistent notifications stored in database

\- Read / unread status

\- Examples:

&nbsp; - Order placed

&nbsp; - Order shipped

&nbsp; - Low stock alerts



---



\## 🧱 Tech Stack



\- \*\*Language:\*\* Java

\- \*\*Database:\*\* Oracle 11g

\- \*\*Connectivity:\*\* JDBC

\- \*\*Application Type:\*\* Console-based

\- \*\*Architecture:\*\* Layered (Model, DAO, Service)



---



\## 🗂 Database Tables



\- USERS

\- CATEGORIES

\- PRODUCTS

\- CART

\- ORDERS

\- ORDER\_ITEMS

\- REVIEWS

\- FAVORITES

\- NOTIFICATIONS



Primary keys are generated using Oracle sequences.



---



\## ⚙️ Setup Instructions



1\. Install Oracle Database 11g

2\. Run the provided SQL schema script

3\. Update database credentials in JDBC connection file

4\. Compile and run the application



```bash

javac \*.java

java Main



