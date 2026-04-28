package com.ecommerce.app.service;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ResourceNotFoundException;
import com.ecommerce.app.modal.Category;
import com.ecommerce.app.payload.CategoryDTO;
import com.ecommerce.app.payload.CategoryResponse;
import com.ecommerce.app.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize, String sortBy, String orderBy) {

        Sort pageSortAndOrderBy = orderBy.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, pageSortAndOrderBy);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty())
            throw new APIException("No categories created till now");

        List<CategoryDTO> categoryDTOList = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOList);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryExist = categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryExist != null) {
            throw new APIException("Category already exists with the name of " + category.getCategoryName());
        }

        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

       /* Category savedCategory = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        */

        Category savedCategory = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(savedCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
     //   Optional<Category> categories = categoryRepository.findById(categoryId);

       /* Category savedCategory = categoryRepository.findById(categoryId).
                                 orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        */


        Category savedCategory = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
