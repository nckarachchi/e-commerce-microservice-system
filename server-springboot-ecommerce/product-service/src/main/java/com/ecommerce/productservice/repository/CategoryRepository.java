package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
