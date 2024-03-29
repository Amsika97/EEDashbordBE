package com.maveric.digital.responsedto;

import java.util.List;

import com.maveric.digital.model.embedded.MetricProjectCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricTemplateDto {

	private Long templateId;
	private String templateName;
	private String projectType;
	private String templateUploadedUserId;
	private String templateUploadedUserName;
	private List<MetricProjectCategory> projectCategory;
	private String description;
	private Boolean isActive;
	private String templateDisplayName;

}
