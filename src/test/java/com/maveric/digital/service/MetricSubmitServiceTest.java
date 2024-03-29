package com.maveric.digital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.exceptions.SubmitMetricLineChartDataNotFoundException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.model.embedded.Reviewer;
import com.maveric.digital.model.embedded.ReviewerQuestionComment;
import com.maveric.digital.model.embedded.ReviewerQuestionWeightage;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.repository.*;
import com.maveric.digital.responsedto.*;

import com.maveric.digital.utils.ServiceConstants;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class MetricSubmitServiceTest {
    @MockBean
    private MetricSubmittedRepository metricSubmittedRepository;
    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private ProjectTypeRepository projectTypeRepository;
    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MetricTemplateRepository metricTemplateRepository;
    @Autowired
    private MetricSubmitServiceImpl metricSubmitService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ConversationService conversationService;

    @MockBean
    private  MetricConversationService metricConversationService;
    private AssessmentStatus assessmentStatus;

    private MetricSubmittedDto metricSubmittedDto;

    private File jsonFile;

    private  List<MetricSubmitted> metricSubmitteds;
    @BeforeEach
    void insertData() throws IOException {
        metricSubmitteds=new ArrayList<>();
        MetricSubmitted metricSubmitted=new MetricSubmitted();
        metricSubmitted.setProject(new Project());
        metricSubmitted.setSubmittedAt(1701757505211L);
        metricSubmitted.setId(1L);
        metricSubmitteds.add(metricSubmitted);

        objectMapper = new ObjectMapper();
        jsonFile = new ClassPathResource("sample/MetricSubmittedDto.json").getFile();
        metricSubmittedDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), MetricSubmittedDto.class);

    }

    @Test
    void calculatePercentageForMetricDashboardPieChartTest() {
        PiechartDashboardDto piechartDashboardDto = new PiechartDashboardDto();
        piechartDashboardDto.setSubmitted("33.33%");
        piechartDashboardDto.setReviewed("33.33%");
        piechartDashboardDto.setReviewedCount(1);
        piechartDashboardDto.setSubmittedCount(1);
        piechartDashboardDto.setUnit("percentage");
        Integer totalMetrics = 3;
        when(metricSubmittedRepository.countBySubmitStatusIn(anyList())).thenReturn(totalMetrics);

        Integer totalMetricSubmitted = 1;
        when(metricSubmittedRepository.countBySubmitStatusIn(List.of(AssessmentStatus.SUBMITTED))).thenReturn(totalMetricSubmitted);

        Integer totalMetricReviewed = 1;
        when(metricSubmittedRepository.countBySubmitStatusIn(List.of(AssessmentStatus.REVIEWED))).thenReturn(totalMetricReviewed);

        PiechartDashboardDto result = metricSubmitService.calculatePercentageForMetricDashboardPieChart();
        assertEquals(piechartDashboardDto, result);
    }

    @Test
    void calculatePercentageForMetricDashboardPieChartTestNoMterics() {
        PiechartDashboardDto piechartDashboardDto = new PiechartDashboardDto();
        Integer totalMetrics = 0;
        when(metricSubmittedRepository.countBySubmitStatusIn(anyList())).thenReturn(totalMetrics);
        MetricNotFoundException exception = assertThrows(MetricNotFoundException.class,
                () -> {
                    metricSubmitService.calculatePercentageForMetricDashboardPieChart();
                });
        assertEquals("No Metrics in DB", exception.getMessage());

    }

    @Test
    void getAllMetricDetails() throws Exception {
        List<MetricAndAssessmentDetailsDto> metricAndAssessmentDetailsDtos = new ArrayList<>();
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto = new MetricAndAssessmentDetailsDto();
        metricAndAssessmentDetailsDto.setReviewerName("RAM");
        metricAndAssessmentDetailsDto.setId(1L);
        Mockito.when(this.metricSubmittedRepository.findAllBySubmittedByOrderByUpdatedAtDesc(anyString())).thenReturn(metricSubmitteds);
        Mockito.when(metricConversationService.toMetricSubmitted(metricSubmitteds)).thenReturn(metricAndAssessmentDetailsDtos);
        List<MetricAndAssessmentDetailsDto> templateDtos = this.metricSubmitService.getAllMetricDetails("a2f876d4-9269-11ee-b9d1-0242ac120002");
        Assertions.assertEquals(metricAndAssessmentDetailsDtos, templateDtos);
        verify(this.metricSubmittedRepository).findAllBySubmittedByOrderByUpdatedAtDesc(anyString());

        verify(this.metricSubmittedRepository).findAllBySubmittedByOrderByUpdatedAtDesc(anyString());

    }

    @Test
    void ThrowingCustomExceptionForGetAllMetricDetails() {
        Mockito.when(this.metricSubmittedRepository.findAllBySubmittedByOrderByUpdatedAtDesc(anyString())).thenThrow(CustomException.class);
        Assertions.assertThrows(CustomException.class, () ->
                this.metricSubmitService.getAllMetricDetails("a2f876d4-9269-11ee-b9d1-0242ac120002"));
        verify(this.metricSubmittedRepository).findAllBySubmittedByOrderByUpdatedAtDesc(anyString());

    }

    @Test
    void ThrowingCustomExceptionForGetAllMetricDetailsWithEmptyMetric() {
        Mockito.when(this.metricSubmittedRepository.findAllBySubmittedByOrderByUpdatedAtDesc(anyString())).thenReturn(Collections.emptyList());
        Assertions.assertThrows(CustomException.class, () ->
                this.metricSubmitService.getAllMetricDetails("a2f876d4-9269-11ee-b9d1-0242ac120002"));
        verify(this.metricSubmittedRepository).findAllBySubmittedByOrderByUpdatedAtDesc(anyString());

    }
    private List<LineChartProjection> submitMetricLinechart() {
        List<LineChartProjection> linechart = new ArrayList<>();
        LineChartProjection projection1 = new LineChartProjection();
        projection1.setCount(4);
        projection1.setId("04-12-2023");
        LineChartProjection projection2 = new LineChartProjection();
        projection2.setCount(3);
        projection2.setId("05-12-2023");
        linechart.add(projection1);
        linechart.add(projection2);
        return linechart;
    }

    @Test
    void submitMetricLineChartStartAndEndDates(){
        when(metricSubmittedRepository.submitMetricLineChartStartandEndDates(anyLong(),anyLong(),anyList())).thenReturn(this.submitMetricLinechart());
        List<LineChartProjection> linechartlist=metricSubmitService.submitMetricLineChartStartAndEndDates(1L,1L,"All","1,2,3");
        System.out.println("MetricSubmitServiceTest.submitMetricLineChartStartAndEndDates():"+linechartlist);
        assertEquals(linechartlist.size(), Integer.valueOf(3));
        assertEquals(linechartlist.get(1), this.submitMetricLinechart().get(0));
    }

    @Test
    void testSubmitMetricLineChartStartAndEndDates_NoDataFound() {
        when(metricSubmittedRepository.submitMetricLineChartStartandEndDates(Mockito.<Long>any(), Mockito.<Long>any(),anyList()))
                .thenReturn(new ArrayList<>());
        assertThrows(SubmitMetricLineChartDataNotFoundException.class,
                () -> metricSubmitService.submitMetricLineChartStartAndEndDates(1L, 1L,"BusinessUnit","1,2,3"));
        verify(metricSubmittedRepository).submitMetricLineChartStartandEndDates(Mockito.<Long>any(), Mockito.<Long>any(),anyList());
    }


    @Test
    void findMetricByIdSuccess() {
        Long metricId = 1L;
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setSubmittedAt(168475L);
        metricSubmitted.setSubmittedBy("john");
        metricSubmitted.setSubmitStatus(AssessmentStatus.SUBMITTED);
        when(metricSubmittedRepository.findById(metricId)).thenReturn(Optional.of(metricSubmitted));
        MetricSubmitted result = metricSubmitService.findMetricById(metricId);
        assertEquals(metricSubmitted, result);
        assertEquals(metricSubmitted.getSubmittedBy(),result.getSubmittedBy());
    }

    @Test
    void findMetricByIdNotFound() {
        Long metricId = 1L;
        when(metricSubmittedRepository.findById(metricId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> metricSubmitService.findMetricById(metricId));
    }
    @Test
    void findMetricByIdWhenMetricIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            metricSubmitService.findMetricById(null);
        });
    }
    @Test
    void findMetricByIdWhenMetricIdIsZero() {
        Long metricId=0L;
        assertThrows(IllegalArgumentException.class, () -> {
            metricSubmitService.findMetricById(metricId);
        });
    }
    @Test
    void findMetricByIdWhenMetricIdIsNegative() {
        Long metricId = -1L;
        assertThrows(IllegalArgumentException.class, () -> {
            metricSubmitService.findMetricById(metricId);
        });
    }


    @Test
    void testSaveMetricReviewerComment() {
        MetricSubmitted metricSubmitted = createSampleMetricSubmitted();
        MetricReviewerCommentDto commentDto = new MetricReviewerCommentDto();
        commentDto.setStatus(AssessmentStatus.REVIEWED);
        when(metricSubmittedRepository.save(any())).thenReturn(metricSubmitted);
        when(metricSubmittedRepository.findById(any())).thenReturn(Optional.of(metricSubmitted));
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        metricSubmitted.setSubmittedBy(String.valueOf(UUID.randomUUID()));
        ReviewerQuestionComment reviewerQuestionComment=new ReviewerQuestionComment();
        reviewerQuestionComment.setReviewerComment("Need improvement");
        ReviewerQuestionWeightage reviewerQuestionWeightage=new ReviewerQuestionWeightage();
        reviewerQuestionWeightage.setReviewerWeightage("Very Good");
        commentDto.setReviewerQuestionWeightage(Arrays.asList(reviewerQuestionWeightage));
        commentDto.setReviewerQuestionComment(Arrays.asList(reviewerQuestionComment));
        metricSubmitted.setProjectCategory(metricSubmittedDto.getProjectCategory());
        when(userRepository.findByOidIn(Collections.singletonList(Mockito.<UUID>any()))).thenReturn(Optional.empty());
        commentDto.setReviewerId(String.valueOf(UUID.randomUUID()));

        MetricSubmitted result = metricSubmitService.saveMetricReviewerComment(commentDto);
        verify(metricSubmittedRepository).findById(any());
        verify(metricSubmittedRepository).save(any());

        List<Reviewer> reviewers = result.getReviewers();
        assertEquals(1, reviewers.size());
        Reviewer reviewer = reviewers.get(0);
        assertSame(metricSubmitted, result);
        if (AssessmentStatus.REVIEWED.equals(commentDto.getStatus())) {
            assertEquals(commentDto.getStatus(), result.getSubmitStatus());
            assertNotNull(reviewer.getReviewerAt());
            assertNotNull(result.getUpdatedAt());
        } else {
            assertNotEquals(commentDto.getStatus(), result.getSubmitStatus());
            assertNull(reviewer.getReviewerAt());
            assertNull(result.getUpdatedAt());
        }
    }

    private MetricSubmitted createSampleMetricSubmitted() {

        Account account = new Account();
        account.setAccountName("Durga");

        Project project = new Project();
        project.setAccount(account);
        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        projectType.setProjectTypeName("CITI");
        MetricTemplate template = new MetricTemplate();
        template.setId(1L);
        template.setTemplateName("Sample Template");
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setAccount(account);
        metricSubmitted.setProject(project);
        metricSubmitted.setProjectType(projectType);
        metricSubmitted.setTemplate(template);
        metricSubmitted.setReviewers(new ArrayList<>());
        metricSubmitted.setSubmitStatus(AssessmentStatus.SAVE);
        return metricSubmitted;
    }

    @Test
    void testSaveMetricReviewerCommentMetricNotFoundException() {
        MetricReviewerCommentDto commentDto = new MetricReviewerCommentDto();
        commentDto.setMetricId(1L);
        when(metricSubmittedRepository.findById(eq(commentDto.getMetricId()))).thenReturn(Optional.empty());
        MetricNotFoundException exception = assertThrows(MetricNotFoundException.class,
                () -> metricSubmitService.saveMetricReviewerComment(commentDto));
        assertEquals("Metric Not Found", exception.getMessage());
        verify(metricSubmittedRepository).findById(eq(commentDto.getMetricId()));
        verify(metricSubmittedRepository, never()).save(any());
    }


    @Test
    void testSaveOrSubmitMetric() {
        MetricTemplate metricTemplate=new MetricTemplate();
        Account account=new Account();
        ProjectType projectType=new ProjectType();
        Project project=new Project();
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setId(1L);
        when(metricSubmittedRepository.findById(1l)).thenReturn(Optional.of(metricSubmitted));
        when(metricTemplateRepository.findById(anyLong())).thenReturn(Optional.of(metricTemplate));
        when(metricConversationService.toMetricSubmitted(metricSubmittedDto, metricTemplate,account,projectType,project)).thenReturn(metricSubmitted);
        when(metricSubmittedRepository.save(metricSubmitted)).thenReturn(metricSubmitted);
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        metricSubmitted.setSubmittedBy(String.valueOf(UUID.randomUUID()));
        MetricSubmitted result = metricSubmitService.saveOrSubmitMetric(metricSubmittedDto);

        assertEquals(metricSubmitted.getId(), result.getId());
        assertEquals(metricSubmitted.getSubmitStatus(), result.getSubmitStatus());
    }
    @Test
    void testSaveOrSubmitMetricWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            metricSubmitService.saveOrSubmitMetric(null);
        });
    }

    @Test
    void testSaveOrSubmitMetricWhenTemplateNotFound() {
        MetricSubmittedDto requestPayload = new MetricSubmittedDto();
        requestPayload.setMetricTemplateId(1L);
        when(metricTemplateRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(TemplateNotFoundException.class, () -> {
            metricSubmitService.saveOrSubmitMetric(requestPayload);
        });
    }
    @Test
    void testSaveOrSubmitMetricWhenNewMetricCreated() {
        when(metricTemplateRepository.findById(metricSubmittedDto.getMetricTemplateId())).thenReturn(Optional.of(new MetricTemplate()));
        when(accountRepository.findById(metricSubmittedDto.getAccountId())).thenReturn(Optional.of(new Account()));
        when(projectTypeRepository.findById(metricSubmittedDto.getProjectTypeId())).thenReturn(Optional.of(new ProjectType()));
        when(projectRepository.findById(metricSubmittedDto.getProjectId())).thenReturn(Optional.of(new Project()));
        when(metricSubmittedRepository.findById(anyLong())).thenReturn(Optional.empty());

        MetricSubmitted newMetric = new MetricSubmitted();
        newMetric.setSubmitStatus(AssessmentStatus.SAVE);
        when(metricConversationService.toMetricSubmitted(eq(metricSubmittedDto), any(MetricTemplate.class), any(Account.class),
             any(ProjectType.class), any(Project.class)))
            .thenReturn(newMetric);
        when(metricSubmittedRepository.save(newMetric)).thenReturn(newMetric);
        MetricSubmitted result = metricSubmitService.saveOrSubmitMetric(metricSubmittedDto);

        assertNotNull(result);
        verify(metricTemplateRepository).findById(metricSubmittedDto.getMetricTemplateId());
        verify(accountRepository).findById(metricSubmittedDto.getAccountId());
        verify(projectTypeRepository).findById(metricSubmittedDto.getProjectTypeId());
        verify(projectRepository).findById(metricSubmittedDto.getProjectId());
        verify(metricConversationService).toMetricSubmitted(eq(metricSubmittedDto), any(MetricTemplate.class), any(Account.class),
                any(ProjectType.class), any(Project.class));

        verify(metricSubmittedRepository).save(newMetric);
    }
    @Test
    void getAllPendingReviewMetricsWhenMetricsFound() {
        String UserId = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<MetricSubmitted> mockMetrics = Arrays.asList(new MetricSubmitted(), new MetricSubmitted());
        when(metricSubmittedRepository.findAllBySubmittedByNotAndSubmitStatusOrderByUpdatedAtDesc(anyString(), anyString(), any(Sort.class))).thenReturn(mockMetrics);
        List<MetricSubmitted> result = metricSubmitService.getAllPendingReviewMetrics(UserId);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(metricSubmittedRepository).findAllBySubmittedByNotAndSubmitStatusOrderByUpdatedAtDesc(
                anyString(), anyString(), any(Sort.class));
    }
    @Test
    void getAllPendingReviewMetricsShouldThrowExceptionWhenNoMetricFound() {
        String userId="a2f876d4-9269-11ee-b9d1-0242ac120002";
        Mockito.when(metricSubmittedRepository.findAllBySubmittedByNotAndSubmitStatusOrderByUpdatedAtDesc(anyString(),anyString(),Mockito.any(Sort.class)))
                .thenReturn(Collections.emptyList());
        MetricNotFoundException metricNotFoundException=assertThrows(MetricNotFoundException.class,
                ()-> metricSubmitService.getAllPendingReviewMetrics(userId));

        assertEquals("Metric Not Found",metricNotFoundException.getMessage());
    }

    @Test
    void getTop10MetricForDashboardTest() {
        List<MetricSubmitted> metricSubmitteds = new ArrayList<>();
        metricSubmitteds.add(new MetricSubmitted());
        metricSubmitteds.add(new MetricSubmitted());
        List<AssessmentStatus> assessmentStatusList=new ArrayList<>();
        assessmentStatusList.add(AssessmentStatus.REVIEWED);
        assessmentStatusList.add(AssessmentStatus.SUBMITTED);
        when(metricSubmittedRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(assessmentStatusList)).thenReturn(metricSubmitteds);
        List<MetricSubmitted> metricSubmittedList = metricSubmitService.getTop10MetricForDashboard();
        assertEquals(metricSubmitteds, metricSubmittedList);
    }
    @Test
    void getTop10MetricForDashboardTestNoAssessments() {
        List<Assessment> assessments = new ArrayList<>();
        List<AssessmentStatus> assessmentStatusList=new ArrayList<>();
        assessmentStatusList.add(AssessmentStatus.SUBMITTED);
        assessmentStatusList.add(AssessmentStatus.REVIEWED);
        when(metricSubmittedRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(assessmentStatusList)).thenReturn(Collections.emptyList());
        MetricNotFoundException exception = assertThrows(MetricNotFoundException.class,
                () -> {
                    metricSubmitService.getTop10MetricForDashboard();
                });

        assertEquals(new MetricNotFoundException("Metric Not Found").getMessage(), exception.getMessage());
    }

    @Test
    void getAllMetricReportDetails() throws Exception {
        List<MetricAndAssessmentReportDetails> metricReportDetails = new ArrayList<>();
        MetricAndAssessmentReportDetails details = new MetricAndAssessmentReportDetails();
        details.setId(1111L);
        details.setTemplateName("T1");
        metricReportDetails.add(details);
        List<MetricSubmitted> mockMetrics = Arrays.asList(new MetricSubmitted(), new MetricSubmitted());
        when(metricSubmittedRepository.findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus.SAVE, Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(mockMetrics);
        when(metricConversationService.toMetricReportDetails(mockMetrics)).thenReturn(metricReportDetails);
        metricSubmitService.getMetricReportDetails();
        verify(metricSubmittedRepository).findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus.SAVE, Sort.by(Sort.Order.desc("updatedAt")));
        verify(metricConversationService).toMetricReportDetails(mockMetrics);
    }

    @Test
    void ThrowingCustomExceptionForGetAllMetricReportDetailsByMakingMetricSubmittedEmpty() throws Exception {
        when(metricSubmittedRepository.findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus.SAVE, Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(Collections.emptyList());
        assertThrows(CustomException.class, () ->  metricSubmitService.getMetricReportDetails());
        verify(metricSubmittedRepository).findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus.SAVE, Sort.by(Sort.Order.desc("updatedAt")));

    }
    @Test
    void ThrowingCustomExceptionForGetAllMetricReportDetails() throws Exception {
        when(metricSubmittedRepository.findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus.SAVE, Sort.by(Sort.Order.desc("updatedAt")))).thenThrow(CustomException.class);
        assertThrows(CustomException.class, () ->  metricSubmitService.getMetricReportDetails());
        verify(metricSubmittedRepository).findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus.SAVE, Sort.by(Sort.Order.desc("updatedAt")));

    }
    List<MetricSubmitted> populateAssessmentList(){
        Account account=new Account();
        account.setId(1L);
        Project project=new Project();
        project.setId(1L);
        ProjectType projectType=new ProjectType();
        projectType.setId(1L);
        MetricSubmitted assessmentObj=new MetricSubmitted();
        assessmentObj.setAccount(account);
        assessmentObj.setProject(project);
        assessmentObj.setProjectType(projectType);
        assessmentObj.setSubmitStatus(AssessmentStatus.SUBMITTED);

        MetricSubmitted assessmentObj1=new MetricSubmitted();
        assessmentObj1.setAccount(account);
        assessmentObj1.setProject(project);
        assessmentObj1.setProjectType(projectType);
        assessmentObj1.setSubmitStatus(AssessmentStatus.REVIEWED);

        List<MetricSubmitted> assessmentList=new ArrayList<>();
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
        when(metricSubmittedRepository.findByAccountId(1L)).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=metricSubmitService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
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
        when(metricSubmittedRepository.findByProjectIds(filterValues)).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=metricSubmitService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        assertEquals(populatePiechartDashboardDto(),result);

    }
    @Test
    void testPiechartDataWithFiltersPT(){
        Filters filterName=Filters.PT;
        String filterValue="1";
        when(metricSubmittedRepository.findByProjectTypeId(1L)).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=metricSubmitService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        assertEquals(populatePiechartDashboardDto(),result);

    }
    @Test
    void testPiechartDataWithFiltersALL(){
        Filters filterName=null;
        String filterValue=null;
        when(metricSubmittedRepository.findBySubmitStatusIn(List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED))).thenReturn(populateAssessmentList());
        PiechartDashboardDto result=metricSubmitService.calculatePercentageForPieChartWithFilters(filterName,filterValue);
        assertEquals(populatePiechartDashboardDto(),result);

    }
    @Test
    void testGetLineChartDataByStartAndEndDatesWithProjectFilter() {
        Long startDate = 1609459200000L;
        Long endDate = 1612137600000L;
        String filterName = ServiceConstants.PROJECT;
        String filterValue = "123";

        List<LineChartProjection> expectedList = new ArrayList<>();
        LineChartProjection lineChartProjection1=new LineChartProjection();
        lineChartProjection1.setId("123");
        LineChartProjection lineChartProjection2=new LineChartProjection();
        lineChartProjection2.setId("456");
        expectedList.add(lineChartProjection1);
        expectedList.add(lineChartProjection2);

        when(metricSubmittedRepository.submitMetricLineChartStartAndEndDatesAndProjectId(
                anyLong(), anyLong(), anyList(),anyList())).thenReturn(expectedList);

        List<LineChartProjection> actualList =
                metricSubmitService.submitMetricLineChartStartAndEndDates(startDate, endDate, filterName, filterValue);

        assertEquals(expectedList, actualList);
        verify(metricSubmittedRepository).submitMetricLineChartStartAndEndDatesAndProjectId(
                anyLong(), anyLong(), anyList(),anyList());
    }

    @Test
    void testGetLineChartDataByStartAndEndDatesWithAccountFilter() {
        Long startDate = 1609459200000L;
        Long endDate = 1612137600000L;
        String filterName = ServiceConstants.ACCOUNT;
        String filterValue = "123";

        List<LineChartProjection> expectedList = new ArrayList<>();
        LineChartProjection lineChartProjection1=new LineChartProjection();
        lineChartProjection1.setId("123");
        LineChartProjection lineChartProjection2=new LineChartProjection();
        lineChartProjection2.setId("456");
        expectedList.add(lineChartProjection1);
        expectedList.add(lineChartProjection2);

        when(metricSubmittedRepository.submitMetricLineChartStartAndEndDatesAndAccountId(
                anyLong(), anyLong(), anyLong(),anyList())).thenReturn(expectedList);

        List<LineChartProjection> actualList =
                metricSubmitService.submitMetricLineChartStartAndEndDates(startDate, endDate, filterName, filterValue);

        assertEquals(expectedList, actualList);
        verify(metricSubmittedRepository).submitMetricLineChartStartAndEndDatesAndAccountId(
                anyLong(), anyLong(), anyLong(),anyList());
    }
    @Test
    void testGetLineChartDataByStartAndEndDatesWithProjectTypeFilter() {
        Long startDate = 1609459200000L;
        Long endDate = 1612137600000L;
        String filterName = ServiceConstants.PROJECT_TYPE;
        String filterValue = "123";

        List<LineChartProjection> expectedList = new ArrayList<>();
        LineChartProjection lineChartProjection1=new LineChartProjection();
        lineChartProjection1.setId("123");
        LineChartProjection lineChartProjection2=new LineChartProjection();
        lineChartProjection2.setId("456");
        expectedList.add(lineChartProjection1);
        expectedList.add(lineChartProjection2);

        when(metricSubmittedRepository.submitMetricLineChartStartAndEndDatesProjectTypeId(
                anyLong(), anyLong(), anyLong(),anyList())).thenReturn(expectedList);

        List<LineChartProjection> actualList =
                metricSubmitService.submitMetricLineChartStartAndEndDates(startDate, endDate, filterName, filterValue);

        assertEquals(expectedList, actualList);
        verify(metricSubmittedRepository).submitMetricLineChartStartAndEndDatesProjectTypeId(
                anyLong(), anyLong(), anyLong(),anyList());
    }
    @Test
    void testGetAssessmentsByReviewerIdSuccess() {
        String reviewerId = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        MetricSubmitted metricSubmitted1=new MetricSubmitted();
        metricSubmitted1.setId(1L);
        metricSubmitted1.setSubmitterName("darshan");
        MetricSubmitted metricSubmitted2 =new MetricSubmitted();
        metricSubmitted2.setId(2L);
        metricSubmitted2.setSubmitterName("ravi");
        List<MetricSubmitted> metricSubmittedList = Arrays.asList(metricSubmitted1,metricSubmitted2);
        when(metricSubmittedRepository.findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(AssessmentStatus.REVIEWED,reviewerId)).thenReturn(metricSubmittedList);
        List<MetricSubmitted> result = metricSubmitService.getReviewedMetricsForReviewer(reviewerId);

        assertEquals(metricSubmittedList, result);
        assertEquals(metricSubmitted1.getSubmitterName(),result.get(0).getSubmitterName());
    }
    @Test
    void testGetAssessmentsByReviewerIdFail() {
        String reviewerId = null;
        CustomException exception = Assertions.assertThrows(CustomException.class, () -> {
            metricSubmitService.getReviewedMetricsForReviewer(reviewerId);});

        assertEquals("Invalid reviewerId Id : " + reviewerId, exception.getMessage());
    }
    @Test
    void testGetAssessmentsByReviewerIdNotFound() {
        String reviewerId = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        when(metricSubmittedRepository.findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(AssessmentStatus.REVIEWED,reviewerId))
                .thenReturn(Collections.emptyList());
        MetricNotFoundException exception =
                Assertions.assertThrows(
                        MetricNotFoundException.class,
                        () -> {
                            metricSubmitService.getReviewedMetricsForReviewer(reviewerId);
                        });
    }
    
    @Test
    public void testInactiveMetricById() {
        Long metricId = 1L;
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setId(metricId);
        metricSubmitted.setSubmitStatus(AssessmentStatus.SUBMITTED);

        Optional<MetricSubmitted> optionalMetricSubmitted = Optional.of(metricSubmitted);
        when(metricSubmittedRepository.findById(metricId)).thenReturn(optionalMetricSubmitted);
        MetricSubmitted result = metricSubmitService.inactiveMetricById(metricId);
        assertEquals(AssessmentStatus.INACTIVE, result.getSubmitStatus());
        assertEquals(false, result.getIsFrequencyRequired());
    }

    @Test
    public void testInactiveMetricById_MetricNotFound() {

        Long metricId = 1L;
        when(metricSubmittedRepository.findById(metricId)).thenReturn(Optional.empty());
        assertThrows(MetricNotFoundException.class, () -> {
            metricSubmitService.inactiveMetricById(metricId);
        });
    }
    
    @Test
    void testInactiveMetricByIdFail() {
    	Long metricId = 1L;
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setId(metricId);
        metricSubmitted.setSubmitStatus(AssessmentStatus.SAVE);

        Optional<MetricSubmitted> optionalMetricSubmitted = Optional.of(metricSubmitted);
        when(metricSubmittedRepository.findById(metricId)).thenReturn(optionalMetricSubmitted);
        CustomException exception = assertThrows(CustomException.class, () -> {
        	metricSubmitService.inactiveMetricById(metricId);});

        assertEquals("Metric not in REVIEWED/SUBMITTED status, can not be Deleted", exception.getMessage());
    }
}
