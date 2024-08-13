package com.borschevski.georegistry.service;

import org.springframework.stereotype.Component;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public interface URLFactory {
    URL createURL(String spec) throws MalformedURLException;
}
