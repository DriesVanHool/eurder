package com.switchfully.eurder.domain.exceptions;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException() {
        super("Unauthorized");
    }
}
