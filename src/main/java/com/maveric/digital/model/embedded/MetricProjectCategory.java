package com.maveric.digital.model.embedded;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class MetricProjectCategory {

	private String categoryName;
	private List<MetricTemplateQuestionnaire> templateQuestionnaire;
	private String categoryDescription;

}
