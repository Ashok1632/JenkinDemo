package com.ashok.service;

import com.ashok.exception.ResourceNotFoundException;
import com.ashok.model.Product;
import com.ashok.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    private Product product;
    private Product product2;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setQuantity(10);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setDescription("Test Description 2");
        product2.setPrice(199.99);
        product2.setQuantity(5);
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));

        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product when valid id is provided")
    void getProductById_WhenValidId_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product foundProduct = productService.getProductById(1L);

        // Assert
        assertNotNull(foundProduct);
        assertEquals(product.getId(), foundProduct.getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product savedProduct = productService.createProduct(product);

        // Assert
        assertNotNull(savedProduct);
        assertEquals(product.getName(), savedProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WithValidId_ShouldReturnUpdatedProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(199.99);
        updatedProduct.setQuantity(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals(updatedProduct.getName(), result.getName());
        assertEquals(updatedProduct.getDescription(), result.getDescription());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_WithValidId_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act & Assert
        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
