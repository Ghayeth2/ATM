package com.atm.business.strategies.abstracts;

import com.atm.model.dtos.TransactionContext;
import org.springframework.beans.factory.annotation.Value;

public interface TransactionsStrategy {
    double[] execute(TransactionContext context);
}
