package com.ecommerce.app.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Size(min = 3, message = "Username must contains at least 3 character")
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must contains at least 6 character")
    private String password;

    @NotBlank
    @Size(min = 5, message = "Email must contains at least 5 character")
    @Email
    private String email;

    private Set<String> role;
}
