package com.maveric.digital.model.embedded;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ProjectCategory {

	private String categoryName;
	private List<TemplateQuestionnaire> templateQuestionnaire;
	private String categoryDescription;

}
