package com.cropcare.service;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageProcessingService {
    
    private static final int ANALYSIS_SIZE = 224;
    
    /**
     * Process uploaded image and extract color features
     */
    public Map<String, Object> analyzeImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        
        // Resize for consistent analysis
        BufferedImage resizedImage = Scalr.resize(originalImage, 
            Scalr.Method.QUALITY, 
            Scalr.Mode.FIT_EXACT, 
            ANALYSIS_SIZE, ANALYSIS_SIZE);
        
        Map<String, Object> features = new HashMap<>();
        
        // Extract color features
        double[] colorProfile = extractColorProfile(resizedImage);
        features.put("avgRed", colorProfile[0]);
        features.put("avgGreen", colorProfile[1]);
        features.put("avgBrown", colorProfile[2]);
        
        // Detect affected areas (simplified)
        double affectedPercentage = detectAffectedAreas(resizedImage);
        features.put("affectedPercentage", affectedPercentage);
        
        // Texture analysis (simplified)
        double textureScore = analyzeTexture(resizedImage);
        features.put("textureScore", textureScore);
        
        return features;
    }
    
    private double[] extractColorProfile(BufferedImage image) {
        long totalRed = 0, totalGreen = 0, totalBrown = 0;
        int pixelCount = 0;
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                
                totalRed += red;
                totalGreen += green;
                
                // Brown detection (high red, medium green, low blue)
                if (red > 100 && green > 50 && green < 150 && blue < 100) {
                    totalBrown++;
                }
                pixelCount++;
            }
        }
        
        return new double[] {
            (double) totalRed / pixelCount / 255.0,
            (double) totalGreen / pixelCount / 255.0,
            (double) totalBrown / pixelCount
        };
    }
    
    private double detectAffectedAreas(BufferedImage image) {
        int affectedPixels = 0;
        int totalPixels = image.getWidth() * image.getHeight();
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                
                // Detect unhealthy colors (yellowing, browning, spots)
                boolean isYellow = red > 180 && green > 150 && blue < 100;
                boolean isBrown = red > 100 && red < 180 && green < 100 && blue < 80;
                boolean isDarkSpot = red < 60 && green < 60 && blue < 60;
                
                if (isYellow || isBrown || isDarkSpot) {
                    affectedPixels++;
                }
            }
        }
        
        return (double) affectedPixels / totalPixels * 100;
    }
    
    private double analyzeTexture(BufferedImage image) {
        // Simplified texture analysis using edge detection
        double edgeScore = 0;
        
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                int center = getGrayValue(image.getRGB(x, y));
                int right = getGrayValue(image.getRGB(x + 1, y));
                int bottom = getGrayValue(image.getRGB(x, y + 1));
                
                edgeScore += Math.abs(center - right) + Math.abs(center - bottom);
            }
        }
        
        return edgeScore / (image.getWidth() * image.getHeight());
    }
    
    private int getGrayValue(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return (red + green + blue) / 3;
    }
}

