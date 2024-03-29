package com.maveric.digital.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricReviewerComment extends IdentifiedEntity{
    private Long metricId;
    private Long reviewerAt;
    private String comment;
    private String reviewerName;
}
