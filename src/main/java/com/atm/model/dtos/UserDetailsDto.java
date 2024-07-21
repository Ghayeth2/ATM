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
    @Pattern(regexp = "^[a-zA-z]{3,25}$", message = "{valid.first.name.regex}")
    @NotEmpty(message = "{valid.not.empty}")
    private String firstName;
    @Pattern(regexp = "^[a-zA-Z ]{3,25}$",
            message = "{valid.last.name.regex}")

    @NotEmpty(message = "{valid.not.empty}")
    private String lastName;
    private String email;
    private String password;
}
