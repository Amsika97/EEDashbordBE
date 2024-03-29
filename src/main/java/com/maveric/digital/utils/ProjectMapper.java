package com.maveric.digital.utils;

import com.maveric.digital.model.Project;
import com.maveric.digital.responsedto.ProjectDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectMapper {

    public ProjectDto convertToProjectDto(Project project) {
        ProjectDto projectDto = new ProjectDto();
        BeanUtils.copyProperties(project, projectDto);
        return projectDto;
    }

    public List<ProjectDto> convertToProjectDto(List<Project> projectList) {
        List<ProjectDto> projectDtoList = new ArrayList<>();
        for (Project project : projectList) {
            projectDtoList.add(convertToProjectDto(project));
        }
        return projectDtoList;
    }

}
