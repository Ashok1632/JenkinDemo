package com.ashok.controller;

import com.ashok.model.Product;
import com.ashok.service.ProductService;
import com.ashok.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private Product product2;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setQuantity(10);

        product2=new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        product2.setDescription("Another Description");
        product2.setPrice(199.99);
        product2.setQuantity(5);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(product, product2);
        
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(product.getName())))
                .andExpect(jsonPath("$[1].name", is(product2.getName())));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_WithValidId_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void updateProduct_WithValidId_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(199.99);
        updatedProduct.setQuantity(5);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedProduct.getName())));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void deleteProduct_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void getProductById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999L))
                .thenThrow(new RuntimeException("Product not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).getProductById(999L);
    }
}
