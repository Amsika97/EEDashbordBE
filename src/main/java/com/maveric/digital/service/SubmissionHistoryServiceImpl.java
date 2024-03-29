package com.maveric.digital.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Reviewer;
import com.maveric.digital.model.embedded.SubmissionHistory;
import com.maveric.digital.pushnotificationservice.EmailConversationUtils;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.maveric.digital.service.ConversationService.ASSESSMENT_NOT_FOUND;
import static com.maveric.digital.service.MetricTemplateServiceImpl.METRIC_NOT_FOUND;
import static com.maveric.digital.utils.ServiceConstants.ZERO;


@Component
@RequiredArgsConstructor
@Slf4j
public class SubmissionHistoryServiceImpl implements SubmissionHistoryService {

    private final AssessmentRepository assessmentRepository;
    private final ConversationService conversationService;
    private final MetricSubmittedRepository metricSubmittedRepository;
    private final MetricConversationService metricConversationService;
    private final EmailConversationUtils emailConversationUtils;
    private final UserRepository userRepository;

    public List<SubmissionHistory> getSubmissionHistory(String submissionFilterRequest) throws JsonProcessingException {
        log.debug("SubmissionHistoryServiceImpl::getSubmissionHistory() call started");
        SubmissionFilterDto submissionFilterDto = conversationService.toSubmissionFilterDtoFromJsonString(submissionFilterRequest);
        List<SubmissionHistory> submissionHistorieList = new ArrayList<>();
        List<Assessment> assessments = switch (evaluateSubmissionFilter(submissionFilterDto)) {
            case SUBMITTED_BY_AND_DATE ->
                    assessmentRepository.findBySubmitedByAndUpdatedAtBetweenOrderByUpdatedAtDesc(submissionFilterDto.getSubmittedBy(), submissionFilterDto.getFromDate(), submissionFilterDto.getToDate());
            case SUBMITTED_BY ->
                    assessmentRepository.findBySubmitedByOrderByUpdatedAtDesc(submissionFilterDto.getSubmittedBy());
            case DATE ->
                    assessmentRepository.findByUpdatedAtBetweenOrderByUpdatedAtDesc(submissionFilterDto.getFromDate(), submissionFilterDto.getToDate());
            case ALL ->
                    assessmentRepository.findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")));
        };

        if (CollectionUtils.isEmpty(assessments)) {
            log.error("Assessments not found from DB for provided filters{}", submissionFilterRequest);
            throw new CustomException(String.format("Assessments not found in DB for provided submission filter details %s", submissionFilterRequest), HttpStatus.OK);
        }
        log.debug("Assessments from DB {}", assessments);
        for (Assessment assessment : assessments) {
            SubmissionHistory submissionHistory = new SubmissionHistory();
            submissionHistory.setTemplateName(assessment.getTemplate().getTemplateName());
            submissionHistory.setSubmitedAt(assessment.getSubmitedAt());
            submissionHistory.setSubmitedBy(assessment.getSubmitedBy());
            submissionHistorieList.add(submissionHistory);
        }
        log.debug("SubmissionHistory list {}", submissionHistorieList);
        log.debug("SubmissionHistoryServiceImpl::getSubmissionHistory() call ended");
        return submissionHistorieList;
    }


    private SubmissionFilterType evaluateSubmissionFilter(SubmissionFilterDto submissionFilterDto) {
        boolean hasSubmittedBy = Objects.nonNull(submissionFilterDto.getSubmittedBy()) && !submissionFilterDto.getSubmittedBy().isBlank();
        boolean hasFromDate = submissionFilterDto.getFromDate() > 0;
        boolean hasToDate = submissionFilterDto.getToDate() > 0;
        if (hasSubmittedBy && hasFromDate && hasToDate) {
            return SubmissionFilterType.SUBMITTED_BY_AND_DATE;
        } else if (hasSubmittedBy) {
            return SubmissionFilterType.SUBMITTED_BY;
        } else if (hasFromDate && hasToDate) {
            return SubmissionFilterType.DATE;
        } else {
            return SubmissionFilterType.ALL;
        }
    }

    private enum SubmissionFilterType {
        ALL,
        SUBMITTED_BY_AND_DATE,
        SUBMITTED_BY,
        DATE
    }

    @Override
    public AssessmentDto editSubmittedAssessments(ReviewerCommentDto request) {
        log.info("SubmissionHistoryServiceImpl::editSubmittedAssessments() call started");
        if (request.getAssessmentId() == null || request.getReviewerId() == null || request.getReviewerName() == null) {
            throw new CustomException("Request is invalid", HttpStatus.BAD_REQUEST);
        }
        Optional<Assessment> assessment = assessmentRepository.findById(request.getAssessmentId());
        if (assessment.isEmpty()) {
            log.error("assessment is not present with the requested ID {}", request.getAssessmentId());
            throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
        }
        if (!assessment.get().getSubmitStatus().equals(AssessmentStatus.SUBMITTED)) {
            log.error("Request cannot be processed for {} status", assessment.get().getSubmitStatus());
            throw new CustomException("Request cannot be processed for " + assessment.get().getSubmitStatus() + " status", HttpStatus.BAD_REQUEST);
        }
        Date submittedDate = new Date(assessment.get().getSubmitedAt());
        assessment.get().setSubmitStatus(AssessmentStatus.SAVE);
        assessment.get().setScore(ZERO);
        assessment.get().setCategoryScores(null);
        assessment.get().setFrequencyOverDueRemindersDate(null);
        assessment.get().setFrequencyReminderDate(null);
        assessment.get().setSubmitedAt(null);
        assessment.get().setIsEdited(Boolean.TRUE);
        assessmentRepository.save(assessment.get());
        log.info("assessment updated");
        prepareAndSendEmailNotificationForAssessment(assessment.get(), submittedDate, request.getReviewerId());
        log.info("SubmissionHistoryServiceImpl::editSubmittedAssessments() call end");
        return conversationService.toAssessmentDto(assessment.get());
    }

    private void prepareAndSendEmailNotificationForAssessment(Assessment assessment, Date submittedDate, String reviewerId) {
        log.info("editSubmittedAssessments() call started");
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(assessment.getSubmitedBy()));
        Optional<User> reviewerObj = userRepository.findByOid(UUID.fromString(reviewerId));
        if (userObj.isPresent()) {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            emailConversationUtils.sendEmailNotificationForAssessmentReturned(setDataForUserEmailTemplateDetails(userEmailTemplateDetails, userObj.get(), reviewerObj.get(), assessment, submittedDate));
        }
    }

    private UserEmailTemplateDetails setDataForUserEmailTemplateDetails(UserEmailTemplateDetails userEmailTemplateDetails, User userObj, User reviewerObj, Assessment assessment, Date submittedDate) {
        log.info("setDataForUserEmailTemplateDetails() call started");
        userEmailTemplateDetails.setUserName(userObj.getName());
        userEmailTemplateDetails.setProjectName(assessment.getProject().getProjectName());
        userEmailTemplateDetails.setProjectCode(assessment.getProject().getProjectCode());
        userEmailTemplateDetails.setAccountName(assessment.getAccount().getAccountName());
        userEmailTemplateDetails.setProjectType(assessment.getProjectType().getProjectTypeName());
        userEmailTemplateDetails.setTemplateName(assessment.getTemplate().getTemplateName());
        userEmailTemplateDetails.setSubmittedOn(String.valueOf(submittedDate));
        userEmailTemplateDetails.setEmailId(userObj.getEmailAddress());
        userEmailTemplateDetails.setReviewedBy(reviewerObj.getName());
        userEmailTemplateDetails.setReviewOn(String.valueOf(new Date(System.currentTimeMillis()).toString()));
        userEmailTemplateDetails.setReviewerEmails(new String[]{reviewerObj.getEmailAddress()});
        log.info("setDataForUserEmailTemplateDetails() call ended");
        return userEmailTemplateDetails;
    }

    @Override
    public MetricSubmittedDto editSubmittedMetrics(MetricReviewerCommentDto request) {
        log.debug("SubmissionHistoryServiceImpl::editSubmittedMetrics() call started");
        if (request.getMetricId() == null || request.getReviewerId() == null || request.getReviewerName() == null) {
            throw new CustomException("Request is invalid", HttpStatus.BAD_REQUEST);
        }
        Optional<MetricSubmitted> metrics = metricSubmittedRepository.findById(request.getMetricId());
        if (metrics.isEmpty()) {
            log.error("metric is not present for the requested ID {}", request.getMetricId());
            throw new MetricNotFoundException(METRIC_NOT_FOUND);
        }
        if (!metrics.get().getSubmitStatus().equals(AssessmentStatus.SUBMITTED)) {
            log.error("Request cannot be processed for {} status", metrics.get().getSubmitStatus());
            throw new CustomException("Request cannot be processed for " + metrics.get().getSubmitStatus() + " status", HttpStatus.BAD_REQUEST);
        }
        Date submittedDate = new Date(metrics.get().getSubmittedAt());
        metrics.get().setSubmitStatus(AssessmentStatus.SAVE);
        metrics.get().setScore(ZERO);
        metrics.get().setCategorywiseScores(null);
        metrics.get().setFrequencyOverDueRemindersDate(null);
        metrics.get().setFrequencyReminderDate(null);
        metrics.get().setSubmittedAt(null);
        metrics.get().setIsEdited(Boolean.TRUE);
        metricSubmittedRepository.save(metrics.get());
        log.info("metric updated in DB");
        prepareAndSendEmailNotificationForMetric(metrics.get(), submittedDate,request.getReviewerId());
        log.info("SubmissionHistoryServiceImpl::editSubmittedMetrics() call ended");
        return metricConversationService.toMetricSubmitDto(metrics.get());
    }

    private void prepareAndSendEmailNotificationForMetric(MetricSubmitted metricSubmitted, Date submittedDate,String reviewerId) {
        log.info("prepareAndSendEmailNotificationForMetric method call started");
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(metricSubmitted.getSubmittedBy()));
        Optional<User> reviewerObj = userRepository.findByOid(UUID.fromString(reviewerId));
        if (userObj.isPresent()) {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            emailConversationUtils.sendEmailNotificationForMetricReturned(setDataForUserEmailTemplateDetailsMetrics(userEmailTemplateDetails, userObj.get(), reviewerObj.get(), metricSubmitted, submittedDate));
        }
    }

    private UserEmailTemplateDetails setDataForUserEmailTemplateDetailsMetrics(UserEmailTemplateDetails userEmailTemplateDetails, User userObj, User reviewerObj, MetricSubmitted metricSubmitted, Date submittedDate) {
        log.info("setDataForUserEmailTemplateDetailsMetrics method call started");
        userEmailTemplateDetails.setUserName(userObj.getName());
        userEmailTemplateDetails.setProjectName(metricSubmitted.getProject().getProjectName());
        userEmailTemplateDetails.setProjectCode(metricSubmitted.getProject().getProjectCode());
        userEmailTemplateDetails.setAccountName(metricSubmitted.getAccount().getAccountName());
        userEmailTemplateDetails.setProjectType(metricSubmitted.getProjectType().getProjectTypeName());
        userEmailTemplateDetails.setTemplateName(metricSubmitted.getTemplate().getTemplateName());
        userEmailTemplateDetails.setSubmittedOn(String.valueOf(submittedDate));
        userEmailTemplateDetails.setEmailId(userObj.getEmailAddress());
        userEmailTemplateDetails.setReviewedBy(reviewerObj.getName());
        userEmailTemplateDetails.setReviewOn(String.valueOf(new Date(System.currentTimeMillis()).toString()));
        userEmailTemplateDetails.setReviewerEmails(new String[]{reviewerObj.getEmailAddress()});
        log.info("setDataForUserEmailTemplateDetailsMetrics method call ended");
        return userEmailTemplateDetails;

    }

}
