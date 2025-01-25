package com.atm.model.dtos.payloads.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.extern.log4j.Log4j2;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Builder
public class TransactionRequest {
    @Pattern(regexp = "^$|^\\d{4}-\\d{4}-\\d{4}-\\d{4}"
    , message = "Account number's format: XXXX-XXXX-XXXX-XXXX")
    private String senderNumber;
    @NotEmpty(message = "Receiver account's number is required!")
    @Pattern(regexp = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}",
    message = "Account number's format: XXXX-XXXX-XXXX-XXXX")
    private String receiverNumber;
    @NotEmpty(message = "Type is required")
    private String type;
    @NotEmpty(message = "Amount is required!")
    private String amount;
}
