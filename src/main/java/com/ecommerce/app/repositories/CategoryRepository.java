package com.ecommerce.app.repositories;

import com.ecommerce.app.modal.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    Category findByCategoryName(String categoryName);
}
