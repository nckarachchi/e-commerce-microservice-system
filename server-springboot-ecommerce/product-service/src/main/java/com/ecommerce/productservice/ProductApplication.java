package com.ecommerce.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "com.ecommerce.productservice",
                "com.ecommerce.amqp"
        }
)
@EnableElasticsearchRepositories(basePackages = "com.ecommerce.productservice.repository")
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}
