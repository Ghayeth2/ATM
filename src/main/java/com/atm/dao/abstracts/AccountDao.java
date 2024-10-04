package com.atm.dao.abstracts;

import com.atm.model.dtos.AccountDto;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public interface AccountDao extends JpaRepository<Account, Long> {
    Optional<Account> findByNumber(String number);
    Optional<Account> findBySlug(String slug);
    /*
    Search query: Account number, Account type, Currency, Created date between ? and ?
    Dates: start-date / end-date >> will be another query concatenated to original one (if exists)
    Can set the start/end dates to default values too, such as (Within a month ago)


    Sort & Page are sat in service layer and sent to DAO so no problem in there.
    can be manipulated in service class depending on which option/s is/are selected

    Page: Default page size from properties and can be edited

    Sort By: number, type, balance. Default sorting By createdAt DESC
    Order of sort: DESC OR ASC will be sat manually

    Sort can be sat to Pageable interface after setting the requirements.
     */
    @Query("select new com.atm.model.dtos.AccountDto(a.slug, a.currency, " +
            "a.createdDate, a.number, a.balance, a.type) from Account a where (a.number like " +
            "CONCAT('%',:searchQuery,'%') or a.type like CONCAT('%', :searchQuery, '%') " +
            "or a.currency like CONCAT('%', :searchQuery,'%') ) " +
            "and a.createdDate between :from and :to and a.user.id = :userId")
    Page<AccountDto> findAllPaginatedAndFiltered(Pageable pageable, @Param("userId") Long userId,
                                                 @Param("searchQuery") String searchQuery,
                                                 @Param("from") Date from,
                                                 @Param("to") Date to);
}
