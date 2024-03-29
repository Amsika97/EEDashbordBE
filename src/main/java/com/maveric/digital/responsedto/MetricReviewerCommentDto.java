package com.maveric.digital.responsedto;

import com.maveric.digital.model.embedded.ReviewerQuestionWeightage;
import java.util.List;

import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.ReviewerQuestionComment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricReviewerCommentDto {
    private Long metricId;
    private String reviewerId;
    private String reviewerName;
    private String comment;
    private AssessmentStatus status;

    private List<ReviewerQuestionComment> reviewerQuestionComment;
    private List<ReviewerQuestionWeightage> reviewerQuestionWeightage;
}
