package com.maveric.digital.responsedto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateSaveRequestDto {
//	@NotEmpty(message = "businessUnits shoud not empty")
	private List<Long> businessUnits;
//	@NotEmpty(message = "projects shoud not empty")
	private List<Long> projects;
//	@NotEmpty(message = "projectTypes shoud not empty")
	private List<Long> projectTypes;
//	@NotNull(message = "scoreScaleId shoud not null")
	private Long scoreScaleId;
	@NotBlank(message = "Template Name should not be null")
	private String templateName;
//	@NotBlank(message = "templateData shoud not null")
	private String templateData;
//	@NotNull(message = "templateUploadedUserId shoud not null")
	private String templateUploadedUserId;
//	@NotEmpty(message = "templateUploadedUserName shoud not empty")
	private String templateUploadedUserName;
	private TemplateFrequencyReminder templateFrequency;



}
