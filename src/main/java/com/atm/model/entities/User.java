package com.atm.model.entities;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data @Entity @Table(name = "users")
@NamedEntityGraph @Builder
@Log4j2 @NoArgsConstructor
@AllArgsConstructor
public class User extends IntermidateBaseEntity {
    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;
    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private boolean enabled;
    @Column(name = "account_non_locked" )
    private int accountNonLocked;
    @Column(name = "failed_attempts")
    private int failedAttempts;
    @Column(name = "lock_time")
    private Date lockTime;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(
            name = "user_id", referencedColumnName = "id"
    ), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ConfirmationToken> tokens;

//    public User(String firstName, String lastName, String email, String password,
//                int accountNonLocked, int failedAttempts, Date lockTime, List<Role> roles) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.password = password;
//        this.accountNonLocked = accountNonLocked;
//        this.failedAttempts = failedAttempts;
//        this.lockTime = lockTime;
//        this.roles = roles;
//    }
}
