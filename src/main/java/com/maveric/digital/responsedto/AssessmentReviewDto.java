package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentReviewDto {
  private Long id;
  private String accountName;
  private String projectName;
  private Long submittedAt;
  private String submitterName;
  private String projectCode;
  private String deliveryUnit;
  private String projectType;
  private Double score;
  private String submitterId;
  private String reviewerId;
}
