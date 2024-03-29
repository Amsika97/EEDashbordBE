package com.maveric.digital.model;

import com.maveric.digital.responsedto.CategoryWiseScore;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.MetricSubmitProjectCategory;
import com.maveric.digital.model.embedded.Reviewer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "metric_submitted")
@Data
@NoArgsConstructor
public class MetricSubmitted extends IdentifiedEntity {
	private Long submittedAt;
	private String submittedBy;// submitter by, submitter id
	private String submitterName;
	@DBRef
	private Project project;
	@DBRef
	private MetricTemplate template;
	private List<Reviewer> reviewers;
	private List<MetricSubmitProjectCategory> projectCategory;
	private AssessmentStatus submitStatus;
	private Long createdAt;
	private Long updatedAt;
	private String description;
	private List<CategoryWiseScore> categorywiseScores;
	private double score;
	@DBRef
	private Account account;
	@DBRef
	private ProjectType projectType;

	private Long frequencyReminderDate;
	private List<Long> frequencyOverDueRemindersDate;
	private List<Long> frequencyRemindersSent;
	private Boolean isFrequencyRequired;
	private Boolean isEdited;

}
