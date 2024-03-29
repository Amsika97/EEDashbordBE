package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentsDashboardDto {
    private int totalAssessmentSubmissions;
    private int totalAssessmentReviewed;
    private int totalMetrics;
    private int totalProjects;
    private int totalAccounts;

}

