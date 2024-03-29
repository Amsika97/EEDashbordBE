package com.maveric.digital.service;

import java.util.List;
import java.util.Map;

import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.responsedto.MetricAndAssessmentDetailsDto;
import com.maveric.digital.responsedto.AssessmentDto;
import com.maveric.digital.responsedto.ReviewerCommentDto;

public interface AssessmentService {
    List<Assessment> getAssessmentsBySubmitedBy(String submitedBy);

    List<Assessment> getAssessmentsBySubmittedByAndStatus(String submittedBy,String submitStatus);

    Assessment findAssessmentById(Long assessmentId);

    List<Assessment> findAllAssessments();
    List<Assessment> getAllSubmittedAssessments();
    List<Assessment> getAllPendingReviewAssessments(String currentUserId);
    List<MetricAndAssessmentDetailsDto> getAllAssessmentsDetails(String submittedBy);
    Assessment saveOrSubmitAssessment(AssessmentDto requestPayload);
    Assessment saveReviewerComment(ReviewerCommentDto request);
    List<LineChartProjection> getLineChartDataByStartAndEndDates(Long startDate,Long endDate,String filterName,String filterValue);
    List<Assessment> findLastFiveAssessments(String submittedBy);
    List<Assessment>getAssessmentsByStatus(List<AssessmentStatus> assessmentStatus, String submittedBy);
    List<Assessment> getTop10AssessmentsForDashboard();
    Map<String,Map<Integer, Long>> getCountOfUserResponse(Long templateId, Long projectId, Integer questionId);
    List<LineChartProjection> getDateRangeBetween(Long startDate, Long endDate,
			List<LineChartProjection> lineChartDataList);
    void removeFileUri(String fileName, String folderName);
    List<Assessment> assessmentReport();
    List<Assessment> getTop10AssessmentsForDashboardFilters(Filters filterName, String filterValue);
    List<Assessment> getReviewedAssessmentsForReviewer(String reviewerId);
    Assessment inactiveAssessmentById(Long assessmentId);
}
