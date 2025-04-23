package com.playdata.orderserviceback.product.repository;

import com.playdata.orderserviceback.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

}
