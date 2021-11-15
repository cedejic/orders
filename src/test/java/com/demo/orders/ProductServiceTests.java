package com.demo.orders;

import com.demo.orders.dto.CreateProductDto;
import com.demo.orders.dto.ProductDto;
import com.demo.orders.dto.UpdateProductDto;
import com.demo.orders.model.Order;
import com.demo.orders.model.Product;
import com.demo.orders.repository.ProductRepository;
import com.demo.orders.service.product.ProductService;
import com.demo.orders.util.ConversionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.demo.orders.util.ConversionUtils.toBigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    private Answer<Product> productRepositorySaveAnswer;

    @BeforeEach
    public void setup() {
        productRepositorySaveAnswer = invocation -> {
            Object[] arguments = invocation.getArguments();
            if (arguments != null && arguments.length > 0 && arguments[0] != null && arguments[0] instanceof Product) {
                return (Product) arguments[0];
            }
            return null;
        };
    }

    @Test
    @DisplayName("Test getting Product SUCCESS")
    void testGettingProductSuccess() {
        Product product1 = new Product(1L, "Coffee", toBigDecimal(5.20));
        doReturn(Optional.of(product1)).when(productRepository).findById(product1.getId());

        ProductDto productDto = productService.getProduct(1L);

        assertNotNull(productDto);
        assertEquals("Coffee", productDto.getName());
        assertEquals(toBigDecimal(5.20),
            productDto.getPrice().setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    @DisplayName("Test getting non-existing Product SUCCESS")
    void testGettingNonExistentProductSuccess() {
        doReturn(Optional.empty()).when(productRepository).findById(anyLong());
        ProductDto productDto = productService.getProduct(1L);
        assertNull(productDto);
    }

    @Test
    @DisplayName("Test getting all Products SUCCESS")
    void testGettingAllProducts() {
        Product product1 = new Product(1L, "Coffee", toBigDecimal(5.20));
        Product product2 = new Product(2L, "Juice", toBigDecimal(3.75));

        doReturn(List.of(product1, product2)).when(productRepository).findAll();

        List<ProductDto> productDtos = productService.getAllProducts();

        assertNotNull(productDtos);
        assertEquals(2, productDtos.size());
        assertEquals(toBigDecimal(5.20), productDtos.get(0).getPrice().setScale(2, RoundingMode.HALF_EVEN));
        assertEquals("Juice", productDtos.get(1).getName());
    }

    @Test
    @DisplayName("Test create Product SUCCESS")
    void testCreateProductSuccess() {
        CreateProductDto createProductDto = new CreateProductDto("Jar of pickled eggs", toBigDecimal(12.25));

        when(productRepository.save(any(Product.class))).then(productRepositorySaveAnswer);

        ProductDto productDto = productService.createProduct(createProductDto);

        assertNotNull(productDto);
        assertEquals("Jar of pickled eggs", productDto.getName());
        assertEquals(toBigDecimal(12.25), productDto.getPrice().setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    @DisplayName("Test update Product SUCCESS")
    void testUpdateProductSuccess() {
        Product product1 = new Product(1L, "Coffee", toBigDecimal(5.20));
        doReturn(Optional.of(product1)).when(productRepository).findById(product1.getId());

        UpdateProductDto updateProductDto = new UpdateProductDto(toBigDecimal(4.75));

        when(productRepository.save(any(Product.class))).then(productRepositorySaveAnswer);

        ProductDto updatedProductDto = productService.updateProduct(1L, updateProductDto);

        assertNotNull(updatedProductDto);
        assertEquals("Coffee", updatedProductDto.getName());
        assertEquals(toBigDecimal(4.75), updatedProductDto.getPrice().setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    @DisplayName("Test update Product FAIL")
    void testUpdateProductFail() {
        Product product1 = new Product(1L, "Coffee", toBigDecimal(5.20));
        doReturn(Optional.of(product1)).when(productRepository).findById(product1.getId());

        UpdateProductDto updateProductDto = new UpdateProductDto(toBigDecimal(4.75));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> productService.updateProduct(5L, updateProductDto));

        assertEquals("Product with id 5 does not exist", exception.getMessage());
    }
}
