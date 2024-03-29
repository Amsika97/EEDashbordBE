package com.maveric.digital.service;


import com.maveric.digital.model.Project;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.responsedto.ProjectInfo;

import java.util.List;

public interface ProjectService {


    ProjectDto createProject(ProjectDto projectDto);
    List<ProjectInfo> getProjectsInfoByAccountId(Long accountId);

    Project createProjectonAccountId(ProjectDto projectDto);
}
