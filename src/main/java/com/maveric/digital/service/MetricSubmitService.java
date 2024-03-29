package com.maveric.digital.service;

import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.responsedto.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MetricSubmitService {
	MetricSubmitted saveMetricReviewerComment(MetricReviewerCommentDto request);
	MetricSubmitted saveOrSubmitMetric(MetricSubmittedDto requestPayload);	
	MetricSubmitted findMetricById(Long metricId);
	List<LineChartProjection> submitMetricLineChartStartAndEndDates(Long startDate, Long endDate, @RequestParam("filterName") String filterName , @RequestParam("filterValue") String filterValue);

	PiechartDashboardDto calculatePercentageForMetricDashboardPieChart();

	List<MetricSubmitted> getAllPendingReviewMetrics(String currentUserId);
	 List<MetricAndAssessmentDetailsDto> getAllMetricDetails(String submittedBy);
	 List<MetricAndAssessmentReportDetails> getMetricReportDetails();

    List<MetricSubmitted> getTop10MetricForDashboard();
	PiechartDashboardDto calculatePercentageForPieChartWithFilters(Filters filterName, String filterValue);
	List<MetricSubmitted> getTop10MetricsForDashboardFilters(Filters filterName, String filterValue);
	List<MetricSubmitted> getReviewedMetricsForReviewer(String reviewerId);
	MetricSubmitted inactiveMetricById(Long metricId);
}


