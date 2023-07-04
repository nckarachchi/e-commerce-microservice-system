package com.ecommerce.productservice.dto.product;


import com.ecommerce.productservice.dto.category.CategoryMapper;
import com.ecommerce.productservice.dto.comment.CommentMapper;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.ProductModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final CommentMapper commentMapper;
    public ProductDto productToProductDto(Product product){
        return ProductDto.builder()
                .name(product.getName())
                .id(product.getId())
                .unitPrice(product.getUnitPrice())
                .description(product.getDescription())
                .category(categoryMapper.categoryToCategoryDto(product.getCategory()))
                .createdDate(product.getCreatedDate())
                .imageUrl(product.getImageUrl())
                .comments(product.getComments()!=null?product.getComments().stream().map(commentMapper::commentToCommentDto).collect(Collectors.toList()):null)
                .build();
    }

    public ProductSearchDto productSearchDtoMapper(SearchHit<ProductModel> productModel){
        return ProductSearchDto.builder()
                .name(productModel.getContent().getName())
                .id(productModel.getContent().getId())
                .unitPrice(productModel.getContent().getUnitPrice())
                .description(productModel.getContent().getDescription())
                .categoryName(productModel.getContent().getCategoryName())
                .imageUrl(productModel.getContent().getImageUrl())
                .build();
    }

}
