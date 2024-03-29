package com.maveric.digital.responsedto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentProjectCategoryDto {
	private String categoryName;
	private List<QuestionAnswerDto> templateQuestionnaire;
	private String categoryDescription;

	
}
