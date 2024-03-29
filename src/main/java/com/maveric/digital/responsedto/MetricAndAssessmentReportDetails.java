package com.maveric.digital.responsedto;

import com.maveric.digital.model.User;
import com.maveric.digital.model.embedded.AssessmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricAndAssessmentReportDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = -4313603062754269127L;
    @Schema(name = "id", example = "344851228")
    private Long id;
    @Schema(name = "projectCode", example = "c123")
    private String projectCode;
    @Schema(name = "projectName", example = "Support Functions | Administration")
    private String projectName;
    @Schema(name = "accountName", example = "Maveric Internal Project")
    private String accountName;
    @Schema(name = "templateName", example = "demo template")
    private String templateName;
    @Schema(name = "submittedAt", example = "1703004336660")
    private Long submittedAt;
    @Schema(name = "reviewerName", example =  "Baranidharan D")
    private String reviewerName;
    @Schema(name = "status", example = "APPROVED")
    private AssessmentStatus status;
    @Schema(name = "score", example = " 41.666666666666664")
    private double score; 
    private String templateDisplayName;
    private String submittedBy;
    private String submitterId;
    private String reviewerId;
    
    

}
