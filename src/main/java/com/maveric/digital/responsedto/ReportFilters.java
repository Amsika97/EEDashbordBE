package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilters implements Serializable {

    @Serial
    private static final long serialVersionUID = -4908489492455374081L;

    private ReportFilterType reportFilterType;
    private Long accountId;
    private List<Long> projectIds;
    private List<Long> templateId;
    private Integer submissionPeriodDays;
    private Long submissionFromDate;
    private Long submissionToDate;
    private Integer scoreFromRange;
    private Integer scoreToRange;
    private List<Long> projectType;
    private List<String> submittedBy;
    private List<String> reviewedBy;
    private String submitStatus;

}
