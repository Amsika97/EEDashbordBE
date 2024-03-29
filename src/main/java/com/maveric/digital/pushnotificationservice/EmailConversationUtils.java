package com.maveric.digital.pushnotificationservice;

import com.maveric.digital.listener.NotificationListener;
import com.maveric.digital.responsedto.UserEmailTemplateDetails;
import com.maveric.digital.utils.EmailConstants;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import static com.maveric.digital.utils.EmailConstants.ASSESSMENT_RETURNED;
import static com.maveric.digital.utils.EmailConstants.METRIC_RETURNED;
import static com.maveric.digital.utils.EmailConstants.RETURNED_ON;
import static com.maveric.digital.utils.EmailConstants.RETURNED_BY;
import static com.maveric.digital.utils.EmailReminderUtils.*;


@Component
@Slf4j
public class EmailConversationUtils {



    @Autowired
    private Configuration configuration;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${eednotification.reply.to}")
    private String replyTo;
    @Value("${eedashboard.url}")
    private String eedashboardurl;
    @Autowired
    private JavaMailSender javaMailSender;


    @Async
    public void sendEmailNotificationForAssessmentSubmittedConfirmation(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.debug("sendEmailNotificationForAssessmentSubmittedConfirmation userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_ASSESSMENT_SUBMISSION_CONFIRMATION);
            String msg = prepareTemplateDataForAssessmentSubmissionConfirmation(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);

        } catch (Throwable throwable) {
            log.error("SendEmailNotificationForAssessmentSubmittedConfirmation failed");

        }

    }

    @Async
    public void sendEmailNotificationForMetricSubmittedConfirmation(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.debug("sendEmailNotificationForMetricSubmittedConfirmation userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_METRICS_SUBMISSION_CONFIRMATION);
            String msg = prepareTemplateDataForMetricsSubmissionConfirmation(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("SendEmailNotificationForMetricSubmittedConfirmation failed");

        }

    }

    @Async
    public void sendEmailNotificationForAssessmentSubmittedToReviewer(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.debug("sendEmailNotificationForAssessmentSubmittedToReviewer userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getReviewerEmails());
            helper.setSubject(EmailConstants.SUBJECT_ASSESSMENT_SUBMITTED_FOR_REVIEW);
            String msg = prepareTemplateDataForAssessmentSubmittedToReviewer(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("SendEmailNotificationForAssessmentSubmittedToReviewer failed");

        }

    }

    @Async
    public void sendEmailNotificationForMetricSubmittedToReviewer(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.debug("sendEmailNotificationForMetricSubmittedToReviewer userDetails-{} ", userEmailTemplateDetails);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getReviewerEmails());
            helper.setSubject(EmailConstants.SUBJECT_METRICS_SUBMITTED_FOR_REVIEW);
            String msg = prepareTemplateDataForSubmittMetricsToReviewer(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("SendEmailNotificationForMetricSubmittedToReviewer failed");

        }

    }

    @Async
    public void sendEmailNotificationForAssessmentReviewed(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.debug("sendEmailNotificationForAssessmentReviewed userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            if (ArrayUtils.isNotEmpty(userEmailTemplateDetails.getReviewerEmails())){
                helper.setCc(userEmailTemplateDetails.getReviewerEmails());
            }
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_ASSESSMENT_REVIEWED);
            String msg = prepareTemplateDataForAssessmentReviewed(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("SendEmailNotificationForAssessmentReviewed failed");

        }

    }

    @Async
    public void sendEmailNotificationForMetricsReviewed(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.debug("sendEmailNotificationForMetricsReviewed userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            if (ArrayUtils.isNotEmpty(userEmailTemplateDetails.getReviewerEmails())){
                helper.setCc(userEmailTemplateDetails.getReviewerEmails());
            }
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_METRICS_REVIEWED);
            String msg = prepareTemplateDataForSubmittedMetricsReviewed(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("SendEmailNotificationForMetricsReviewed failed");

        }

    }

    public String prepareTemplateDataForAssessmentSubmissionConfirmation(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.STATUS_MSG, EmailConstants.ASSESSMENT_SUBMISSION_CONFIRMATION);
        model.put(EmailConstants.REVIEWER_GROUP, userEmailTemplateDetails.getUserName());
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);
        StringBuilder details = prepareTemplateDetails(userEmailTemplateDetails);
        model.put(EmailConstants.DETAILS, details.toString());
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email.ftl"), model);

    }

    public String prepareTemplateDataForMetricsSubmissionConfirmation(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.STATUS_MSG, EmailConstants.METRICS_SUBMISSION_CONFIRMATION);
        model.put(EmailConstants.REVIEWER_GROUP, userEmailTemplateDetails.getUserName());
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);

        StringBuilder details = prepareTemplateDetails(userEmailTemplateDetails);
        model.put(EmailConstants.DETAILS, details.toString());
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email.ftl"), model);

    }


    public String prepareTemplateDataForSubmittMetricsToReviewer(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.STATUS_MSG, EmailConstants.METRICS_SUBMITTED_FOR_REVIEW);
        model.put(EmailConstants.REVIEWER_GROUP, EmailConstants.REVIEWER);
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);
        StringBuilder details = prepareTemplateDetails(userEmailTemplateDetails);
        model.put(EmailConstants.DETAILS, details.toString());
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email.ftl"), model);

    }

    public String prepareTemplateDataForAssessmentReviewed(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.STATUS_MSG, EmailConstants.ASSESSMENT_REVIEWED);
        model.put(EmailConstants.REVIEWER_GROUP, userEmailTemplateDetails.getUserName());
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);
        StringBuilder details = prepareTemplateDetails(userEmailTemplateDetails);
        model.put(EmailConstants.DETAILS, details.toString());
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email.ftl"), model);

    }

    public String prepareTemplateDataForSubmittedMetricsReviewed(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.STATUS_MSG, EmailConstants.METRICS_REVIEWED);
        model.put(EmailConstants.REVIEWER_GROUP, userEmailTemplateDetails.getUserName());
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);
        StringBuilder details = prepareTemplateDetails(userEmailTemplateDetails);
        model.put(EmailConstants.DETAILS, details.toString());
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email.ftl"), model);

    }


    public String prepareTemplateDataForAssessmentSubmittedToReviewer(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.STATUS_MSG, EmailConstants.ASSESSMENT_SUBMITTED_FOR_REVIEW);
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);
        model.put(EmailConstants.REVIEWER_GROUP, EmailConstants.REVIEWER);
        StringBuilder details = prepareTemplateDetails(userEmailTemplateDetails);
        model.put(EmailConstants.DETAILS, details.toString());
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email.ftl"), model);
    }
    private static StringBuilder prepareTemplateDetails(UserEmailTemplateDetails userEmailTemplateDetails,String... optionalArgs) {
        StringBuilder details = new StringBuilder();
        details.append("<table border=\"1\">");
        details.append("<tr><th>Category</th><th>Value</th></tr>");
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getProjectCode())) {
            details.append("<tr><td>").append(EmailConstants.PROJECT_CODE).append("</td><td>").append(userEmailTemplateDetails.getProjectCode()).append("</td></tr>");
        }
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getProjectName())) {
            details.append("<tr><td>").append(EmailConstants.PROJECT_NAME).append("</td><td>").append(userEmailTemplateDetails.getProjectName()).append("</td></tr>");
        }
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getAccountName())) {
            details.append("<tr><td>").append(EmailConstants.ACCOUNT_NAME).append("</td><td>").append(userEmailTemplateDetails.getAccountName()).append("</td></tr>");
        }
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getProjectType())) {
            details.append("<tr><td>").append(EmailConstants.PROJECT_TYPE).append("</td><td>").append(userEmailTemplateDetails.getProjectType()).append("</td></tr>");
        }
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getTemplateName())) {
            details.append("<tr><td>").append(EmailConstants.TEMPLATE_NAME).append("</td><td>").append(userEmailTemplateDetails.getTemplateName()).append("</td></tr>");
        }
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getDeliveryUnit())) {
            details.append("<tr><td>").append(EmailConstants.DELIVERY_UNIT).append("</td><td>").append(userEmailTemplateDetails.getDeliveryUnit()).append("</td></tr>");
        }
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getSubmittedBy())) {
            details.append("<tr><td>").append(EmailConstants.SUBMISSION_BY).append("</td><td>").append(userEmailTemplateDetails.getSubmittedBy()).append("</td></tr>");
        }
        if (StringUtils.isNotBlank(userEmailTemplateDetails.getSubmittedOn())) {
            details.append("<tr><td>").append(EmailConstants.SUBMISSION_DATE).append("</td><td>").append(userEmailTemplateDetails.getSubmittedOn()).append("</td></tr>");
        }
        if(optionalArgs.length>0) {
            if (StringUtils.isNotBlank(userEmailTemplateDetails.getReviewedBy())) {
                details.append("<tr><td>").append(EmailConstants.RETURNED_BY).append("</td><td>").append(userEmailTemplateDetails.getReviewedBy()).append("</td></tr>");
            }
            if (StringUtils.isNotBlank(userEmailTemplateDetails.getReviewOn())) {
                details.append("<tr><td>").append(EmailConstants.RETURNED_ON).append("</td><td>").append(userEmailTemplateDetails.getReviewOn()).append("</td></tr>");
            }
        }else {
            if (StringUtils.isNotBlank(userEmailTemplateDetails.getReviewedBy())) {
                details.append("<tr><td>").append(EmailConstants.REVIEWED_BY).append("</td><td>").append(userEmailTemplateDetails.getReviewedBy()).append("</td></tr>");
            }
            if (StringUtils.isNotBlank(userEmailTemplateDetails.getReviewOn())) {
                details.append("<tr><td>").append(EmailConstants.REVIEWED_ON).append("</td><td>").append(userEmailTemplateDetails.getReviewOn()).append("</td></tr>");
            }
        }
        details.append("</table>");
        return details;
    }


    public void sendActualRemindersForAssessments(UserEmailTemplateDetails userEmailTemplateDetails, NotificationListener notificationListener) {
        try {
            log.debug("sendActualRemindersForAssessments userDetails- {} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_ASSESSMENT_REMINDER);
            String msg = prepareTemplateDataForAssessmentReminders(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("sendActualRemindersForAssessments failed");
        }
        //after  notification sends
        notificationListener.sendNotification(true);
    }

    public String prepareTemplateDataForAssessmentReminders(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(MESSAGE, String.format(EmailConstants.ASSESSMENT_SUBMISSION_IS_DUE_IN_NEXT_X_DAYS,1));
        prepareModelDataForTemplate(userEmailTemplateDetails, model);



        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("emailreminder.ftl"), model);
    }
    public void sendOverDueRemindersForAssessments(UserEmailTemplateDetails userEmailTemplateDetails, NotificationListener notificationListener) {
        try {
            log.debug("sendOverDueRemindersForAssessments userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_ASSESSMENT_OVERDUE_REMINDER);
            String msg = sendOverDueRemindersForAssessments(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("sendActualRemindersForMetrics failed");
        }
        //after  notification sends
        notificationListener.sendNotification(true);
    }

    public String sendOverDueRemindersForAssessments(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(MESSAGE, "<p style=\"color: red;\">"+
                EmailConstants.ASSESSMENT_SUBMISSION_OVERDUE_MESSAGE+" <p>");
        prepareModelDataForTemplate(userEmailTemplateDetails, model);

        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("emailreminder.ftl"), model);
    }

    public void sendActualRemindersForMetrics(UserEmailTemplateDetails userEmailTemplateDetails, NotificationListener notificationListener) {
        try {
            log.debug("sendActualRemindersForMetrics userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
             helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_METRIC_REMINDER);
            String msg = sendActualRemindersForMetrics(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("sendActualRemindersForMetrics failed");
        }
        //after  notification sends
        notificationListener.sendNotification(true);
    }

    public String sendActualRemindersForMetrics(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(MESSAGE, String.format(EmailConstants.METRICS_SUBMISSION_IS_DUE_IN_NEXT_X_DAYS,1));
        prepareModelDataForTemplate(userEmailTemplateDetails, model);
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("emailreminder.ftl"), model);
    }
    public void sendRemindersForMetricsOverdue(UserEmailTemplateDetails userEmailTemplateDetails, NotificationListener notificationListener) {
        try {
            log.debug("sendRemindersForMetricsOverdue userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
             helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            helper.setSubject(EmailConstants.SUBJECT_METRIC_OVERDUE_REMINDER);
            String msg = prepareTemplateDataForMetricsOverdue(userEmailTemplateDetails);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);
        } catch (Throwable throwable) {
            log.error("sendRemindersForMetricsOverdue failed");
        }
        //after  notification sends
        notificationListener.sendNotification(true);
    }

    public String prepareTemplateDataForMetricsOverdue(UserEmailTemplateDetails userEmailTemplateDetails) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(MESSAGE, "<p style=\"color: red;\">"+
                EmailConstants.METRIC_SUBMISSION_OVERDUE_MESSAGE+" <p>");
        prepareModelDataForTemplate(userEmailTemplateDetails, model);
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("emailreminder.ftl"), model);
    }

    private  void prepareModelDataForTemplate(UserEmailTemplateDetails userEmailTemplateDetails, Map<String, Object> model) {
        model.put(USER, userEmailTemplateDetails.getUserName());
        model.put(PROJECT_CODE, userEmailTemplateDetails.getProjectCode());
        model.put(PROJECT_NAME, userEmailTemplateDetails.getProjectName());
        model.put(ACCOUNT_NAME, userEmailTemplateDetails.getAccountName());
        model.put(PROJECT_TYPE, userEmailTemplateDetails.getProjectType());
        model.put(TEMPLAT_ENAME, userEmailTemplateDetails.getTemplateName());
        model.put(DUE_DATE,userEmailTemplateDetails.getDueDate());
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);

    }
    @Async
    public void sendEmailNotificationForAssessmentReturned(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.info("sendEmailNotificationForAssessmentReturned userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            if (ArrayUtils.isNotEmpty(userEmailTemplateDetails.getReviewerEmails())) {
                helper.setCc(userEmailTemplateDetails.getReviewerEmails());
            }
            helper.setSubject(EmailConstants.SUBJECT_ASSESSMENT_RETURNED);
            String msg = prepareTemplateData(userEmailTemplateDetails,ASSESSMENT_RETURNED);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);

        } catch (Throwable throwable) {
            log.error("sendEmailNotificationForAssessmentReturned failed");

        }

    }

    @Async
    public void sendEmailNotificationForMetricReturned(UserEmailTemplateDetails userEmailTemplateDetails) {
        try {
            log.debug("sendEmailNotificationForMetricReturned userDetails-{} ", userEmailTemplateDetails);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setReplyTo(replyTo);
            helper.setTo(userEmailTemplateDetails.getEmailId());
            if (ArrayUtils.isNotEmpty(userEmailTemplateDetails.getReviewerEmails())) {
                helper.setCc(userEmailTemplateDetails.getReviewerEmails());
            }
            helper.setSubject(EmailConstants.SUBJECT_METRIC_RETURNED);
            String msg = prepareTemplateData(userEmailTemplateDetails,METRIC_RETURNED);
            helper.setText(msg, true);
            javaMailSender.send(mimeMessage);

        } catch (Throwable throwable) {
            log.error("sendEmailNotificationForMetricReturned failed");

        }

    }
    public String prepareTemplateData(UserEmailTemplateDetails userEmailTemplateDetails,String value) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.STATUS_MSG, value);
        model.put(EmailConstants.REVIEWER_GROUP, userEmailTemplateDetails.getUserName());
        model.put(EmailConstants.EE_DASHBOARD_URL, eedashboardurl);
        StringBuilder details = prepareTemplateDetails(userEmailTemplateDetails,RETURNED_ON,RETURNED_BY);
        model.put(EmailConstants.DETAILS, details.toString());
        return FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email.ftl"), model);

    }

}

