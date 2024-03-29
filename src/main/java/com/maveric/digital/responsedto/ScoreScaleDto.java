package com.maveric.digital.responsedto;

import java.util.List;

import com.maveric.digital.model.embedded.ScoreRange;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreScaleDto {
	private Long id;
	@NotBlank(message = "name should not blank")
	private String name;
	@NotBlank(message=" Score Type should not blank")
	private String scoreScaleType;
	@NotNull(message=" CreatedUserId should not blank")
	@Min(1)
	private Long createdUserId;
	@NotNull(message = "range should not blank")
	private List<ScoreRange> range;
	
	

}
