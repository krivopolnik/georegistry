package com.borschevski.georegistry.config;

import com.borschevski.georegistry.service.URLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@EnableJpaAuditing
public class AppConfig {

    @Bean
    public URLFactory urlFactory() {
        return spec -> {
            try {
                return new URL(spec);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL: " + spec, e);
            }
        };
    }
}