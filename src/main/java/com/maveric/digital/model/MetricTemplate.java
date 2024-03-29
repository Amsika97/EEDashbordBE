package com.maveric.digital.model;

import java.util.List;

import com.maveric.digital.responsedto.TemplateFrequencyReminder;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.maveric.digital.model.embedded.MetricProjectCategory;

import lombok.Data;
import lombok.NoArgsConstructor;


@Document(value = "metric_template")
@Data
@NoArgsConstructor
public class MetricTemplate extends IdentifiedEntity {

	private String templateName;
	private String templateUploadedUserId;
	private Long createdAt;
	private Long updatedAt;
	private String templateUploadedUserName;
	private List<MetricProjectCategory> projectCategory;
	private String description;
	@DBRef
	private ScoringScale score;
	@DBRef
	private List<ScoreCategory> scoreCategories;
	@DBRef
	private List<ProjectType> projectTypes;
	private Integer version;
	private Boolean isActive;
	private String templateDisplayName;
	private TemplateFrequencyReminder templateFrequency;


}
