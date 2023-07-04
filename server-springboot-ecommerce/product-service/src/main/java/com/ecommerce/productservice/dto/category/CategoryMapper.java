package com.ecommerce.productservice.dto.category;

import com.ecommerce.productservice.model.Category;
import org.springframework.stereotype.Component;


@Component
public class CategoryMapper {
    public CategoryDto categoryToCategoryDto(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}
