package com.maveric.digital.model.embedded;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Reviewer {
	private String reviewerId;
	private Long reviewerAt;
	private String comment;
	private String reviewerName;

}
