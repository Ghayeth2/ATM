package com.atm.dao.daos;

import com.atm.model.dtos.payloads.responses.AccountDto;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountDao extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String number);
    Optional<Account> findBySlug(String slug);
}
