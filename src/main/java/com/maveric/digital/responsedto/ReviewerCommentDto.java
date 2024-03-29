package com.maveric.digital.responsedto;

import java.util.List;

import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.ReviewerQuestionComment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewerCommentDto {
	private Long assessmentId;
	private String reviewerId;
	private String reviewerName;
	private String comment;
	private AssessmentStatus status;
	private List<ReviewerQuestionComment> reviewerQuestionComment;
}
