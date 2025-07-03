package com.ashok.service;

import com.ashok.exception.ResourceNotFoundException;
import com.ashok.model.Product;
import com.ashok.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setQuantity(10);
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        
        given(productRepository.findAll()).willReturn(Arrays.asList(product, product2));

        // When
        List<Product> products = productService.getAllProducts();

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("Test Product");
        assertThat(products.get(1).getName()).isEqualTo("Another Product");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product when valid id is given")
    void getProductById_WithValidId_ShouldReturnProduct() {
        // Given
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // When
        Product foundProduct = productService.getProductById(1L);

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void getProductById_WithInvalidId_ShouldThrowException() {
        // Given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found with id: 999");
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should save product successfully")
    void createProduct_ShouldSaveProduct() {
        // Given
        given(productRepository.save(any(Product.class))).willReturn(product);

        // When
        Product savedProduct = productService.createProduct(product);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product when valid id is provided")
    void updateProduct_WithValidId_ShouldUpdateProduct() {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(199.99);
        updatedProduct.setQuantity(5);

        // Create a copy of the original product to simulate the updated state
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Updated Product");
        savedProduct.setDescription("Updated Description");
        savedProduct.setPrice(199.99);
        savedProduct.setQuantity(5);

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        // When
        Product result = productService.updateProduct(1L, updatedProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getPrice()).isEqualTo(199.99);
        assertThat(result.getQuantity()).isEqualTo(5);
        
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product when valid id is provided")
    void deleteProduct_WithValidId_ShouldDeleteProduct() {
        // Given
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        // Given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found with id: 999");
        verify(productRepository, never()).delete(any(Product.class));
    }
}
