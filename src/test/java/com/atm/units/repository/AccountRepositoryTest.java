package com.atm.units.repository;

import com.atm.core.config.TestAuditingConfig;
import com.atm.dao.daos.AccountDao;
import com.atm.model.entities.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountDao accountDao;

    @Test
    void shouldSaveAccount_WhenAccountIsProvided() {
        Account account = Account.builder()
                .type("savings").number("1000-1123-1212-2222")
                .currency("$").balance(300).build();
        account.setSlug("slug");
        accountDao.save(account);
        Assertions.assertThat(account.getId()).isGreaterThan(0);
    }

    @Test
    void shouldReturnAccount_WhenSlugIsProvided() {
        Account account = Account.builder()
                .number("1212-1212-1212-2222").currency("$")
                .balance(300).type("personal").build();
        account.setSlug("slug");
        accountDao.save(account);
        Account foundAccount = accountDao.findBySlug("slug").get();
        Assertions.assertThat(foundAccount).isSameAs(account);
    }

    @Test
    void shouldReturnAccount_WhenNumberIsProvided() {
        Account account = Account.builder()
                .number("1212-1212-1212-1212").currency("$")
                .balance(300).type("business").build();
        account.setSlug("slug");
        accountDao.save(account);
        Assertions.assertThat(
                accountDao.findByNumber(account.getNumber())
        ).isPresent();
    }

    @Test
    void shouldDeleteAccount_WhenAccountIdIsProvided() {
        Account account = Account.builder()
                .balance(400).currency("$").type("Savings Account")
                .number("1212-2222-3232-2233").build();
        account.setSlug("slug");
        Account savedAccount = accountDao.save(account);
        accountDao.deleteById(savedAccount.getId());
        Assertions.assertThat(
                accountDao.findById(savedAccount.getId())
        ).isEmpty(); // The reason of using isEmpty is that we r calling findById(id:long):Optional<>
                     // so instead of asserting Null value, it will be an Empty value coming from Optional<>
    }
}
