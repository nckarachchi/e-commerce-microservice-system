package com.ecommerce.userservice;

import com.ecommerce.userservice.constant.FileConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

@SpringBootApplication(
        scanBasePackages = {
                "com.ecommerce.userservice",
                "com.ecommerce.amqp"
        }
)
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
        new File(FileConstant.USER_FOLDER).mkdirs();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
