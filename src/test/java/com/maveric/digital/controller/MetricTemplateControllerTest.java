package com.maveric.digital.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.responsedto.*;
import com.maveric.digital.service.*;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import com.maveric.digital.responsedto.MetricTemplateDetailsDto;
import com.maveric.digital.responsedto.MetricTemplateDto;
import com.maveric.digital.responsedto.MetricTemplateInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricTemplateController.class)
@ExtendWith(SpringExtension.class)
public class MetricTemplateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MetricTemplateController metricTemplateController;
    @MockBean
    private MetricTemplateService metricTemplateService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private MetricConversationService metricConversationService;
    private File jsonFile;
    private MetricTemplateDto metricTemplateDto;
    private MetricTemplateSaveRequestDto metricTemplateSaveRequestDto;

    @BeforeEach
    void initial() throws IOException {

        jsonFile = new ClassPathResource("sample/MetricTemplate_save_dto.json").getFile();
        metricTemplateSaveRequestDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), MetricTemplateSaveRequestDto.class);
        metricTemplateDto=new MetricTemplateDto();
        metricTemplateDto.setProjectType(metricTemplateSaveRequestDto.getProjectTypes().get(0).toString());
        BeanUtils.copyProperties(metricTemplateSaveRequestDto,metricTemplateDto);
    }
    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    @Test
    void updateMetricTemplateStatusTest() throws Exception {
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        metricTemplateDto.setIsActive(Boolean.FALSE);
        metricTemplateDto.setTemplateId(111L);
        Long templateId = 111L;
        Boolean isActive = Boolean.FALSE;
        when(metricTemplateService.updateMetricTemplateStatus(templateId, isActive)).thenReturn(metricTemplateDto);
        ResponseEntity<MetricTemplateDto> response = metricTemplateController.updateMetricTemplateStatus(templateId, isActive);
        this.mockMvc.perform(put("/v1/metric/template/updateMetricTemplateStatus/{templateId}", 111L)
                        .param("isActive", String.valueOf(false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateMetricTemplateStatusTestNoMetricTemplate() throws Exception {
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        Long templateId = 111L;
        Boolean isActive = Boolean.FALSE;
        when(metricTemplateService.updateMetricTemplateStatus(templateId, isActive)).thenThrow(TemplateNotFoundException.class);
        this.mockMvc.perform(put("/v1/metric/template/updateMetricTemplateStatus/{templateId}", 111L)
                        .param("isActive", String.valueOf(false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void getMetricTemplateByIdTest() throws Exception {
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        Long metricTemplateId = 111L;
        when(metricTemplateService.getMetricTemplateById(metricTemplateId)).thenReturn(metricTemplateDto);
        ResponseEntity<MetricTemplateDto> response = metricTemplateController.getMetricTemplateById(metricTemplateId);
        this.mockMvc.perform(get("/v1/metric/templateId/{metricTemplateId}", 111L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getMetricTemplateByIdTestNo() throws Exception {
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        Long metricTemplateId = 111L;
        when(metricTemplateService.getMetricTemplateById(metricTemplateId)).thenThrow(CustomException.class);
        this.mockMvc.perform(get("/v1/metric/templateId/{metricTemplateId}", 111L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }


    @Test
    void previewMetricTemplateTest() throws Exception {

        when(metricTemplateService.previewMetricTemplate(any())).thenReturn(metricTemplateDto);
        this.mockMvc.perform(post("/v1/metric/template/preview")
                        .content(asJsonString(metricTemplateSaveRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.templateName", Matchers.is(this.metricTemplateDto.getTemplateName())))
                .andReturn();
    }

    @Test
    void getAllMetricTemplates() throws Exception {
        List<MetricTemplateDetailsDto> metricTemplates = new ArrayList<>();
        metricTemplates.add(new MetricTemplateDetailsDto());
        metricTemplates.add(new MetricTemplateDetailsDto());
        when(metricTemplateService.getMetricTemplates()).thenReturn(metricTemplates);
        ResponseEntity<List<MetricTemplateDetailsDto>> response = metricTemplateController.getAllMetricTemplates();
        mockMvc.perform(get("/v1/metric/templates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllMetricTemplateWithNoTemplateFound() throws Exception {
        when(metricTemplateService.getMetricTemplates()).thenThrow(MetricNotFoundException.class);
        mockMvc.perform(get("/v1/metric/templates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }

    @Test
    void testGetMetricTemplateInfoByProjectTypeId() throws Exception {
        Long projectTypeId = 1L;
        List<MetricTemplateInfo> mockMetricTemplateInfo = new ArrayList<>();
        MetricTemplateInfo metricTemplateInfo1 = new MetricTemplateInfo(1L, "Template1");
        mockMetricTemplateInfo.add(metricTemplateInfo1);
        when(metricTemplateService.getMetricTemplateInfoByProjectTypeId(anyLong()))
                .thenReturn(Optional.of(mockMetricTemplateInfo));

        mockMvc = MockMvcBuilders.standaloneSetup(metricTemplateController).build();

        ResultActions result = mockMvc.perform(get("/v1/metricTemplate/info/projectTypeId/{projectTypeId}", projectTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].templateId").value(1))
                .andExpect(jsonPath("$[0].templateName").value("Template1"));
    }
    @Test
    void testGetMetricTemplateByProjectTypeIdNoContent() throws Exception {
        Long projectTypeId = 1L;
        when(metricTemplateService.getMetricTemplateInfoByProjectTypeId(projectTypeId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/metricTemplate/info/projectTypeId/" + projectTypeId))
            .andExpect(status().isNoContent());
    }
    @Test
    void testCreateMetricTemplate() throws Exception {
        MetricTemplateSaveRequestDto requestDto = new MetricTemplateSaveRequestDto();
        List<Long> projectTypes= Arrays.asList(1L,2L);
        requestDto.setTemplateName("T1");
        requestDto.setTemplateUploadedUserName("Ravi");
        requestDto.setTemplateUploadedUserId("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        requestDto.setTemplateData("data");
        requestDto.setProjectTypes(projectTypes);
        MetricTemplateDto responseDto = new MetricTemplateDto();

        when(metricTemplateService.createMetricTemplate(any(MetricTemplateSaveRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/metric/template/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

}



