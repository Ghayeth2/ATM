package com.atm.model.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2 @Builder
public class UserDto {
    @Pattern(regexp = "^[a-zA-z]{3,25}$", message = "First name should contain only alphabetic characters" +
            "and within 3 - 25 characters.")
    @NotEmpty(message = "This field is required!")
    private String firstName;
    @Pattern(regexp = "^[a-zA-Z ]{3,25}$",
            message = "Last name should only contain alphabetic letters, spaces, and be between 3 and 25 characters long")

    @NotEmpty(message = "This field is required!")
    private String lastName;
    @Email(message = "Enter email formatted text")
    @NotEmpty(message = "This field is required!")
    private String email;
    @NotEmpty(message = "This field is required!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,50}$",
            message = "Password must be 6-50 characters long, include at least one uppercase letter, and one symbol")
    @Size(min = 6, max = 50)
    private String password;
    @NotEmpty(message = "This field is required!")
    private String password2;
}
