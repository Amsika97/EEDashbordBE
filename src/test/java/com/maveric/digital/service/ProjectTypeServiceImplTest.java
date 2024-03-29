package com.maveric.digital.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.responsedto.ProjectTypeDto;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProjectTypeServiceImplTest {

    @MockBean
    private ProjectTypeRepository projectTypeRepository;

    @MockBean
    private ConversationService conversationService;

    @Autowired
    private ProjectTypeServiceImpl projectTypeServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<ProjectType> projectTypes = new ArrayList<>();
        ProjectType projectType =new ProjectType();
        ProjectType projectType1 =new ProjectType();
        projectType.setId(1l);
        projectType.setProjectTypeName("Internal");
        projectType1.setId(1l);
        projectType1.setProjectTypeName("Select All");
        projectTypes.add(projectType);
        projectTypes.add(projectType1);
        List<ProjectTypeDto> projectTypeDtos = new ArrayList<>();
        projectTypeDtos.add(new ProjectTypeDto(1L, "a"));
        projectTypeDtos.add(new ProjectTypeDto(2L, "a"));
        when(projectTypeRepository.findAll(Sort.by("projectTypeName"))).thenReturn(projectTypes);
        when(conversationService.convertToProjectTypeDto(projectTypes)).thenReturn(projectTypeDtos);
        List<ProjectTypeDto> result = projectTypeServiceImpl.getAll();
        assertEquals(projectTypes.size(), result.size(), "Size of the returned list should match");
    }


    @Test
    void testGetAllWithEmptyList() {
        List<ProjectType> emptyList = new ArrayList<>();
        List<ProjectTypeDto> projectTypeDtos=new ArrayList<>();
        when(conversationService.convertToProjectTypeDto(emptyList)).thenReturn(projectTypeDtos);
        List<ProjectTypeDto> result = projectTypeServiceImpl.getAll();
        assertTrue(result.isEmpty(), "The result should be an empty list");

    }


    @Test
    public void testGetAllfilteredprojectTypes() {
        ProjectType projectType1 = new ProjectType();
        ProjectType projectType2 = new ProjectType();
        projectType1.setProjectTypeName("Fixed Bid");
        projectType2.setProjectTypeName("Internal");
        List<ProjectType> projectTypes = Arrays.asList(projectType1, projectType2);

        when(projectTypeRepository.findAll(Sort.by("projectTypeName"))).thenReturn(projectTypes);

        ProjectTypeDto projectTypeDto1 = new ProjectTypeDto(1L, "Fixed Bid");
        ProjectTypeDto projectTypeDto2 = new ProjectTypeDto(2L, "Internal");
        List<ProjectTypeDto> allProjectTypes = Arrays.asList(projectTypeDto1, projectTypeDto2);
        when(conversationService.convertToProjectTypeDto(projectTypes)).thenReturn(allProjectTypes);

        // When
        List<ProjectTypeDto> result = projectTypeServiceImpl.getAllfilteredprojectTypes();

        // Then
        assertThat("Size of result list", result, hasSize(2));
        assertThat("ID of the first element", result.get(0).getId(), is(1L));
        assertThat("ProjectTypeName of the first element", result.get(0).getProjectTypeName(), is("Fixed Bid"));
        assertThat("ID of the second element", result.get(1).getId(), is(2L));
        assertThat("ProjectTypeName of the second element", result.get(1).getProjectTypeName(), is("Internal"));

        verify(projectTypeRepository, times(1)).findAll(Sort.by("projectTypeName"));
        verify(conversationService, times(1)).convertToProjectTypeDto(projectTypes);
    }

    @Test
    void testGetAllfilteredprojectTypesWithEmptyList() {
        List<ProjectType> emptyList = new ArrayList<>();
        List<ProjectTypeDto> projectTypeDtos=new ArrayList<>();
        when(conversationService.convertToProjectTypeDto(emptyList)).thenReturn(projectTypeDtos);
        List<ProjectTypeDto> result = projectTypeServiceImpl.getAllfilteredprojectTypes();
        assertTrue(result.isEmpty(), "The result should be an empty list");

    }

}