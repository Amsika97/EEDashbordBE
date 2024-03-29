package com.maveric.digital.service;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.exceptions.ProjectNotFoundException;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.responsedto.AssessmentsDashboardDto;
import com.maveric.digital.responsedto.ConsulateAccountCountDto;
import com.maveric.digital.responsedto.ConsulateProjectCountDto;
import com.maveric.digital.responsedto.PiechartDashboardDto;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class DasboardServiceImplTest {
    @Mock
    private AssessmentRepository assessmentRepository;
    @Mock
    private MetricSubmittedRepository metricSubmittedRepository;
    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void TestWithEmptyProjectsAndAssessments() {
        List<ConsulateProjectCountDto> consulateProjectCountDtos=new ArrayList<>();
        ConsulateProjectCountDto mockDto = new ConsulateProjectCountDto();
        mockDto.setCount(0);
        mockDto.setId(new Project());

        consulateProjectCountDtos.add(mockDto);
        when(assessmentRepository.countByProject()).thenReturn(consulateProjectCountDtos);
        when(metricSubmittedRepository.findAll()).thenReturn(Collections.emptyList());
        when(assessmentRepository.findAllAssessmentsBySubmitStatus("SAVE")).thenReturn(Collections.emptyList());
        when(assessmentRepository.findAllAssessmentsBySubmitStatus("SUBMITTED")).thenReturn(Collections.emptyList());
        when(assessmentRepository.findAllAssessmentsBySubmitStatus("REVIEWED")).thenReturn(Collections.emptyList());
        AssessmentsDashboardDto result = dashboardService.getAssessmentsConsolidatedData();
        assertNotNull(result);
        assertEquals(0, result.getTotalAssessmentSubmissions());
        assertEquals(0, result.getTotalAssessmentReviewed());
        assertEquals(0, result.getTotalAccounts());
        assertEquals(0, result.getTotalMetrics());
    }

    @Test
    void TestNonEmptyProjectsAndEmptyAssessments() {
        List<ConsulateProjectCountDto> consulateProjectCountDtos=new ArrayList<>();
        ConsulateProjectCountDto mockDto = new ConsulateProjectCountDto();
        mockDto.setCount(0);
        mockDto.setId(new Project());
        consulateProjectCountDtos.add(mockDto);
        when(assessmentRepository.countByProject()).thenReturn(consulateProjectCountDtos);
        List<Project> projects = List.of(new Project());
        when(metricSubmittedRepository.findAll()).thenReturn(Collections.emptyList());
        when(assessmentRepository.findAllAssessmentsBySubmitStatus("SAVE")).thenReturn(Collections.emptyList());
        when(assessmentRepository.findAllAssessmentsBySubmitStatus("SUBMITTED")).thenReturn(Collections.emptyList());
        when(assessmentRepository.findAllAssessmentsBySubmitStatus("REVIEWED")).thenReturn(Collections.emptyList());
        AssessmentsDashboardDto result = dashboardService.getAssessmentsConsolidatedData();
        Assertions.assertNotNull(result);
        assertEquals(projects.size(), result.getTotalProjects());
        assertEquals(0, result.getTotalAssessmentSubmissions());
        assertEquals(0, result.getTotalAssessmentReviewed());
        assertEquals(0, result.getTotalAccounts());
        assertEquals(0, result.getTotalMetrics());

    }
    @Test
    public void testCountAssessmentsByProject() {
        List<ConsulateProjectCountDto> consulateProjectCountDtos=new ArrayList<>();
        ConsulateProjectCountDto mockDto = new ConsulateProjectCountDto();
        mockDto.setCount(0);
        mockDto.setId(new Project());

        consulateProjectCountDtos.add(mockDto);
        when(assessmentRepository.countByProject()).thenReturn(consulateProjectCountDtos);
        int result = dashboardService.countAssessmentsByProject();
        assertEquals(1, result);
    }
/*
    @Test
    public void testCountAssessmentsByProjectWithNullDto() {
        Mockito.when(assessmentRepository.countByProject()).thenReturn(null);
        assertThrows(ProjectNotFoundException.class, () -> {
            dashboardService.countAssessmentsByProject();
        });
    }*/

    @Test
     void testCountAssessmentsByAccount() {
        List<ConsulateAccountCountDto> consulateProjectCountDtos=new ArrayList<>();
        ConsulateAccountCountDto mockDto = new ConsulateAccountCountDto();
        mockDto.setId(new Account());
        consulateProjectCountDtos.add(mockDto);
        when(assessmentRepository.countByAccount()).thenReturn(consulateProjectCountDtos);
        int result = dashboardService.countAssessmentsByAccount();
        assertEquals(1, result);
    }

    @Test
    void testCalculatePercentageForPieChart() {
        List<ConsulateProjectCountDto> consulateProjectCountDtos=new ArrayList<>();
        ConsulateProjectCountDto mockDto = new ConsulateProjectCountDto();
        mockDto.setCount(0);
        mockDto.setId(new Project());

        consulateProjectCountDtos.add(mockDto);
        when(assessmentRepository.countByProject()).thenReturn(consulateProjectCountDtos);

        Assessment assessment = new Assessment();
        ArrayList<Assessment> assessmentList = new ArrayList<>();
        assessmentList.add(assessment);
        when(assessmentRepository.findAllAssessmentsBySubmitStatus(Mockito.any())).thenReturn(assessmentList);
        when(assessmentRepository.countBySubmitStatusIn(Mockito.any())).thenReturn(3);
        PiechartDashboardDto actualCalculatePercentageForPieChartResult = dashboardService
                .calculatePercentageForPieChart();
        assertEquals("33.33%", actualCalculatePercentageForPieChartResult.getReviewed());
        assertEquals(1, actualCalculatePercentageForPieChartResult.getReviewedCount());
        assertEquals(1, actualCalculatePercentageForPieChartResult.getSubmittedCount());
        assertEquals("percentage", actualCalculatePercentageForPieChartResult.getUnit());
        assertEquals("33.33%", actualCalculatePercentageForPieChartResult.getSubmitted());
        verify(assessmentRepository).countBySubmitStatusIn(Mockito.any());
    }
    @Test
    void testCalculatePercentageForPieChart4() {
        when(assessmentRepository.countBySubmitStatusIn(Mockito.any())).thenReturn(0);
        assertThrows(AssessmentNotFoundException.class, () -> dashboardService.calculatePercentageForPieChart());
        verify(assessmentRepository).countBySubmitStatusIn(Mockito.any());
    }
    List<Assessment> populateAssessmentList(){

        Account account=new Account();
        account.setId(1L);
        Project project=new Project();
        project.setId(1L);
        ProjectType projectType=new ProjectType();
        projectType.setId(1L);
        Assessment assessmentObj=new Assessment();
        assessmentObj.setAccount(account);
        assessmentObj.setProject(project);
        assessmentObj.setProjectType(projectType);
        assessmentObj.setSubmitStatus(AssessmentStatus.SUBMITTED);

        Assessment assessmentObj1=new Assessment();
        assessmentObj1.setAccount(account);
        assessmentObj1.setProject(project);
        assessmentObj1.setProjectType(projectType);
        assessmentObj1.setSubmitStatus(AssessmentStatus.REVIEWED);

        List<Assessment> assessmentList=new ArrayList<>();
        assessmentList.add(assessmentObj);
        assessmentList.add(assessmentObj1);
        return assessmentList;
    }
    PiechartDashboardDto populatePiechartDashboardDto(){
        PiechartDashboardDto piechartDashboardDto=new PiechartDashboardDto();
        piechartDashboardDto.setReviewedCount(1);
        piechartDashboardDto.setReviewed("50.00%");
        piechartDashboardDto.setSubmittedCount(1);
        piechartDashboardDto.setSubmitted("50.00%");
        piechartDashboardDto.setUnit("percentage");
        return piechartDashboardDto;
    }


    @Test
    void testPiechartDataWithFiltersAC(){
        Filters filterName=Filters.AC;
        String filterValue="1";
        when(assessmentRepository.findByAccountId(1L)).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=dashboardService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        assertEquals(populatePiechartDashboardDto(),result);

    }
    @Test
    void testPiechartDataWithFiltersPR(){
        Filters filterName=Filters.PR;
        String filterValue="1";
        List<Long> filterValues = Arrays.stream(filterValue.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        when(assessmentRepository.findByProjectIdIn(filterValues)).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=dashboardService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        assertEquals(populatePiechartDashboardDto(),result);

    }
    @Test
    void testPiechartDataWithFiltersPT(){
        Filters filterName=Filters.PT;
        String filterValue="1";
        when(assessmentRepository.findByProjectTypeId(1L)).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=dashboardService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        assertEquals(populatePiechartDashboardDto(),result);

    }
    @Test
    void testPiechartDataWithFiltersALL(){
        Filters filterName=null;
        String filterValue=null;
        when(assessmentRepository.findBySubmitStatusIn(List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED))).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=dashboardService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        assertEquals(populatePiechartDashboardDto(),result);

    }
}
