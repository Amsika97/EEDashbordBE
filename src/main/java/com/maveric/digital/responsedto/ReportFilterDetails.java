package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportFilterDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 5242703628438522856L;

    private List<MetricAndAssessmentReportDetails> assessmentReportDetails;
    private List<MetricAndAssessmentReportDetails> metricReportDetails;

}
