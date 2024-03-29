package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDto {
	private Integer questionId;
	private Integer answerOptionIndex;
	private String fieldType;
	private String comment;
	private String fileUri;
	private String mimeType;
	private String question;
	private String scoreCategory;
	private String questionDescription;
	private String questionSubText;
	private TemplateQuestionarieExtraInfo extraInfo;
	private String reviewerComment;

}
