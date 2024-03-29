package com.maveric.digital.responsedto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetricTemplateSaveRequestDto {
    @NotNull(message = "ProjectTypes should not null")
    private List<Long> projectTypes;
    @NotNull(message = "TemplateName should not null")
    private String templateName;
    @NotNull(message = "TemplateData should not null")
    private String templateData;
    private String description;
    @NotNull(message = "TemplateUploadedUserId should not null")
    private String templateUploadedUserId;
    @NotEmpty(message = "TemplateUploadedUserName should not empty")
    private String templateUploadedUserName;
    private TemplateFrequencyReminder templateFrequency;


}
