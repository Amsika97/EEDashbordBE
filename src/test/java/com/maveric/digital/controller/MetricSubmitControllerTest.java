package com.maveric.digital.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.responsedto.*;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.MetricConversationService;
import com.maveric.digital.service.MetricSubmitService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricSubmitController.class)
@ExtendWith(SpringExtension.class)
class MetricSubmitControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MetricSubmitController metricSubmitController;
    @MockBean
    private MetricSubmitService metricSubmitService;
    @Autowired
    ObjectMapper objectMapper;
    private MetricSubmittedDto metricSubmittedDto;
    private File jsonFile;
    @MockBean
    private MetricConversationService metricConversationService;

    @MockBean
    private ConversationService conversationService;

    @BeforeEach
    void intial() throws IOException {
        objectMapper = new ObjectMapper();
        jsonFile = new ClassPathResource("sample/MetricSubmittedDto.json").getFile();
        metricSubmittedDto = objectMapper.readValue(Files.readString(jsonFile.toPath()),
            MetricSubmittedDto.class);
    }

    @Test
    void calculatePercentageForMetricDashboardPieCharttest() throws Exception {
        PiechartDashboardDto piechartDashboardDto = new PiechartDashboardDto();
        piechartDashboardDto.setSubmitted("33.33%");
        piechartDashboardDto.setReviewed("33.33%");

        when(metricSubmitService.calculatePercentageForMetricDashboardPieChart()).thenReturn(
            piechartDashboardDto);
        ResponseEntity<PiechartDashboardDto> response = metricSubmitController.calculatePercentageForMetricDashboardPieChart();
        mockMvc.perform(get("/v1/metricDashboard/piechartData")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void calculatePercentageForMetricDashboardPieCharttestNoMetric() throws Exception {
        PiechartDashboardDto piechartDashboardDto = new PiechartDashboardDto();
        when(metricSubmitService.calculatePercentageForMetricDashboardPieChart()).thenThrow(
            MetricNotFoundException.class);
        mockMvc.perform(get("/v1/metricDashboard/piechartData")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    }

    @Test
    void submitMetricLineChartStartAndEndDates() throws Exception {

        List<LineChartDto> lineChartDtos=new ArrayList<>();
        LineChartDto lineChartDto=new LineChartDto();
        lineChartDtos.add(lineChartDto);
        when(metricConversationService.tosubmitmetriclinechartdto( anyList())).thenReturn(lineChartDtos);
        mockMvc.perform(
                get("/v1/metric/lineChart/startDate/{startDate}/endDate/{endDate}", anyLong(),
                    anyLong(),anyString(),anyString()))
            .andExpect(status().isOk()).andExpect(content().contentType("application/json"))
            .andReturn();

    }

    @Test
    void getAllMetricDetails() throws Exception {
        List<MetricAndAssessmentDetailsDto> metricAndAssessmentDetailsDtos = new ArrayList<>();
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto = new MetricAndAssessmentDetailsDto();
        metricAndAssessmentDetailsDto.setReviewerName("Ram");
        metricAndAssessmentDetailsDto.setId(1L);
        Mockito.when(this.metricSubmitService.getAllMetricDetails("111"))
            .thenReturn(metricAndAssessmentDetailsDtos);
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/metric/details/submittedBy/{submittedBy}", "111").
                    contentType(MediaType.APPLICATION_JSON).content(
                        this.asJsonString(metricAndAssessmentDetailsDtos)))
            .andExpect(MockMvcResultMatchers.status().isOk()).
            andExpect(jsonPath("$.size()", Matchers.is(metricAndAssessmentDetailsDtos.size())));


    }

    @Test
    void ThrowingCustomExceptionForgetAllMetricDetails() throws Exception {
        List<MetricAndAssessmentDetailsDto> metricAndAssessmentDetailsDtos = new ArrayList<>();
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto = new MetricAndAssessmentDetailsDto();
        metricAndAssessmentDetailsDto.setReviewerName("Ram");
        metricAndAssessmentDetailsDto.setId(1L);
        Mockito.when(this.metricSubmitService.getAllMetricDetails("111"))
            .thenThrow(CustomException.class);
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/metric/details/submittedBy/{submittedBy}", "111").
                    contentType(MediaType.APPLICATION_JSON)
                    .content(this.asJsonString(metricAndAssessmentDetailsDtos)))
            .andExpect(status().isOk()).andExpect((result) -> {
                Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
            });


    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    @Test
    void getMetricIdByIdSuccess() throws Exception {
        Long metricId = 1L;
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        when(metricSubmitService.findMetricById(metricId)).thenReturn(metricSubmitted);
        MetricSubmittedDto metricSubmittedDto = new MetricSubmittedDto();
        when(metricConversationService.toMetricSubmitDto(metricSubmitted)).thenReturn(
            metricSubmittedDto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/metric/{metricId}", metricId).
                contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(metricSubmittedDto)))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getMetricIdByIdNotFound() throws Exception {
        Long metricId = 1L;
        when(metricSubmitService.findMetricById(metricId))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Metric not found"));
        mockMvc.perform(get("/metric/{metricId}", metricId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testSaveMetricReviewerComment() throws Exception {

        MetricReviewerCommentDto requestPayload = new MetricReviewerCommentDto();
        MetricSubmittedDto metricSubmittedDto = new MetricSubmittedDto();
        when(metricSubmitService.saveMetricReviewerComment(requestPayload)).thenReturn(
            new MetricSubmitted());
        when(metricConversationService.toMetricSubmitDto(any())).thenReturn(metricSubmittedDto);
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/v1/metric/metricreviewercomment/save")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(metricSubmittedDto)));

        verify(metricSubmitService, times(1)).saveMetricReviewerComment(requestPayload);
        verify(metricConversationService, times(1)).toMetricSubmitDto(any());
    }

    @Test
    void testSaveOrSubmitMetric() throws Exception {
        Long metricId = 1L;
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setId(metricId);
        when(metricConversationService.toMetricSubmitDto(
            metricSubmitService.findMetricById(anyLong()))).thenReturn(metricSubmittedDto);
        mockMvc.perform(post("/v1/metric/saveOrSubmit")
                .content(Files.readString(jsonFile.toPath())).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(Files.readString(jsonFile.toPath()))).andReturn();
    }

    @Test
    void getAllPendingReviewMetrics() throws Exception {
        String userId = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<MetricReviewDto> metrics = new ArrayList<>();
        metrics.add(new MetricReviewDto());
        metrics.add(new MetricReviewDto());

        when(metricConversationService.toMetricReviewDtos(
            metricSubmitService.getAllPendingReviewMetrics(anyString()))).thenReturn(metrics);
        ResponseEntity<List<MetricReviewDto>> response = metricSubmitController.
            getAllPendingReviewMetrics(userId);
        mockMvc.perform(get("/v1/metric/pendingReview/all").param("userId", userId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
     }
    @Test
    void getAllPendingReviewMetricsWithNoMetricFound() throws Exception {
        String userId = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        when(metricConversationService.toMetricReviewDtos(
            metricSubmitService.getAllPendingReviewMetrics(anyString()))).thenThrow(
            MetricNotFoundException.class);
        mockMvc.perform(get("/v1/metric/pendingReview/all").param("userId", userId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andReturn();
    }

    @Test
    void testGetTop10MetricForDashboard() throws Exception {
        when(metricSubmitService.getTop10MetricForDashboard()).thenReturn(new ArrayList<>());
        when(conversationService.toAssessmentsSubmittedDashboardDtos(Mockito.<List<Assessment>>any()))
                .thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/metric/Top10Metricfordashboard");
        MockMvcBuilders.standaloneSetup(metricSubmitController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    @Test
    void getAllMetricReportDetails() throws Exception {
        List<MetricAndAssessmentReportDetails> metricReportDetails = new ArrayList<>();
        MetricAndAssessmentReportDetails details = new MetricAndAssessmentReportDetails();
        details.setId(1111L);
        details.setTemplateName("T1");
        metricReportDetails.add(details);
        when(metricSubmitService.getMetricReportDetails()).thenReturn(metricReportDetails);
        mockMvc.perform(get("/v1/metric/report/details"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }

    @Test
    void ThrowingCustomExceptionForGetAllMetricReportDetails() throws Exception {
        when(metricSubmitService.getMetricReportDetails()).thenThrow(CustomException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/metric/report/details").
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
                });
    }
    @Test
    void testCalculatePercentageForPieChartWithSpecificFilter() throws Exception {
        Filters filterName = Filters.AC;
        String filterValue = "testValue";
        PiechartDashboardDto mockDto = new PiechartDashboardDto();
        mockDto.setSubmittedCount(15);
        mockDto.setReviewedCount(8);
        when(metricSubmitService.calculatePercentageForPieChartWithFilters(filterName, filterValue))
            .thenReturn(mockDto);
        mockMvc.perform(get("/v1/metric/piechartData/filters")
                .param("filterName", filterName.toString())
                .param("filterValue", filterValue))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.submittedCount").value("15"));

        verify(metricSubmitService).calculatePercentageForPieChartWithFilters(filterName, filterValue);
    }
    @Test
    void getReviewedMetricsTest() throws Exception {
        String reviewerId = "reviewer123";
        List<MetricAndAssessmentDetailsDto> mockResponse = Arrays.asList(new MetricAndAssessmentDetailsDto(), new MetricAndAssessmentDetailsDto());
        when(metricConversationService.toMetricSubmitted(metricSubmitService.getReviewedMetricsForReviewer(reviewerId))).thenReturn(mockResponse);
        mockMvc.perform(get("/v1/metric/reviewed/{reviewerId}", reviewerId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    @Test
    public void testInactiveMetricById() throws Exception {
        Long metricId = 1L;
        MetricSubmittedDto mockMetricSubmittedDto = new MetricSubmittedDto();
        mockMetricSubmittedDto.setMetricId(metricId);
        when(metricSubmitService.inactiveMetricById(metricId)).thenReturn(new MetricSubmitted());
        when(metricConversationService.toMetricSubmitDto(ArgumentMatchers.any())).thenReturn(mockMetricSubmittedDto);

        MockHttpServletRequestBuilder requestBuilder = put("/v1/metric/submitStatus/{id}", metricId)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.metricId").value(metricId));
        verify(metricSubmitService).inactiveMetricById(metricId);
        verify(metricConversationService).toMetricSubmitDto(ArgumentMatchers.any());
    }
}
