// AnalysisResult.java
// AnalysisResult.java
package com.cropcare.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AnalysisResult {
    private String status;
    private String diseaseName;
    private double confidence;
    private String severity;
    private String description;
    private List<String> symptoms;
    private List<TreatmentSuggestion> treatments;
    private String imageAnalysis;
}






