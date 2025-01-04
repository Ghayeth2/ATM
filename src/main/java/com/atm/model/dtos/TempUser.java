package com.atm.model.dtos;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter @Setter
@RedisHash("temp-user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempUser implements Serializable {
    private boolean notConfirmed;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
