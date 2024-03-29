package com.maveric.digital.pushnotificationservice;

import com.maveric.digital.model.User;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.UserEmailTemplateDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;


/*
 * This is for Testing purpose to check mails are sending or not
 * */
@Service
public class EmailServiceImpl {

    @Autowired
    private EmailConversationUtils emailConversationUtils;

    @Autowired
    private UserRepository userRepository;

    public String sendEmail() {
        try {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            userEmailTemplateDetails.setAccountName("A1");
            userEmailTemplateDetails.setUserName("Ram");
            userEmailTemplateDetails.setDeliveryUnit("DU-1");
            userEmailTemplateDetails.setProjectName("Project-1");
            userEmailTemplateDetails.setSubmittedOn(new Date(System.currentTimeMillis()).toString());
            userEmailTemplateDetails.setReviewOn(new Date(System.currentTimeMillis()).toString());
            userEmailTemplateDetails.setSubmittedBy("Ram");
            userEmailTemplateDetails.setReviewedBy("Reddy");
            userEmailTemplateDetails.setTemplateName("Template-1");
            Optional<User> userObj = userRepository.findByOid(UUID.fromString("a1c48093-d8ea-4986-9a53-edee52b69008"));
            userEmailTemplateDetails.setEmailId("rameshbo@maveric-systems.com");
            emailConversationUtils.sendEmailNotificationForAssessmentSubmittedConfirmation(userEmailTemplateDetails);
            emailConversationUtils.sendEmailNotificationForAssessmentSubmittedToReviewer(userEmailTemplateDetails);
            emailConversationUtils.sendEmailNotificationForMetricsReviewed(userEmailTemplateDetails);
            userEmailTemplateDetails.setStatus(AssessmentStatus.REVIEWED.name());
            emailConversationUtils.sendEmailNotificationForAssessmentReviewed(userEmailTemplateDetails);
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            return "Error while Sending Mail";
        }

    }

}
