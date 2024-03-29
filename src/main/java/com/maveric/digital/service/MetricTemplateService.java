package com.maveric.digital.service;


import java.util.List;
import java.util.Optional;

import com.maveric.digital.responsedto.MetricTemplateDetailsDto;
import com.maveric.digital.responsedto.MetricTemplateDto;
import com.maveric.digital.responsedto.MetricTemplateInfo;
import com.maveric.digital.responsedto.MetricTemplateSaveRequestDto;

public interface MetricTemplateService {

    MetricTemplateDto createMetricTemplate(MetricTemplateSaveRequestDto templateSaveRequest);

    List<MetricTemplateDetailsDto> getMetricTemplates();

    MetricTemplateDto updateMetricTemplateStatus(Long templateId, Boolean isActive);

    MetricTemplateDto getMetricTemplateById(Long metricTemplateId);

    Optional<List<MetricTemplateInfo>> getMetricTemplateInfoByProjectTypeId(Long projectTypeId);

    MetricTemplateDto previewMetricTemplate(MetricTemplateSaveRequestDto metricTemplateSaveRequestDto);
}

