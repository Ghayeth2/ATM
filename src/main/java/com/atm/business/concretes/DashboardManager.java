package com.atm.business.concretes;

import com.atm.business.abstracts.DashboardServices;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.core.utils.converter.DateFormatConverter;
import com.atm.dao.jpqls.DashboardDao;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.payloads.records.requests.AccountFilters;
import com.atm.model.dtos.payloads.records.requests.TransactionFilters;
import com.atm.model.dtos.payloads.records.requests.UserFilters;
import com.atm.model.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service @RequiredArgsConstructor
public class DashboardManager implements DashboardServices {
    private final DashboardDao dashboardDao;
    private final UserAccountServices userService;
    @Override
    public long users(UserFilters request) {
        LocalDateTime from;
        LocalDateTime to;
        if (request.from() != null && !request.from().isEmpty()) {
            from = customDates(request.from(), request.to())[0];
            to = customDates(request.to(), request.from())[1];
        } else {
            from = defaultDates()[0];
            to = defaultDates()[1];
        }
        return dashboardDao.countUsers(request.firstName(),
                request.lastName(), from, to);
    }

    @Override
    public long accounts(AccountFilters request, CustomUserDetailsDto auth) {
        AtomicReference<String> role = new AtomicReference<>("");
        User user = new User();
        auth.getAuthorities().forEach(au -> {
            System.out.println(au.getAuthority());
            role.set(au.getAuthority());
        });
        String roleString = "";
        if (!role.get().isEmpty()) {
            roleString = role.get();
        }
        log.info("Role after role is extracted: "+roleString);
        log.info("User email: "+ auth.getUser().getEmail());
        if (roleString.equalsIgnoreCase("ROLE_USER")) {
            log.info("Logged in user's role: "+roleString);
            user = userService.findByEmail(auth.getUser().getEmail());
            log.info("Logged in user's id: "+user.getId());
        } else if (request.email() != null && !request.email().isEmpty()) {
            log.info("User's email from Admin request: "+request.email());
            user = userService.findByEmail(request.email());
            log.info("User's id from Admin request: "+user.getId());
        } else {
            user.setId(0L);
            log.info("Logged in user is Admin, no Email request, id: "+user.getId());
        }
        LocalDateTime from;
        LocalDateTime to;

        if (request.from() != null && !request.from().isEmpty()) {
            from = customDates(request.from(), request.to())[0];
            to = customDates(request.to(), request.from())[1];
        } else {
            from = defaultDates()[0];
            to = defaultDates()[1];
        }

        return dashboardDao.countAccounts(user.getId(),
                request.type(), from, to);
    }

    @Override
    public long transactions(TransactionFilters request, CustomUserDetailsDto auth) {

        AtomicReference<String> role = new AtomicReference<>("");
        User user = new User();
        auth.getAuthorities().forEach(au -> {
            System.out.println(au.getAuthority());
            role.set(au.getAuthority());
        });
        String roleString = "";
        if (!role.get().isEmpty()) {
            roleString = role.get();
        }
        if (roleString.equalsIgnoreCase("ROLE_USER")) {
            user = userService.findByEmail(auth.getUser().getEmail());
        } else if (request.email() != null && !request.email().isEmpty()) {
            user = userService.findByEmail(request.email());
        } else {
            user.setId(0L);
        }

        LocalDateTime from;
        LocalDateTime to;
        if (request.from() != null && !request.from().isEmpty()) {
            from = customDates(request.from(), request.to())[0];
            to = customDates(request.to(), request.from())[1];
        } else {
            from = defaultDates()[0];
            to = defaultDates()[1];
        }
        return dashboardDao.countTransactions(user.getId(),
                request.accountType(), request.accountNumber(),
                request.transactionType(), from, to);
    }

    private LocalDateTime[] customDates(String from, String to) {
        LocalDateTime fromDate = new DateFormatConverter().formatRequestDate(
                from
        );
        LocalDateTime toDate = new DateFormatConverter().formatRequestDate(
                to
        );
        return new LocalDateTime[]{fromDate, toDate};
    }

    private LocalDateTime[] defaultDates() {
        LocalDateTime from = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now().minusMonths(1)
        );
        LocalDateTime to = new DateFormatConverter().formatRequestDate(
                LocalDateTime.now()
        );
        return new LocalDateTime[]{from, to};
    }
}
