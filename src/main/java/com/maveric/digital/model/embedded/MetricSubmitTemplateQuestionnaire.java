package com.maveric.digital.model.embedded;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetricSubmitTemplateQuestionnaire {
	private Integer questionId;
	private String question;
	private String fieldType;
	private ValueTypeEnum valueType;
	private String reviewerWeightage;
	private String questionDescription;
	private String scoreCategory;
	private List<AnswerData> answerData;
	private List answerValue; //generic type
	private Integer min;
    private Integer max;
	private String questionSubText;
	private String fileWithComment;
	private String reviewerComment;
}