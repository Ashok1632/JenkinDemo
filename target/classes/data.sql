-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Clear existing data
TRUNCATE TABLE products;

-- Sample product data
INSERT INTO products (name, description, price, quantity) VALUES
('Laptop', 'High-performance laptop with 16GB RAM', 999.99, 10),
('Smartphone', 'Latest smartphone with 128GB storage', 699.99, 20),
('Headphones', 'Wireless noise-cancelling headphones', 199.99, 30),
('Keyboard', 'Mechanical gaming keyboard', 129.99, 25),
('Mouse', 'Wireless ergonomic mouse', 49.99, 40);

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
