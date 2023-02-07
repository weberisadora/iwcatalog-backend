package iwcatalog.repositories;

import iwcatalog.entities.Product;
import iwcatalog.repositories.ProductRepository;
import iwcatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTests {
    @Autowired
    ProductRepository productRepository;

    long existingId;
    long nonExistingId;
    long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Optional<Product> result = productRepository.findById(existingId);
        assertTrue(result.isPresent());
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<Product> result = productRepository.findById(nonExistingId);
        assertTrue(result.isEmpty());
    }

    @Test
    void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);
        product = productRepository.save(product);
        assertNotNull(product.getId());
        assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
        assertThrows(EmptyResultDataAccessException.class, () -> {
            productRepository.deleteById(nonExistingId);
        });
    }
}
