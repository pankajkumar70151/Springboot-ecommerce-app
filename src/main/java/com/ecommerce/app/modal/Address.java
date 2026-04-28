package com.ecommerce.app.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name must be atleast 5 characters")
    private String street;

    private String buildingName;

    @NotBlank
    @Size(min = 2, message = "City name required")
    private String city;

    @NotBlank
    @Size(min = 5, message = "Country name required")
    private String country;

    @NotBlank
    @Size(min = 5, message = "Zip Code required")
    private String zipCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
