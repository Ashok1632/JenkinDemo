-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS jenkin_demo;

-- Switch to the database
USE jenkin_demo;

-- Create products table if it doesn't exist
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL
);

-- Insert sample data
INSERT INTO products (name, description, price, quantity) VALUES
('Laptop', 'High-performance laptop with 16GB RAM', 999.99, 10),
('Smartphone', 'Latest smartphone with 128GB storage', 699.99, 20),
('Headphones', 'Wireless noise-cancelling headphones', 199.99, 30),
('Keyboard', 'Mechanical gaming keyboard', 129.99, 25),
('Mouse', 'Wireless ergonomic mouse', 49.99, 40);
