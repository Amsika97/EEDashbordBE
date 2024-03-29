package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PiechartDashboardDto {
    private String submitted;
    private Integer submittedCount;
    private String reviewed;
    private Integer reviewedCount;
    private String unit;



}
