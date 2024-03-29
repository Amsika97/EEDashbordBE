package com.maveric.digital.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.maveric.digital.model.embedded.Options;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "scoreCategory")
@Data
@NoArgsConstructor
public class ScoreCategory extends IdentifiedEntity {

	private String categoryName;
	private List<Options> categoryOptions;
	private Long createdAt;
	private Long updatedAt;
	private String createdBy;
}
