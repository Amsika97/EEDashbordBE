package com.maveric.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionFilterDto {
    private String submittedBy;
    private long fromDate;
    private long toDate;
    private String all;
}
