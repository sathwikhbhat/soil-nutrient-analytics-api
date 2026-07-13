package com.sathwikhbhat.soilanalytics.gis.dto;

public record GisMarkerResponse(
        String sampleId,
        double latitude,
        double longitude,
        String state,
        String district,
        String taluk,
        String village,
        String crop) {}
