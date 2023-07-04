package com.ecommerce.productservice.dto.product;

import com.ecommerce.productservice.dto.category.CategoryDto;
import com.ecommerce.productservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private UUID id;
    private String name;
    private BigDecimal unitPrice;
    private CategoryDto category;
    private String description;
    private LocalDateTime createdDate;
    private String imageUrl;
    private List<CommentDto> comments;
}
