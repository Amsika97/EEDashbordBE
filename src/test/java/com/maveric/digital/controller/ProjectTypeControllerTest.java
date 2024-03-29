package com.maveric.digital.controller;

import com.maveric.digital.responsedto.ProjectTypeDto;
import com.maveric.digital.service.ProjectTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectTypeControllerTest {

    @Mock
    private ProjectTypeService projectTypeService;

    @InjectMocks
    private ProjectTypeController projectTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetAllSuccess() {
        List<ProjectTypeDto> projectTypeDtos = new ArrayList<>();
        projectTypeDtos.add(new ProjectTypeDto(/* your data here */));
        when(projectTypeService.getAll()).thenReturn(projectTypeDtos);
        List<ProjectTypeDto> result = projectTypeController.getAll();
        verify(projectTypeService, times(1)).getAll();
        assertEquals(projectTypeDtos, result);

    }

    @Test
     void testGetAllEmptyList() {
        when(projectTypeService.getAll()).thenReturn(new ArrayList<>());
        List<ProjectTypeDto> result = projectTypeController.getAll();
        verify(projectTypeService, times(1)).getAll();
        assertEquals(0, result.size());
    }
    @Test
    void testGetAllprojectTypes() throws Exception {
        when(projectTypeService.getAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/filtered/projectTypes");
        MockMvcBuilders.standaloneSetup(projectTypeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }
}
