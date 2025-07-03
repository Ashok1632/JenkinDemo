package com.ashok.controller;

import com.ashok.exception.ResourceNotFoundException;
import com.ashok.exception.RestExceptionHandler;
import com.ashok.model.Product;
import com.ashok.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;



import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Product product;

    @BeforeEach
    void setUp() {
        // Create a validator that supports JSR-303/JSR-349
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        
        // Create a standalone setup with the controller
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setValidator(validator)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
                
        objectMapper = new ObjectMapper();

        // Initialize test product
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setQuantity(10);
        
        // Reset mocks
        reset(productService);
    }

    @Test
    @DisplayName("GET /api/products - Success")
    void getAllProducts_ShouldReturnProducts() throws Exception {
        // Given
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        
        List<Product> products = Arrays.asList(product, product2);
        given(productService.getAllProducts()).willReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[1].name", is("Another Product")));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Success")
    void getProductById_ShouldReturnProduct() throws Exception {
        // Given
        given(productService.getProductById(1L)).willReturn(product);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} - Not Found")
    void getProductById_ShouldReturnNotFound() throws Exception {
        // Given
        long productId = 999L;
        String errorMessage = "Product not found with id: " + productId;
        given(productService.getProductById(productId))
                .willThrow(new ResourceNotFoundException(errorMessage));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getMessage()));

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    @DisplayName("POST /api/products - Success")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        given(productService.createProduct(any(Product.class))).willReturn(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product")));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Success")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(199.99);
        updatedProduct.setQuantity(5);

        given(productService.updateProduct(eq(1L), any(Product.class))).willReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product")));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Success")
    void deleteProduct_ShouldReturnOk() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("POST /api/products - Validation Error")
    void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        Product invalidProduct = new Product();
        invalidProduct.setName(""); // Empty name is invalid
        invalidProduct.setPrice(null); // Null price is invalid

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists());

        verify(productService, never()).createProduct(any());
    }
}
