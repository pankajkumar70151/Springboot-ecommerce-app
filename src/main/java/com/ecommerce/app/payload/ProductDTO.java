package com.ecommerce.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private double productPrice;
    private double productDiscount;
    private double productDiscountedPrice;
    private int productStock;
}
