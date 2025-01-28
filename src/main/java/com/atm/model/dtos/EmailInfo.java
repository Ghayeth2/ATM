package com.atm.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @AllArgsConstructor
@NoArgsConstructor @Getter
public class EmailInfo {
    private String name;
    private String emailAddress;
}
