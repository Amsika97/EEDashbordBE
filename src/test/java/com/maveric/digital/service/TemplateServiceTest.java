//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.maveric.digital.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.maveric.digital.model.*;
import com.maveric.digital.repository.*;
import com.maveric.digital.responsedto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.ScoreCategoryNotFoundException;
import com.maveric.digital.exceptions.ScoreScaleNotFoundException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.model.embedded.ProjectCategory;
import com.maveric.digital.model.embedded.TemplateQuestionnaire;

import jakarta.validation.ConstraintViolationException;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class TemplateServiceTest {
    @MockBean
    TemplateRepository templateRepository;
    @Autowired
    TemplateService templateService;

    @MockBean
    ScoreScaleRepository scoreScaleRepository;
    @MockBean
    ScoreCategoryRepository scoreCategoryRepository;
    private TemplateDto templateDto;
    private TemplateSaveRequestDto templateSaveRequestDto;
    private Template template;
    @Autowired
    private ModelMapper modelMapper;
    @MockBean
    private ConversationService conversationService;
    private TemplateInfo templateInfo;
    private String templateName;

    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private ProjectTypeRepository projectTypeRepository;
    private final static String TEMPLATE_NAME="T1";


    TemplateServiceTest() {
    }

    @BeforeEach
    public void insertData() {
        ScoreScaleDto scoreScaleDto = new ScoreScaleDto(111L, "score","singleValue",1L, new ArrayList());
        List<ScoreCategoryDto> scoreCategories=List.of(scoreCategoryDto());
        this.templateDto = TemplateDto.builder().scoreScale(scoreScaleDto).templateName(TEMPLATE_NAME).projectType("Digital").
                scoreCategories(scoreCategories).
                projectCategory(List.of(this.populateProjectPhase())).build();
        this.templateSaveRequestDto = TemplateSaveRequestDto.builder().templateName("Type-c").scoreScaleId(111L)
                .templateData(
                        "{\r\n\t\r\n\t\"projectCategory\":[\r\n\t{\r\n\t\t\"categoryName\":\"intial\",\r\n\t\t\"templateQuestionnaire\": [\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"satisfy\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t}\r\n\t]\r\n\t\t\r\n\t},\r\n\t{\r\n\t\t\"categoryName\":\"Testing phase\",\r\n\t\t\"templateQuestionnaire\": [\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"satisfy\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t}\r\n\t]\r\n\t\t\r\n\t}\r\n\t]\r\n\t\r\n\r\n  \r\n}")
                .businessUnits(List.of(1L))
                .projects(List.of(1L))
                .projectTypes(List.of(1L)).build();
        this.template = this.toTemplate(this.templateDto);

        this.templateInfo = new TemplateInfo(1L, "internal",1,true);
        this.templateName=templateDto.getTemplateName();

        template.setProjects(List.of(getProject()));
        template.setProjectTypes(List.of(projectType()));
    }

    private Project getProject() {
        Project project
                = new Project();
        project.setId(1L);
        project.setProjectName("P-1");
        return project;
    }

    private ProjectType projectType(){
        ProjectType projectType=new ProjectType();
        projectType.setId(1L);
        projectType.setProjectTypeName("PT-1");
        return projectType;
    }
    private ProjectCategory populateProjectPhase() {
        ProjectCategory projectCategory = new ProjectCategory();
        projectCategory.setCategoryName("Intial Phase");
        projectCategory.setTemplateQuestionnaire(Collections.singletonList(this.populateTemplateQuestionnaire()));
        return projectCategory;
    }




    private TemplateQuestionnaire populateTemplateQuestionnaire() {
        TemplateQuestionnaire questionnaire = new TemplateQuestionnaire();
        questionnaire.setFieldType("radio");
        questionnaire.setQuestion("How well-defined are the project objectives?");
        questionnaire.setScoreCategory("importance");
        return questionnaire;
    }
    private ScoreCategoryDto scoreCategoryDto() {
        ScoreCategoryDto scoreCategory = new ScoreCategoryDto();
        scoreCategory.setCategoryName("importance");
        scoreCategory.setCategoryId(1l);

        return scoreCategory;
    }
    @Test
    void creatingTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.scoreScaleRepository.findById(111L)).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(template.getScoreCategories());
        Mockito.when(this.templateRepository.save(any(Template.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        TemplateDto templateDto = this.templateService.createTemplate(this.templateSaveRequestDto);
        Assertions.assertNotNull(templateDto);
        Assertions.assertEquals(templateDto, this.templateDto);
        Mockito.verify(this.conversationService, times(1)).toTemplate(any(TemplateDto.class));
      //  Mockito.verify(this.scoreScaleRepository).findById(this.template.getScore().getId());
        Mockito.verify(this.scoreCategoryRepository).findByCategoryNameIn(any(List.class));
        Mockito.verify(this.templateRepository).save(any(Template.class));
        Mockito.verify(this.conversationService).toTemplateDto(this.template);
    }
    @Test
    void updateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.scoreScaleRepository.findById(111L)).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(template.getScoreCategories());
        Mockito.when(this.templateRepository.save(any(Template.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        doNothing().when(templateRepository).deActivateTemplate(anyLong(), anyBoolean());
        TemplateDto templateDto = this.templateService.updateTemplate(this.templateSaveRequestDto);
        Assertions.assertNotNull(templateDto);
        Assertions.assertEquals(templateDto, this.templateDto);
        Mockito.verify(this.conversationService, times(1)).toTemplate(any(TemplateDto.class));
  //      Mockito.verify(this.scoreScaleRepository).findById(this.template.getScore().getId());
        Mockito.verify(this.scoreCategoryRepository).findByCategoryNameIn(any(List.class));
        Mockito.verify(this.templateRepository).save(any(Template.class));
        Mockito.verify(this.conversationService).toTemplateDto(this.template);
    }
    @Test
    void ThrowingConstraintViolationExceptionForUpdateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(this.template.getScoreCategories());
        Mockito.when(this.templateRepository.save(any(Template.class))).thenThrow(ConstraintViolationException.class);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        doNothing().when(templateRepository).deActivateTemplate(anyLong(), anyBoolean());
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.templateService.updateTemplate(this.templateSaveRequestDto);
        });
   //     Mockito.verify(this.scoreScaleRepository).findById(this.template.getScore().getId());
        Mockito.verify(this.scoreCategoryRepository).findByCategoryNameIn(any(List.class));
        Mockito.verify(this.templateRepository).save(any(Template.class));
    }

    //@Test
    void ThrowingScoreScaleNotFoundExceptionForUpdateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.empty());
		/*
		 * Assertions.assertThrows(ScoreScaleNotFoundException.class, () -> {
		 * this.templateService.updateTemplate(this.templateSaveRequestDto); });
		 */
        Mockito.verify(this.scoreScaleRepository).findById(this.template.getScore().getId());
    }
  //  @Test
    void ThrowingCustomExceptionForCreateTemplateByMakingBusinessUnitIdsEmpty() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(Collections.emptyList());
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());

        Assertions.assertThrows(CustomException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
    }
  //  @Test
    void ThrowingCustomExceptionForCreateTemplateByMakingProjectIdsEmpty() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(Collections.emptyList());
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(List.of());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());

        Assertions.assertThrows(CustomException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
    }
    @Test
    void ThrowingCustomExceptionForCreateTemplateByMakingProjectTypeIdsEmpty() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(Collections.emptyList());
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(List.of());

        Assertions.assertThrows(CustomException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
    }

 //   @Test
    void ThrowingCustomExceptionForUpdateTemplateByMakingProjectIdsEmpty() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(Collections.emptyList());
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(List.of());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());

        Assertions.assertThrows(CustomException.class, () -> {
            this.templateService.updateTemplate(this.templateSaveRequestDto);
        });
    }
   // @Test
    void ThrowingCustomExceptionForUpdateTemplateByMakingProjectTypeIdsEmpty() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(Collections.emptyList());
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(List.of());

        Assertions.assertThrows(CustomException.class, () -> {
            this.templateService.updateTemplate(this.templateSaveRequestDto);
        });
    }
    @Test
    void ThrowingScoreCategoryNotFoundExceptionForUpdateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(templateInfo);

        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(Collections.emptyList());
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());

        Assertions.assertThrows(ScoreCategoryNotFoundException.class, () -> {
            this.templateService.updateTemplate(this.templateSaveRequestDto);
        });
    }

    @Test
    void ThrowingTemplateNotFoundExceptionForUpdateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        when(templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(anyString())).thenReturn(null);
        Assertions.assertThrows(TemplateNotFoundException.class, () -> {
            this.templateService.updateTemplate(this.templateSaveRequestDto);
        });
    }



    @Test
    void ThrowingConstraintViolationExceptionForCreateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(this.template.getScoreCategories());
        Mockito.when(this.templateRepository.save(any(Template.class))).thenThrow(ConstraintViolationException.class);
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
 //       Mockito.verify(this.scoreScaleRepository).findById(this.template.getScore().getId());
        Mockito.verify(this.scoreCategoryRepository).findByCategoryNameIn(any(List.class));
        Mockito.verify(this.templateRepository).save(any(Template.class));
    }

  //  @Test
    void ThrowingScoreScaleNotFoundExceptionForCreateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ScoreScaleNotFoundException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
        Mockito.verify(this.scoreScaleRepository).findById(this.template.getScore().getId());
    }

    @Test
    void ThrowingDataIntegrityViolationExceptionForCreateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(this.template.getScoreCategories());
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        Mockito.when(this.templateRepository.save(any(Template.class))).thenThrow(DataIntegrityViolationException.class);
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
    }
    @Test
    void ThrowingResourceCreationExceptionForCreateTemplate() {
        when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(this.template.getScoreCategories());
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        when(this.templateRepository.save(any(Template.class))).thenThrow(ResourceCreationException.class);
        Assertions.assertThrows(ResourceCreationException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
    }
    @Test
    void ThrowingScoreCategoryNotFoundExceptionForCreateTemplate() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.scoreScaleRepository.findById(this.template.getScore().getId())).thenReturn(Optional.of(this.template.getScore()));
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(Collections.emptyList());
        when(projectRepository.findByIdIn(anyList())).thenReturn(template.getProjects());
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        Mockito.when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        Assertions.assertThrows(ScoreCategoryNotFoundException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
    }
    @Test
    void ThrowingCustomExceptionForCreateTemplateTemplateNameAlreadyExisted() throws JsonProcessingException {
        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        Mockito.when(this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData()))).thenReturn(this.template);
        Mockito.when(this.templateRepository.findTemplateByTemplateNameAndIsActiveTrue(any(String.class))).thenReturn(template) ;
        Assertions.assertThrows(CustomException.class, () -> {
            this.templateService.createTemplate(this.templateSaveRequestDto);
        });
    }

    @Test
    void getTemplateByTemplateName() {
        when(this.templateRepository.findTemplateByTemplateNameAndIsActiveTrue(any(String.class))).thenReturn(this.template);
        when(this.conversationService.toTemplateDto(any(Template.class))).thenReturn(this.templateDto);
        TemplateDto dto = this.templateService.getTemplateByTemplateName(this.templateDto.getTemplateName());
        Assertions.assertEquals(this.templateDto, dto);
    }
    @Test
    void ThrowingTemplateNotFoundExceptionTestForGetTemplateByTemplateName() {
        when(this.templateRepository.findTemplateByTemplateNameAndIsActiveTrue(any(String.class))).thenReturn( null);
        Assertions.assertThrows(TemplateNotFoundException.class, () ->templateService.getTemplateByTemplateName(templateName)
        );
    }



    @Test
    void getTemplatesByProjectTypeTest() {
        List<Template> templateList = List.of(this.template);
        List<TemplateDto> templateDtoList = List.of(this.templateDto);
        Mockito.when(this.templateRepository.findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class))).thenReturn(templateList);
        Mockito.when(this.conversationService.toTemplateDtos(templateList)).thenReturn(templateDtoList);
        List<TemplateDto> templateDtos = this.templateService.getTemplatesByProjectType(any(Long.class));
        Assertions.assertEquals(templateDtoList, templateDtos);
    }

    @Test
    void ThrowingTemplateNotFoundExceptionForGetTemplatesByProjectType() {
        Mockito.when(this.templateRepository.findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class))).thenReturn( null);
        Assertions.assertThrows(TemplateNotFoundException.class, () ->
                this.templateService.getTemplatesByProjectType(any(Long.class)));
    }

    @Test
    void getTemplateInfoByProjectTypeTest() {
        List<Template> templateList = List.of(this.template);
        List<TemplateInfo> templateInfo = List.of(this.templateInfo);
        List<TemplateInfo> templateInfoList=new ArrayList<>(templateInfo);
        Mockito.when(this.templateRepository.findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class))).thenReturn(templateList);
        Mockito.when(conversationService.toTemplateInfo(templateList)).thenReturn(templateInfo);
        List<TemplateInfo> templateDtos = this.templateService.getTemplateInfoByProjectType(any(Long.class));
        Assertions.assertEquals(templateInfoList, templateDtos);
    }

    @Test
    void ThrowingTemplateNotFoundExceptionForGetTemplateInfoByProjectType() {
        List<Template> templateList=new ArrayList<>();
        Mockito.when(this.templateRepository.findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class))).thenReturn( templateList);
        Assertions.assertThrows(TemplateNotFoundException.class, () ->
                templateService.getTemplateInfoByProjectType(any(Long.class))
        );
    }

    @Test
    void getTemplateByIdTest() {
        when(this.templateRepository.findById(any(Long.class))).thenReturn(Optional.of(template));
        when(this.conversationService.toTemplateDto(this.template)).thenReturn(this.templateDto);
        TemplateDto dto = this.templateService.getTemplateById(111L);
        Assertions.assertEquals(templateDto, dto);
        Mockito.verify(this.templateRepository).findById(any(Long.class));
        Mockito.verify(this.conversationService).toTemplateDto(this.template);


    }

    @Test
    void ThrowingTemplateNotFoundExceptionByMakingTemplateEmptyForGettingTemplateById() {
        when(this.templateRepository.findById(any(Long.class))).thenReturn( Optional.empty());
        Assertions.assertThrows(TemplateNotFoundException.class, () ->
                templateService.getTemplateById(111L)
        );
        Mockito.verify(this.templateRepository).findById(any(Long.class));
    }
    @Test
    void ThrowingTemplateNotFoundExceptionForGettingTemplateById() {
        when(this.templateRepository.findById(any(Long.class))).thenThrow( TemplateNotFoundException.class);
        Assertions.assertThrows(TemplateNotFoundException.class, () ->
                templateService.getTemplateById(111L)
        );
        Mockito.verify(this.templateRepository).findById(any(Long.class));
    }

    @Test
    void getTemplateInfoBySelectedFilters() {
        List<TemplateInfo> templateInfoList = List.of(this.templateInfo);
        Mockito.when(this.templateRepository.findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class),any(Long.class))).thenReturn(templateInfoList);
        List<TemplateInfo> templateDtos = this.templateService.getTemplateInfoBySelectedFilters(any(Long.class),any(Long.class));
        Assertions.assertEquals(templateInfoList, templateDtos);
        Mockito.verify(this.templateRepository).findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class),any(Long.class));

    }
    @Test
    void ThrowingTemplateNotFoundExceptionByMakingDBDataNullForgetTemplateInfoBySelectedFilters() {
        Mockito.when(this.templateRepository.findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class),any(Long.class))).thenReturn( null);
        Assertions.assertThrows(TemplateNotFoundException.class, () -> {
            templateService.getTemplateInfoBySelectedFilters( 111L, 111L);
        } );
        Mockito.verify(this.templateRepository).findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class),any(Long.class));
    }
    @Test
    void ThrowingTemplateNotFoundExceptionByMakingDBDataEmptyForgetTemplateInfoBySelectedFilters() {
        Mockito.when(this.templateRepository.findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class),any(Long.class))).thenReturn( Collections.emptyList());
        Assertions.assertThrows(TemplateNotFoundException.class, () -> {
            templateService.getTemplateInfoBySelectedFilters( 111L, 111L);
        } );
        Mockito.verify(this.templateRepository).findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(any(Long.class),any(Long.class));
    }


    public Template toTemplate(TemplateDto templateDto) {
        return this.modelMapper.map(templateDto, Template.class);
    }
    @Test
    void testGetAllTemplateInfos() {
        Sort sortByCreatedAtDesc = Sort.by(Sort.Order.desc("createdAt"));
        when(templateRepository.findByIsActiveTrue(sortByCreatedAtDesc)).thenReturn(List.of(template));
        List<Template> templateList=templateService.getAllTemplateInfos();
        assertEquals(templateList.size(),Integer.valueOf(1));
        assertEquals(TEMPLATE_NAME,templateList.get(0).getTemplateName());
    }
    @Test
    void discardTemplateTest() {
        when(this.templateRepository.findById(eq(1L))).thenReturn(Optional.of(template));
        when(this.conversationService.toTemplateInfo(this.template)).thenReturn(this.templateInfo);
        TemplateInfo dto = this.templateService.discardTemplate(false,1L);
        Assertions.assertTrue(dto.getIsActive());
        Assertions.assertEquals(templateInfo, dto);
        verify(this.templateRepository).findById(any(Long.class));
        verify(this.conversationService).toTemplateInfo(this.template);
    }

    @Test
    void discardTemplate_TemplateNotFound() {
        when(this.templateRepository.findById(any(Long.class))).thenReturn( Optional.empty());
        Assertions.assertThrows(TemplateNotFoundException.class, () ->
                templateService.discardTemplate(true,1L)
        );
        verify(this.templateRepository).findById(any(Long.class));
    }

    @Test
    void testGetAssessmentTemplates() {
        Template template1=new Template();
        template1.setId(1L);
        template1.setTemplateName("T1");
        Template template2 =new Template();
        template2.setId(2L);
        template2.setTemplateName("T2");
        List<Template> mockTemplates = Arrays.asList(template1,template2);
        Sort sortByCreatedAtDesc = Sort.by(Sort.Order.desc("createdAt"));
        when(templateRepository.findAll(sortByCreatedAtDesc)).thenReturn(mockTemplates);
        List<AssessmentTemplateDto> expectedDtos = Arrays.asList(new AssessmentTemplateDto(), new AssessmentTemplateDto());
        when(conversationService.toAssessmentTemplateDtos(mockTemplates)).thenReturn(expectedDtos);
        List<AssessmentTemplateDto> result = templateService.getAssessmentTemplates();
        verify(conversationService).toAssessmentTemplateDtos(mockTemplates);
        assertEquals(expectedDtos, result);
        assertEquals(expectedDtos.size(),result.size());
    }

    @Test
    void testGetAssessmentTemplatesThrowsException() {
        Mockito.when(templateRepository.findAll()).thenReturn(Collections.emptyList());
        TemplateNotFoundException templateNotFoundException=assertThrows(TemplateNotFoundException.class,
            ()-> templateService.getAssessmentTemplates());

        assertEquals("Template not found",templateNotFoundException.getMessage());
    }
    @Test
     void testCreatePreview() throws JsonProcessingException {

        when(conversationService.toTemplateDtoFromJsonString(Mockito.any())).thenReturn(templateDto);
        when(conversationService.toTemplate(Mockito.any())).thenReturn(template);
        when(conversationService.toTemplateDto(Mockito.any())).thenReturn(templateDto);
        ScoringScale scoringScale = createMockScoringScale();
        Optional<ScoringScale> ofResult = Optional.of(scoringScale);
        when(scoreScaleRepository.findById(Mockito.any())).thenReturn(ofResult);
        when( projectTypeRepository.findByIdIn(anyList())).thenReturn(template.getProjectTypes());
        Mockito.when(this.scoreCategoryRepository.findByCategoryNameIn(any(List.class))).thenReturn(template.getScoreCategories());
        TemplateDto resultDto = templateService.createPreview(templateSaveRequestDto);
        verify(conversationService).toTemplate(Mockito.any());
        verify(conversationService).toTemplateDtoFromJsonString(Mockito.any());
        verify(conversationService).toTemplateDto(Mockito.any());

    }

    @Test
     void testCreatePreviewCustomException() throws JsonProcessingException {

        Mockito.when(conversationService.toTemplateDtoFromJsonString(templateSaveRequestDto.getTemplateData())).thenReturn(this.templateDto);
        Mockito.when(this.conversationService.toTemplate(any(TemplateDto.class))).thenReturn(this.template);
        when(conversationService.toTemplateDtoFromJsonString(Mockito.any())).thenReturn(templateDto);
        ScoringScale scoringScale = createMockScoringScale();
        Optional<ScoringScale> ofResult = Optional.of(scoringScale);
        when(scoreScaleRepository.findById(Mockito.any())).thenReturn(ofResult);
        when(projectTypeRepository.findByIdIn(Mockito.any()))
                .thenThrow(new CustomException("An error occurred", HttpStatus.CONTINUE));
        assertThrows(CustomException.class, () -> templateService.createPreview(new TemplateSaveRequestDto()));
        verify(conversationService).toTemplate(Mockito.any());
        verify(conversationService).toTemplateDtoFromJsonString(Mockito.any());
     //   verify(scoreScaleRepository).findById(Mockito.any());
        verify(projectTypeRepository).findByIdIn(Mockito.any());
    }
    @Test
     void testCreatePreviewResourceCreationException() throws JsonProcessingException {
        when(conversationService.toTemplate(any())).thenReturn(template);
        when(conversationService.toTemplateDtoFromJsonString(Mockito.any())).thenReturn(templateDto);
        ScoringScale scoringScale = createMockScoringScale();
        Optional<ScoringScale> ofResult = Optional.of(scoringScale);
        when(scoreScaleRepository.findById(any())).thenReturn(ofResult);
        when(projectTypeRepository.findByIdIn(any()))
                .thenThrow(new ResourceCreationException("An error occurred"));
        assertThrows(ResourceCreationException.class,
                () -> templateService.createPreview(new TemplateSaveRequestDto()));
        verify(conversationService).toTemplate(any());
        verify(conversationService).toTemplateDtoFromJsonString(any());
        //verify(scoreScaleRepository).findById(any());
        verify(projectTypeRepository).findByIdIn(any());
    }

    private ScoringScale createMockScoringScale() {
        ScoringScale score = new ScoringScale();
        score.setComment("Comment");
        score.setCreatedAt(1L);
        score.setCreatedBy("createdby");
        score.setCreatedUserId(1L);
        score.setId(1L);
        score.setName("Name");
        score.setRange(new ArrayList<>());
        score.setScoreScaleType("Score Scale Type");
        score.setUpdatedAt(1L);
        return score;
    }

}

