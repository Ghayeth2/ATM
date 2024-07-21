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
    @Pattern(regexp = "^[a-zA-z]{3,25}$", message = "{valid.first.name.regex}")
    @NotEmpty(message = "{valid.not.empty}")
    private String firstName;
    @Pattern(regexp = "^[a-zA-Z ]{3,25}$",
            message = "{valid.last.name.regex}")

    @NotEmpty(message = "{valid.not.empty}")
    private String lastName;
    @Email(message = "{valid.email}")
    @NotEmpty(message = "{valid.not.empty}")
    private String email;
    @NotEmpty(message = "{valid.not.empty}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,50}$",
            message = "{valid.password.regex}")
    @Size(min = 6, max = 50)
    private String password;
    private String slug;
    @NotEmpty(message = "{valid.not.empty}")
    private String password2;
}
