package com.maveric.digital.responsedto;

import java.util.List;

import com.maveric.digital.model.Project;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Reviewer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssessmentDto {
	private Long assessmentId;
	@NotNull(message = "templateId shoud be not empty")
	private Long templateId;
	@NotBlank(message = "Template Name Should not be null")
	private String templateName;
	private String submitterName;
	private Long submitedAt;
	private String projectType;
	@NotNull(message = "templateUploadedUserId shoud be not empty")
	private String templateUploadedUserId;
	@NotNull(message = "submitedBy shoud be not empty")
	private String submitedBy;
	private Long projectId;
	private String templateUploadedUserName;
	private ScoreScaleDto scoreScale;
	private List<Reviewer> reviewers;
	private List<AssessmentProjectCategoryDto> projectCategory;
	private AssessmentStatus submitStatus;
	private List<ScoreCategoryDto> scoreCategories;
	private List<CategoryWiseScore> categorywiseScores;
	private double score;
	private String assessmentDescription;
	private Long accountId;
	private Long projectTypeId;
	private Project project;
	private String accountName;
	private String businessUnitName;
	private String projectName;
	private Boolean isEdited;

}
