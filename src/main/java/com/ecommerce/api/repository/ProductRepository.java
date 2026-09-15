package com.ecommerce.api.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.api.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
