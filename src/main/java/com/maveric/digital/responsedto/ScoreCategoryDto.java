package com.maveric.digital.responsedto;

import java.util.List;

import com.maveric.digital.model.embedded.Options;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreCategoryDto {
	private Long categoryId;
	private String categoryName;
	private List<Options> categoryOptions;
	private String createdBy;
	private Long createdAt;
	private Long updatedAt;
}
