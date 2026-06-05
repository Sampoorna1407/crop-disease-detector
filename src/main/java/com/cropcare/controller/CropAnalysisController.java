package com.cropcare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/crops")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CropAnalysisController {

    private final String PYTHON_API_URL = "http://localhost:5000/predict";

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeCrop(@RequestParam("image") MultipartFile image) {

        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("No image uploaded");
            }

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Prepare body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new MultipartInputStreamFileResource(
                    image.getInputStream(),
                    image.getOriginalFilename()
            ));

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // Send request to Python API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.postForEntity(PYTHON_API_URL, requestEntity, String.class);

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // Helper class for file upload
    static class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() {
            return -1;
        }
    }
}