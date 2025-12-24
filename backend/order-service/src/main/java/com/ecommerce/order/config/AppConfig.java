package com.ecommerce.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for application beans.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Configuration
public class AppConfig {

    @Value("${product.service.url}")
    private String productServiceUrl;

    /**
     * Create a RestTemplate bean for making HTTP requests.
     *
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
