package com.maveric.digital.controller;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.responsedto.*;

import java.util.List;

import com.maveric.digital.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maveric.digital.service.MetricConversationService;
import com.maveric.digital.service.MetricSubmitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.maveric.digital.utils.ServiceConstants.*;

@RestController
@RequestMapping({ "/v1" })
@RequiredArgsConstructor
@Slf4j
public class MetricSubmitController {
	
	private final MetricConversationService metricConversationService;
	private final MetricSubmitService metricSubmitService;
	private final ConversationService conversationService;
	
	private static final Logger logger = LoggerFactory.getLogger(MetricSubmitController.class);

	
	@PostMapping(path = "/metric/metricreviewercomment/save")
	public ResponseEntity<MetricSubmittedDto> saveMetricReviewerComment(@RequestBody @Valid MetricReviewerCommentDto requestPayload) {
		log.debug("AssessmentController::saveReviewerComment()::Start");
		log.debug("AssessmentController::saveReviewerComment()::{}",
				requestPayload);
		return ResponseEntity
				.ok(metricConversationService.toMetricSubmitDto(metricSubmitService.saveMetricReviewerComment(requestPayload)));
	}
	@Operation(summary = "save or submit Metric")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MetricSubmittedDto.class))) })
	@PostMapping(path = "/metric/saveOrSubmit")
	public ResponseEntity<MetricSubmittedDto> saveOrSubmitMetric(@RequestBody @Valid MetricSubmittedDto requestPayload) {
		return ResponseEntity.ok().body(
				metricConversationService.toMetricSubmitDto(metricSubmitService.saveOrSubmitMetric(requestPayload)));
	}

	@Operation(summary = "Get Metric by MetricId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MetricSubmittedDto.class))) })
	@GetMapping(path = "/metric/{metricId}")
	public ResponseEntity<MetricSubmittedDto> getMetricIdById(@PathVariable Long metricId) {
		logger.debug("MetricSubmitController::getMetricIdById::Retrieving metricId with ID {}", metricId);
		return ResponseEntity.ok()
				.body(metricConversationService.toMetricSubmitDto(metricSubmitService.findMetricById(metricId)));

	}

	@GetMapping (path = "/metric/lineChart/startDate/{startDate}/endDate/{endDate}")
	public ResponseEntity<List<LineChartDto>> submitMetricLineChartStartAndEndDates(@PathVariable Long startDate, @PathVariable Long endDate,@RequestParam(name = "filterName" , required = false,defaultValue = FILTER_NAME_DEFAULT) String filterName ,@RequestParam(name = "filterValue", required = false,defaultValue = FILTER_VALUE_DEFAULT) String filterValue) {
		log.debug("MetricSubmitController::submitMetricLineChartStartAndEndDates()::sstartDate::{}::endDate::{}",
				startDate, endDate);
		return ResponseEntity.ok(metricConversationService
				.tosubmitmetriclinechartdto(metricSubmitService.submitMetricLineChartStartAndEndDates(startDate, endDate,filterName,filterValue)));
	}

	@Operation(summary = "Get all pending review metrics")
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200"
	), @ApiResponse(
			responseCode = "200",
			description = "Metrics not found"
	)})
	@GetMapping(path = "/metric/pendingReview/all")
	public ResponseEntity<List<MetricReviewDto>> getAllPendingReviewMetrics(@RequestParam String userId) {
		log.debug("MetricSubmitController::getAllPendingReviewMetrics():: call started");
		List<MetricReviewDto> metrics=metricConversationService.toMetricReviewDtos(metricSubmitService.getAllPendingReviewMetrics(userId));
		log.debug("MetricSubmitController::getAllPendingReviewMetrics():: call ended");
		return ResponseEntity.ok(metrics);
	}
	@Operation(
			summary = "calculate percentages of submitted,rejected and approved metrics for metric dashboard piechart"
	)
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200"
	), @ApiResponse(
			responseCode = "200",
			description = "metrics not found"
	)})
	@GetMapping("/metricDashboard/piechartData")
	public ResponseEntity<PiechartDashboardDto> calculatePercentageForMetricDashboardPieChart() {
		log.debug("MetricSubmitController::calculatePercentageForMetricDashboardPieChart() call started");
		PiechartDashboardDto piechartMetricDashboardDto = metricSubmitService.calculatePercentageForMetricDashboardPieChart();
		log.debug("MetricSubmitController::calculatePercentageForMetricDashboardPieChart() call ended");
		return new ResponseEntity<>(piechartMetricDashboardDto, HttpStatus.OK);
	}
	@Operation(summary = "Get all MetricDetails")
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200",
			description = "Return All MetricDetails"
	), @ApiResponse(
			responseCode = "200",
			description = "MetricDetails not found"
	)})
	@GetMapping("/metric/details/submittedBy/{submittedBy}")
	public ResponseEntity<List<MetricAndAssessmentDetailsDto>> getAllMetricDetails(@PathVariable("submittedBy") String submittedBy) {
		logger.debug("MetricSubmitController::getAllMetricDetails:: call started");
		logger.debug("submittedBy id : {} ",submittedBy);
		List<MetricAndAssessmentDetailsDto> allMetricDetails = metricSubmitService.getAllMetricDetails(submittedBy);
		logger.debug("MetricSubmitController::getAllMetricDetails:: call ended");
		return ResponseEntity.ok(allMetricDetails);
	}

	@Operation(summary = "Get all Metric Report Details")
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200",
			description = "Return All Metric Report Details"
	), @ApiResponse(
			responseCode = "200",
			description = "Metric Report Details not found"
	)})
	@GetMapping("/metric/report/details")
	public ResponseEntity<List<MetricAndAssessmentReportDetails>> getAllMetricReportDetails() {
		logger.debug("MetricSubmitController::getAllMetricReportDetails:: call started");
		List<MetricAndAssessmentReportDetails> allMetricDetails = metricSubmitService.getMetricReportDetails();
		logger.debug("MetricSubmitController::getAllMetricReportDetails:: call ended");
		return ResponseEntity.ok(allMetricDetails);
	}

	@GetMapping("metric/Top10Metricfordashboard")
	public ResponseEntity<List<AssessmentsSubmittedDashboardDto>> getTop10MetricForDashboard(){
		logger.debug("MetricSubmitController::getTop10MetricForDashboard::call started");
		List<AssessmentsSubmittedDashboardDto> metricSubmittedDashboardDtos = metricConversationService.toMetricSubmittedSubmittedDashboardDtos(metricSubmitService.getTop10MetricForDashboard());
		return ResponseEntity.ok(metricSubmittedDashboardDtos);
	}
	@Operation(
			summary = "calculate percentages of submitted,rejected and approved metrics for piechart " +
					"based on BusinessUnit,project,projectType and Account"
	)
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200"
	), @ApiResponse(
			responseCode = "200",
			description = "Assessments not found"
	)})
	@GetMapping("/metric/piechartData/filters")
	public ResponseEntity<PiechartDashboardDto> calculatePercentageForPieChart(
			@RequestParam(name = "filterName", required = false) Filters filterName,
			@RequestParam(name = "filterValue", required = false) String filterValue) {
		log.debug("MetricSubmitController::calculatePercentageForPieChart() call started");
		PiechartDashboardDto piechartDashboardDto = metricSubmitService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
		log.debug("MetricSubmitController::calculatePercentageForPieChart() call ended");
		return new ResponseEntity<>(piechartDashboardDto, HttpStatus.OK);
	}
	@Operation(
			summary = "Get Top 10 metrics for Dashboard with filters"
	)
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200"
	), @ApiResponse(
			responseCode = "204",
			description = "Metrics not found"
	)})
	@GetMapping("metric/top10/dashboard/filters")
	public ResponseEntity<List<AssessmentsSubmittedDashboardDto>> getTop10MetricsForDashboardFilters( @RequestParam(name = "filterName", required = false) Filters filterName,
			@RequestParam(name = "filterValue", required = false) String filterValue) {
		logger.debug("MetricSubmitController::getTop10MetricsForDashboardFilters::call started");
		List<AssessmentsSubmittedDashboardDto> assessmentsSubmittedDashboardDtos = metricConversationService.toMetricSubmittedSubmittedDashboardDtos(metricSubmitService.getTop10MetricsForDashboardFilters(filterName,filterValue));
		logger.debug("MetricSubmitController::getTop10MetricsForDashboardFilters::call ended");
		return ResponseEntity.ok(assessmentsSubmittedDashboardDtos);
	}
	@Operation(
			summary = "Get Metrics by reviewerId"
	)
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200",
			content = @Content(schema = @Schema(implementation = MetricAndAssessmentDetailsDto.class))
	)})
	@GetMapping("/metric/reviewed/{reviewerId}")
	public ResponseEntity<List<MetricAndAssessmentDetailsDto>> getReviewedMetrics(@PathVariable String reviewerId) {
		logger.debug("MetricSubmitController::getReviewedMetrics:: started");
		logger.debug("MetricSubmitController::getReviewedMetrics::Retrieving metrics by reviewer Id {}",
				reviewerId);
		List<MetricAndAssessmentDetailsDto> metrics = metricConversationService.toMetricSubmitted(metricSubmitService.getReviewedMetricsForReviewer(reviewerId));
		logger.debug("MetricSubmitController::getReviewedMetrics:: started");
		return ResponseEntity.ok(metrics);
	}
	
	@Operation(summary = "Metric status to INACTIVE")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MetricSubmittedDto.class))) })
	@PutMapping(path = "/metric/submitStatus/{id}")
	public ResponseEntity<MetricSubmittedDto> inactiveMetricById(@PathVariable Long id) {
		logger.debug("MetricSubmitController::inactiveMetricById:: started");
		logger.debug("MetricSubmitController::inactiveMetricById::{}", id);
		MetricSubmittedDto metricSubmittedDto = metricConversationService
				.toMetricSubmitDto(metricSubmitService.inactiveMetricById(id));
		logger.debug("MetricSubmitController::inactiveMetricById:: end");
		return ResponseEntity.ok().body(metricSubmittedDto);
	}
}