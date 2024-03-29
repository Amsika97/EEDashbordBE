package com.maveric.digital.pushnotificationservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class EmailController {

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private EmailReminderService emailReminderService;

    @PostMapping("/sendMail")
    public ResponseEntity<String> sendMail() {
        log.debug("EmailController::sendMail:: call started");
        String message = (emailService.sendEmail());
        log.debug("EmailController::sendMail:: call ended");
        return ResponseEntity.ok(message);

    }

    @PostMapping("/send/assessment/reminder/mail")
    public ResponseEntity<String> sendAssessmentReminderMail() {
        log.debug("EmailController::sendAssessmentReminderMail:: call started");
        emailReminderService.sendActualRemindersForAssessments();
        log.debug("EmailController::sendAssessmentReminderMail:: call ended");
        return ResponseEntity.ok("Sent AssessmentReminder Email Successfully.");

    }

    @PostMapping("/send/assessment/overdue/reminder/mail")
    public ResponseEntity<String> sendAssessmentOverdueReminderMail() {
        log.debug("EmailController::sendAssessmentOverdueReminderMail:: call started");
        emailReminderService.sendOverDueRemindersForAssessment();
        log.debug("EmailController::sendAssessmentOverdueReminderMail:: call ended");
        return ResponseEntity.ok("Sent Overdue AssessmentReminder Email Successfully.");

    }

    @PostMapping("/send/metrics/reminder/mail")
    public ResponseEntity<String> sendMetricsReminderMail() {
        log.debug("EmailController::sendMetricsReminderMail:: call started");
        emailReminderService.sendActualRemindersForMetrics();
        log.debug("EmailController::sendMetricsReminderMail:: call ended");
        return ResponseEntity.ok("Sent  Metrics Reminder Email Successfully.");

    }

    @PostMapping("/send/metrics/overdue/reminder/mail")
    public ResponseEntity<String> sendMetricsOverdueReminderMail() {
        log.debug("EmailController::sendAssessmentReminderMail:: call started");
        emailReminderService.sendOverdueRemindersForMetrics();
        log.debug("EmailController::sendAssessmentReminderMail:: call ended");
        return ResponseEntity.ok("Sent Overdue Metrics Reminder Email Successfully.");

    }


}
