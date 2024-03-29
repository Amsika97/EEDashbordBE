package com.maveric.digital.model;

import com.maveric.digital.utils.ServiceConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


@Document(value = "project")
@Data
@NoArgsConstructor
@ToString
public class Project extends IdentifiedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2260289930606296720L;
    @NotBlank(message = "ProjectName should not be Empty")
    private String projectName;
    @NotBlank(message = "ManagerName should not be Empty")
    private String managerName;
    private String businessUnit;
    @DateTimeFormat(pattern = ServiceConstants.DATE_PATTERN)
    @CreatedDate
    private LocalDate startDate;
    @DateTimeFormat(pattern = ServiceConstants.DATE_PATTERN)
    private LocalDate endDate;
    @NotBlank(message = "UpdatedBy should not be Empty")
    private String updatedBy;
    private boolean status;
    @DateTimeFormat(pattern = ServiceConstants.DATE_PATTERN)
    private LocalDate createdAt;
    @DateTimeFormat(pattern = ServiceConstants.DATE_PATTERN)
    private LocalDate updatedAt;
    @DBRef
    @NotNull
    private Account account;
    @NotBlank(message = "ProjectCode should not be empty")
    private String projectCode;

    private String portfolio;
    private String deliveryPartner;
    private String deliveryPartnerEmail;

    private String growthPartner;
    private String growthPartnerEmail;


    private String projectBillingType;

    private String engagementType;


    private String deliveryManager;
    private String deliveryManagerEmail;

    private String accountManager;
    private String accountManagerEmail;





}
