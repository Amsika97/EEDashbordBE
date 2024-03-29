package com.maveric.digital.service;

import com.maveric.digital.responsedto.ProjectTypeDto;
import java.util.List;

public interface ProjectTypeService {
    List<ProjectTypeDto> getAll();
    List<ProjectTypeDto> getAllfilteredprojectTypes();


}
