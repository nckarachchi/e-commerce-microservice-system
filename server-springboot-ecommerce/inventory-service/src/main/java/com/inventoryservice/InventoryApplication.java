package com.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(
        scanBasePackages = {
                "com.inventoryservice",
                "com.ecommerce.amqp"
                }
)
@EnableEurekaClient
public class InventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class,args);
    }
}

