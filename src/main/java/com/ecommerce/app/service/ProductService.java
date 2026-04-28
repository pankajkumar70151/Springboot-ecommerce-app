package com.ecommerce.app.service;

import com.ecommerce.app.payload.ProductDTO;
import com.ecommerce.app.payload.ProductReponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {


    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductReponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String orderBy, String keyword, String category);

    ProductReponse getProductByCategory(Long categoryId);

    ProductReponse getProductsByKeyword(String keyword);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
