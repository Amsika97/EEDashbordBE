package com.maveric.digital.model.embedded;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssessmentProjectCategory {
	private String categoryName;
	private List<QuestionAnswer> templateQuestionnaire;
	private String categoryDescription;
	
}
