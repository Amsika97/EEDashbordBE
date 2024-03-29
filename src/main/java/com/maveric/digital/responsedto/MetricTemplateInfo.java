package com.maveric.digital.responsedto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricTemplateInfo {
    private Long templateId;
    private String templateName;

    public MetricTemplateInfo(Long templateId, String templateName) {
        this.templateId = templateId;
        this.templateName = templateName;
    }


}

