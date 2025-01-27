package ru.app.cicd.exception;

public class DeveloperWithDuplicateEmailException extends RuntimeException {

    public DeveloperWithDuplicateEmailException(String message) {
        super(message);
    }
}
