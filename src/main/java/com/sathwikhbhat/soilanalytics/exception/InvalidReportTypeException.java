package com.sathwikhbhat.soilanalytics.exception;

public class InvalidReportTypeException extends RuntimeException {
    public InvalidReportTypeException(String type) {
        super("Invalid report type: " + type
                + ". Valid values are NUTRIENT_STATUS, SOIL_FERTILITY, CROP_WISE, DISTRICT_SUMMARY.");
    }
}
