package com.ecommerce.app.repositories;

import com.ecommerce.app.modal.Category;
import com.ecommerce.app.modal.Product;
import com.ecommerce.app.payload.ProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByCategoryOrderByProductPriceAsc(Category category);

    List<Product> findByProductNameLikeIgnoreCase(String keyword);

}
