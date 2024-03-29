package com.maveric.digital.model.embedded;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetricTemplateQuestionnaire {
	private Integer questionId;
	private String question;
	private String fieldType;
	private ValueTypeEnum valueType;
	private String questionDescription;
	private List<AnswerData> answerData;
    private Integer min;
    private Integer max;
	private String questionSubText;

}