package com.atm.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class UserDetailsDto {
    @Pattern(regexp = "^[a-zA-z]{3,25}$", message = "First name should contain only alphabetic characters" +
            "and within 3 - 25 characters.")
    @NotEmpty(message = "This field is required!")
    private String firstName;
    @Pattern(regexp = "^[a-zA-Z ]{3,25}$",
            message = "Last name should only contain alphabetic letters, spaces, and be between 3 and 25 characters long")

    @NotEmpty(message = "This field is required!")
    private String lastName;
    private String email;
    private String password;
}
