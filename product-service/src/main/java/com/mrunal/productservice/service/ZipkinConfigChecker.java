package com.mrunal.productservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ZipkinConfigChecker implements CommandLineRunner {

    @Value("${spring.zipkin.base-url}")
    private String zipkinBaseUrl;

    @Override
    public void run(String... args) {
        System.out.println("Zipkin Base URL: " + zipkinBaseUrl);
    }
}

