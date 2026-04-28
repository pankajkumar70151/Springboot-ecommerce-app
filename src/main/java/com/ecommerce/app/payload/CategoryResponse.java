package com.ecommerce.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

     /*

      Q. Why we are using CategoryResponse, If we have CategoryDTO class

      Ans. So, we can see CategoryDTO can hold value for one object at the moment.
            but, In DB maybe there will be multiple of data like multiple of category can be present in DB.
            So, to hold all the categories or get all the categories we are using List that hold the value of
            CategoryDTO type value.
      */

    private List<CategoryDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;

}
