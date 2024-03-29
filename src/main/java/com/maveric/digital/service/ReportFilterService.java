package com.maveric.digital.service;

import com.maveric.digital.responsedto.ReportFilterDetails;
import com.maveric.digital.responsedto.ReportFilters;

public interface ReportFilterService {

    ReportFilterDetails getReportFilterDetails(ReportFilters reportFilters);
}
