package com.atm.business.abstracts;

import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.payloads.records.requests.AccountFilters;
import com.atm.model.dtos.payloads.records.requests.TransactionFilters;
import com.atm.model.dtos.payloads.records.requests.UserFilters;
import com.atm.model.entities.User;
import org.springframework.security.core.Authentication;

public interface DashboardServices {
    long users(UserFilters request);
    long accounts(AccountFilters request, CustomUserDetailsDto auth);
    long transactions(TransactionFilters request, CustomUserDetailsDto auth);
}
