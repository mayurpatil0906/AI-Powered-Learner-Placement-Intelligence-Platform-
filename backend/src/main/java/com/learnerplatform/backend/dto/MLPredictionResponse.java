package com.learnerplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLPredictionResponse {
    public Long getLearnerId() {
		return learnerId;
	}
	public void setLearnerId(Long learnerId) {
		this.learnerId = learnerId;
	}
	public String getLearnerName() {
		return learnerName;
	}
	public void setLearnerName(String learnerName) {
		this.learnerName = learnerName;
	}
	public Double getProbability() {
		return probability;
	}
	public void setProbability(Double probability) {
		this.probability = probability;
	}
	public Boolean getPlaceable() {
		return placeable;
	}
	public void setPlaceable(Boolean placeable) {
		this.placeable = placeable;
	}
	public List<String> getTopFactors() {
		return topFactors;
	}
	public void setTopFactors(List<String> topFactors) {
		this.topFactors = topFactors;
	}
	private Long learnerId;
    private String learnerName;
    private Double probability;
    private Boolean placeable;
    private List<String> topFactors;
}
