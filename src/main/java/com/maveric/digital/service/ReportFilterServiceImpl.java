package com.maveric.digital.service;

import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.responsedto.ReportFilterDetails;
import com.maveric.digital.responsedto.ReportFilters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportFilterServiceImpl implements ReportFilterService {

    private final AssessmentRepository assessmentRepository;
    private final ConversationService conversationService;
    private final MetricConversationService metricConversationService;
    private final MetricSubmittedRepository metricSubmittedRepository;

    @Override
    public ReportFilterDetails getReportFilterDetails(ReportFilters reportFilters) {
        log.debug("ReportFilterServiceImpl::getReportFilterDetails call started");
        ReportFilterDetails reportFilterDetails = new ReportFilterDetails();
        if (reportFilters.getSubmissionPeriodDays()!=null && reportFilters.getSubmissionPeriodDays()>0){
            Date fromDate = DateUtils.addDays(new Date(),-reportFilters.getSubmissionPeriodDays());
            reportFilters.setSubmissionFromDate(fromDate.getTime());
            reportFilters.setSubmissionToDate(System.currentTimeMillis());
        }
        switch (reportFilters.getReportFilterType()) {
            case ASSESSMENT -> getAndPrepareAssessmentsForReportFilters(reportFilters, reportFilterDetails);
            case METRICS -> getAndPrepareMetricsForReportFilters(reportFilters, reportFilterDetails);
            default -> {
                getAndPrepareAssessmentsForReportFilters(reportFilters, reportFilterDetails);
                getAndPrepareMetricsForReportFilters(reportFilters, reportFilterDetails);
            }
        }
        log.debug("ReportFilterServiceImpl::getReportFilterDetails call ended");
        return reportFilterDetails;
    }

    private void getAndPrepareAssessmentsForReportFilters(ReportFilters reportFilters, ReportFilterDetails reportFilterDetails) {

        List<Assessment> assessmentList = assessmentRepository.findByFilterCriteria(reportFilters);
        log.debug("ReportFilterServiceImpl::getAndPrepareAssessmentsForReportFilters- assessmentList{}", assessmentList);
        if (!CollectionUtils.isEmpty(assessmentList)) {
            reportFilterDetails.setAssessmentReportDetails(conversationService.toReportDetailsDtos(assessmentList));
        }
    }

    private void getAndPrepareMetricsForReportFilters(ReportFilters reportFilters, ReportFilterDetails reportFilterDetails) {
        List<MetricSubmitted> metricSubmittedList = metricSubmittedRepository.findByFilterCriteria(reportFilters);
        log.debug("ReportFilterServiceImpl::getAndPrepareMetricsForReportFilters- metricSubmittedList{}", metricSubmittedList);
        if (!CollectionUtils.isEmpty(metricSubmittedList)) {
            reportFilterDetails.setMetricReportDetails(metricConversationService.toMetricReportDetails(metricSubmittedList));
        }
    }
}
