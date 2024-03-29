package com.maveric.digital.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.responsedto.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.model.Assessment;
import com.maveric.digital.service.AssessmentService;
import com.maveric.digital.service.ConversationService;

@WebMvcTest(AssessmentController.class)
@ExtendWith(SpringExtension.class)
class AssessmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AssessmentController assessmentController;
    @MockBean
    private ConversationService conversationService;
    @MockBean
    private AssessmentService assessmentService;
    private AssessmentDto assessmentDto;
    @Autowired
    private ObjectMapper modelMapper;
    private File jsonFile;
    private File saveReviewerCommentjsonFile;
    private ReviewerCommentDto reviewerCommentDto;
    @BeforeEach
    void intial() throws IOException {

        jsonFile = new ClassPathResource("sample/AssessmentDto.json").getFile();
        assessmentDto = modelMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        saveReviewerCommentjsonFile= new ClassPathResource("sample/saveReviewerComment-request.json").getFile();
        reviewerCommentDto = modelMapper.readValue(Files.readString(saveReviewerCommentjsonFile.toPath()), ReviewerCommentDto.class);
    }

    @Test
    void testGetAssessmentByIdSuccess() throws Exception {
        Long assessmentId = 1L;
        Assessment assessment = new Assessment();
        assessment.setId(assessmentId);

        when(conversationService.toAssessmentDto(assessmentService.findAssessmentById(anyLong())))
                .thenReturn(assessmentDto);

        ResponseEntity<AssessmentDto> response = assessmentController.getAssessmentById(assessmentId);

        mockMvc.perform(get("/v1/assessment/1").contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(Files.readString(jsonFile.toPath()))).andReturn();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assessmentDto, response.getBody());
    }

    @Test
    void testGetAssessmentByIdNotFound() {
        Long assessmentId = 1L;

        when(assessmentService.findAssessmentById(assessmentId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment with ID " + assessmentId + " not found"));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> assessmentController.getAssessmentById(assessmentId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    void testGetAllAssessmentsSuccess() {

        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        when(assessmentService.findAllAssessments()).thenReturn(assessments);

        ResponseEntity<List<AssessmentDto>> response = assessmentController.getAllAssessments();
        List<AssessmentDto> actualAssessments = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(actualAssessments, response.getBody());

    }
    @Test
    void testGetAssessmentsBySubmitedByEmptyAssessments() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentSubmitedByDto> assessments = new ArrayList<>();
        when(conversationService.toAssessmentSubmitedByDtos(assessmentService.getAssessmentsBySubmitedBy(submittedBy))).thenReturn(assessments);
        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController.getAssessmentsBySubmitedBy(submittedBy);
        when(assessmentService.getAssessmentsBySubmitedBy(submittedBy))
                .thenThrow(new ResponseStatusException(HttpStatus.NO_CONTENT, "No Content Found"));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                assessmentController.getAssessmentsBySubmitedBy(submittedBy));
        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
    }

    @Test
    void testGetAllSubmittedAssessmentsSuccess() {

        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        when(assessmentService.getAllSubmittedAssessments()).thenReturn(assessments);

        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController.getAllSubmittedAssessments();
        List<AssessmentSubmitedByDto> actualAssessments = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(actualAssessments, response.getBody());

    }

    @Test
    void testGetAllSubmittedAssessmentsNoContent() {

        List<Assessment> assessments = new ArrayList<>();

        when(assessmentService.getAllSubmittedAssessments()).thenReturn(assessments);

        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController.getAllSubmittedAssessments();
        assertNotNull(response);

        when(assessmentService.findAllAssessments())
                .thenThrow(new ResponseStatusException(HttpStatus.NO_CONTENT, "No Content Found"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                assessmentController.getAllAssessments());

        assertEquals(HttpStatus.NO_CONTENT,exception.getStatusCode());

    }
    @Test
    void testGetAllAssessmentsNoContent() {
        List<Assessment> assessments = new ArrayList<>();

        when(assessmentService.findAllAssessments()).thenReturn(assessments);

        ResponseEntity<List<AssessmentDto>> response = assessmentController.getAllAssessments();
        assertNotNull(response);

        when(assessmentService.findAllAssessments())
                .thenThrow(new ResponseStatusException(HttpStatus.NO_CONTENT, "No Content Found"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                assessmentController.getAllAssessments());

        assertEquals(HttpStatus.NO_CONTENT,exception.getStatusCode());
    }


    @Test
    void testGetAssessmentsBySubmitedBySuccess() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentSubmitedByDto> assessments = new ArrayList<>();
        assessments.add(new AssessmentSubmitedByDto());
        assessments.add(new AssessmentSubmitedByDto());

        when(conversationService.toAssessmentSubmitedByDtos(assessmentService.getAssessmentsBySubmitedBy(submittedBy))).thenReturn(assessments);

        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController
                .getAssessmentsBySubmitedBy(submittedBy);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assessments, response.getBody());
    }



    @Test
    void testGetAssessmentsBySubmittedByAndStatusSuccess() throws Exception {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        String submitStatus = "SUBMITTED";

        List<Assessment> assessments = new ArrayList<>();

        when(assessmentService.getAssessmentsBySubmittedByAndStatus(submittedBy, submitStatus))
                .thenReturn(assessments);

        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController
                .getAssessmentsBySubmittedByAndStatus(submittedBy,submitStatus);

        mockMvc.perform(get("/v1/assessment/submittedBy/{submittedBy}/{submitStatus}", submittedBy, submitStatus))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assessments, response.getBody());
    }

    // @Test
    void testGetAssessmentsBySubmitedByNoContent() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<Assessment> assessments = new ArrayList<>();

        when(assessmentService.getAssessmentsBySubmitedBy(submittedBy)).thenReturn(assessments);

        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController.getAssessmentsBySubmitedBy(submittedBy);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    void testSaveOrSubmitAssessment() throws IOException, Exception {
        Long assessmentId = 1L;
        Assessment assessment = new Assessment();
        assessment.setId(assessmentId);

        when(conversationService.toAssessmentDto(assessmentService.findAssessmentById(anyLong())))
                .thenReturn(assessmentDto);

        ResponseEntity<AssessmentDto> response = assessmentController.saveOrSubmitAssessment(assessmentDto);

        mockMvc.perform(post("/v1/assessment/saveOrSubmit").content(Files.readString(jsonFile.toPath())).contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(Files.readString(jsonFile.toPath()))).andReturn();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assessmentDto, response.getBody());
    }

    @Test
    void testSaveReviewerComment() throws IOException, Exception {
        Long assessmentId = 1L;
        Assessment assessment = new Assessment();
        assessment.setId(assessmentId);

        when(conversationService.toAssessmentDto(assessmentService.saveReviewerComment(reviewerCommentDto)))
                .thenReturn(assessmentDto);

        ResponseEntity<AssessmentDto> response = assessmentController.saveReviewerComment(reviewerCommentDto);

        mockMvc.perform(post("/v1/assessment/reviewerComment/save").content(Files.readString(jsonFile.toPath())).contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(Files.readString(jsonFile.toPath()))).andReturn();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assessmentDto, response.getBody());
    }

    @Test
    void testGetLineChartDataByStartAndEndDates() throws Exception {

        List<LineChartDto> lineChartDtos=new ArrayList<>();
        LineChartDto lineChartDto=new LineChartDto();
        lineChartDtos.add(lineChartDto);
        when(conversationService.toLineChartDtos( anyList())).thenReturn(lineChartDtos);

        mockMvc.perform(get("/v1/assessment/lineChart/startDate/{startDate}/endDate/{endDate}", anyLong(), anyLong(),anyString(),anyString()))
                .andExpect(status().isOk()).andExpect(content().contentType("application/json")).andReturn();


    }

    @Test
    void testGetFiveAssessmentsBySubmittedBySuccess() throws Exception {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";

        List<Assessment> assessments = new ArrayList<>();

        when(assessmentService.findLastFiveAssessments(submittedBy))
                .thenReturn(assessments);

        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController
                .getLastFiveAssessments(submittedBy);

        mockMvc.perform(get("/v1/assessment/lastfive/{submittedBy}", submittedBy))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assessments, response.getBody());
    }

    @Test
    void testGetLast5AssessmentsInvalidInput() throws Exception {
        String invalidSubmittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";

        List<Assessment> assessments = new ArrayList<>();

        when(assessmentService.findLastFiveAssessments(invalidSubmittedBy)).thenThrow( IllegalArgumentException.class);
        mockMvc.perform(get("/v1/assessment/lastfive/{submittedBy}", invalidSubmittedBy))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }

    @Test
    void testGetLast5AssessmentsInvalidInput1() throws Exception {
        String invalidSubmittedBy = null;

        List<Assessment> assessments = new ArrayList<>();

        when(assessmentService.findLastFiveAssessments(invalidSubmittedBy)).thenThrow( IllegalArgumentException.class);
        mockMvc.perform(get("/v1/assessment/lastfive/{submittedBy}", invalidSubmittedBy))
                .andExpect(status().isNotFound())
                .andReturn();

    }
    @Test
    void testGetAssessmentsByStatusListAndSubmittedBy() throws Exception {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentStatus> assessmentStatusList = new ArrayList<>();
        assessmentStatusList.add(AssessmentStatus.REVIEWED);
        assessmentStatusList.add(AssessmentStatus.SUBMITTED);

        List<Assessment> assessments = new ArrayList<>();
        when(assessmentService.getAssessmentsByStatus(assessmentStatusList, submittedBy)).thenReturn(assessments);

        ResponseEntity<List<AssessmentSubmitedByDto>> response = assessmentController
                .getAssessmentsByStatus(assessmentStatusList, submittedBy);

        mockMvc.perform(get("/v1/assessment/assessmentStatusList")
                        .param("assessmentStatusList", "REVIEWED,SUBMITTED")
                        .param("submittedBy", String.valueOf(submittedBy))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assessments, response.getBody());
    }
    @Test
    void testGetAssessmentsByStatusListAndSubmittedByIllegalArgs() throws Exception {
        String submittedBy = null;
        List<AssessmentStatus> assessmentStatusList = new ArrayList<>();
        assessmentStatusList.add(AssessmentStatus.REVIEWED);
        assessmentStatusList.add(AssessmentStatus.SUBMITTED);

        List<Assessment> assessments = new ArrayList<>();
        when(assessmentService.getAssessmentsByStatus(assessmentStatusList, submittedBy)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get("/v1/assessment/assessmentStatusList")
                        .param("assessmentStatusList", "REVIEWED,SUBMITTED")
                        .param("submittedBy", submittedBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
    @Test
    void testGetAssessmentsByStatusListAndSubmittedByInvalidStatus() throws Exception {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentStatus> assessmentStatusList = new ArrayList<>();
        List<Assessment> assessments = new ArrayList<>();
        when(assessmentService.getAssessmentsByStatus(assessmentStatusList, submittedBy)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get("/v1/assessment/assessmentStatusList")
                        .param("assessmentStatusList", "SAVED")
                        .param("submittedBy", String.valueOf(submittedBy))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }




    @Test
    void getTop10AssessmentsForDashboardTest() throws Exception {
        List<AssessmentsSubmittedDashboardDto> assessments =new ArrayList<>();
        assessments.add(new AssessmentsSubmittedDashboardDto());
        assessments.add(new AssessmentsSubmittedDashboardDto());

        when(conversationService.toAssessmentsSubmittedDashboardDtos(assessmentService.getTop10AssessmentsForDashboard())).thenReturn(assessments);
        ResponseEntity<List<AssessmentsSubmittedDashboardDto>> response = assessmentController.
                getTop10AssessmentsForDashboard();
        mockMvc.perform(get("/v1/assessment/Top10Assessmentsfordashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
    @Test
    void getTop10AssessmentsForDashboardNoAssessmentsFound() throws Exception {
        List<AssessmentsSubmittedDashboardDto> assessments =new ArrayList<>();
        when(conversationService.toAssessmentsSubmittedDashboardDtos(assessmentService.getTop10AssessmentsForDashboard())).thenThrow(AssessmentNotFoundException.class);
        mockMvc.perform(get("/v1/assessment/Top10Assessmentsfordashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }

    @Test
    void getAllAssessmentsDetails() throws Exception {
        List<MetricAndAssessmentDetailsDto> metricAndAssessmentDetailsDtos=new ArrayList<>();
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto=new MetricAndAssessmentDetailsDto();
        metricAndAssessmentDetailsDto.setReviewerName("RAM");
        metricAndAssessmentDetailsDto.setId(1L);
        Mockito.when(this.assessmentService.getAllAssessmentsDetails("111")).thenReturn(metricAndAssessmentDetailsDtos);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/assessment/details/submittedBy/{submittedBy}","111").
        contentType(MediaType.APPLICATION_JSON).content(
                this.asJsonString(metricAndAssessmentDetailsDtos))).andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(metricAndAssessmentDetailsDtos.size())));


    }

    @Test
    void ThrowingCustomExceptionForgetAllAssessmentsDetails() throws Exception {
        List<MetricAndAssessmentDetailsDto> metricAndAssessmentDetailsDtos = new ArrayList<>();
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto = new MetricAndAssessmentDetailsDto();
        metricAndAssessmentDetailsDto.setReviewerName("RAM");
        metricAndAssessmentDetailsDto.setId(1L);
        Mockito.when(this.assessmentService.getAllAssessmentsDetails("111")).thenThrow(CustomException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/assessment/details/submittedBy/{submittedBy}","111").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(metricAndAssessmentDetailsDtos)))
                .andExpect(status().isOk()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
                });


    }

    @Test
    void getAllPendingReviewAssessments() throws Exception {
        String userId="a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentReviewDto> assessments =new ArrayList<>();
        assessments.add(new AssessmentReviewDto());
        assessments.add(new AssessmentReviewDto());

        when(conversationService.toAssessmentReviewDtos(assessmentService.getAllPendingReviewAssessments(anyString()))).thenReturn(assessments);
        ResponseEntity<List<AssessmentReviewDto>> response = assessmentController.
            getAllPendingReviewAssessments(userId);
        mockMvc.perform(get("/v1/assessment/pendingReview/all").param("userId",userId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllPendingReviewAssessmentsWithNoAssessmentsFound() throws Exception {
        String userId="a2f876d4-9269-11ee-b9d1-0242ac120002";
        when(conversationService.toAssessmentReviewDtos(assessmentService.getAllPendingReviewAssessments(anyString()))).thenThrow(AssessmentNotFoundException.class);
        mockMvc.perform(get("/v1/assessment/pendingReview/all").param("userId",userId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andReturn();
    }
  
    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }


    @Test
  void getAssessmentReportDetails() throws Exception {
        List<MetricAndAssessmentReportDetails> metricReportDetails = new ArrayList<>();
        MetricAndAssessmentReportDetails details = new MetricAndAssessmentReportDetails();
        details.setId(1111L);
        details.setTemplateName("T1");
        metricReportDetails.add(details);
        when(conversationService.toReportDetailsDtos(anyList())).thenReturn(metricReportDetails);
        mockMvc.perform(get("/v1/assessment/report/details")).andExpect(status().isOk()).andExpect(content().contentType("application/json")).andReturn();
    }

    @Test
    void ThrowingCustomExceptionForGetGetAssessmentReportDetails() throws Exception {
        when(conversationService.toReportDetailsDtos(assessmentService.assessmentReport())).thenThrow(CustomException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/assessment/report/details").
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
                });
    }
    @Test
    void testGetCountOfUserResponse() throws Exception {

        when(assessmentService.getCountOfUserResponse(Mockito.<Long>any(), Mockito.<Long>any(), Mockito.<Integer>any()))
                .thenReturn(new HashMap<>());

        mockMvc.perform(get("/v1/count/{templateId}/{projectId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }
    @Test
    void testTop10AssessmentFilters() throws Exception {
        Filters filterName= Filters.PR;
        String filterValue="1";
        List<AssessmentsSubmittedDashboardDto> assessments =new ArrayList<>();
        assessments.add(new AssessmentsSubmittedDashboardDto());
        assessments.add(new AssessmentsSubmittedDashboardDto());

        when(conversationService.toAssessmentsSubmittedDashboardDtos(assessmentService.getTop10AssessmentsForDashboardFilters(filterName,filterValue))).thenReturn(assessments);
        mockMvc.perform(get("/v1/assessment/Top10Assessmentsfordashboard/Filters").param("filterName", String.valueOf(filterName)).param("filterValue",filterValue))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }
    @Test
    void getReviewedAssessmentsTest() throws Exception {
        String reviewerId = "reviewer123";
        List<MetricAndAssessmentDetailsDto> mockResponse = Arrays.asList(new MetricAndAssessmentDetailsDto(), new MetricAndAssessmentDetailsDto());
        when(conversationService.toMetricAndAssessmentDetailsDto(assessmentService.getReviewedAssessmentsForReviewer(reviewerId))).thenReturn(mockResponse);
        mockMvc.perform(get("/v1/assessment/reviewed/{reviewerId}", reviewerId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    @Test
    public void testInactiveAssessmentById() throws IOException, Exception {
        Long assessmentId = 1L;
        AssessmentDto mockAssessmentDto = new AssessmentDto();
        mockAssessmentDto.setAssessmentId(assessmentId);
        when(assessmentService.inactiveAssessmentById(assessmentId)).thenReturn(new Assessment());
        when(conversationService.toAssessmentDto(ArgumentMatchers.any())).thenReturn(mockAssessmentDto);
        MockHttpServletRequestBuilder requestBuilder = put("/v1/assessment/submitStatus/{id}", assessmentId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assessmentId").value(assessmentId));
    }
    

}