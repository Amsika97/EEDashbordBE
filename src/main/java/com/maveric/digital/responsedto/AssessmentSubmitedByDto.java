package com.maveric.digital.responsedto;

import com.maveric.digital.model.embedded.AssessmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSubmitedByDto {

    private String submitedBy;
    private Long assessmentId;
    private Long submitedAt;

    private Long projectId;
    private Long templateId;
    private String clientName;
    private String projectName;
    private AssessmentStatus submitStatus;
    private String reviewerName;
    private Long reviewerAt;

}
