package com.example.ecommerce.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.ecommerce.product.Category;
import com.example.ecommerce.product.CategoryRepository;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.ProductRepository;
import com.example.ecommerce.user.Role;
import com.example.ecommerce.user.User;
import com.example.ecommerce.user.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(
            UserRepository userRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create default admin
            if (!userRepository.existsByEmail("admin@shopsphere.com")) {

                User admin = new User();
                admin.setName("ShopSphere Admin");
                admin.setEmail("admin@shopsphere.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            // Create sample products
            if (productRepository.count() == 0) {

                Category category = new Category();
                category.setName("Electronics");
                categoryRepository.save(category);

                String[] productNames = {
                        "Laptop",
                        "Wireless Headphones",
                        "Smart Watch",
                        "Mechanical Keyboard"
                };

                for (String name : productNames) {

                    Product product = new Product();
                    product.setName(name);
                    product.setDescription("Premium " + name + " from ShopSphere");
                    product.setPrice(new BigDecimal("1999"));
                    product.setStock(50);
                    product.setCategory(category);
                    product.setRating(4.5);

                    productRepository.save(product);
                }
            }
        };
    }
}