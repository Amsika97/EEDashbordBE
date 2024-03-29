package com.maveric.digital.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maveric.digital.responsedto.UserProfileDto;
import com.maveric.digital.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1")
@Slf4j
@RequiredArgsConstructor
public class UserProfileController {
	
	private final UserProfileService userProfileService;
	 private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

	 @Operation(
	            summary = "Get Assessment and matric list by userId"
	    )
	    @ApiResponses(value = {@ApiResponse(
	            responseCode = "200",
	            content = @Content(schema = @Schema(implementation = UserProfileDto.class))
	    )})
	@GetMapping("/user/profile/details/{userid}/{type}")
    public ResponseEntity<UserProfileDto> getAssessmentsAndMatricBySubmitedBy(@PathVariable String userid,@PathVariable String type) {
		 logger.debug("UserProfileController::getAssessmentsAndMatricBySubmitedBy:: started");
		 logger.debug("userId {}",userid);
	UserProfileDto userProfileDto= userProfileService.getAssessmentsAndMatricBySubmitedBy(userid,type);
	logger.debug("UserProfileController::getAssessmentsAndMatricBySubmitedBy:: end");
	return ResponseEntity.ok(userProfileDto);
	}
}
	
