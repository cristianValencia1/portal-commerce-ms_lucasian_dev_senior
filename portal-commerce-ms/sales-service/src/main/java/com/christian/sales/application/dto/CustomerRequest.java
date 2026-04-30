package com.christian.sales.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    @NotBlank(message = "fullName is required")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "phone must be 10 digits")
    private String phone;

    @NotBlank(message = "postalCode is required")
    @Pattern(regexp = "^\\d{5,6}$", message = "postalCode must be 5 or 6 digits")
    private String postalCode;
}
