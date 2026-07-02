package com.sathwikhbhat.soilanalytics.entity;

import jakarta.validation.constraints.NotBlank;

public record Location(
        @NotBlank String state,
        @NotBlank String district,
        @NotBlank String taluk,
        @NotBlank String village) {}
