package com.atm.dao;

import com.atm.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findBySlug(String slug);

    @Query("UPDATE User u SET u.failedAttempts = ?1 WHERE u.email = ?2")
    @Modifying
    void updateFailedAttempts(int failAttempts, String email);
}
