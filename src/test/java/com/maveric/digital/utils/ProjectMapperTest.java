package com.maveric.digital.utils;

import com.maveric.digital.model.Account;
import com.maveric.digital.model.Project;
import com.maveric.digital.responsedto.ProjectDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ProjectMapperTest {
    @Test
    public void testConvertToProjectDto() {

        ProjectMapper converter = new ProjectMapper();
        Project project = mock(Project.class);
        when(project.isStatus()).thenReturn(true);
        when(project.getId()).thenReturn(1L);
        when(project.getManagerName()).thenReturn("Priya");
        when(project.getProjectCode()).thenReturn("CITI001");
        when(project.getProjectName()).thenReturn("CMS");
        doNothing().when(project).setAccount(Mockito.<Account>any());
        doNothing().when(project).setManagerName(Mockito.<String>any());
        doNothing().when(project).setProjectCode(Mockito.<String>any());
        doNothing().when(project).setProjectName(Mockito.<String>any());
        ProjectDto actualConvertToProjectDtoResult = converter.convertToProjectDto(project);
        assertEquals("Priya", actualConvertToProjectDtoResult.getManagerName());
        assertEquals("CITI001", actualConvertToProjectDtoResult.getProjectCode());
        assertEquals("CMS", actualConvertToProjectDtoResult.getProjectName());
        assertTrue(actualConvertToProjectDtoResult.isStatus());
    }
    @Test
    public void testConvertToProjectDtoList() {
         ProjectMapper converter = new ProjectMapper();
        List<Project> projectList = new ArrayList<>();
        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);

        when(project1.getId()).thenReturn(1L);
        when(project1.getProjectName()).thenReturn("Project 1");
        when(project1.getManagerName()).thenReturn("Priya");
        when(project1.getProjectCode()).thenReturn("CITI001");
        when(project1.getProjectName()).thenReturn("CMS");
        when(project2.getId()).thenReturn(2L);
        when(project2.getProjectName()).thenReturn("Project 2");
        when(project2.getManagerName()).thenReturn("Anil");
        when(project2.getProjectCode()).thenReturn("CITI051");
        when(project2.getProjectName()).thenReturn("NAM");
        projectList.add(project1);
        projectList.add(project2);

        List<ProjectDto> projectDtoList = converter.convertToProjectDto(projectList);

        assertEquals(2, projectDtoList.size());
        assertEquals(project1.getId(), projectDtoList.get(0).getId());
        assertEquals(project1.getProjectName(), projectDtoList.get(0).getProjectName());
        assertEquals(project2.getId(), projectDtoList.get(1).getId());
        assertEquals(project2.getProjectName(), projectDtoList.get(1).getProjectName());
    }
}
