//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.maveric.digital.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maveric.digital.responsedto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.ScoreCategoryNotFoundException;
import com.maveric.digital.exceptions.ScoreScaleNotFoundException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.TemplateService;

import jakarta.validation.ConstraintViolationException;

@WebMvcTest({TemplateController.class})
@ExtendWith({SpringExtension.class})
class TemplateControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    TemplateService templateService;
    @MockBean
    ConversationService conversationService;
    @Autowired
    TemplateController templateController;
    TemplateDto templateDto;
    TemplateInfo templateInfo;
    private TemplateSaveRequestDto templateSaveRequestDto;

    @BeforeEach
    private void insertData() {
        ScoreScaleDto scoreScaleDto = new ScoreScaleDto(111L, "score", "singleValue",1L,new ArrayList());
        List<Long> testList = new ArrayList<>();
        testList.add(1l);
        this.templateDto = TemplateDto.builder().scoreScale(scoreScaleDto).templateName("Type-c").scoreCategories(new ArrayList()).build();

        this.templateInfo = new TemplateInfo(1L, "internal",1,true);
        this.templateSaveRequestDto = TemplateSaveRequestDto.builder().templateName("Type-c").scoreScaleId(111L)
				.templateData(
						"{\r\n\t\r\n\t\"projectCategory\":[\r\n\t{\r\n\t\t\"categoryName\":\"intial\",\r\n\t\t\"templateQuestionnaire\": [\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"satisfy\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t}\r\n\t]\r\n\t\t\r\n\t},\r\n\t{\r\n\t\t\"categoryName\":\"Testing phase\",\r\n\t\t\"templateQuestionnaire\": [\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"satisfy\"\r\n\t\t\t\t},\r\n\t\t\t\t{\r\n\t\t\t\t\t\r\n\t\t\t\t  \r\n\t\t\t\t  \"question\": \"1. How well-defined are the project objectives? \",\r\n\t\t\t\t  \"scoreCategory\":\"importance\"\r\n\t\t\t\t}\r\n\t]\r\n\t\t\r\n\t}\r\n\t]\r\n\t\r\n\r\n  \r\n}")
				.businessUnits(testList)
				.projects(testList)
				.projectTypes(testList)
				.templateUploadedUserId("a2f876d4-9269-11ee-b9d1-0242ac120002")
				.templateUploadedUserName("joe")
				.build();
    }

    @Test
    void creatingTemplate() throws Exception {
        Mockito.when(this.templateService.createTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenReturn(this.templateDto);
        this.mockMvc.perform(post("/v1/template/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.templateName", Matchers.is(this.templateDto.getTemplateName())));
    }

    @Test
    void updatingTemplate() throws Exception {
        Mockito.when(this.templateService.updateTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenReturn(this.templateDto);
        this.mockMvc.perform(post("/v1/template/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.templateName", Matchers.is(this.templateDto.getTemplateName())));
    }

    @Test
    void ThrowingScoreScaleNotFoundExceptionTestForUpdateTemplate() throws Exception {
        Mockito.when(this.templateService.updateTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(ScoreScaleNotFoundException.class);
        this.mockMvc.perform(post("/v1/template/update").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isOk()).andExpect((result) ->
                        Assertions.assertTrue(result.getResolvedException() instanceof ScoreScaleNotFoundException));
    }
  @Test
    void ThrowingTemplateNotFoundExceptionTestForUpdateTemplate() throws Exception {
        Mockito.when(this.templateService.updateTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(TemplateNotFoundException.class);
        this.mockMvc.perform(post("/v1/template/update").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isOk()).andExpect((result) ->
                        Assertions.assertTrue(result.getResolvedException() instanceof TemplateNotFoundException));
    }

    @Test
    void ThrowingDataIntegrityViolationExceptionForUpdateTemplate() throws Exception {
        Mockito.when(this.templateService.updateTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(DataIntegrityViolationException.class);
        this.mockMvc.perform(post("/v1/template/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof DataIntegrityViolationException);
                });
    }


    @Test
    void ThrowingConstraintViolationExceptionForCreateTemplate() throws Exception {
        Mockito.when(this.templateService.createTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(ConstraintViolationException.class);
        this.mockMvc.perform(post("/v1/template/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof ConstraintViolationException);
                });
    }

    @Test
    void ThrowingResourceCreationExceptionForUpdateTemplate() throws Exception {
        Mockito.when(this.templateService.updateTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(ResourceCreationException.class);
        this.mockMvc.perform(post("/v1/template/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof ResourceCreationException);
                });
    }

    @Test
    void ThrowingScoreCategoryNotFoundExceptionForUpdateTemplate() throws Exception {
        Mockito.when(this.templateService.updateTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(ScoreCategoryNotFoundException.class);
        this.mockMvc.perform(post("/v1/template/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isOk()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof ScoreCategoryNotFoundException);
                });
    }

    @Test
    void ThrowingConstraintViolationExceptionForUpdateTemplate() throws Exception {
        Mockito.when(this.templateService.updateTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(ConstraintViolationException.class);
        this.mockMvc.perform(post("/v1/template/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof ConstraintViolationException);
                });
    }
    @Test
    void ThrowingScoreScaleNotFoundExceptionTestForCreateTemplate() throws Exception {
        Mockito.when(this.templateService.createTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(ScoreScaleNotFoundException.class);
        this.mockMvc.perform(post("/v1/template/create").
                contentType(MediaType.APPLICATION_JSON)
                .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isOk()).andExpect((result) ->
            Assertions.assertTrue(result.getResolvedException() instanceof ScoreScaleNotFoundException));
    }

    @Test
    void ThrowingDataIntegrityViolationExceptionForCreateTemplate() throws Exception {
        Mockito.when(this.templateService.createTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(DataIntegrityViolationException.class);
        this.mockMvc.perform(post("/v1/template/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof DataIntegrityViolationException);
        });
    }



 @Test
    void ThrowingDuplicateResourceExceptionForCreateTemplate() throws Exception {
        Mockito.when(this.templateService.createTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(new CustomException("Resource Already Existed for Template name", HttpStatus.INTERNAL_SERVER_ERROR));
        this.mockMvc.perform(post("/v1/template/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
        });
    }

 @Test
    void ThrowingResourceCreationExceptionForCreateTemplate() throws Exception {
        Mockito.when(this.templateService.createTemplate(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenThrow(ResourceCreationException.class);
        this.mockMvc.perform(post("/v1/template/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof ResourceCreationException);
        });
    }





    @Test
    void getTemplateByTemplateName() throws Exception {
        Mockito.when(this.templateService.getTemplateByTemplateName(ArgumentMatchers.any(String.class))).thenReturn(this.templateDto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/{templateName}", "T1").contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.templateDto))).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.templateName", Matchers.is(this.templateDto.getTemplateName())));
    }

    @Test
    void ThrowingTemplateNotFoundExceptionForGetTemplateByTemplateName() throws Exception {
        Mockito.when(this.templateService.getTemplateByTemplateName(ArgumentMatchers.any(String.class))).thenThrow(TemplateNotFoundException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/{templateName}", "T1").contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.templateDto))).andExpect(status().isOk()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof TemplateNotFoundException);
        });
    }

    @Test
    void getTemplateByProjectType() throws Exception {
        List<TemplateDto> templateDtos = List.of(this.templateDto);
        Mockito.when(this.templateService.getTemplatesByProjectType(ArgumentMatchers.any(Long.class))).thenReturn(templateDtos);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/projectType/{projectTypeId}", 1)
        		.contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(templateDtos))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(templateDtos.size())));
    }

    
    @Test
	void ThrowingTemplateNotFoundExceptionForGetTemplatesByProjectType() throws Exception {
		Mockito.when(this.templateService.getTemplatesByProjectType(ArgumentMatchers.any(Long.class)))
				.thenThrow(TemplateNotFoundException.class);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/v1/template/projectType/{projectTypeId}", 1)
						.contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.templateDto)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect((result) -> {
					Assertions.assertTrue(result.getResolvedException() instanceof TemplateNotFoundException);
				});
	}

    @Test
    void getTemplateInfoByProjectType() throws Exception {
        List<TemplateInfo> templateInfos = List.of(this.templateInfo);
        Mockito.when(this.templateService.getTemplateInfoByProjectType(ArgumentMatchers.any(Long.class))).thenReturn(templateInfos);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/info/{projectTypeId}", 1l).contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(templateInfos))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(templateInfos.size())));
    }

    @Test
    void ThrowingTemplateNotFoundExceptionTestForGetTemplateInfoByProjectType() throws Exception {
        Mockito.when(this.templateService.getTemplateInfoByProjectType(ArgumentMatchers.any(Long.class))).thenThrow(TemplateNotFoundException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/info/{projectType}", 1l).contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.templateInfo))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof TemplateNotFoundException);
        });
    }
    @Test
    void getTemplateById() throws Exception {
        Mockito.when(this.templateService.getTemplateById(ArgumentMatchers.any(Long.class))).thenReturn(templateDto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/id/{id}", 111).contentType(MediaType.APPLICATION_JSON)
                .content(this.asJsonString(templateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.templateName", Matchers.is(this.templateDto.getTemplateName())));

    }
    @Test
    void getTemplateInfoBySelectedFilters() throws Exception {
        List<TemplateInfo> templateInfos = List.of(this.templateInfo);
        Mockito.when(this.templateService.getTemplateInfoBySelectedFilters(ArgumentMatchers.any(Long.class),ArgumentMatchers.any(Long.class))).thenReturn(templateInfos);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/info/{projectId}/{projectTypeId}", 1,1).contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(templateInfos))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(templateInfos.size())));
    }

    @Test
    void ThrowingTemplateNotFoundExceptionForGetTemplateInfoBySelectedFilters() throws Exception {
        Mockito.when(this.templateService.getTemplateInfoBySelectedFilters(ArgumentMatchers.any(Long.class),ArgumentMatchers.any(Long.class))).thenThrow(TemplateNotFoundException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/info/{projectId}/{projectTypeId}",  1, 1).contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.templateInfo))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof TemplateNotFoundException);
        });
    }

    @Test
    void ThrowingTemplateNotFoundExceptionTestForGetTemplateById() throws Exception {
        Mockito.when(this.templateService.getTemplateById(ArgumentMatchers.any(Long.class))).thenThrow(TemplateNotFoundException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/template/id/{id}", 111).contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.templateInfo))).andExpect(status().isOk()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof TemplateNotFoundException);
        });
    }
    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
    
	@Test
	void testGetAllTemplateInfos() throws Exception {

		ResponseEntity<List<TemplateInfo>> response = templateController.getAllTemplateInfos();

		mockMvc.perform(get("/v1/template/info/allTemplates")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json")).andReturn();
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
    @Test
    void discardTemplate() throws Exception {
        Mockito.when(this.templateService.discardTemplate(ArgumentMatchers.any(Boolean.class), ArgumentMatchers.any(Long.class))).thenReturn(templateInfo);
        this.mockMvc.perform(put("/v1/template/discard/{id}", 111).param("isActive", String.valueOf(true)).contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(templateInfo)))
                .andExpect(status().isOk());

    }

    @Test
    void ThrowingTemplateNotFoundExceptionTestForDiscardTemplate() throws Exception {
        Mockito.when(this.templateService.discardTemplate(ArgumentMatchers.any(Boolean.class), ArgumentMatchers.any(Long.class))).thenThrow(TemplateNotFoundException.class);
        mockMvc.perform(put("/v1/template/discard/{id}", 1).param("isActive", String.valueOf(true)).contentType(MediaType.APPLICATION_JSON)
                        .content(this.asJsonString(this.templateDto)))
                .andExpect(status().isOk()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof TemplateNotFoundException);
                });
    }
  @Test
  void getAllAssessmentTemplates() throws Exception {
    List<AssessmentTemplateDto> assessmentTemplates =new ArrayList<>();
    assessmentTemplates.add(new AssessmentTemplateDto());
    assessmentTemplates.add(new AssessmentTemplateDto());
    when(templateService.getAssessmentTemplates()).thenReturn(assessmentTemplates);
    ResponseEntity<List<AssessmentTemplateDto>> response = templateController.getAllAssessmentTemplates();
    mockMvc.perform(get("/v1/templates"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andReturn();
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getAllAssessmentTemplateWithNoTemplateFound() throws Exception {
    when(templateService.getAssessmentTemplates()).thenThrow(TemplateNotFoundException.class);
    mockMvc.perform(get("/v1/templates"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andReturn();
  }
    @Test
    void createPreviewTest() throws Exception {

        Mockito.when(this.templateService.createPreview(ArgumentMatchers.any(TemplateSaveRequestDto.class))).thenReturn(this.templateDto);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(this.templateSaveRequestDto);
        mockMvc.perform(post("/v1/template/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
    }
    @Test
    void testGetDistinctDisplayNames() throws Exception {

        ResponseEntity<List<TemplateDisplayDto>> response = templateController.getUniqueTemplateDisplayNames(anyString());

        mockMvc.perform(get("/v1/template/distinct/template-display-names")).andExpect(status().isOk())
                .andExpect(content().contentType("application/json")).andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


}
