package com.atm.core.exceptions;

public class InsufficientFundsExceptionWithdraw extends Exception{
    public InsufficientFundsExceptionWithdraw(String message) {
        super(message);
    }
}
