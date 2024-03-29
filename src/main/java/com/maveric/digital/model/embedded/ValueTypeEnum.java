package com.maveric.digital.model.embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ValueTypeEnum {
//	@JsonProperty("number")
	NUMBER("number"),
//	@JsonProperty("decimal")
	DECIMAL("decimal"),
//	@JsonProperty("text")
	PERCENTAGE("percentage"),
	FILE("file"),
	BOTH("both"),
	TEXT("text");

	public final String label;

	ValueTypeEnum(String label) {
		this.label = label;
	}
}
