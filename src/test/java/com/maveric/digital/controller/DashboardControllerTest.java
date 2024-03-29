package com.maveric.digital.controller;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.responsedto.AssessmentsDashboardDto;
import com.maveric.digital.responsedto.PiechartDashboardDto;
import com.maveric.digital.service.DashboardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DashboardController.class)
@ExtendWith(SpringExtension.class)
class DashboardControllerTest {
    @MockBean
    private DashboardServiceImpl dashboardService;
    @Autowired
    private DashboardController dashboardController;
    @Autowired
    private MockMvc mockMvc;
    @Test
     void testGetAssessmentConsolidatedDataFound() throws Exception {
        AssessmentsDashboardDto mockAssessmentDto = new AssessmentsDashboardDto(5, 3,  1, 10,1);
        when(dashboardService.getAssessmentsConsolidatedDataWithFilter(null,null)).thenReturn(mockAssessmentDto);
        mockMvc.perform(get("/v1/consolidated-assessmentsdata")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjects").value(10))
                .andExpect(jsonPath("$.totalAssessmentSubmissions").value(5))
                .andExpect(jsonPath("$.totalAssessmentReviewed").value(3))
                .andExpect(jsonPath("$.totalAccounts").value(1))
                .andExpect(jsonPath("$.totalMetrics").value(1));
        ;

    }
    @Test
    void calculatePercentageForPieCharttest() throws Exception {
        PiechartDashboardDto piechartDashboardDto=new PiechartDashboardDto("33%",1,"33%",1, "Unit");
        when(dashboardService.calculatePercentageForPieChart()).thenReturn(piechartDashboardDto);
        ResponseEntity<PiechartDashboardDto> response=dashboardController.calculatePercentageForPieChart();
        mockMvc.perform(get("/v1/dashboard/piechartData")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void calculatePercentageForPieChartNoMetricsTest() throws Exception {
        when(dashboardService.calculatePercentageForPieChart()).thenThrow(AssessmentNotFoundException.class);
        mockMvc.perform(get("/v1/dashboard/piechartData")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
    @Test
    void testCalculatePercentageForPieChartWithValidFilters() throws Exception {
        Filters filterName = Filters.AC;
        String filterValue = "someValue";
        PiechartDashboardDto mockDto = new PiechartDashboardDto();
        mockDto.setSubmittedCount(10);
        mockDto.setReviewedCount(5);
        when(dashboardService.calculatePercentageForPieChartWithFilters(filterName, filterValue)).thenReturn(mockDto);
        mockMvc.perform(get("/v1/dashboard/piechartData/filters")
                .param("filterName", filterName.toString())
                .param("filterValue", filterValue))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.submittedCount").value("10"));

        verify(dashboardService).calculatePercentageForPieChartWithFilters(filterName, filterValue);
    }
}