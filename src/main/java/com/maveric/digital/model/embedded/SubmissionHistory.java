package com.maveric.digital.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionHistory {

    private String submitedBy;
    private Long submitedAt;
    private String projectName;
    private String templateName;
    private String clientName;
    private String submitStatus;

}
