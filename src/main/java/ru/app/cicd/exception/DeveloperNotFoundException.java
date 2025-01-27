package ru.app.cicd.exception;

public class DeveloperNotFoundException extends RuntimeException {

    public DeveloperNotFoundException(String message) {
        super(message);
    }
}
