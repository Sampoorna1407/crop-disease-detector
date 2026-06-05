package com.cropcare.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentSuggestion {
    private String name;
    private String type;
    private String description;
    private String applicationMethod;
    private String frequency;
}
