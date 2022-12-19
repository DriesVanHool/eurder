package com.switchfully.eurder.domain.exceptions;

public class KeyCloakCreateException extends RuntimeException{
    public KeyCloakCreateException() {
        super("Error creating user");
    }
}
