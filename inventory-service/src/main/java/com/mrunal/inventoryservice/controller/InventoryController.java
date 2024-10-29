package com.mrunal.inventoryservice.controller;

import com.mrunal.inventoryservice.dto.InventoryStockResponse;
import com.mrunal.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/isInStock")
    public List<InventoryStockResponse> isInStock(@RequestParam("sku-code") List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }
}
