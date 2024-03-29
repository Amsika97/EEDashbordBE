package com.maveric.digital.model.embedded;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetricSubmitProjectCategory {
	private String categoryName;
	private List<MetricSubmitTemplateQuestionnaire> templateQuestionnaire;
	private String categoryDescription;
	 
	
}
