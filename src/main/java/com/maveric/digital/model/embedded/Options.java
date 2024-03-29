package com.maveric.digital.model.embedded;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Options {

	private Long optionId;
	private String label;
	private Integer optionIndex;
	private Double optionScore;
	public Long getOptionId() {
		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getOptionIndex() {
		return optionIndex;
	}

	public void setOptionIndex(Integer optionIndex) {
		this.optionIndex = optionIndex;
	}

}
