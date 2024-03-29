package com.maveric.digital.controller;

import java.util.List;
import java.util.Map;

import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.responsedto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maveric.digital.service.AssessmentService;
import com.maveric.digital.service.ConversationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import static com.maveric.digital.utils.ServiceConstants.FILTER_NAME_DEFAULT;
import static com.maveric.digital.utils.ServiceConstants.FILTER_VALUE_DEFAULT;

@RestController
@RequestMapping("/v1")
@Slf4j
@RequiredArgsConstructor
public class AssessmentController {
    private static final Logger logger = LoggerFactory.getLogger(AssessmentController.class);

    private final AssessmentService assessmentService;
    private final ConversationService conversationService;


    @Operation(
            summary = "Get Assessment by AssessmentId"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    )})
    @GetMapping("/assessment/{assessmentId}")
    public ResponseEntity<AssessmentDto> getAssessmentById(@PathVariable Long assessmentId) {
        logger.debug("AssessmentController::getAssessmentById::Retrieving assessment with ID {}", assessmentId);
        return ResponseEntity.ok()
                .body(conversationService.toAssessmentDto(assessmentService.findAssessmentById(assessmentId)));

    }

    @Operation(
            summary = "Get All Assessment"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    )})

    @GetMapping("/assessment/all")
    public ResponseEntity<List<AssessmentDto>> getAllAssessments() {
        logger.debug("AssessmentController::getAllAssessments::Retrieving all assessments");

        List<AssessmentDto> assessments = conversationService.toAssessmentDtos(assessmentService.findAllAssessments());
        logger.debug("AssessmentController::getAllAssessments::Found {} assessments::", assessments);
        return ResponseEntity.ok(assessments);

    }

    @Operation(
            summary = "Get All Submitted Assessment"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    )})

    @GetMapping("/assessment/submitted/all")
    public ResponseEntity<List<AssessmentSubmitedByDto>> getAllSubmittedAssessments() {
        logger.debug("AssessmentController::getAllSubmittedAssessments::Retrieving all submitted assessments");

        List<AssessmentSubmitedByDto> assessments = conversationService.toAssessmentSubmitedByDtos(assessmentService.getAllSubmittedAssessments());
        logger.debug("AssessmentController::getAllSubmittedAssessments::Found {} assessments::", assessments);
        return ResponseEntity.ok(assessments);

    }

    @Operation(
            summary = "Get Assessment by submittedBy"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    )})
    @GetMapping("/assessment/submittedBy/{submittedBy}")
    public ResponseEntity<List<AssessmentSubmitedByDto>> getAssessmentsBySubmitedBy(@PathVariable String submittedBy) {
        logger.debug("AssessmentController::getAssessmentsBySubmitedBy::Retrieving assessments by Submitted Id {}",
                submittedBy);

        List<AssessmentSubmitedByDto> assessments = conversationService.toAssessmentSubmitedByDtos(assessmentService.getAssessmentsBySubmitedBy(submittedBy));

        if (!assessments.isEmpty()) {
            logger.info("AssessmentController::getAssessmentsBySubmitedBy::Found {} assessments submitted by {}",
                    assessments.size(), submittedBy);
            return ResponseEntity.ok(assessments);
        } else {
            logger.info("AssessmentController::getAssessmentsBySubmitedBy::No assessments found for Submitted ID {}",
                    submittedBy);
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(
            summary = "Get Assessment by submittedByAndStatus"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    ), @ApiResponse(
            responseCode = "204",
            description = "Assessments not found"
    )})
    @GetMapping("/assessment/submittedBy/{submittedBy}/{submitStatus}")
    public ResponseEntity<List<AssessmentSubmitedByDto>> getAssessmentsBySubmittedByAndStatus(
            @PathVariable String submittedBy,
            @PathVariable String submitStatus) {

        log.debug("AssessmentController::getAssessmentsBySubmittedByAndStatus()::Start ");
        log.debug("Retrieving assessments by Submitted Id {} , submitStatus {}", submittedBy,
                submitStatus);

        List<AssessmentSubmitedByDto> assessments = conversationService.toAssessmentSubmitedByDtos(assessmentService.getAssessmentsBySubmittedByAndStatus(
                submittedBy, submitStatus));
        log.debug("AssessmentController::getAssessmentsBySubmittedByAndStatus()::End");
        return ResponseEntity.ok(assessments);
    }


    @Operation(
            summary = "save or submit Assessment"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    )})
    @PostMapping(path = "/assessment/saveOrSubmit")
    public ResponseEntity<AssessmentDto> saveOrSubmitAssessment(@RequestBody @Valid AssessmentDto requestPayload) {
        logger.debug("AssessmentController::saveOrSubmitAssessment::{}",
                requestPayload);
        return ResponseEntity
                .ok(conversationService.toAssessmentDto(assessmentService.saveOrSubmitAssessment(requestPayload)));
    }

    @Operation(
            summary = "Save Reviewer Comment"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    )})
    @PostMapping(path = "/assessment/reviewerComment/save")
    public ResponseEntity<AssessmentDto> saveReviewerComment(@RequestBody @Valid ReviewerCommentDto requestPayload) {
        logger.debug("AssessmentController::saveReviewerComment()::{}",
                requestPayload);
        return ResponseEntity
                .ok(conversationService.toAssessmentDto(assessmentService.saveReviewerComment(requestPayload)));
    }

    @Operation(summary = "get Line chart data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LineChartDto.class)))})
    @GetMapping(path = "/assessment/lineChart/startDate/{startDate}/endDate/{endDate}")
    public ResponseEntity<List<LineChartDto>> getLineChartDataByStartAndEndDates(@PathVariable Long startDate, @PathVariable Long endDate,@RequestParam(name = "filterName" , required = false,defaultValue = FILTER_NAME_DEFAULT) String filterName ,@RequestParam(name = "filterValue", required = false,defaultValue = FILTER_VALUE_DEFAULT) String filterValue ) {
        logger.debug("AssessmentController::getLineChartDataByStartAndEndDates()::sstartDate::{}::endDate::{}",
                startDate, endDate);
        return ResponseEntity.ok(conversationService
                .toLineChartDtos(assessmentService.getLineChartDataByStartAndEndDates(startDate, endDate,filterName,filterValue)));
    }


    @Operation(summary = "Get last five Assessments by submittedBy")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AssessmentSubmitedByDto.class)))})

    @GetMapping("/assessment/lastfive/{submittedBy}")
    public ResponseEntity<List<AssessmentSubmitedByDto>> getLastFiveAssessments(@PathVariable @Valid String submittedBy) {
        logger.debug("AssessmentController::getLast5Assessments::Retrieving the last 5 assessments");

        List<AssessmentSubmitedByDto> lastFiveAssessments = conversationService.toAssessmentSubmitedByDtos(assessmentService.findLastFiveAssessments(submittedBy));
        return ResponseEntity.ok(lastFiveAssessments);

    }

    @Operation(
            summary = "Get Assessment by StatusListAndSubmittedBy"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentSubmitedByDto.class))
    ), @ApiResponse(
            responseCode = "204",
            description = "Assessments not found"
    )})
    @GetMapping("/assessment/assessmentStatusList")
    public ResponseEntity<List<AssessmentSubmitedByDto>> getAssessmentsByStatus(@RequestParam List<AssessmentStatus> assessmentStatusList,
                                                                                @RequestParam String submittedBy) {
        logger.debug("AssessmentController::getAssessmentsByStatus::Retrieving list Of Assessments By status");
        List<AssessmentSubmitedByDto> assessmentsListByStatus = conversationService.toAssessmentSubmitedByDtos(assessmentService.getAssessmentsByStatus(assessmentStatusList, submittedBy));
        return ResponseEntity.ok(assessmentsListByStatus);

    }

    @Operation(
            summary = "Get Top 10 assessments for Dashboard"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200"
    ), @ApiResponse(
            responseCode = "204",
            description = "Assessments not found"
    )})
    @GetMapping("assessment/Top10Assessmentsfordashboard")
    public ResponseEntity<List<AssessmentsSubmittedDashboardDto>> getTop10AssessmentsForDashboard(){
        logger.debug("AssessmentController::getTop10AssessmentsForDashboard::call started");
        List<AssessmentsSubmittedDashboardDto> assessmentsSubmittedDashboardDtos = conversationService.toAssessmentsSubmittedDashboardDtos(assessmentService.getTop10AssessmentsForDashboard());
        return ResponseEntity.ok(assessmentsSubmittedDashboardDtos);
    }
    @Operation(summary = "Get all pending review assessments")
    @ApiResponses(value = {@ApiResponse(
        responseCode = "200"
    ), @ApiResponse(
        responseCode = "200",
        description = "Assessments not found"
    )})
    @GetMapping("/assessment/pendingReview/all")
    public ResponseEntity<List<AssessmentReviewDto>> getAllPendingReviewAssessments(@RequestParam String userId) {
        logger.debug("AssessmentController::getAllPendingReviewAssessments:: call started");
        List<AssessmentReviewDto> assessments = conversationService.toAssessmentReviewDtos(assessmentService.getAllPendingReviewAssessments(userId));
        logger.debug("AssessmentController::getAllPendingReviewAssessments:: call ended");
        return ResponseEntity.ok(assessments);
    }

    @Operation(summary = "Get all Assessment Details")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Return All Assessment Details"
    ), @ApiResponse(
            responseCode = "200",
            description = "Assessments not found"
    )})
    @GetMapping("/assessment/details/submittedBy/{submittedBy}")
    public ResponseEntity<List<MetricAndAssessmentDetailsDto>> getAllAssessmentsDetails(@PathVariable("submittedBy") String submittedBy) {
        logger.debug("AssessmentController::getAllAssessmentsDetails:: call started");
        logger.debug("submittedBy id : {} ",submittedBy);
        List<MetricAndAssessmentDetailsDto> assessments = assessmentService.getAllAssessmentsDetails(submittedBy);
        logger.debug("AssessmentController::getAllAssessmentsDetails:: call ended");
        return ResponseEntity.ok(assessments);
    }
    @Operation(summary = "Get userResponse count")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Return the user response count for the question,if questionId is null,then return user response count for all the questions in the template "
    ), @ApiResponse(
            responseCode = "200",
            description = "Assessments not found"
    )})
    @GetMapping("/count/{templateId}/{projectId}")
    public Map<String, Map<Integer, Long>> getCountOfUserResponse(
            @PathVariable("templateId") Long templateId,
            @PathVariable("projectId") Long projectId,
            @RequestParam(name = "questionId", required = false) Integer questionId) {
        return assessmentService.getCountOfUserResponse(templateId, projectId, questionId);
    }
    
    @Operation(summary = "Get all Assessment Report Details")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Return All Assessment Report Details",
            content = {@Content( mediaType = "application/json",
            
            	array = @ArraySchema(schema = @Schema(implementation = MetricAndAssessmentReportDetails.class))
            		)}
    )})
	@GetMapping("/assessment/report/details")
	public ResponseEntity<List<MetricAndAssessmentReportDetails>> getAssessmentReportDetails() {
		return ResponseEntity.ok(conversationService.toReportDetailsDtos(assessmentService.assessmentReport()));
	}
    @Operation(
            summary = "Get Top 10 assessments for Dashboard with filters"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200"
    ), @ApiResponse(
            responseCode = "204",
            description = "Assessments not found"
    )})
    @GetMapping("assessment/Top10Assessmentsfordashboard/Filters")
    public ResponseEntity<List<AssessmentsSubmittedDashboardDto>> getTop10AssessmentsForDashboardFilters( @RequestParam(name = "filterName", required = false) Filters filterName,
                                                                                                          @RequestParam(name = "filterValue", required = false) String filterValue) {
        logger.debug("AssessmentController::getTop10AssessmentsForDashboardFilters::call started");
        List<AssessmentsSubmittedDashboardDto> assessmentsSubmittedDashboardDtos = conversationService.toAssessmentsSubmittedDashboardDtos(assessmentService.getTop10AssessmentsForDashboardFilters(filterName,filterValue));
        return ResponseEntity.ok(assessmentsSubmittedDashboardDtos);
    }
    @Operation(
        summary = "Get Assessment by reviewerId"
    )
    @ApiResponses(value = {@ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = MetricAndAssessmentDetailsDto.class))
    )})
    @GetMapping("/assessment/reviewed/{reviewerId}")
    public ResponseEntity<List<MetricAndAssessmentDetailsDto>> getReviewedAssessmentsForReviewer(@PathVariable String reviewerId) {
        logger.debug("AssessmentController::getReviewedAssessments:: started");
        logger.debug("AssessmentController::getReviewedAssessments::Retrieving assessments by reviewer Id {}",
            reviewerId);
        List<MetricAndAssessmentDetailsDto> assessments = conversationService.toMetricAndAssessmentDetailsDto(assessmentService.getReviewedAssessmentsForReviewer(reviewerId));
        logger.debug("AssessmentController::getReviewedAssessments:: ended");
        return ResponseEntity.ok(assessments);
    }
    
    @Operation(
            summary = "Assessment status to INACTIVE"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = AssessmentDto.class))
    )})
    @PutMapping(path = "/assessment/submitStatus/{id}")
    public ResponseEntity<AssessmentDto> inactiveAssessmentById(@PathVariable Long id) {
    	logger.debug("AssessmentController::inactiveAssessmentById:: started");
        logger.debug("AssessmentController::inactiveAssessmentById::{}",id);
        AssessmentDto assessmentDto=conversationService.toAssessmentDto(assessmentService.inactiveAssessmentById(id));
        logger.debug("AssessmentController::inactiveAssessmentById:: end");
        return ResponseEntity.ok().body(assessmentDto);
    }
}


