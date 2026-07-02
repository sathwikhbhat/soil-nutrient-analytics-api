package com.sathwikhbhat.soilanalytics.exception;

import java.io.IOException;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, IOException e) {
        super(message, e);
    }
}
