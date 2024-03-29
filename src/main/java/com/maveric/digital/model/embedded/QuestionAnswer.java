package com.maveric.digital.model.embedded;

import com.maveric.digital.responsedto.TemplateQuestionarieExtraInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionAnswer {
	private Integer questionId;
	private Integer answerOptionIndex;
	private String comment;
	private String fileUri;
	private String mimeType;
	private String questionDescription;
	private String questionSubText;
	private TemplateQuestionarieExtraInfo extraInfo;
	private String reviewerComment;
}
