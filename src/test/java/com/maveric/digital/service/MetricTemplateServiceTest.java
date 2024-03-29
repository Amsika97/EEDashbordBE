package com.maveric.digital.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.model.MetricTemplate;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.model.Template;
import com.maveric.digital.model.embedded.MetricProjectCategory;
import com.maveric.digital.model.embedded.MetricTemplateQuestionnaire;
import com.maveric.digital.model.embedded.ProjectCategory;
import com.maveric.digital.model.embedded.TemplateQuestionnaire;
import com.maveric.digital.repository.MetricTemplateRepository;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.responsedto.MetricTemplateDetailsDto;
import com.maveric.digital.responsedto.MetricTemplateDto;

import com.maveric.digital.responsedto.TemplateDto;
import com.maveric.digital.utils.ServiceConstants;
import java.util.*;
import com.maveric.digital.responsedto.MetricTemplateSaveRequestDto;
import com.maveric.digital.responsedto.MetricTemplateInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class MetricTemplateServiceTest {
    @MockBean
    private MetricTemplateRepository metricTemplateRepository;
    @MockBean
    private ProjectTypeRepository projectTypeRepository;
    @Autowired
    private MetricTemplateServiceImpl metricTemplateService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @MockBean
    private MetricConversationService conversationService;
   
   // private MetricTemplate metricTemplate;

    @Test
    void updateMetricTemplateStatus() {
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        metricTemplateDto.setTemplateId(1L);
        metricTemplateDto.setIsActive(Boolean.TRUE);
        MetricTemplate metricTemplate1 = new MetricTemplate();
        metricTemplate1.setId(1L);
        metricTemplate1.setIsActive(Boolean.TRUE);
        when(metricTemplateRepository.findById(eq(1L))).thenReturn(Optional.of(metricTemplate1));
        when(conversationService.toMetricTemplateDto(metricTemplate1)).thenReturn(metricTemplateDto);
        MetricTemplateDto dto = this.metricTemplateService.updateMetricTemplateStatus(1L, true);
        Assertions.assertTrue(dto.getIsActive());
        Assertions.assertEquals(metricTemplateDto, dto);
        verify(this.metricTemplateRepository).findById(any(Long.class));

    }

    @Test
    void updateMetricTemplateStatusMetricNotFoundException() {
        when(metricTemplateRepository.findById(111L)).thenReturn(Optional.empty());
        Assertions.assertThrows(TemplateNotFoundException.class, () ->
                metricTemplateService.updateMetricTemplateStatus(111L, true)
        );
        verify(this.metricTemplateRepository).findById(any(Long.class));
    }

    @Test
    void testGetMetricTemplates() {
        MetricTemplate metricTemplate1 = new MetricTemplate();
        metricTemplate1.setTemplateName("MT1");
        metricTemplate1.setId(1L);
        MetricTemplate metricTemplate2 = new MetricTemplate();
        metricTemplate2.setTemplateName("MT2");
        metricTemplate2.setId(2L);
        List<MetricTemplate> mockTemplates = Arrays.asList(metricTemplate1, metricTemplate2);
        Sort sortByCreatedAtAsc = Sort.by(Sort.Order.desc("createdAt"));
        when(metricTemplateRepository.findAll(sortByCreatedAtAsc)).thenReturn(mockTemplates);
        List<MetricTemplateDetailsDto> expectedDtos = Arrays.asList(new MetricTemplateDetailsDto(), new MetricTemplateDetailsDto());
        when(conversationService.toMetricTemplateDetailsDtos(mockTemplates)).thenReturn(expectedDtos);
        List<MetricTemplateDetailsDto> result = metricTemplateService.getMetricTemplates();
        verify(conversationService).toMetricTemplateDetailsDtos(mockTemplates);
        assertEquals(expectedDtos, result);
        assertEquals(expectedDtos.size(), result.size());
    }

    @Test
    void testGetMetricTemplatesThrowsException() {
        Mockito.when(metricTemplateRepository.findAll()).thenReturn(Collections.emptyList());
        MetricNotFoundException metricNotFoundException = assertThrows(MetricNotFoundException.class,
                () -> metricTemplateService.getMetricTemplates());

        assertEquals("Metric Not Found", metricNotFoundException.getMessage());
    }

    @Test
    void testGetMetricTemplateInfoByProjectTypeId() {
        Long projectTypeId = 1L;
        MetricTemplateInfo metricTemplateInfo = new MetricTemplateInfo(1L,"templatename");
        List<MetricTemplateInfo> metricTemplateInfoList = Collections.singletonList(metricTemplateInfo);

        List<MetricTemplateInfo> metricTemplateInfoFinalList1=new ArrayList<>(metricTemplateInfoList);
        ProjectType projectType=new ProjectType();
        projectType.setId(1L);
        MetricTemplate metricTemplates=new MetricTemplate();
        metricTemplates.setProjectTypes(Collections.singletonList(projectType));
        metricTemplates.setId(1L);
        metricTemplates.setTemplateName("templatename");
        List<MetricTemplate> metricTemplateList=new ArrayList<>();
        metricTemplateList.add(metricTemplates);
        Mockito.when(metricTemplateRepository.findByIsActiveTrueAndProjectTypes(anyLong())).thenReturn(Optional.of(metricTemplateList));
        Optional<List<MetricTemplateInfo>> result = metricTemplateService.getMetricTemplateInfoByProjectTypeId(projectTypeId);
        Assertions.assertEquals(Optional.of(metricTemplateInfoFinalList1), result);
    }

    @Test
    void testGetMetricTemplateInfoByProjectTypeIdWithEmptyResult() {
        List<MetricTemplate> templateList=new ArrayList<>();
        Mockito.when(this.metricTemplateRepository.findByIsActiveTrueAndProjectTypes(any(Long.class))).thenReturn( Optional.of(templateList));
        MetricNotFoundException exception=assertThrows(MetricNotFoundException.class, () ->
                metricTemplateService.getMetricTemplateInfoByProjectTypeId(any(Long.class))
        );
        assertEquals("Metric Not Found", exception.getMessage());

    }

    @Test
    void testGetMetricTemplateInfoByProjectTypeIdWithIllegalArgumentException() {
        Long projectTypeId = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            metricTemplateService.getMetricTemplateInfoByProjectTypeId(projectTypeId);
        });
        assertEquals("ProjectTypeId cannot be null", exception.getMessage());
    }

    @Test
    void getMetricTemplateByIdTest() {
        MetricTemplate metricTemplate = new MetricTemplate();
        metricTemplate.setId(1L);
        metricTemplate.setTemplateName("");
        metricTemplate.setIsActive(true);
        metricTemplate.setVersion(1);
        metricTemplate.setTemplateUploadedUserName("");
        metricTemplate.setCreatedAt(1L);
        metricTemplate.setUpdatedAt(1L);
        metricTemplate.setTemplateUploadedUserId("");
        metricTemplate.setDescription("");
        List<MetricProjectCategory> projectCategory = new ArrayList<>();
        projectCategory.add(new MetricProjectCategory());
        metricTemplate.setProjectCategory(projectCategory);
        List<ProjectType> projectTypes = new ArrayList<>();
        projectTypes.add(new ProjectType());
        metricTemplate.setProjectTypes(projectTypes);

        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        metricTemplateDto.setTemplateName("");
        metricTemplateDto.setIsActive(true);
        metricTemplateDto.setTemplateUploadedUserName("");
        metricTemplateDto.setTemplateUploadedUserId("");
        metricTemplateDto.setDescription("");
        List<MetricProjectCategory> projectCategory1 = new ArrayList<>();
        projectCategory.add(new MetricProjectCategory());
        metricTemplateDto.setProjectCategory(projectCategory1);
        when(metricTemplateRepository.findById(1L)).thenReturn(Optional.of(metricTemplate));
        when(conversationService.toMetricTemplateDto(metricTemplate)).thenReturn(metricTemplateDto);
        MetricTemplateDto result = metricTemplateService.getMetricTemplateById(1L);
        Assertions.assertEquals(metricTemplateDto, result);

    }

    @Test
    void getMetricTemplateByIdTestNoMetrics() {
        MetricTemplate metricTemplate = new MetricTemplate();
        when(metricTemplateRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(CustomException.class, () ->
                metricTemplateService.getMetricTemplateById(111L)
        );
        verify(this.metricTemplateRepository).findById(any(Long.class));
    }

    @Test
    void testPreviewMetricTemplate() throws JsonProcessingException {
        MetricTemplate metricTemplate = new MetricTemplate();
        metricTemplate.setCreatedAt(1L);
        metricTemplate.setDescription("Description");
        metricTemplate.setId(1L);
        metricTemplate.setIsActive(true);
        metricTemplate.setProjectCategory(new ArrayList<>());
        metricTemplate.setProjectTypes(new ArrayList<>());
        metricTemplate.setTemplateName("Template Name");
        metricTemplate.setTemplateUploadedUserId("127-726-7");
        metricTemplate.setTemplateUploadedUserName("name");
        metricTemplate.setUpdatedAt(1L);
        metricTemplate.setVersion(1);
        when(conversationService.toMetricTemplate(Mockito.<MetricTemplateDto>any())).thenReturn(metricTemplate);

        MetricTemplateDto.MetricTemplateDtoBuilder descriptionResult = MetricTemplateDto.builder()
                .description("Description");
        when(conversationService.toMetricTemplateDto(Mockito.<MetricTemplate>any()))
                .thenReturn(descriptionResult.projectCategory(new ArrayList<>())
                        .projectType("Project Type")
                        .templateId(1L)
                        .templateName("Template Name")
                        .templateUploadedUserId("127-726-7")
                        .templateUploadedUserName("name")
                        .build());
        MetricTemplateDto.MetricTemplateDtoBuilder descriptionResult2 = MetricTemplateDto.builder()
                .description("Description");
        when(conversationService.toMetricTemplateDtoFromJsonString(Mockito.<String>any()))
                .thenReturn(descriptionResult2.projectCategory(new ArrayList<>())
                        .projectType("Project Type")
                        .templateId(1L)
                        .templateName("Template Name")
                        .templateUploadedUserId("127-726-7")
                        .templateUploadedUserName("name")
                        .build());
        metricTemplateService.previewMetricTemplate(new MetricTemplateSaveRequestDto());
        verify(conversationService).toMetricTemplate(Mockito.<MetricTemplateDto>any());
        verify(conversationService).toMetricTemplateDto(Mockito.<MetricTemplate>any());
        verify(conversationService).toMetricTemplateDtoFromJsonString(Mockito.<String>any());
    }

    @Test
    void testPreviewMetricTemplate_ResourceCreationException() throws JsonProcessingException {
        when(conversationService.toMetricTemplateDtoFromJsonString(Mockito.<String>any()))
                .thenThrow(ResourceCreationException.class);
        Assertions.assertThrows(ResourceCreationException.class, () -> {
            metricTemplateService.previewMetricTemplate(new MetricTemplateSaveRequestDto());
        });
    }

    @Test
    void testPreviewMetricTemplate_DataIntegrityViolationException() throws JsonProcessingException {
        when(conversationService.toMetricTemplateDtoFromJsonString(Mockito.<String>any()))
                .thenThrow(DataIntegrityViolationException.class);
        when(conversationService.toMetricTemplateDto(Mockito.<MetricTemplate>any()))
                .thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            metricTemplateService.previewMetricTemplate(new MetricTemplateSaveRequestDto());
        });
    }

    @Test
    void testPreviewMetricTemplate_CustomException() throws JsonProcessingException, JsonProcessingException {
        when(conversationService.toMetricTemplateDtoFromJsonString(Mockito.<String>any()))
                .thenThrow(CustomException.class);
        when(conversationService.toMetricTemplateDto(Mockito.<MetricTemplate>any()))
                .thenThrow(CustomException.class);
        Assertions.assertThrows(CustomException.class, () -> {
            metricTemplateService.previewMetricTemplate(new MetricTemplateSaveRequestDto());
        });

    }
    @Test
    void testCreateMetricTemplateSuccess() throws Exception {
        List<Long> projectTypeIds = Arrays.asList(1L, 2L, 3L);
        List<ProjectType> mockProjectTypes = Arrays.asList(new ProjectType(), new ProjectType());
        MetricTemplateSaveRequestDto requestDto = new MetricTemplateSaveRequestDto();
        requestDto.setTemplateData( "{\n\n  \"projectCategory\":[\n    {\n      \"categoryName\":\"intial\",\n      \"categoryDescription\":\"This project category contains intial level question\",\n      \"templateQuestionnaire\": [\n        {\n\n\n          \"question\": \"1. How well-defined are the project objectives? \",\n          \"questionDescription\":\"project Related question\",\n          \"fieldType\":\"radio\",\n          \"valueType\":\"NUMBER\",\n          \"answerData\":[\n            {\n              \"lable\":\"yes\",\n              \"value\":1\n            },\n            {\n              \"lable\":\"No\",\n              \"value\":0\n            }\n          ]\n\n        }\n      ]\n\n    }\n  ]\n}");
        requestDto.setTemplateName("T1");
        requestDto.setDescription(" This Assessment contains testing project related category");
        requestDto.setTemplateUploadedUserName("Ravi");
        requestDto.setTemplateUploadedUserId("1");
        requestDto.setProjectTypes(projectTypeIds);
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        metricTemplateDto.setTemplateName("");
        metricTemplateDto.setIsActive(true);
        metricTemplateDto.setTemplateUploadedUserName("");
        metricTemplateDto.setTemplateUploadedUserId("");
        metricTemplateDto.setDescription("");
        List<MetricProjectCategory> projectCategoryList = List.of(this.populateProjectPhase());
        metricTemplateDto.setProjectCategory(projectCategoryList);

        MetricTemplate metricTemplate = toTemplate(metricTemplateDto);
        when(projectTypeRepository.findByIdIn(projectTypeIds)).thenReturn(mockProjectTypes);
        when(conversationService.toMetricTemplateDtoFromJsonString(requestDto.getTemplateData())).thenReturn(metricTemplateDto);
        when(conversationService.toMetricTemplate(any(MetricTemplateDto.class))).thenReturn(metricTemplate);
        when(metricTemplateRepository.findMetricTemplateByTemplateNameAndIsActiveTrue(anyString())).thenReturn(null);
        when(metricTemplateRepository.save(any(MetricTemplate.class))).thenReturn(metricTemplate);
        when(conversationService.toMetricTemplateDto(metricTemplate)).thenReturn(metricTemplateDto);

        MetricTemplateDto result = metricTemplateService.createMetricTemplate(requestDto);
        Assertions.assertNotNull(result);
        verify(metricTemplateRepository).save(metricTemplate);

    }
    @Test
    void testCreateMetricTemplateDuplicateName() throws JsonProcessingException {
        List<Long> projectTypeIds = Arrays.asList(1L, 2L, 3L);
        MetricTemplateSaveRequestDto requestDto = new MetricTemplateSaveRequestDto();
        requestDto.setTemplateData( "{\n\n  \"projectCategory\":[\n    {\n      \"categoryName\":\"intial\",\n      \"categoryDescription\":\"This project category contains intial level question\",\n      \"templateQuestionnaire\": [\n        {\n\n\n          \"question\": \"1. How well-defined are the project objectives? \",\n          \"questionDescription\":\"project Related question\",\n          \"fieldType\":\"radio\",\n          \"valueType\":\"NUMBER\",\n          \"answerData\":[\n            {\n              \"lable\":\"yes\",\n              \"value\":1\n            },\n            {\n              \"lable\":\"No\",\n              \"value\":0\n            }\n          ]\n\n        }\n      ]\n\n    }\n  ]\n}");
        requestDto.setTemplateName("T1");
        requestDto.setDescription(" This Assessment contains testing project related category");
        requestDto.setTemplateUploadedUserName("Ravi");
        requestDto.setTemplateUploadedUserId("1");
        requestDto.setProjectTypes(projectTypeIds);
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        MetricTemplate existingTemplate = new MetricTemplate();
        existingTemplate.setTemplateName("existingTemplateName");

        when(conversationService.toMetricTemplateDtoFromJsonString(anyString())).thenReturn(metricTemplateDto);
        when(conversationService.toMetricTemplate(any(MetricTemplateDto.class))).thenReturn(new MetricTemplate());
        when(metricTemplateRepository.findMetricTemplateByTemplateNameAndIsActiveTrue(anyString())).thenReturn(existingTemplate);

        assertThrows(CustomException.class, () -> metricTemplateService.createMetricTemplate(requestDto));
        verify(metricTemplateRepository, never()).save(any(MetricTemplate.class));
    }
    @Test
    void testCreateMetricTemplateThrowsException() throws JsonProcessingException {

        MetricTemplateSaveRequestDto requestDto = new MetricTemplateSaveRequestDto();
        MetricTemplateDto metricTemplateDto = new MetricTemplateDto();
        MetricTemplate existingTemplate = new MetricTemplate();

        when(conversationService.toMetricTemplateDtoFromJsonString(anyString())).thenReturn(metricTemplateDto);
        when(conversationService.toMetricTemplate(any(MetricTemplateDto.class))).thenReturn(new MetricTemplate());
        when(metricTemplateRepository.findMetricTemplateByTemplateNameAndIsActiveTrue(anyString())).thenReturn(existingTemplate);

        assertThrows(ResourceCreationException.class, () -> metricTemplateService.createMetricTemplate(requestDto));
        verify(metricTemplateRepository, never()).save(any(MetricTemplate.class));
    }
    public MetricTemplate toTemplate(MetricTemplateDto templateDto) {
        return this.modelMapper.map(templateDto, MetricTemplate.class);
    }
    private MetricTemplateQuestionnaire populateTemplateQuestionnaire() {
        MetricTemplateQuestionnaire questionnaire = new MetricTemplateQuestionnaire();
        questionnaire.setFieldType("radio");
        questionnaire.setQuestion("How well-defined are the project objectives?");
        return questionnaire;
    }
    private MetricProjectCategory populateProjectPhase() {
        MetricProjectCategory projectCategory = new MetricProjectCategory();
        projectCategory.setCategoryName("Intial Phase");
        projectCategory.setTemplateQuestionnaire(Collections.singletonList(this.populateTemplateQuestionnaire()));
        return projectCategory;
    }

}