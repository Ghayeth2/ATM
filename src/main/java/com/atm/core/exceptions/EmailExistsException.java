package com.atm.core.exceptions;

public class EmailExistsException extends Exception{
    public EmailExistsException(String message) {
        super(message);
    }
}
