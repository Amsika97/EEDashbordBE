package com.maveric.digital.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.responsedto.ScoreCategoryDto;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.ScoreCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1")
public class ScoreCategoryController {
	Logger logger = LoggerFactory.getLogger(ScoreCategoryController.class);
	@Autowired
	private ConversationService conversationService;
	@Autowired
	private ScoreCategoryService scoreCategoryService;

	@Operation(summary = "Thi API used to fetch all score category")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Feched all score categories") })
	@GetMapping(value = "/score/category")
	ResponseEntity<List<ScoreCategoryDto>> getAllScoreCategories() throws JsonProcessingException {
		return ResponseEntity.ok()
				.body(conversationService.toScoreCategoryDtos(scoreCategoryService.getAllScoreCategories()));
	}
	
	@Operation(summary = "Thi API used to score category by Id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Feched Score category by scoreCategoryId") })
	@GetMapping(value = "/score/category/{scoreCategoryId}")
	ResponseEntity<ScoreCategoryDto> getScoreCategoryById(@PathVariable("scoreCategoryId") Long scoreCategoryId)
			throws JsonProcessingException {
		return ResponseEntity.ok().body(
				conversationService.toScoreCategoryDto(scoreCategoryService.getScoreCategoryById(scoreCategoryId)));
	}
	@Operation(summary = "Thi API used to add new score category")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Add new scorecategory") })
	@PostMapping(value = "/score/category/save")
	ResponseEntity<ScoreCategoryDto> addNewScoreCategory (@RequestBody ScoreCategoryDto request){
		logger.debug("ScoreCategoryController::addNewScoreCategory()::{} started", request);
		return ResponseEntity.ok().body(conversationService.toScoreCategoryDto(scoreCategoryService.addScoreCategory(request)));
	}

}
