package com.maveric.digital.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.embedded.SubmissionHistory;
import com.maveric.digital.responsedto.AssessmentDto;
import com.maveric.digital.responsedto.MetricReviewerCommentDto;
import com.maveric.digital.responsedto.MetricSubmittedDto;
import com.maveric.digital.responsedto.ReviewerCommentDto;

import java.util.List;

public interface SubmissionHistoryService {

    List<SubmissionHistory> getSubmissionHistory(String submissionFilterRequest) throws JsonProcessingException;
    AssessmentDto editSubmittedAssessments(ReviewerCommentDto request);

    MetricSubmittedDto editSubmittedMetrics(MetricReviewerCommentDto request);
}
