package com.maveric.digital.responsedto;


import java.util.List;

import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto {
 
	private UserDto userDto; 
	private  List<MetricAndAssessmentDetailsDto> assessmentlist;
	private List<MetricAndAssessmentDetailsDto> matricsubmitlist;
	
}
