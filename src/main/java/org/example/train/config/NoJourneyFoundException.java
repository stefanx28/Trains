package org.example.train.config;

public class NoJourneyFoundException extends RuntimeException {
    public NoJourneyFoundException(String message) {
        super(message);
    }
}
