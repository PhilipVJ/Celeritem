package com.example.celeritem.Exceptions;

public class InvalidServiceUsageException extends Exception {
    public InvalidServiceUsageException(String errorMessage) {
        super(errorMessage);
    }
}
