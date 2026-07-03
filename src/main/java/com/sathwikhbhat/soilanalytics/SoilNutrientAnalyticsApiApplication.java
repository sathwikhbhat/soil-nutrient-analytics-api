package com.sathwikhbhat.soilanalytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SoilNutrientAnalyticsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoilNutrientAnalyticsApiApplication.class, args);
    }
}
