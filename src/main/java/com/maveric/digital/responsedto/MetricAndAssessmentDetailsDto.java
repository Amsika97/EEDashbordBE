package com.maveric.digital.responsedto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricAndAssessmentDetailsDto {
    private Long id;
    private String projectName;
    private String submittedBy;
    private String submittedByName;
    private Long lastUpdateAt;
    private String reviewerName;
    private Long reviewerAt;
    private String status;
    private String accountName;
    private String projectCode;
    private Double score;
    private Long submittedAt;
    private String reviewerId;
    private String templateName; 

}
