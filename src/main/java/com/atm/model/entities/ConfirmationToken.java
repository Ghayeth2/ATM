package com.atm.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
@RedisHash("confirmation-token")
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ConfirmationToken implements Serializable {
    private String token;
    private LocalDateTime confirmedAt;
    private LocalDateTime expiresAt;
    private String email;
}
