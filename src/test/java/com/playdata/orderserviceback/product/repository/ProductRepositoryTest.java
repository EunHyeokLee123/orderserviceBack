package com.playdata.orderserviceback.product.repository;

import com.playdata.orderserviceback.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void bulkInsert(){
        for (int i = 0; i < 51; i++) {
            Product build = Product.builder()
                    .name("상품 " + i)
                    .category("카테고리" + i)
                    .price(3000)
                    .stockQuantity(120)
                    .build();
            productRepository.save(build);
        }
    }

}