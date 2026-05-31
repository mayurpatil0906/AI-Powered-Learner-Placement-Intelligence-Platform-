package com.learnerplatform.backend.service;

import com.learnerplatform.backend.dto.MLPredictionResponse;
import com.learnerplatform.backend.exception.ResourceNotFoundException;
import com.learnerplatform.backend.model.Learner;
import com.learnerplatform.backend.model.MLPrediction;
import com.learnerplatform.backend.repository.LearnerRepository;
import com.learnerplatform.backend.repository.MLPredictionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MLIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(MLIntegrationService.class);

    @Value("${app.ml-service.url}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;
    private final LearnerRepository learnerRepository;
    private final MLPredictionRepository predictionRepository;

    public MLIntegrationService(RestTemplate restTemplate, LearnerRepository learnerRepository,
                                 MLPredictionRepository predictionRepository) {
        this.restTemplate = restTemplate;
        this.learnerRepository = learnerRepository;
        this.predictionRepository = predictionRepository;
    }

    @SuppressWarnings("unchecked")
    public MLPredictionResponse predictPlacement(Long learnerId) {
        log.info("Requesting ML prediction for learner: {}", learnerId);

        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found: " + learnerId));

        // Build request payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("learner_id", learnerId);
        requestBody.put("gpa", learner.getGpa() != null ? learner.getGpa() : 0.0);
        requestBody.put("skills", learner.getSkills() != null ? learner.getSkills() : "");
        requestBody.put("experience_months", learner.getExperienceMonths() != null ? learner.getExperienceMonths() : 0);
        requestBody.put("course", learner.getCourse() != null ? learner.getCourse() : "");
        requestBody.put("semester", learner.getSemester() != null ? learner.getSemester() : 1);

        try {
            String url = mlServiceUrl + "/predict/" + learnerId;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);
            Map<String, Object> result = response.getBody();

            if (result != null) {
                Double probability = ((Number) result.getOrDefault("probability", 0.0)).doubleValue();
                Boolean placeable = (Boolean) result.getOrDefault("placeable", false);
                List<String> topFactors = (List<String>) result.getOrDefault("top_factors", List.of());

                // Save prediction
                MLPrediction prediction = new MLPrediction();

                prediction.setLearner(learner);
                prediction.setPlacementProbability(probability);
                prediction.setPlaceable(placeable);
                prediction.setTopFactors(String.join(",", topFactors));
                prediction.setFeaturesUsed(requestBody.toString());
                predictionRepository.save(prediction);

                log.info("ML prediction saved for learner {}: probability={}", learnerId, probability);

                MLPredictionResponse response2 = new MLPredictionResponse();

                response2.setLearnerId(learnerId);
                response2.setLearnerName(learner.getName());
                response2.setProbability(probability);
                response2.setPlaceable(placeable);
                response2.setTopFactors(topFactors);

                return response2;
            }
        } catch (Exception e) {
            log.error("ML service call failed for learner {}: {}", learnerId, e.getMessage());

            // Fallback: simple heuristic prediction
            return fallbackPrediction(learner);
        }

        return fallbackPrediction(learner);
    }

    public List<MLPrediction> getPredictionHistory(Long learnerId) {
        return predictionRepository.findByLearnerIdOrderByPredictedAtDesc(learnerId);
    }

    private MLPredictionResponse fallbackPrediction(Learner learner) {
        log.warn("Using fallback prediction for learner: {}", learner.getId());

        double gpa = learner.getGpa() != null ? learner.getGpa() : 0.0;
        int experience = learner.getExperienceMonths() != null ? learner.getExperienceMonths() : 0;
        int skillCount = learner.getSkills() != null ? learner.getSkills().split(",").length : 0;

        double probability = Math.min(1.0, (gpa / 10.0) * 0.4 + (experience / 24.0) * 0.3 + (skillCount / 10.0) * 0.3);
        probability = Math.round(probability * 100.0) / 100.0;

        MLPredictionResponse response = new MLPredictionResponse();

        response.setLearnerId(learner.getId());
        response.setLearnerName(learner.getName());
        response.setProbability(probability);
        response.setPlaceable(probability >= 0.5);

        response.setTopFactors(
                Arrays.asList(
                        "GPA: " + gpa,
                        "Experience: " + experience + " months",
                        "Skills: " + skillCount
                )
        );

        return response;
    }
}
