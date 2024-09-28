package com.atm.dao.abstracts;

import com.atm.model.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountDao extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String number);
    Optional<Account> findBySlug(String slug);
}
