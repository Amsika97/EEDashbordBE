package com.maveric.digital.responsedto;

import java.util.List;

import com.maveric.digital.model.embedded.ProjectCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateDto {

	private Long templateId;
	private String templateName;
	private String projectType;
	private String templateUploadedUserId;
	private String templateUploadedUserName;
	private ScoreScaleDto scoreScale;
	private List<ProjectCategory> projectCategory;
	private List<ScoreCategoryDto> scoreCategories;
	private String assessmentDescription;
	private String templateDisplayName;
	private TemplateFrequencyReminder templateFrequency;

}
