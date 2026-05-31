package com.learnerplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalLearners;
    private long activeLearners;
    private long totalDrives;
    private long upcomingDrives;
    private long totalApplications;
    private long placedCount;
    private double averageGpa;
    private Map<String, Long> courseDistribution;
    private Map<String, Long> placementTrend;
	public long getTotalLearners() {
		return totalLearners;
	}
	public void setTotalLearners(long totalLearners) {
		this.totalLearners = totalLearners;
	}
	public long getActiveLearners() {
		return activeLearners;
	}
	public void setActiveLearners(long activeLearners) {
		this.activeLearners = activeLearners;
	}
	public long getTotalDrives() {
		return totalDrives;
	}
	public void setTotalDrives(long totalDrives) {
		this.totalDrives = totalDrives;
	}
	public long getUpcomingDrives() {
		return upcomingDrives;
	}
	public void setUpcomingDrives(long upcomingDrives) {
		this.upcomingDrives = upcomingDrives;
	}
	public long getTotalApplications() {
		return totalApplications;
	}
	public void setTotalApplications(long totalApplications) {
		this.totalApplications = totalApplications;
	}
	public long getPlacedCount() {
		return placedCount;
	}
	public void setPlacedCount(long placedCount) {
		this.placedCount = placedCount;
	}
	public double getAverageGpa() {
		return averageGpa;
	}
	public void setAverageGpa(double averageGpa) {
		this.averageGpa = averageGpa;
	}
	public Map<String, Long> getCourseDistribution() {
		return courseDistribution;
	}
	public void setCourseDistribution(Map<String, Long> courseDistribution) {
		this.courseDistribution = courseDistribution;
	}
	public Map<String, Long> getPlacementTrend() {
		return placementTrend;
	}
	public void setPlacementTrend(Map<String, Long> placementTrend) {
		this.placementTrend = placementTrend;
	}
}
