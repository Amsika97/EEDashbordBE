package com.maveric.digital.controller;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.responsedto.AssessmentsDashboardDto;
import com.maveric.digital.responsedto.PiechartDashboardDto;
import com.maveric.digital.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    @GetMapping("/consolidated-assessmentsdata")
    public ResponseEntity<AssessmentsDashboardDto> getAssessmentConsolidatedData(@RequestParam(name = "fieldName", required = false) String fieldName,
                                                                                 @RequestParam(name = "fieldValue", required = false) List<Long> fieldValue) {
        log.debug("DashboardController::getAssessmentConsolidatedData() call started");
        AssessmentsDashboardDto assessmentStatisticsDto = dashboardService.getAssessmentsConsolidatedDataWithFilter(fieldName, fieldValue);
        log.debug("DashboardController::getAssessmentConsolidatedData() call ended");
        return new ResponseEntity<>(assessmentStatisticsDto, HttpStatus.OK);
    }
    @Operation(
            summary = "calculate percentages of submitted,rejected and approved assessments for piechart"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200"
    ), @ApiResponse(
            responseCode = "200",
            description = "Assessments not found"
    )})
    @GetMapping("/dashboard/piechartData")
    public ResponseEntity<PiechartDashboardDto> calculatePercentageForPieChart() {
        log.debug("DashboardController::calculatePercentageForPieChart() call started");
        PiechartDashboardDto piechartDashboardDto = dashboardService.calculatePercentageForPieChart();
        log.debug("DashboardController::calculatePercentageForPieChart() call ended");
        return new ResponseEntity<>(piechartDashboardDto, HttpStatus.OK);
    }
    @Operation(
            summary = "calculate percentages of submitted,rejected and approved assessments for piechart " +
                    "based on BusinessUnit,project,projectType and Account"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200"
    ), @ApiResponse(
            responseCode = "200",
            description = "Assessments not found"
    )})
    @GetMapping("/dashboard/piechartData/filters")
    public ResponseEntity<PiechartDashboardDto> calculatePercentageForPieChart(
            @RequestParam(name = "filterName", required = false) Filters filterName,
            @RequestParam(name = "filterValue", required = false) String filterValue) {
        log.debug("DashboardController::calculatePercentageForPieChart() call started");
        PiechartDashboardDto piechartDashboardDto = dashboardService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        log.debug("DashboardController::calculatePercentageForPieChart() call ended");
        return new ResponseEntity<>(piechartDashboardDto, HttpStatus.OK);
    }
}
