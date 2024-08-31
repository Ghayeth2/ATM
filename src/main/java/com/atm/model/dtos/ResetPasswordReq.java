package com.atm.model.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder @Data
@NoArgsConstructor
public class ResetPasswordReq {
    @NotEmpty(message = "{valid.not.empty}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,50}$",
            message = "{valid.password.regex}")
    @Size(min = 6, max = 50)
    private String password;
    @NotEmpty(message = "{valid.not.empty}")
    private String password2;
}
