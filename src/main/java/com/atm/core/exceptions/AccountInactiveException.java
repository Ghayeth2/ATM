package com.atm.core.exceptions;

public class AccountInactiveException extends Exception{
    public AccountInactiveException(String message){
        super(message);
    }
}
