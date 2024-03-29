package com.maveric.digital.controller;


import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.maveric.digital.exceptions.ErrorDetails;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.maveric.digital.model.Account;
import com.maveric.digital.model.Project;
import com.maveric.digital.service.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.responsedto.ProjectInfo;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.ProjectService;

import jakarta.validation.ConstraintViolationException;

@WebMvcTest(ProjectController.class)
@ExtendWith(SpringExtension.class)
class ProjectControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ProjectService projectService;
    @MockBean
    ConversationService conversationService;
    ProjectDto projectDto;

    @Autowired
    private ProjectController projectController;

    @BeforeEach
    private void initiateProjectDtoData() {
        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setProjectName("Digital");
        projectDto.setBusinessUnit("Bu");
        projectDto.setStatus(true);
        projectDto.setManagerName("Rams");
        projectDto.setUpdatedBy("Rams");
        projectDto.setAccountId(1L);
        projectDto.setProjectCode("C35689");

    }

    @Test
    void createProject() throws Exception {
        when(projectService.createProject(any(ProjectDto.class))).thenReturn(projectDto);
        mockMvc.perform(post("/v1/project/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(projectDto)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", is(projectDto.isStatus())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessUnit", is(projectDto.getBusinessUnit())));
    }

    @Test
    void ThrowingConstraintViolationExceptionForCreateProject() throws Exception {
        when(projectService.createProject(any(ProjectDto.class))).thenThrow(ConstraintViolationException.class);
        mockMvc.perform(post("/v1/project/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(projectDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }



    @Test
    void ThrowingDataIntegrityViolationExceptionForCreateProject() throws Exception {
        when(projectService.createProject(any(ProjectDto.class))).thenThrow(DataIntegrityViolationException.class);
        mockMvc.perform(post("/v1/project/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(projectDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataIntegrityViolationException));

    }

    @Test
    void ThrowingResourceCreationExceptionForCreateProject() throws Exception {
        when(projectService.createProject(any(ProjectDto.class))).thenThrow(ResourceCreationException.class);
        mockMvc.perform(post("/v1/project/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(projectDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceCreationException));

    }





    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    @Test
    void shouldCreateProjectOnAccountId() {


        Account account = new Account();
        account.setAccountName("Madhu");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        Project project = new Project();
        project.setAccount(account);
        project.setCreatedAt(LocalDate.of(2023, 12, 15));
        project.setEndDate(LocalDate.of(2023, 12, 15));
        project.setId(1L);
        project.setManagerName("Manager Name");
        project.setProjectCode("Project Code");
        project.setProjectName("Project Name");
        project.setStartDate(LocalDate.of(2023, 12, 15));
        project.setStatus(true);
        project.setUpdatedAt(LocalDate.of(2023, 12, 15));
        project.setUpdatedBy("2023-12-15");
        ProjectServiceImpl projectService = mock(ProjectServiceImpl.class);
        when(projectService.createProjectonAccountId(Mockito.<ProjectDto>any())).thenReturn(project);
        ProjectController projectController = new ProjectController(projectService, new ConversationService());
        ResponseEntity<ProjectDto> actualCreateProjectOnAccountIdResult = projectController
                .createProjectOnAccountId(new ProjectDto());
        verify(projectService).createProjectonAccountId(Mockito.<ProjectDto>any());
        ProjectDto body = actualCreateProjectOnAccountIdResult.getBody();
        assertEquals("2023-12-15", body.getCreatedAt().toString());
        assertEquals("2023-12-15", body.getEndDate().toString());
        assertEquals("2023-12-15", body.getStartDate().toString());
        assertEquals("2023-12-15", body.getUpdatedBy());
        assertEquals("Manager Name", body.getManagerName());
        assertEquals("Project Name", body.getProjectName());
        assertEquals(1L, body.getAccountId().longValue());
        assertEquals(1L, body.getId().longValue());
        assertEquals(201, actualCreateProjectOnAccountIdResult.getStatusCodeValue());
        assertTrue(body.isStatus());
        assertTrue(actualCreateProjectOnAccountIdResult.hasBody());
        assertTrue(actualCreateProjectOnAccountIdResult.getHeaders().isEmpty());
    }

    @Test
    void testCreateProjectOnAccountId_Success() {



        Account account = new Account();
        account.setAccountName("Dr Jane Doe");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        Project project = new Project();
        project.setAccount(account);
        project.setCreatedAt(LocalDate.of(1970, 1, 1));
        project.setEndDate(LocalDate.of(1970, 1, 1));
        project.setId(1L);
        project.setManagerName("Manager Name");
        project.setProjectCode("Project Code");
        project.setProjectName("Project Name");
        project.setStartDate(LocalDate.of(1970, 1, 1));
        project.setStatus(true);
        project.setUpdatedAt(LocalDate.of(1970, 1, 1));
        project.setUpdatedBy("2020-03-01");
        ProjectServiceImpl projectService = mock(ProjectServiceImpl.class);
        when(projectService.createProjectonAccountId(Mockito.<ProjectDto>any())).thenReturn(project);
        ProjectController projectController = new ProjectController(projectService, new ConversationService());
        ResponseEntity<ProjectDto> actualCreateProjectOnAccountIdResult = projectController
                .createProjectOnAccountId(new ProjectDto());
        verify(projectService).createProjectonAccountId(Mockito.<ProjectDto>any());
        ProjectDto body = actualCreateProjectOnAccountIdResult.getBody();
        assertEquals("1970-01-01", body.getCreatedAt().toString());
        assertEquals("1970-01-01", body.getEndDate().toString());
        assertEquals("1970-01-01", body.getStartDate().toString());
        assertEquals("2020-03-01", body.getUpdatedBy());
        assertEquals("Manager Name", body.getManagerName());
        assertEquals("Project Name", body.getProjectName());
        assertEquals(1L, body.getAccountId().longValue());
        assertEquals(1L, body.getId().longValue());
        assertEquals(201, actualCreateProjectOnAccountIdResult.getStatusCodeValue());
        assertTrue(body.isStatus());
        assertTrue(actualCreateProjectOnAccountIdResult.hasBody());
        assertTrue(actualCreateProjectOnAccountIdResult.getHeaders().isEmpty());
    }
    @Test
    void testCreateProjectOnAccountId_NonSpring() {

        Account account = new Account();
        account.setAccountName("Madhu");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        Account account2 = new Account();
        account2.setAccountName("Madhu");
        account2.setCreatedAt(1L);
        account2.setId(1L);
        account2.setUpdatedAt(1L);
        Project project = mock(Project.class);
        when(project.isStatus()).thenReturn(true);
        when(project.getAccount()).thenReturn(account2);
        when(project.getId()).thenReturn(1L);
        when(project.getManagerName()).thenReturn("Manager Name");
        when(project.getProjectName()).thenReturn("Project Name");
        when(project.getUpdatedBy()).thenReturn("2023-12-16");
        when(project.getCreatedAt()).thenReturn(LocalDate.of(2023, 12, 16));
        when(project.getEndDate()).thenReturn(LocalDate.of(2023, 12, 16));
        when(project.getStartDate()).thenReturn(LocalDate.of(2023, 12, 16));
        doNothing().when(project).setId(Mockito.<Long>any());
        doNothing().when(project).setAccount(Mockito.<Account>any());
        doNothing().when(project).setCreatedAt(Mockito.<LocalDate>any());
        doNothing().when(project).setEndDate(Mockito.<LocalDate>any());
        doNothing().when(project).setManagerName(Mockito.<String>any());
        doNothing().when(project).setProjectCode(Mockito.<String>any());
        doNothing().when(project).setProjectName(Mockito.<String>any());
        doNothing().when(project).setStartDate(Mockito.<LocalDate>any());
        doNothing().when(project).setStatus(anyBoolean());
        doNothing().when(project).setUpdatedAt(Mockito.<LocalDate>any());
        doNothing().when(project).setUpdatedBy(Mockito.<String>any());
        project.setAccount(account);
        project.setCreatedAt(LocalDate.of(2023, 12, 16));
        project.setEndDate(LocalDate.of(2023, 12, 16));
        project.setId(1L);
        project.setManagerName("Manager Name");
        project.setProjectCode("Project Code");
        project.setProjectName("Project Name");
        project.setStartDate(LocalDate.of(2023, 12, 16));
        project.setStatus(true);
        project.setUpdatedAt(LocalDate.of(2023, 12, 16));
        project.setUpdatedBy("2023-12-16");
        ProjectServiceImpl projectService = mock(ProjectServiceImpl.class);
        when(projectService.createProjectonAccountId(Mockito.<ProjectDto>any())).thenReturn(project);
        ProjectController projectController = new ProjectController(projectService, new ConversationService());
        ResponseEntity<ProjectDto> actualCreateProjectOnAccountIdResult = projectController
                .createProjectOnAccountId(new ProjectDto());
        verify(project).getId();
        verify(project).setId(Mockito.<Long>any());
        verify(project).getAccount();
        verify(project).getCreatedAt();
        verify(project).getEndDate();
        verify(project).getManagerName();
        verify(project).getProjectName();
        verify(project).getStartDate();
        verify(project).getUpdatedBy();
        verify(project).isStatus();
        verify(project).setAccount(Mockito.<Account>any());
        verify(project).setCreatedAt(Mockito.<LocalDate>any());
        verify(project).setEndDate(Mockito.<LocalDate>any());
        verify(project).setManagerName(Mockito.<String>any());
        verify(project).setProjectCode(Mockito.<String>any());
        verify(project).setProjectName(Mockito.<String>any());
        verify(project).setStartDate(Mockito.<LocalDate>any());
        verify(project).setStatus(anyBoolean());
        verify(project).setUpdatedAt(Mockito.<LocalDate>any());
        verify(project).setUpdatedBy(Mockito.<String>any());
        verify(projectService).createProjectonAccountId(Mockito.<ProjectDto>any());
        ProjectDto body = actualCreateProjectOnAccountIdResult.getBody();
        assertEquals("2023-12-16", body.getCreatedAt().toString());
        assertEquals("2023-12-16", body.getEndDate().toString());
        assertEquals("2023-12-16", body.getStartDate().toString());
        assertEquals("2023-12-16", body.getUpdatedBy());
        assertEquals("Manager Name", body.getManagerName());
        assertEquals("Project Name", body.getProjectName());
        assertEquals(1L, body.getAccountId().longValue());
        assertEquals(1L, body.getId().longValue());
        assertEquals(201, actualCreateProjectOnAccountIdResult.getStatusCodeValue());
        assertTrue(body.isStatus());
        assertTrue(actualCreateProjectOnAccountIdResult.hasBody());
        assertTrue(actualCreateProjectOnAccountIdResult.getHeaders().isEmpty());
    }

    @Test
    void testCreateProjectOnAccountId_ConversationService() {

        Account account = new Account();
        account.setAccountName("Madhu");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);
        Project project = mock(Project.class);
        doNothing().when(project).setId(Mockito.<Long>any());
        doNothing().when(project).setAccount(Mockito.<Account>any());
        doNothing().when(project).setCreatedAt(Mockito.<LocalDate>any());
        doNothing().when(project).setEndDate(Mockito.<LocalDate>any());
        doNothing().when(project).setManagerName(Mockito.<String>any());
        doNothing().when(project).setProjectCode(Mockito.<String>any());
        doNothing().when(project).setProjectName(Mockito.<String>any());
        doNothing().when(project).setStartDate(Mockito.<LocalDate>any());
        doNothing().when(project).setStatus(anyBoolean());
        doNothing().when(project).setUpdatedAt(Mockito.<LocalDate>any());
        doNothing().when(project).setUpdatedBy(Mockito.<String>any());
        project.setAccount(account);
        project.setCreatedAt(LocalDate.of(2023, 12, 16));
        project.setEndDate(LocalDate.of(2023, 12, 16));
        project.setId(1L);
        project.setManagerName("Manager Name");
        project.setProjectCode("Project Code");
        project.setProjectName("Project Name");
        project.setStartDate(LocalDate.of(2023, 12, 16));
        project.setStatus(true);
        project.setUpdatedAt(LocalDate.of(2023, 12, 16));
        project.setUpdatedBy("2023-12-16");
        ProjectServiceImpl projectService = mock(ProjectServiceImpl.class);
        when(projectService.createProjectonAccountId(Mockito.<ProjectDto>any())).thenReturn(project);
        ConversationService conversationService = mock(ConversationService.class);
        when(conversationService.toProjectDto(Mockito.<Project>any())).thenReturn(new ProjectDto());
        ProjectController projectController = new ProjectController(projectService, conversationService);
        ResponseEntity<ProjectDto> actualCreateProjectOnAccountIdResult = projectController
                .createProjectOnAccountId(new ProjectDto());
        verify(project).setId(Mockito.<Long>any());
        verify(project).setAccount(Mockito.<Account>any());
        verify(project).setCreatedAt(Mockito.<LocalDate>any());
        verify(project).setEndDate(Mockito.<LocalDate>any());
        verify(project).setManagerName(Mockito.<String>any());
        verify(project).setProjectCode(Mockito.<String>any());
        verify(project).setProjectName(Mockito.<String>any());
        verify(project).setStartDate(Mockito.<LocalDate>any());
        verify(project).setStatus(anyBoolean());
        verify(project).setUpdatedAt(Mockito.<LocalDate>any());
        verify(project).setUpdatedBy(Mockito.<String>any());
        verify(conversationService).toProjectDto(Mockito.<Project>any());
        verify(projectService).createProjectonAccountId(Mockito.<ProjectDto>any());
        assertEquals(201, actualCreateProjectOnAccountIdResult.getStatusCodeValue());
        assertTrue(actualCreateProjectOnAccountIdResult.hasBody());
        assertTrue(actualCreateProjectOnAccountIdResult.getHeaders().isEmpty());
    }
    @Test
    void testGetProjectsInfosByAccountIdWithProjects() {
        Long accountId = 1L;
        List<ProjectInfo> projectInfos = Collections.singletonList(new ProjectInfo(1L,"test"));
        when(projectService.getProjectsInfoByAccountId(accountId)).thenReturn(projectInfos);

        ResponseEntity<?> response = projectController.getProjectsInfosByAccountId(accountId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(projectInfos, response.getBody());
    }

    @Test
    void testGetProjectsInfosByAccountIdNoProjects() {
        Long accountId = 1L;
        when(projectService.getProjectsInfoByAccountId(accountId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = projectController.getProjectsInfosByAccountId(accountId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List);
        assertTrue(((List) response.getBody()).get(0) instanceof ErrorDetails);
    }
}
