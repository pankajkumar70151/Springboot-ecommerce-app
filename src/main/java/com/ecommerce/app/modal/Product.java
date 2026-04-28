package com.ecommerce.app.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @NotBlank
    @Size(min = 3, message = "Product name must contains at least  3 character")
    private String productName;
    @NotBlank
    @Size(min = 5, message = "Product description must contains at least 5 character")
    private String productDescription;
    private String productImage;
    private double productPrice;
    private double productDiscount;
    private double productDiscountedPrice;
    private int productStock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<CartItem> products = new ArrayList<>();

}
