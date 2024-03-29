package com.maveric.digital.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.User;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.MetricAndAssessmentDetailsDto;
import com.maveric.digital.responsedto.UserProfileDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService{
	
	private final AssessmentRepository assessmentRepository;
	private final MetricSubmittedRepository metricSubmittedRepository;
	private final UserRepository userRepository;
	private final MetricConversationService conversationService;
	private final ConversationService conversationServices;


	@Override
	public UserProfileDto getAssessmentsAndMatricBySubmitedBy(String submitedBy, String type) {
		log.debug("UserProfileServiceImpl::getAssessmentsAndMatricBySubmitedBy()::Start");
		 UserProfileDto userProfileDto=new UserProfileDto();
		 log.debug("my profile details");
		 Optional<User> user = userRepository.findByOid(UUID.fromString(submitedBy));
		 if(user.isPresent()) {
	        	userProfileDto.setUserDto(conversationServices.convertToUserDto(user.get()));
	        }
			
		    List<AssessmentStatus> statuses=List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED);
		    if(type.equals("self")) {
		    	statuses=List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED,AssessmentStatus.SAVE);
		    }
		    log.debug("AssessmentList[]");
	        List<Assessment> assessment= assessmentRepository.findBySubmitStatusInAndSubmitedByOrReviewerId(submitedBy,statuses);
	        if ( !assessment.isEmpty()) {
	        	userProfileDto.setAssessmentlist(conversationServices.toMetricAndAssessmentDetailsDto(assessment));
	        }
	        
	        log.debug("MetricList[]");
	        List<MetricSubmitted> metricSubmitteds = metricSubmittedRepository.findBySubmitStatusInAndSubmitedByOrReviewerId(submitedBy,statuses);
	        if ( !metricSubmitteds.isEmpty()) {
	        	userProfileDto.setMatricsubmitlist(conversationService.toMetricSubmitted(metricSubmitteds));
	        }
	       
			log.debug("UserProfileServiceImpl::getAssessmentsAndMatricBySubmitedBy()::End");
			return userProfileDto;
	}
	

}
