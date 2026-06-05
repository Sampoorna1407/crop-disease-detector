package com.cropcare.service;

import com.cropcare.Repository.DiseaseRepository;
import com.cropcare.model.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiseaseDetectionService {
    
    private final ImageProcessingService ImageProcessingService;
    private final DiseaseRepository DiseaseRepository;
    
    public AnalysisResult analyzeAndPredict(MultipartFile image, String cropType) {
        try {
            // Step 1: Process the image
            Map<String, Object> features = ImageProcessingService.analyzeImage(image);
            
            double avgRed = (double) features.get("avgRed");
            double avgGreen = (double) features.get("avgGreen");
            double avgBrown = (double) features.get("avgBrown");
            double affectedPercentage = (double) features.get("affectedPercentage");
            
            // Step 2: Check if plant appears healthy
            if (affectedPercentage < 5 && avgGreen > 0.4) {
                return AnalysisResult.builder()
                    .status("HEALTHY")
                    .diseaseName("No disease detected")
                    .confidence(calculateHealthConfidence(avgGreen, affectedPercentage))
                    .severity("None")
                    .description("Your crop appears healthy. Continue regular maintenance.")
                    .symptoms(List.of())
                    .treatments(List.of())
                    .imageAnalysis(buildImageAnalysis(features))
                    .build();
            }
            
            // Step 3: Find matching disease
            List<Disease> candidates = DiseaseRepository
                .findByColorProfile(cropType, avgRed, avgGreen, avgBrown);
            
            if (candidates.isEmpty()) {
                return buildUnknownResult(features);
            }
            
            Disease matchedDisease = candidates.get(0);
            double confidence = calculateConfidence(matchedDisease, features);
            
            // Step 4: Build result with treatments
            return AnalysisResult.builder()
                .status("DISEASE_DETECTED")
                .diseaseName(matchedDisease.getName())
                .confidence(confidence)
                .severity(determineSeverity(affectedPercentage))
                .description(matchedDisease.getDescription())
                .symptoms(matchedDisease.getSymptoms())
                .treatments(mapTreatments(matchedDisease.getTreatments()))
                .imageAnalysis(buildImageAnalysis(features))
                .build();
                
        } catch (IOException e) {
            return AnalysisResult.builder()
                .status("ERROR")
                .description("Failed to process image: " + e.getMessage())
                .build();
        }
    }
    
    private double calculateHealthConfidence(double greenValue, double affectedPercentage) {
        return Math.min(0.95, (greenValue * 0.6 + (1 - affectedPercentage / 100) * 0.4));
    }
    
    private double calculateConfidence(Disease disease, Map<String, Object> features) {
        double avgRed = (double) features.get("avgRed");
        double avgGreen = (double) features.get("avgGreen");
        
        double redDiff = Math.abs(disease.getAvgRedValue() - avgRed);
        double greenDiff = Math.abs(disease.getAvgGreenValue() - avgGreen);
        
        double similarity = 1 - (redDiff + greenDiff) / 2;
        return Math.max(0.5, Math.min(0.95, similarity));
    }
    
    private String determineSeverity(double affectedPercentage) {
        if (affectedPercentage < 10) return "Low";
        if (affectedPercentage < 30) return "Moderate";
        if (affectedPercentage < 60) return "High";
        return "Severe";
    }
    
    private List<TreatmentSuggestion> mapTreatments(List<Treatment> treatments) {
        return treatments.stream()
            .map(t -> TreatmentSuggestion.builder()
                .name(t.getName())
                .type(t.getType())
                .description(t.getDescription())
                .applicationMethod(t.getApplicationMethod())
                .frequency(t.getFrequency())
                .build())
            .collect(Collectors.toList());
    }
    
    private String buildImageAnalysis(Map<String, Object> features) {
        return String.format(
            "Color Profile - Red: %.1f%%, Green: %.1f%%, Affected Area: %.1f%%",
            (double) features.get("avgRed") * 100,
            (double) features.get("avgGreen") * 100,
            (double) features.get("affectedPercentage")
        );
    }
    
    private AnalysisResult buildUnknownResult(Map<String, Object> features) {
        return AnalysisResult.builder()
            .status("UNKNOWN")
            .diseaseName("Unidentified Condition")
            .confidence(0.3)
            .severity("Unknown")
            .description("Could not match to known diseases. Consider consulting an expert.")
            .symptoms(List.of())
            .treatments(List.of(
                TreatmentSuggestion.builder()
                    .name("General Care")
                    .type("PREVENTIVE")
                    .description("Ensure proper watering and drainage")
                    .applicationMethod("Regular monitoring")
                    .frequency("Daily")
                    .build()
            ))
            .imageAnalysis(buildImageAnalysis(features))
            .build();
    }
}

