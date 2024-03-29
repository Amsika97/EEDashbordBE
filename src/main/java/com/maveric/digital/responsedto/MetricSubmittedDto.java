package com.maveric.digital.responsedto;

import com.maveric.digital.model.embedded.MetricSubmitProjectCategory;
import java.util.List;

import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Reviewer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetricSubmittedDto {
	private Long metricId;
	private Long metricTemplateId;
	@NotBlank(message = "Template Name Should not be null")
	private String templateName;
	private Long submittedAt;
	private String templateUploadedUserId;
	private String submittedBy;
	private String submitterName;
	private Long projectId;
	private String templateUploadedUserName;
	private List<Reviewer> reviewers;
	private List<MetricSubmitProjectCategory> projectCategory;
	private AssessmentStatus submitStatus;
	private String description;
	private List<CategoryWiseScore> categorywiseScores;
	private double score;
	private Long accountId;
	private Long projectTypeId;
	private String accountName;
	private String businessUnitName;
	private String projectName;
	private String templateDisplayName;
	private Boolean isEdited;

}
