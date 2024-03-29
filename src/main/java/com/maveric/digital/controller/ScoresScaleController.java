package com.maveric.digital.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.embedded.ScoreRange;
import com.maveric.digital.responsedto.ScoreScaleDto;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.ScoreScaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1")
public class ScoresScaleController {
	Logger logger = LoggerFactory.getLogger(ScoresScaleController.class);
	@Autowired
	private ConversationService conversationService;
	@Autowired
	private ScoreScaleService scoreService;

	@Operation(summary = "This Api Used to Fetch All Score scales")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "fetched all Score scale data") })
	@GetMapping(value = "/scoreScales")
	ResponseEntity<List<ScoreScaleDto>> getAllScoresScale() throws JsonProcessingException {
		return ResponseEntity.ok().body(conversationService.toScoreDtos(scoreService.getScoreScale()));
	}

	@Operation(summary = "This Api Used to add Score scales")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Add Score scale data"),@ApiResponse(responseCode = "400", description = "Score scale Data is Invalid") })
	@PostMapping(value="/addScoreScale")
	ResponseEntity<ScoreScaleDto> addScoreScale(@RequestBody @Valid ScoreScaleDto dto){
		
		logger.debug("ScoresScaleController::addScoreScale()::Start");
		List<ScoreRange> ranges = dto.getRange();
		if(CollectionUtils.isEmpty(ranges))
		{
		 logger.error("Score Range is empty or Null");
		 throw new CustomException("Scrore Range is Empty or Null",HttpStatus.BAD_REQUEST); 
		}
		ScoreScaleDto scoreScaleDto=conversationService.toScoreDto(scoreService.addScoreScale(dto));
		logger.debug("ScoresScaleController::addScoreScale()::End");
		 return ResponseEntity.ok().body(scoreScaleDto);
	}
}
