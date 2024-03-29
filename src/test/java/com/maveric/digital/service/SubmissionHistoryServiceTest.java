package com.maveric.digital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.SubmissionFilterDto;
import com.maveric.digital.model.Template;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.SubmissionHistory;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.responsedto.AssessmentDto;
import com.maveric.digital.responsedto.MetricSubmittedDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLOutput;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class SubmissionHistoryServiceTest {
    @MockBean
    private ConversationService conversationService;
    @Autowired
    private SubmissionHistoryService submissionHistoryService;
    @MockBean
    private AssessmentRepository assessmentRepository;
    @MockBean
    private MetricSubmittedRepository metricSubmittedRepository;
    @MockBean
    private MetricConversationService metricConversationService;
    private SubmissionHistory submissionHistory;

    private Assessment assessment;
    File jsonFile;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new ClassPathResource("sample/AssessmentDto.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        assessment = new Assessment();
        BeanUtils.copyProperties(assessmentDto, assessment);
        Template template = new Template();
        template.setId(1L);
        template.setTemplateName("T1");
        assessment.setTemplate(template);
        submissionHistory = new SubmissionHistory();
        submissionHistory.setSubmitedBy("a2f876d4-9269-11ee-b9d1-0242ac120002");
        submissionHistory.setSubmitedAt(1692618576L);
        submissionHistory.setTemplateName("T1");
    }

    @Test
    void getSubmissionHistoryWithSubmittedByAndFromDateAndToDate() throws Exception {
        List<SubmissionHistory> submissionHistories = List.of(this.submissionHistory);
        List<Assessment> assessments = List.of(assessment);
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        submissionFilterDto.setSubmittedBy("a2f876d4-9269-11ee-b9d1-0242ac120002");
        submissionFilterDto.setFromDate(1692618576L);
        submissionFilterDto.setToDate(2L);
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findBySubmitedByAndUpdatedAtBetweenOrderByUpdatedAtDesc(anyString(), anyLong(), anyLong())).thenReturn(assessments);
        List<SubmissionHistory> histories = submissionHistoryService.getSubmissionHistory("{\"submittedBy\":56,\"fromDate\":1,\"toDate\":1699554600000}");
        Assertions.assertEquals(submissionHistories, histories);
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findBySubmitedByAndUpdatedAtBetweenOrderByUpdatedAtDesc(anyString(), anyLong(), anyLong());


    }

    @Test
    void ThrowingCustomExceptionForgetSubmissionHistoryWithSubmittedByAndFromDateAndToDate() throws Exception {
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        submissionFilterDto.setSubmittedBy("a2f876d4-9269-11ee-b9d1-0242ac120002");
        submissionFilterDto.setFromDate(1692618576L);
        submissionFilterDto.setToDate(2L);
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findBySubmitedByAndUpdatedAtBetweenOrderByUpdatedAtDesc(anyString(), anyLong(), anyLong())).thenReturn(Collections.emptyList());
        Assertions.assertThrows(CustomException.class, () ->
                submissionHistoryService.getSubmissionHistory("{\"submittedBy\":\"a2f876d4-9269-11ee-b9d1-0242ac120002\",\"fromDate\":1,\"toDate\":1699554600000}"));
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findBySubmitedByAndUpdatedAtBetweenOrderByUpdatedAtDesc(anyString(), anyLong(), anyLong());

    }


    @Test
    void getSubmissionHistoryWithSubmittedBy() throws Exception {
        List<SubmissionHistory> submissionHistories = List.of(this.submissionHistory);
        List<Assessment> assessments = List.of(assessment);
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        submissionFilterDto.setSubmittedBy("a2f876d4-9269-11ee-b9d1-0242ac120002");
        submissionFilterDto.setFromDate(0L);
        submissionFilterDto.setToDate(0L);
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findBySubmitedByOrderByUpdatedAtDesc(anyString())).thenReturn(assessments);
//        when(assessmentRepository.findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(assessments);
        
        List<SubmissionHistory> histories = submissionHistoryService.getSubmissionHistory("{\"submittedBy\":\"a2f876d4-9269-11ee-b9d1-0242ac120002\"}");
        Assertions.assertEquals(submissionHistories, histories);
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findBySubmitedByOrderByUpdatedAtDesc(anyString());
//        Mockito.verify(this.assessmentRepository).findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")));

    }

    @Test
    void ThrowingCustomExceptionForSubmissionHistoryWithSubmittedBy() throws Exception {
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        submissionFilterDto.setSubmittedBy("a2f876d4-9269-11ee-b9d1-0242ac120002");
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findBySubmitedByOrderByUpdatedAtDesc(anyString())).thenReturn(Collections.emptyList());
        Assertions.assertThrows(CustomException.class, () ->
                submissionHistoryService.getSubmissionHistory("{\"submittedBy\":a2f876d4-9269-11ee-b9d1-0242ac120002,\"fromDate\":1,\"toDate\":1699554600000}"));
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findBySubmitedByOrderByUpdatedAtDesc(anyString());

    }

    @Test
    void getSubmissionHistoryWithFromDateAndToDate() throws Exception {
        List<SubmissionHistory> submissionHistories = List.of(this.submissionHistory);
        List<Assessment> assessments = List.of(assessment);
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        submissionFilterDto.setFromDate(1692618576L);
        submissionFilterDto.setToDate(2L);
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findByUpdatedAtBetweenOrderByUpdatedAtDesc(anyLong(), anyLong())).thenReturn(assessments);
        List<SubmissionHistory> histories = submissionHistoryService.getSubmissionHistory("{fromDate\":1,\"toDate\":1699554600000}");
        Assertions.assertEquals(submissionHistories, histories);
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findByUpdatedAtBetweenOrderByUpdatedAtDesc(anyLong(), anyLong());


    }

    @Test
    void ThrowingCustomExceptionForgetSubmissionHistoryWithFromDateAndToDate() throws Exception {
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        submissionFilterDto.setSubmittedBy("");
        submissionFilterDto.setFromDate(1692618576L);
        submissionFilterDto.setToDate(2L);
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findByUpdatedAtBetweenOrderByUpdatedAtDesc(anyLong(), anyLong())).thenReturn(Collections.emptyList());
        Assertions.assertThrows(CustomException.class, () ->
                submissionHistoryService.getSubmissionHistory("{\"submittedBy\":\"a2f876d4-9269-11ee-b9d1-0242ac120002\",\"fromDate\":1,\"toDate\":1699554600000}"));
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findByUpdatedAtBetweenOrderByUpdatedAtDesc(anyLong(), anyLong());

    }


    @Test
    void getSubmissionHistoryForAll() throws Exception {
        List<SubmissionHistory> submissionHistories = List.of(this.submissionHistory);
        List<Assessment> assessments = List.of(assessment);
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        submissionFilterDto.setSubmittedBy("");
        submissionFilterDto.setFromDate(0L);
        submissionFilterDto.setToDate(0L);
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(assessments);
        List<SubmissionHistory> histories = submissionHistoryService.getSubmissionHistory("");
        Assertions.assertEquals(submissionHistories, histories);
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")));


    }

    @Test
    void ThrowingCustomExceptionForGetSubmissionHistoryForAll() throws Exception {
        SubmissionFilterDto submissionFilterDto = new SubmissionFilterDto();
        when(conversationService.toSubmissionFilterDtoFromJsonString(anyString())).thenReturn(submissionFilterDto);
        when(assessmentRepository.findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(Collections.emptyList());
        Assertions.assertThrows(CustomException.class, () ->
                submissionHistoryService.getSubmissionHistory("{\"submittedBy\":\"a2f876d4-9269-11ee-b9d1-0242ac120002\",\"fromDate\":1,\"toDate\":1699554600000}"));
        Mockito.verify(this.conversationService).toSubmissionFilterDtoFromJsonString(anyString());
        Mockito.verify(this.assessmentRepository).findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")));

    }
 /*   @Test
    void editSubmittedAssessment() throws IOException {
        jsonFile = new ClassPathResource("sample/AssessmentDto.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        assessmentDto.setScore(45);
        Assessment assessment=new Assessment();
        BeanUtils.copyProperties(assessmentDto,assessment);
        assessment.setSubmitStatus(AssessmentStatus.SUBMITTED);
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.of(assessment));
        assessmentDto.setSubmitedAt(null);
        assessmentDto.setScore(0);
        when(conversationService.toAssessmentDto(assessment)).thenReturn(assessmentDto);
        AssessmentDto response= submissionHistoryService.editSubmittedAssessments(anyLong());
        Assertions.assertEquals(0, response.getScore());
        Assertions.assertEquals(null, response.getSubmitedAt());

    }
    @Test
    void editSubmittedAssessmentNotfound() {
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.empty());
        AssessmentNotFoundException exception = assertThrows(AssessmentNotFoundException.class, () -> {
            submissionHistoryService.editSubmittedAssessments(anyLong());
        });
        assertEquals("Assessment not found",exception.getMessage());
    }
    @Test
    void editSubmittedMetric() throws IOException {
        jsonFile = new ClassPathResource("sample/MetricSubmittedDto.json").getFile();
        MetricSubmittedDto metricSubmittedDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), MetricSubmittedDto.class);
        metricSubmittedDto.setScore(45);
        MetricSubmitted metricSubmitted=new MetricSubmitted();
        BeanUtils.copyProperties(metricSubmittedDto,metricSubmitted);
        metricSubmitted.setSubmitStatus(AssessmentStatus.SUBMITTED);
        when(metricSubmittedRepository.findById(anyLong())).thenReturn(Optional.of(metricSubmitted));
        metricSubmittedDto.setSubmittedAt(null);
        metricSubmittedDto.setScore(0);
        when(metricConversationService.toMetricSubmitDto(metricSubmitted)).thenReturn(metricSubmittedDto);
        MetricSubmittedDto response= submissionHistoryService.editSubmittedMetrics(anyLong());
        Assertions.assertEquals(0, response.getScore());
        Assertions.assertEquals(null, response.getSubmittedAt());

    }
    @Test
    void editSubmittedMetricNotfound() {
        when(metricSubmittedRepository.findById(anyLong())).thenReturn(Optional.empty());
        MetricNotFoundException exception = assertThrows(MetricNotFoundException.class, () -> {
            submissionHistoryService.editSubmittedMetrics(anyLong());
        });
        assertEquals("Metric Not Found",exception.getMessage());
    }*/


}
