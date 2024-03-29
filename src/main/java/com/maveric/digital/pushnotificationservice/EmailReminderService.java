package com.maveric.digital.pushnotificationservice;

import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.User;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.UserEmailTemplateDetails;
import com.maveric.digital.utils.ServiceConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailReminderService {


    private final AssessmentRepository assessmentRepository;
    private final MetricSubmittedRepository metricSubmittedRepository;

    private final EmailConversationUtils emailConversationUtils;
    private final UserRepository userRepository;

    private record CurrentDateWithStartAndEndTime(long startOfDay, long endOfDay) {
    }

    private CurrentDateWithStartAndEndTime getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        long startOfDay = currentDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        long endOfDay = currentDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        return new CurrentDateWithStartAndEndTime(startOfDay, endOfDay);
    }

    public void sendActualRemindersForAssessments() {
        log.info("sendActualRemindersForAssessments called");
        CurrentDateWithStartAndEndTime currentDateWithStartAndEndTime = getCurrentDate();
        List<Assessment> assessmentList = assessmentRepository.findByFrequencyReminderDateBetweenAndIsFrequencyRequiredTrue(currentDateWithStartAndEndTime.startOfDay(), currentDateWithStartAndEndTime.endOfDay());
        if (CollectionUtils.isEmpty(assessmentList)) {
            return;
        }
        sendRemindersForAssessments(assessmentList, false);
    }

    public void sendOverDueRemindersForAssessment() {
        log.info("sendOverDueRemindersForAssessment called");
        CurrentDateWithStartAndEndTime currentDateWithStartAndEndTime = getCurrentDate();
        List<Assessment> assessmentsReminders = assessmentRepository.findByFrequencyOverDueRemindersDateBetweenAndIsFrequencyRequiredTrue(currentDateWithStartAndEndTime.startOfDay(), currentDateWithStartAndEndTime.endOfDay());
        if (CollectionUtils.isEmpty(assessmentsReminders)) {
            return;
        }
        sendRemindersForAssessments(assessmentsReminders, true);
    }


    private void sendRemindersForAssessments(List<Assessment> assessments, boolean isOverdue) {
        assessments.forEach(assessment -> prepareAndSendMetricSubmittedEmailReminder(assessment, isOverdue));
    }

    private void updateFrequencyOverDueRemindersSent(Assessment assessment) {
        if (CollectionUtils.isEmpty(assessment.getFrequencyRemindersSent())) {
            assessment.setFrequencyRemindersSent(new ArrayList<>());
        }
        assessment.getFrequencyRemindersSent().add(System.currentTimeMillis());
        assessmentRepository.updateFrequencyRemindersSent(assessment.getId(), assessment.getFrequencyRemindersSent());
    }

    private void prepareAndSendMetricSubmittedEmailReminder(Assessment assessment, boolean isOverdue) {
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(assessment.getSubmitedBy()));
        if (userObj.isPresent()) {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            userEmailTemplateDetails.setUserName(userObj.get().getName());
            userEmailTemplateDetails.setProjectName(assessment.getProject().getProjectName());
            userEmailTemplateDetails.setProjectCode(assessment.getProject().getProjectCode());
            userEmailTemplateDetails.setAccountName(assessment.getAccount().getAccountName());
            userEmailTemplateDetails.setProjectType(assessment.getProjectType().getProjectTypeName());
            userEmailTemplateDetails.setTemplateName(assessment.getTemplate().getTemplateName());
            userEmailTemplateDetails.setDueDate( new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(new Date(assessment.getFrequencyReminderDate()),1)));
            userEmailTemplateDetails.setEmailId(userObj.get().getEmailAddress());
            if (!isOverdue) {
                emailConversationUtils.sendActualRemindersForAssessments(userEmailTemplateDetails, flag -> updateFrequencyOverDueRemindersSent(assessment));
            } else {
                emailConversationUtils.sendOverDueRemindersForAssessments(userEmailTemplateDetails, flag -> updateFrequencyOverDueRemindersSent(assessment));
            }

        }
    }


    public void sendActualRemindersForMetrics() {
        log.info("sendActualRemindersForMetrics called");
        CurrentDateWithStartAndEndTime currentDateWithStartAndEndTime = getCurrentDate();
        List<MetricSubmitted> metricSubmittedList = metricSubmittedRepository.findByFrequencyReminderDateBetweenAndIsFrequencyRequiredTrue(currentDateWithStartAndEndTime.startOfDay(), currentDateWithStartAndEndTime.endOfDay());
        if (CollectionUtils.isEmpty(metricSubmittedList)) {
            return;
        }
        sendRemindersForMetricSubmitted(metricSubmittedList, false);
    }

    public void sendOverdueRemindersForMetrics() {
        log.info("sendOverdueRemindersForMetrics called");
        CurrentDateWithStartAndEndTime currentDateWithStartAndEndTime = getCurrentDate();
        List<MetricSubmitted> metricSubmittedList = metricSubmittedRepository.findByFrequencyOverDueRemindersDateBetweenAndIsFrequencyRequiredTrue(currentDateWithStartAndEndTime.startOfDay(), currentDateWithStartAndEndTime.endOfDay());
        if (CollectionUtils.isEmpty(metricSubmittedList)) {
            return;
        }
        sendRemindersForMetricSubmitted(metricSubmittedList, true);
    }

    private void sendRemindersForMetricSubmitted(List<MetricSubmitted> metricSubmitteds, boolean isOverDue) {
        for (MetricSubmitted assessment : metricSubmitteds) {
            prepareAndSendMetricSubmittedEmailReminder(assessment, isOverDue);
        }
    }

    private void prepareAndSendMetricSubmittedEmailReminder(MetricSubmitted metricSubmitted, boolean isOverDue) {
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(metricSubmitted.getSubmittedBy()));
        if (userObj.isPresent()) {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            userEmailTemplateDetails.setUserName(userObj.get().getName());
            userEmailTemplateDetails.setProjectName(metricSubmitted.getProject().getProjectName());
            userEmailTemplateDetails.setProjectCode(metricSubmitted.getProject().getProjectCode());
            userEmailTemplateDetails.setAccountName(metricSubmitted.getAccount().getAccountName());
            userEmailTemplateDetails.setProjectType(metricSubmitted.getProjectType().getProjectTypeName());
            userEmailTemplateDetails.setTemplateName(metricSubmitted.getTemplate().getTemplateName());
            userEmailTemplateDetails.setDueDate( new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(new Date(metricSubmitted.getFrequencyReminderDate()),1)));
            userEmailTemplateDetails.setEmailId(userObj.get().getEmailAddress());
            if (isOverDue) {
                emailConversationUtils.sendRemindersForMetricsOverdue(userEmailTemplateDetails, flag -> {
                    updateFrequencyOverDueRemindersSent(metricSubmitted);
                });
            } else {
                emailConversationUtils.sendActualRemindersForMetrics(userEmailTemplateDetails, flag -> {
                    updateFrequencyOverDueRemindersSent(metricSubmitted);
                });
            }

        }
    }

    private void updateFrequencyOverDueRemindersSent(MetricSubmitted metricSubmitted) {
        if (CollectionUtils.isEmpty(metricSubmitted.getFrequencyRemindersSent())) {
            metricSubmitted.setFrequencyRemindersSent(new ArrayList<>());
        }
        metricSubmitted.getFrequencyRemindersSent().add(System.currentTimeMillis());
        metricSubmittedRepository.updateFrequencyRemindersSent(metricSubmitted.getId(), metricSubmitted.getFrequencyRemindersSent());
    }

}
