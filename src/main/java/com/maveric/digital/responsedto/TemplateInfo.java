package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateInfo {
    private Long id;
    private String templateName;
    private Integer version;
    private Boolean isActive;
}
