package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 5071372614693693195L;

    private Long updatedById;
    private String updatedByName;
    private String auditEntity;
    private Long auditEntityId;
    private String operation;
    private Long updatedAt;


}
