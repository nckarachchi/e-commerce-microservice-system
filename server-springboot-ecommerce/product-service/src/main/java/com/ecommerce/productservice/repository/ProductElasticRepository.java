package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.ProductModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface ProductElasticRepository extends ElasticsearchRepository<ProductModel, UUID> {
}
