package com.mrunal.productservice.util;

import com.mrunal.productservice.dto.ProductRequest;
import com.mrunal.productservice.dto.ProductResponse;
import com.mrunal.productservice.model.Product;

public class ProductMapper {

    public static Product toProduct(ProductRequest productRequest){
        return Product
                .builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
    }

    public static ProductResponse toProductReponse(Product product) {
        return ProductResponse
                .builder()
                .id(product.getId())
                .description(product.getDescription())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}
