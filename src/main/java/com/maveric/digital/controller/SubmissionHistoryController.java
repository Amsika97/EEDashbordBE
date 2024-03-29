package com.maveric.digital.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.model.SubmissionFilterDto;
import com.maveric.digital.model.embedded.SubmissionHistory;
import com.maveric.digital.responsedto.*;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.SubmissionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class SubmissionHistoryController {

    private final SubmissionHistoryService submissionHistoryService;

    @Operation(
            summary = "Get SubmissionHistory"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns SubmissionHistory"
    ), @ApiResponse(
            responseCode = "200",
            description = "SubmissionHistory not found"
    )})
    @GetMapping(value = "/submission/history")
    public ResponseEntity<List<SubmissionHistory>> getSubmissionHistory(@RequestParam("submissionFilterRequest") String submissionFilterRequest) throws JsonProcessingException {
        log.debug("SubmissionHistoryController::getSubmissionHistory() call started");
        log.debug("SubmissionFilterRequest details - {}",submissionFilterRequest);

        List<SubmissionHistory> submissionHistories = submissionHistoryService.getSubmissionHistory(submissionFilterRequest);
        log.debug("SubmissionHistoryController::getSubmissionHistory() call ended");
        return new ResponseEntity<>(submissionHistories, HttpStatus.OK);
    }
    @Operation(
            summary = "Get SubmissionHistory"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns Assessment"
    )})
    @PutMapping(path="return/assessment")
    public ResponseEntity<AssessmentDto> editSubmittedAssessments(@RequestBody ReviewerCommentDto request){
        log.debug("SubmissionHistoryController::editSubmittedAssessments()");
        AssessmentDto assessmentDto=submissionHistoryService.editSubmittedAssessments(request);
        return ResponseEntity.ok(assessmentDto);

    }
    @Operation(
            summary = "Get SubmissionHistory"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns Metric"
    )})
    @PutMapping(path="return/metric")
    public ResponseEntity<MetricSubmittedDto> editSubmittedMetrics(@RequestBody MetricReviewerCommentDto request){
        log.debug("SubmissionHistoryController::editSubmittedMetrics()");
        MetricSubmittedDto metricSubmittedDto=submissionHistoryService.editSubmittedMetrics(request);
        return ResponseEntity.ok(metricSubmittedDto);

    }

}
