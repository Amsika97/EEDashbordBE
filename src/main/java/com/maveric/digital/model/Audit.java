package com.maveric.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "audit")
public class Audit extends IdentifiedEntity {

  private String updatedById;
  private String updatedByName;
  private String auditEntity;
  private Long auditEntityId;
  private String operation;
  private Long updatedAt;

}
