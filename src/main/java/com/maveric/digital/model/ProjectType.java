package com.maveric.digital.model;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(value = "projectType")
@ToString
public class ProjectType extends IdentifiedEntity {
    private static final Logger logger = LoggerFactory.getLogger(ProjectType.class);

  
    @NotBlank(message = "Name should not be blank")
    @Length(min = 3, max = 20,message = "name should contain more than 3 letter")
    private String projectTypeName;
}
