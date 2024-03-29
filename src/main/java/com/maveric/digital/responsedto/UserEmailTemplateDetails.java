package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailTemplateDetails {

    private String userName;
    private String projectCode;
    private String projectName;
    private String projectType;
    private String accountName;
    private String deliveryUnit;
    private String templateName;
    private String submittedBy;
    private String submittedOn;
    private String status;
    private String emailId;
    private String reviewedBy;
    private String reviewOn;
    private String[] reviewerEmails;
    private String dueDate;

}
