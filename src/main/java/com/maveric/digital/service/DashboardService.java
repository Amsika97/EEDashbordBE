package com.maveric.digital.service;

import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.responsedto.AssessmentsDashboardDto;
import com.maveric.digital.responsedto.PiechartDashboardDto;

import java.util.List;

public interface DashboardService {
    AssessmentsDashboardDto getAssessmentsConsolidatedData();
    AssessmentsDashboardDto getAssessmentsConsolidatedDataWithFilter(String fieldName, List<Long> fieldValue);
    PiechartDashboardDto calculatePercentageForPieChart();
    PiechartDashboardDto calculatePercentageForPieChartWithFilters(Filters filterName, String filterValue);
}
