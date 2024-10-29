package com.mrunal.productservice.service;

import com.mrunal.productservice.dto.ProductRequest;
import com.mrunal.productservice.dto.ProductResponse;
import com.mrunal.productservice.model.Product;
import com.mrunal.productservice.repository.ProductRepository;
import com.mrunal.productservice.util.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest){
        Product product = ProductMapper.toProduct(productRequest);
        Product savedProduct = productRepository.save(product);
        log.info("product {} is saved", savedProduct.getId());
        return ProductMapper.toProductReponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();
        return products.stream().map(ProductMapper::toProductReponse).toList();
    }
}
