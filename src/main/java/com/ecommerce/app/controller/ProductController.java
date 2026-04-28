package com.ecommerce.app.controller;

import com.ecommerce.app.config.AppConstants;
import com.ecommerce.app.payload.ProductDTO;
import com.ecommerce.app.payload.ProductReponse;
import com.ecommerce.app.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    private ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId)
    {
        ProductDTO savedProductDTO = productService.addProduct(productDTO, categoryId);

        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/getAllProduct")
    private ResponseEntity<ProductReponse> getAllProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false ) String category,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "orderBy", defaultValue = AppConstants.ORDER_BY, required = false) String orderBy
    ) {

        ProductReponse productReponse = productService.getAllProduct(pageNumber, pageSize, sortBy, orderBy, keyword, category);

        return new ResponseEntity<>(productReponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/product")
    private ResponseEntity<ProductReponse> getProductByCategory(@PathVariable Long categoryId) {

        ProductReponse productReponse = productService.getProductByCategory(categoryId);

        return new ResponseEntity<>(productReponse, HttpStatus.OK);
    }

    @GetMapping("/public/product/{keyword}")
    private ResponseEntity<ProductReponse> getProductsByKeyword(@PathVariable String keyword) {

        ProductReponse productReponse = productService.getProductsByKeyword(keyword);

        return new ResponseEntity<>(productReponse, HttpStatus.FOUND);
    }

    @PutMapping("/admin/product/{productId}")
    private ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId) {

        ProductDTO savedproductDTO = productService.updateProduct(productDTO, productId);

        return new ResponseEntity<>(savedproductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/product/{productId}")
    private ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {

        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @PutMapping("admin/product/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {

        ProductDTO productDTO = productService.updateProductImage(productId, image);

        return new ResponseEntity<>(productDTO, HttpStatus.OK);

    }

}
