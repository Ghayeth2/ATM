package com.atm.dao;

import com.atm.model.dtos.AccountsUsers;
import com.atm.model.dtos.UsersTransactionsResponse;
import com.atm.model.entities.User;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {

    User findByEmail(String email);

//    @Query("select u from User u where u.email = ?1")
//    User findByEmail(String email);

    @Query("select u from User u where u.firstName like %?1")
    Optional<List<User>> findByFirstnameEndsWith(String firstname);


    /*
    The below function will be used for filtering results in Admin view page
    the following will be listed and done:
    search request API will come
    searching by ( <firstName, lastName, email> like not exact, account number contains ,
    type, amount) Sort >> (Default sorting is  in acsend order)
     */
    // it should be dynamic query not like the below one
//    repo.findByAndSort("lannister", Sort.by("firstname"));
//repo.findByAndSort("stark", Sort.by("LENGTH(firstname)"));
    @Query("select u from User u where u.firstName like %?1")
    Optional<List<User>> findByAndSort(String firstName, Sort sort);

    boolean existsByEmail(String email);

    Optional<User> findBySlug(String slug);

    @Query("UPDATE User u SET u.failedAttempts = ?1 WHERE u.email = ?2")
    @Modifying
    void updateFailedAttempts(int failAttempts, String email);

    @Query("from User u join Account a")
    Optional<Object[]> findUsersAndAccounts();

    @Query("from User u join Account a")
    Optional<Object[]> findAllUsersAndAccounts();


    // careful of the parameters. control for sql injections in there.
    @Query("select u from User u where u.email = ?1 and u.slug = ?2")
    Optional<User> findByEmailAndSlug(String email, String slug);

    // JPQL Query for Spring
    // We select our class's constructor (all args) and continue with the query
    @Query("select new com.atm.model.dtos.AccountsUsers(u.firstName, u.lastName, a.number) " +
            "from User u join u.accounts a where u.id = ?1")
    Optional<AccountsUsers> getAccountsUsers(Long id);

}
