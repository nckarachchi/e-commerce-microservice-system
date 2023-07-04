package com.ecommerce.productservice.service;

import com.ecommerce.amqp.RabbitMQMessageProducer;
import com.ecommerce.amqp.dto.DeleteInventoryRequest;
import com.ecommerce.amqp.dto.InventoryRequest;
import com.ecommerce.productservice.dto.Pagination;
import com.ecommerce.productservice.dto.comment.CommentDto;
import com.ecommerce.productservice.dto.comment.CommentMapper;
import com.ecommerce.productservice.dto.product.*;
import com.ecommerce.productservice.enumeration.Sort;
import com.ecommerce.productservice.exception.ProductNotFoundException;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.ProductModel;
import com.ecommerce.productservice.repository.ExampleRepository;
import com.ecommerce.productservice.repository.ProductElasticRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.dto.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final CommentMapper commentMapper;
    private final ProductElasticRepository productElasticRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ExampleRepository exampleRepository;

    public Pagination<ProductDto> getAllProducts(int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productRepository.findAll(paging);

        return new Pagination<>(products.stream().map(productMapper::productToProductDto).collect(Collectors.toList()),
                products.getTotalElements());
    }

    public ProductDto getProductDtoById(UUID id) {
        return productMapper.productToProductDto(productRepository.findById(id)
                .orElseThrow(()->{
                    log.error("Product with id: {} could not be found!", id);
                    throw new ProductNotFoundException("Product with id " + id + " could not be found!");
                }));
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(()->{
                    log.error("Product with id: {} could not be found!", id);
                    throw new ProductNotFoundException("Product with id " + id + " could not be found!");
                });
    }

    public List<CommentDto> getCommentsByProductId(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()->{
                    log.error("Product with id: {} could not be found!", id);
                    throw new ProductNotFoundException("Product with id " + id + " could not be found!");
                });
        return product.getComments().stream().map(commentMapper::commentToCommentDto).collect(Collectors.toList());
    }

    public List<ProductDto> getProductsByIds(List<UUID> productIds) {
        List<Product> products = productRepository.findByIdIn(productIds);
        return products.stream().map(productMapper::productToProductDto).collect(Collectors.toList());
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest createProductRequest) {
        Category category = categoryService.getCategoryById(createProductRequest.getCategoryId());

        Product product =  Product.builder()
                .name(createProductRequest.getName())
                .unitPrice(createProductRequest.getUnitPrice())
                .description(createProductRequest.getDescription())
                .category(category)
                .imageUrl(createProductRequest.getImageUrl())
                .build();

        Product savedProduct = productRepository.save(product);

        // create to inventory service
        InventoryRequest inventoryRequest = new InventoryRequest(savedProduct.getId()
                ,createProductRequest.getQuantityInStock());
        rabbitMQMessageProducer.publish(
                inventoryRequest,
                "inventory.exchange",
                "create.inventory.routing-key"
        );

        saveProductToElastic(savedProduct);

        log.info("end successfully.");

        ProductDto pdResponse=  productMapper.productToProductDto(product);
        log.info("end successfully2.");
        return pdResponse;
    }

    public ProductDto updateProduct(UpdateProductRequest updateProductRequest, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->{
                    log.error("Product with id: {} could not be found!", productId);
                    throw new ProductNotFoundException("Product with id " + productId + " could not be found!");
                });

        Category category = categoryService.getCategoryById(updateProductRequest.getCategoryId());

        product.setCategory(category);
        product.setDescription(updateProductRequest.getDescription());
        product.setName(updateProductRequest.getName());
        product.setUnitPrice(updateProductRequest.getUnitPrice());
        product.setImageUrl(updateProductRequest.getImageUrl());

        // update from inventory service
//        InventoryRequest inventoryRequest = new InventoryRequest(productId,updateProductRequest.getQuantityInStock());
//        rabbitMQMessageProducer.publish(
//                inventoryRequest,
//                "inventory.exchange",
//                "update.inventory.routing-key"
//        );

        productRepository.save(product);
        updateProductFromElastic(product);

        return productMapper.productToProductDto(product);
    }

    @Transactional
    public UUID deleteProduct(UUID id) {
        productRepository.deleteById(id);
        productElasticRepository.deleteById(id);;

        // delete from inventory service
        DeleteInventoryRequest deleteInventoryRequest = new DeleteInventoryRequest(id);
        rabbitMQMessageProducer.publish(
                deleteInventoryRequest,
                "inventory.exchange",
                "delete.inventory.routing-key"
        );
        return id;
    }

    public List<ProductSearchDto> searchProduct(String searchTerm, int page, int size, Sort sort, String filter) {
        QueryBuilder queryBuilder;
        if(searchTerm == null || searchTerm.length() ==0 ) {
            queryBuilder = matchAllQuery();
        } else {
            queryBuilder = multiMatchQuery(searchTerm)
                    .field("name")
                    .field("categoryName")
                    .field("description")
                    .operator(Operator.AND)
                    .fuzziness(Fuzziness.TWO)
                    .prefixLength(0);
        }

        BoolQueryBuilder filterBuilder = boolQuery();
        if(filter != null && filter.length() != 0){
            filterBuilder.filter(matchQuery("categoryName",filter));
        }

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort(sort.getField()).order(sort.getOrder()))
                .withPageable(PageRequest.of(page, size))
                .withFilter(filterBuilder)
                .build();

        List<SearchHit<ProductModel>> productModels = elasticsearchOperations.search(query, ProductModel.class,
              IndexCoordinates.of("product")).getSearchHits();
        log.info("product getting successfully.");

        return productModels.stream().map(productMapper::productSearchDtoMapper).collect(Collectors.toList());
    }

    private void saveProductToElastic(Product product) {
        ProductModel productModel = ProductModel.builder()
                .categoryName(product.getCategory().getName())
                .description(product.getDescription())
                .id(product.getId())
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .imageUrl(product.getImageUrl())
                .build();

        System.out.println(productModel);
        productElasticRepository.save(productModel);
        log.info("saved successfully in product elastic.");

    }

    private void updateProductFromElastic(Product product) {
        ProductModel productModel = ProductModel.builder()
                .categoryName(product.getCategory().getName())
                .description(product.getDescription())
                .id(product.getId())
                .name(product.getName())
                .unitPrice(product.getUnitPrice())
                .imageUrl(product.getImageUrl())
                .build();

        productElasticRepository.save(productModel);
    }

}


