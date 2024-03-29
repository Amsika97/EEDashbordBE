package com.maveric.digital.config;

import com.maveric.digital.pushnotificationservice.EmailReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfigurations {

    private final EmailReminderService emailReminderService;

    @Value("${overdue.reminders}")
    private final String assessmentReminders="0 0 6 * * *";
    @Value("${assessment.overdue.reminder.cron}")
    private final String assessmentOverDueReminders="0 0 7 * * *";
    @Value("${metric.reminder.cron}")
    private final String metricsReminders="0 0 6 * * *";
    @Value("${threshold.overdue.reminders}")
    private final String metricsOverDueReminders="0 0 7 * * *";

    @Scheduled(cron = assessmentReminders)
    public void sendAssessmentReminders() {
        emailReminderService.sendActualRemindersForAssessments();
    }
    @Scheduled(cron = assessmentOverDueReminders)

    public void senOverdueAssessmentReminders() {
        emailReminderService.sendOverDueRemindersForAssessment();
    }
    @Scheduled(cron = metricsReminders)

    public void sendMetricSubmitReminders() {
        emailReminderService.sendActualRemindersForMetrics();
    }
    @Scheduled(cron = metricsOverDueReminders)
    public void sendMetricSubmitOverDueReminders() {
        emailReminderService.sendOverdueRemindersForMetrics();
    }
}
