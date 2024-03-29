package com.maveric.digital.service;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.responsedto.AssessmentsDashboardDto;
import com.maveric.digital.responsedto.ConsulateAccountCountDto;
import com.maveric.digital.responsedto.ConsulateProjectCountDto;
import com.maveric.digital.responsedto.PiechartDashboardDto;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.maveric.digital.utils.ServiceConstants.UNIT;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AssessmentRepository assessmentRepository;
    private final MetricSubmittedRepository metricSubmittedRepository;

    @Override
    public AssessmentsDashboardDto getAssessmentsConsolidatedDataWithFilter(String fieldName, List<Long> fieldValue) {
        log.debug("DashboardServiceImpl::getAssessmentsConsolidatedDataWithFilter() call started");
        if (StringUtils.isNotBlank(fieldName) && fieldName.equalsIgnoreCase(String.valueOf(Filters.AC)))
            return accountBasedAssessmentCount(fieldValue.get(0));
        else if (StringUtils.isNotBlank(fieldName) && fieldName.equalsIgnoreCase(String.valueOf(Filters.PR)))
            return projectBasedAssessmentCount(fieldValue);
        else
            return consolidatedAssessment();
    }

    private AssessmentsDashboardDto accountBasedAssessmentCount(Long accountId) {
        List<Assessment> assessmentProjects = assessmentRepository.findByAccountIdAndSubmitStatusIn(accountId,
                        List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        List<Long> totalAssessmentProject = assessmentProjects.stream()
                .map(Assessment::getProjectId)
                .distinct()
                .toList();
        List<MetricSubmitted> metricProjects = metricSubmittedRepository.findByAccountIdAndSubmitStatusIn(accountId,
                        List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        List<Long> totalMetricProject = metricProjects.stream()
                .map(MetricSubmitted::getProject)
                .map(Project::getId)
                .distinct()
                .toList();
        Set<Long> distinctProjects = new HashSet<>(totalAssessmentProject);
        distinctProjects.addAll(totalMetricProject);
        int totalProject = distinctProjects.size();
        int totalAssessmentBySub = getAssessmentProjectsBasedOnStatus(
                assessmentProjects,List.of(AssessmentStatus.SUBMITTED)).size();
        int totalAssessmentBYRev = getAssessmentProjectsBasedOnStatus(
                assessmentProjects,List.of(AssessmentStatus.REVIEWED)).size();
        int totalMetric = getMetricProjectsBasedOnStatus(metricProjects,
                List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED)).size();
        log.info("Total Accounts: {}, Total project count: {},Total Assessments Submitted: {}, Total Assessments Reviewed: {}, Total Metrics: {}",
                1, totalProject, totalAssessmentBySub, totalAssessmentBYRev, totalMetric);
        AssessmentsDashboardDto assessmentsDashboardDto = new AssessmentsDashboardDto(totalAssessmentBySub,
                totalAssessmentBYRev, totalMetric, totalProject, 1);
        log.debug("DashboardServiceImpl::accountBasedCount() call ended");
        return assessmentsDashboardDto;
    }

    private List<Assessment> getAssessmentProjectsBasedOnStatus(List<Assessment> assessmentList, List<AssessmentStatus> assessmentStatus){
        return assessmentList.stream().filter(assessment ->
                assessmentStatus.contains(assessment.getSubmitStatus())).toList();
    }

    private List<MetricSubmitted> getMetricProjectsBasedOnStatus(List<MetricSubmitted> metricSubmittedList, List<AssessmentStatus> assessmentStatus){
        return metricSubmittedList.stream().filter(metric ->
                assessmentStatus.contains(metric.getSubmitStatus())).toList();
    }

    private AssessmentsDashboardDto projectBasedAssessmentCount(List<Long> projectIds) {
        int totalProject = projectIds.size();
        Integer totalAssessmentBySub = assessmentRepository.countByProjectIdInAndSubmitStatus(projectIds, AssessmentStatus.SUBMITTED.toString());
        Integer totalAssessmentBYRev = assessmentRepository.countByProjectIdInAndSubmitStatus(projectIds, AssessmentStatus.REVIEWED.toString());
        Integer totalMetric = metricSubmittedRepository.countByProjectIdInAndSubmitStatusIn(projectIds, List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        log.info("Total Accounts: {}, Total project count: {},Total Assessments Submitted: {}, Total Assessments Reviewed: {}, Total Metrics: {}",
                1, totalProject, totalAssessmentBySub, totalAssessmentBYRev, totalMetric);
        AssessmentsDashboardDto assessmentsDashboardDto = new AssessmentsDashboardDto( ObjectUtils.isEmpty(totalAssessmentBySub)?0:totalAssessmentBySub,
                ObjectUtils.isEmpty(totalAssessmentBYRev)?0:totalAssessmentBYRev, ObjectUtils.isEmpty(totalMetric)?0:totalMetric, totalProject, 1);
        log.debug("DashboardServiceImpl::projectBasedCount() call ended");
        return assessmentsDashboardDto;
    }

    private AssessmentsDashboardDto consolidatedAssessment() {
        List<Assessment> allAssessments = assessmentRepository.findAll();
        List<MetricSubmitted> metricSubmittedList = metricSubmittedRepository.findBySubmitStatusIn(
                List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        List<Assessment> assessmentSubmittedList = assessmentRepository.findBySubmitStatusIn(
                List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        long totalAccount = allAssessments.stream()
                .map(assessment -> assessment.getAccount().getId())
                .distinct()
                .count();
        List<Long> totalAssessmentProject = assessmentSubmittedList.stream()
                .map(Assessment::getProjectId)
                .distinct()
                .toList();
        List<Long> totalMetricProject = metricSubmittedList.stream()
                .map(MetricSubmitted::getProject)
                .map(Project::getId)
                .distinct()
                .toList();
        Set<Long> distinctProjects = new HashSet<>(totalAssessmentProject);
        distinctProjects.addAll(totalMetricProject);
        int totalProject = distinctProjects.size();
        Integer totalAssessmentBySub = getAssessmentProjectsBasedOnStatus(
                assessmentSubmittedList,List.of(AssessmentStatus.SUBMITTED)).size();
        Integer totalAssessmentBYRev = getAssessmentProjectsBasedOnStatus(
                assessmentSubmittedList,List.of(AssessmentStatus.REVIEWED)).size();
        List<MetricSubmitted> totalMetricSubmitteds = getMetricProjectsBasedOnStatus(metricSubmittedList,
                List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
       int totalMetric;
       if (CollectionUtils.isEmpty(totalMetricSubmitteds)) totalMetric = 0;
       else totalMetric = totalMetricSubmitteds.size();
       log.info("Total Accounts: {}, Total project count: {},Total Assessments Submitted: {}, Total Assessments Reviewed: {}, Total Metrics: {}",
                1, totalProject, totalAssessmentBySub, totalAssessmentBYRev, totalMetric);
        AssessmentsDashboardDto assessmentsDashboardDto = new AssessmentsDashboardDto(ObjectUtils.isEmpty(totalAssessmentBySub)?0:totalAssessmentBySub,
                ObjectUtils.isEmpty(totalAssessmentBYRev)?0:totalAssessmentBYRev, totalMetric, totalProject, (int) totalAccount);
        log.debug("DashboardServiceImpl::consolidatedAssessment() call ended");
        return assessmentsDashboardDto;
    }

    public AssessmentsDashboardDto getAssessmentsConsolidatedData() {
        log.debug("DashboardServiceImpl::getAssessmentsConsolidatedData() call started");
        int totalProjects = countAssessmentsByProject();
        int totalAccounts = countAssessmentsByAccount();
        int totalMetrics = metricSubmittedRepository.countBySubmitStatusIn(List.of(AssessmentStatus.SUBMITTED,AssessmentStatus.REVIEWED));
        List<Assessment> totalAssessmentSubmissions = assessmentRepository.findAllAssessmentsBySubmitStatus("SUBMITTED");
        List<Assessment> totalAssessmentReviewed = assessmentRepository.findAllAssessmentsBySubmitStatus("REVIEWED");


        if (totalProjects==0) {
            log.error("DashboardServiceImpl::getAssessmentsConsolidatedData() - No projects found");
        }

        if (totalAccounts==0){
            log.error("DashboardServiceImpl::getAssessmentsConsolidatedData() - No accounts found");
        }

        if (CollectionUtils.isEmpty(totalAssessmentSubmissions)) {
            log.error("DashboardServiceImpl::getAssessmentsConsolidatedData() - No submitted assessments found");
        }

        if (CollectionUtils.isEmpty(totalAssessmentReviewed)) {
            log.error("DashboardServiceImpl::getAssessmentsConsolidatedData() - No reviewed assessments found");
        }
        if (totalMetrics==0) {
            log.error("DashboardServiceImpl::getAssessmentsConsolidatedData() - No assessments metrics found");

        }
        log.info("Total project count: {},Total Assessments Submitted: {}, Total Assessments Reviewed: {},Total Accounts: {}, Total Metrics: {}",
                totalProjects,  totalAssessmentSubmissions.size(), totalAssessmentReviewed.size(), totalAccounts, totalMetrics);
        AssessmentsDashboardDto assessmentsDashboardDto = new AssessmentsDashboardDto(
                totalAssessmentSubmissions.size(),
                totalAssessmentReviewed.size(),
                totalMetrics,
                totalProjects,
                totalAccounts
        );

        assessmentsDashboardDto.setTotalProjects(totalProjects);
        assessmentsDashboardDto.setTotalAccounts(totalAccounts);
        log.debug("DashboardServiceImpl::getAssessmentsConsolidatedData() call ended");
        return assessmentsDashboardDto;
    }
    public Integer countAssessmentsByProject() {
        List<ConsulateProjectCountDto> assessmentProjects = assessmentRepository.countByProject();
        List<ConsulateProjectCountDto> metricsProjects = metricSubmittedRepository.countByProject();
        Set<Long> totalProjects=new HashSet<>();
        if (!CollectionUtils.isEmpty(assessmentProjects)) {
            List<Long> assIds = assessmentProjects.stream().map(projectCountDto -> projectCountDto.getId().getId()).toList();
            totalProjects.addAll(assIds);

        }
        if (!CollectionUtils.isEmpty(metricsProjects)) {
            List<Long> metIds = metricsProjects.stream().map(projectCountDto -> projectCountDto.getId().getId()).toList();
            totalProjects.addAll(metIds);
        }
        if (CollectionUtils.isEmpty(totalProjects)){
            return 0;

        }
        return totalProjects.size();
    }
    public Integer countAssessmentsByAccount() {
        List<ConsulateAccountCountDto> assessmentAccounts = assessmentRepository.countByAccount();
        List<ConsulateAccountCountDto> metricsAccounts = metricSubmittedRepository.countByAccount();
        Set<Long> totalProjects=new HashSet<>();
        if (!CollectionUtils.isEmpty(assessmentAccounts)) {
            List<Long> assIds = assessmentAccounts.stream().map(accountCountDto -> accountCountDto.getId().getId()).toList();
            totalProjects.addAll(assIds);
        }
        if (!CollectionUtils.isEmpty(metricsAccounts)) {
            List<Long> metIds = metricsAccounts.stream().map(accountCountDto -> accountCountDto.getId().getId()).toList();
            totalProjects.addAll(metIds);
        }

        if (CollectionUtils.isEmpty(totalProjects)){
            return 0;
        }
        return totalProjects.size();
    }
    @Override
    public PiechartDashboardDto calculatePercentageForPieChart() {
        log.debug("DashboardServiceImpl::calculatePercentageForPieChart() call started");
        Integer totalAssessments = assessmentRepository.countBySubmitStatusIn(List.of(AssessmentStatus.SUBMITTED,AssessmentStatus.REVIEWED));
        log.debug("calculatePercentageForPieChart() -Total Assessments: {}", totalAssessments);
        if (totalAssessments == 0) {
            log.error("calculatePercentageForPieChart() - No assessments found");
            throw new AssessmentNotFoundException("No assessments in DB");
        }
        AssessmentsDashboardDto consolidatedData = getAssessmentsConsolidatedData();
        String submittedPercentage = String.format("%.2f%%", ((double) consolidatedData.getTotalAssessmentSubmissions() / totalAssessments) * 100);
        log.debug("calculatePercentageForPieChart() - Submitted Assessments Percentage calculated: {}%", submittedPercentage);

        String reviewedPercentage = String.format("%.2f%%", ((double) consolidatedData.getTotalAssessmentReviewed() / totalAssessments) * 100);
        log.debug("calculatePercentageForPieChart() - Reviewed Assessments Percentage calculated: {}%", reviewedPercentage);

        log.debug("DashboardServiceImpl::calculatePercentageForPieChart() call ended");
        return new PiechartDashboardDto(submittedPercentage,consolidatedData.getTotalAssessmentSubmissions(),reviewedPercentage,consolidatedData.getTotalAssessmentReviewed(),UNIT);

    }
    @Override
    public PiechartDashboardDto calculatePercentageForPieChartWithFilters(Filters filterName, String filterValue) {
        log.debug("DashboardServiceImpl::calculatePercentageForPieChartWithFilters() call started");
        List<Assessment> assessmentList;
        if (filterName != null && filterValue != null) {
            List<Long> filterValues = Arrays.stream(filterValue.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            assessmentList = switch (filterName) {
                case PR -> assessmentRepository.findByProjectIdIn(filterValues);
                case PT -> assessmentRepository.findByProjectTypeId(Long.parseLong(filterValue));
                case AC -> assessmentRepository.findByAccountId(Long.parseLong(filterValue));
            };
        } else {
            assessmentList = assessmentRepository.findBySubmitStatusIn(
                    List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));

        }
        log.debug("DashboardServiceImpl::calculatePercentageForPieChartWithFilters() call ended");
        return calculatePercentages(assessmentList);
    }

    private PiechartDashboardDto calculatePercentages(List<Assessment> assessmentList) {
        log.debug("Calculating percentages for the pie chart");
        Long submitted = getStatusCount(AssessmentStatus.SUBMITTED,assessmentList);
        Long reviewed = getStatusCount(AssessmentStatus.REVIEWED,assessmentList);
        Long totalAssessments = Math.addExact(reviewed,submitted);
        return new PiechartDashboardDto(calculatePercentage(submitted,totalAssessments), Math.toIntExact(submitted),calculatePercentage(reviewed,totalAssessments), Math.toIntExact(reviewed),UNIT);
    }
    private String calculatePercentage(Long status,Long totalAssessments){
        log.debug("Calculating percentage: status={}, totalAssessments={}", status, totalAssessments);
        return String.format("%.2f%%", ((double) status / totalAssessments) * 100);
    }
    private Long getStatusCount(AssessmentStatus status,List<Assessment> assessmentList){
        log.debug("Counting assessments with status: {}", status);
        return assessmentList.stream().filter(assessment -> status.equals(assessment.getSubmitStatus())).count();
    }

}
