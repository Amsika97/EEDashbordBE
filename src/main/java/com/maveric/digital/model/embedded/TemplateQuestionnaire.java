package com.maveric.digital.model.embedded;

import com.maveric.digital.responsedto.TemplateQuestionarieExtraInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemplateQuestionnaire {
	private Integer questionId;
	private String question;
	private String fieldType;
	private String scoreCategory;
	private String questionDescription;
	private String questionSubText;
	private TemplateQuestionarieExtraInfo extraInfo;

	

}
