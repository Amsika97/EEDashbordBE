package com.maveric.digital.responsedto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricTemplateDetailsDto {
  private Long templateId;
  private String templateName;
  private List<String> projectType;
  private Long createdOn;
  private String templateUploadedUserName;
  private Boolean isActive;
  private String templateDisplayName;
  private TemplateFrequencyReminder templateFrequency;
}
