package com.maveric.digital.service;

import java.util.List;
import com.maveric.digital.responsedto.UserProfileDto;

public interface UserProfileService {

	
	UserProfileDto getAssessmentsAndMatricBySubmitedBy(String submitedBy,String type);
}
