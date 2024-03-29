package com.maveric.digital.service;

import com.maveric.digital.responsedto.*;

import java.util.List;
import java.util.Map;

import com.maveric.digital.model.Template;

public interface TemplateService {
    TemplateDto createTemplate(TemplateSaveRequestDto templateSaveRequest);
    TemplateDto updateTemplate(TemplateSaveRequestDto templateSaveRequest);

    List<TemplateDto> getTemplatesByProjectType(Long projectTypeId);

    List<TemplateInfo> getTemplateInfoByProjectType(Long projectTypId);

    TemplateDto getTemplateByTemplateName(String projectType);
    TemplateDto getTemplateById(Long id);

    List<TemplateInfo> getTemplateInfoBySelectedFilters(Long projectId, Long projectTypeId) ;
    
    List<Template> getAllTemplateInfos();
    TemplateInfo discardTemplate(Boolean isActive,Long id);

    List<AssessmentTemplateDto> getAssessmentTemplates();

    TemplateDto createPreview(TemplateSaveRequestDto templateSaveRequest);
    List<TemplateDisplayDto> getUniqueTemplateDisplayNames(String filterName);
}


