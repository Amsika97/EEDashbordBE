package com.maveric.digital.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.embedded.SubmissionHistory;
import com.maveric.digital.responsedto.AssessmentDto;
import com.maveric.digital.responsedto.MetricSubmittedDto;
import com.maveric.digital.service.SubmissionHistoryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({SubmissionHistoryController.class})
@ExtendWith({SpringExtension.class})
class SubmissionHistoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SubmissionHistoryService submissionHistoryService;

    SubmissionHistory submissionHistory;
    File jsonFile;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SubmissionHistoryController submissionHistoryController;

    @BeforeEach
    private void insertData() {
        submissionHistory = new SubmissionHistory();
        submissionHistory.setSubmitedBy("a2f876d4-9269-11ee-b9d1-0242ac120002");
    }

    @Test
    void getSubmissionHistory() throws Exception {
        List<SubmissionHistory> submissionHistories = List.of(this.submissionHistory);
        when(submissionHistoryService.getSubmissionHistory(anyString())).thenReturn(submissionHistories);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/submission/history").param("submissionFilterRequest", "{\"all\":\"all\",\"submittedBy\":56,\"fromDate\":1699468200000,\"toDate\":1699554600000}").contentType(MediaType.APPLICATION_JSON).
                        content(this.asJsonString(submissionHistories)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(submissionHistories.size())));
        Mockito.verify(this.submissionHistoryService).getSubmissionHistory(anyString());

    }

    @Test
    void ThrowingCustomExceptionForGetSubmissionHistory() throws Exception {
        when(submissionHistoryService.getSubmissionHistory(anyString())).thenThrow(CustomException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/submission/history").param("submissionFilterRequest", "{\"all\":\"all\",\"submittedBy\":56,\"fromDate\":1699468200000,\"toDate\":1699554600000}").contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.submissionHistory))).andExpect(status().isOk()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
        });
        Mockito.verify(this.submissionHistoryService).getSubmissionHistory(anyString());

    }


    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    /*@Test
    void editSubmittedAssessment() throws Exception {
        Long id=1L;
        jsonFile = new ClassPathResource("sample/AssessmentDto.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        when(submissionHistoryService.editSubmittedAssessments(anyLong())).thenReturn(assessmentDto);
        ResponseEntity<AssessmentDto> response = submissionHistoryController.editSubmittedAssessments(id);
        mockMvc.perform(put("/v1/edit/submitted/assessment/{id}",id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void editSubmittedMetric() throws Exception {
        Long id=1L;
        jsonFile = new ClassPathResource("sample/MetricSubmittedDto.json").getFile();
        MetricSubmittedDto metricSubmittedDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), MetricSubmittedDto.class);
        when(submissionHistoryService.editSubmittedMetrics(anyLong())).thenReturn(metricSubmittedDto);
        ResponseEntity<MetricSubmittedDto> response = submissionHistoryController.editSubmittedMetrics(id);
        mockMvc.perform(put("/v1/edit/submitted/metric/{id}",id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/



}
