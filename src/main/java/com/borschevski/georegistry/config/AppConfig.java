package com.borschevski.georegistry.config;

import com.borschevski.georegistry.service.URLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class AppConfig {

    /**
     * Configuring Marshaller to work with XML.
     * This bin will be used to convert XML to and from Java objects.
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.borschevski.georegistry.model");
        return marshaller;
    }

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