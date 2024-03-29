package com.maveric.digital.responsedto;

import com.maveric.digital.model.embedded.AssessmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentsSubmittedDashboardDto {
    private Long id;
	private String submitterName;
    private String projectName;
    private String reviewerName;
    private Double score;
    private String businessUnitName;
    private AssessmentStatus submitStatus;
    private String accountName;
    private String projectCode;
    private Long submitedAt;
    private String submitterId;
    private String reviewerId;
    private String templateName;

}