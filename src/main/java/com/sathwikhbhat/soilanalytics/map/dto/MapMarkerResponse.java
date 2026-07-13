package com.sathwikhbhat.soilanalytics.map.dto;

public record MapMarkerResponse(
        String sampleId,
        double latitude,
        double longitude,
        String state,
        String district,
        String taluk,
        String village,
        String crop) {}
