package com.maveric.digital.model;

import java.util.List;

import com.maveric.digital.responsedto.CategoryWiseScore;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.maveric.digital.model.embedded.AssessmentProjectCategory;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Reviewer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "assessment")
@Data
@NoArgsConstructor
public class Assessment extends IdentifiedEntity {
	private Long submitedAt;
	private String submitedBy;
	private String  submitterName;// submitter by, submitter id
	private Long projectId;
	@DBRef
	private Template template;
	private List<Reviewer> reviewers;
	private List<AssessmentProjectCategory> projectCategory;
	private AssessmentStatus submitStatus;
	private Long createdAt;
	private Long updatedAt;
	private List<CategoryWiseScore> categoryScores;
	private double score;
	private String assessmentDescription;
	@DBRef
	private Account account;
	@DBRef
	private ProjectType projectType;
	@DBRef
	private Project project;
	private Long frequencyReminderDate;
	private List<Long> frequencyOverDueRemindersDate;
	private List<Long> frequencyRemindersSent;
	private Boolean isFrequencyRequired;
	private Boolean isEdited;

}
