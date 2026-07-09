package com.sathwikhbhat.soilanalytics.exception;

public class InvalidTrendTypeException extends RuntimeException {
    public InvalidTrendTypeException(String type) {
        super("Invalid trend type: " + type + ". Valid values are MONTHLY, SEASONAL, YEARLY.");
    }
}
