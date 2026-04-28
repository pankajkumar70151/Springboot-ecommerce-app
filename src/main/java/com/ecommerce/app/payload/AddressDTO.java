package com.ecommerce.app.payload;

import com.ecommerce.app.modal.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long addressId;
    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters")
    private String Street;
    private String buildingName;
    private String city;
    @NotBlank
    @Size(min = 2, message = "City name required")
    private String country;
    @NotBlank
    @Size(min = 5, message = "Zip Code required")
    private String zipCode;
}
