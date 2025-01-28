package com.atm.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private EmailInfo fromAddress;
    private EmailInfo toAddress;
    private String subject;
    // Content
    private String body;
}
