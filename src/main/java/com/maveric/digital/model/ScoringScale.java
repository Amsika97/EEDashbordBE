package com.maveric.digital.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.maveric.digital.model.embedded.ScoreRange;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "scoringScale")
@Data
@NoArgsConstructor
public class ScoringScale extends IdentifiedEntity {

	private String name;
	private List<ScoreRange> range;
	private Long createdAt;
	private Long updatedAt;
	private String comment;
	private String createdBy;
	private Long createdUserId;
	private String scoreScaleType;
}
