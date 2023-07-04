package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleRepository extends JpaRepository<example, Long> {
}
