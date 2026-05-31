package com.learnerplatform.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getAssessedBy() {
		return assessedBy;
	}

	public void setAssessedBy(String assessedBy) {
		this.assessedBy = assessedBy;
	}

	public LocalDateTime getAssessedAt() {
		return assessedAt;
	}

	public void setAssessedAt(LocalDateTime assessedAt) {
		this.assessedAt = assessedAt;
	}

	@Column(nullable = false)
    private String type; // APTITUDE, CODING, COMMUNICATION, ATTENDANCE, MOCK_INTERVIEW

    @Column(nullable = false)
    private Double score;

    @Builder.Default
    private Double maxScore = 100.0;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private String assessedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime assessedAt;
}
