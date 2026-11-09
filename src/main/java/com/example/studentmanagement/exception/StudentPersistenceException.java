package com.example.studentmanagement.exception;


public class StudentPersistenceException extends RuntimeException {

    public enum Reason {
        DUPLICATE_EMAIL,
        INVALID_AGE,
        DATABASE_ERROR
    }

    private final Reason reason;

    public StudentPersistenceException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
