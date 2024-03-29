package com.maveric.digital.controller;

import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.responsedto.ReportFilterDetails;
import com.maveric.digital.responsedto.ReportFilters;
import com.maveric.digital.service.ReportFilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RequestFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class ReportFilterController {

    private final ReportFilterService reportFilterService;
    @Operation(summary = "Get ReportFilterDetails")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Report Filter Details"),
            @ApiResponse(responseCode = "204", description = "No data found for the given reportFilters") })

    @PostMapping("/report/filters")
    public ReportFilterDetails  getReportFilterDetails(@RequestBody ReportFilters reportFilters){
        log.debug("ReportFilterController::getReportFilterDetails-call started");
        ReportFilterDetails reportFilterDetails=reportFilterService.getReportFilterDetails(reportFilters);
        log.debug("ReportFilterController::getReportFilterDetails-call ended");
        return  reportFilterDetails;
    }


}
