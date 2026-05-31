package com.learnerplatform.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ml_predictions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MLPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", nullable = false)
    private Learner learner;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Learner getLearner() {
		return learner;
	}

	public void setLearner(Learner learner) {
		this.learner = learner;
	}

	public Double getPlacementProbability() {
		return placementProbability;
	}

	public void setPlacementProbability(Double placementProbability) {
		this.placementProbability = placementProbability;
	}

	public Boolean getPlaceable() {
		return placeable;
	}

	public void setPlaceable(Boolean placeable) {
		this.placeable = placeable;
	}

	public String getTopFactors() {
		return topFactors;
	}

	public void setTopFactors(String topFactors) {
		this.topFactors = topFactors;
	}

	public String getFeaturesUsed() {
		return featuresUsed;
	}

	public void setFeaturesUsed(String featuresUsed) {
		this.featuresUsed = featuresUsed;
	}

	public LocalDateTime getPredictedAt() {
		return predictedAt;
	}

	public void setPredictedAt(LocalDateTime predictedAt) {
		this.predictedAt = predictedAt;
	}

	@Column(nullable = false)
    private Double placementProbability;

    @Column(nullable = false)
    @Builder.Default
    private Boolean placeable = false;

    @Column(columnDefinition = "TEXT")
    private String topFactors;

    @Column(columnDefinition = "TEXT")
    private String featuresUsed;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime predictedAt;
}
