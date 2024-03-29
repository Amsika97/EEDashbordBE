package com.maveric.digital.service;

import com.maveric.digital.exceptions.AccountsNotFoundException;
import com.maveric.digital.exceptions.ProjectNotFoundException;
import com.maveric.digital.exceptions.ResourceNotFoundException;

import com.maveric.digital.model.embedded.MetricSubmitProjectCategory;
import com.maveric.digital.model.embedded.MetricSubmitTemplateQuestionnaire;
import com.maveric.digital.model.embedded.ReviewerQuestionWeightage;

import java.util.*;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.Filters;
import com.maveric.digital.pushnotificationservice.EmailConversationUtils;
import com.maveric.digital.repository.*;
import com.maveric.digital.responsedto.*;
import com.maveric.digital.utils.ServiceConstants;

import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.exceptions.SubmitMetricLineChartDataNotFoundException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Reviewer;
import com.maveric.digital.model.embedded.ReviewerQuestionComment;
import com.maveric.digital.projection.LineChartProjection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.maveric.digital.utils.EmailReminderUtils.*;
import static com.maveric.digital.utils.ServiceConstants.REVIEWER;
import static com.maveric.digital.utils.ServiceConstants.UNIT;
import static com.maveric.digital.utils.WeightageMap.WEIGHTAGE_MAP;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricSubmitServiceImpl implements MetricSubmitService {
    private final MetricSubmittedRepository metricSubmittedRepository;
    private final MetricTemplateRepository metricTemplateRepository;
    private final MetricConversationService conversationService;
    private final AccountRepository accountRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final ProjectRepository projectRepository;
    private final AssessmentService assessmentService;
    private final EmailConversationUtils emailConversationUtils;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(MetricSubmitServiceImpl.class);
    public static final String METRIC_TEMPLATE_NOT_FOUND = "metric tempate not found";
    public static final String METRIC_NOT_FOUND = "Metric Not Found";
    public static final String AUDIT_ENTITY_METRIC = "METRIC";
    public static final String SUBMITMETRIC_DATA_NOT_FOUND = "Submit Metric Line chart data not found";

    @Value("${overdue.reminders}")
    private int overDueReminders = 3;
    @Value("${threshold.overdue.reminders}")
    private int thresholdForOverDueReminders = 2;

    @Value("${send.reminder.days.before}")
    private int sendReminderAtBefore = 1;

    @Override
    public MetricSubmitted saveMetricReviewerComment(MetricReviewerCommentDto request) {
        log.debug("MetricSubmitServiceImpl::submittedMetric()::Start");
        Optional<MetricSubmitted> metricsubmittedobj = metricSubmittedRepository.findById(request.getMetricId());

        if (!metricsubmittedobj.isPresent()) {
            throw new MetricNotFoundException(METRIC_NOT_FOUND);
        }
        double score;
        MetricSubmitted metricsubmitted = metricsubmittedobj.orElseThrow();
        this.populateSaveMetricReviewerCommentByMetricId(metricsubmitted, request.getReviewerId(), request.getComment(), request.getReviewerName());

        request.getReviewerQuestionComment().stream()
                .filter(comment -> StringUtils.isNotBlank(comment.getReviewerComment()))
                .forEach(comment -> updateQuestionCommentInSubmittedMetric(metricsubmitted, comment));
        request.getReviewerQuestionWeightage().stream().filter(weightage -> StringUtils.isNotBlank(weightage.getReviewerWeightage())).forEach(weightage -> updateReviewerWeightage(metricsubmitted, weightage));

        List<ReviewerQuestionWeightage> weightages = request.getReviewerQuestionWeightage().stream().toList();
        score = scoreCalculation(weightages, metricsubmitted);
        if (AssessmentStatus.REVIEWED.equals(request.getStatus())) {
            metricsubmitted.setSubmitStatus(request.getStatus());
            metricsubmitted.setScore(score);
            metricsubmitted.setUpdatedAt(System.currentTimeMillis());
        }
        metricSubmittedRepository.save(metricsubmitted);
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(metricsubmitted.getSubmittedBy()));
        Optional<User> reviewerObj = userRepository.findByOid(UUID.fromString(request.getReviewerId()));
        if (userObj.isPresent()) {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            userEmailTemplateDetails.setUserName(userObj.get().getName());
            userEmailTemplateDetails.setProjectName(metricsubmitted.getProject().getProjectName());
            userEmailTemplateDetails.setProjectCode(metricsubmitted.getProject().getProjectCode());
            userEmailTemplateDetails.setAccountName(metricsubmitted.getAccount().getAccountName());
            userEmailTemplateDetails.setProjectType(metricsubmitted.getProjectType().getProjectTypeName());
            userEmailTemplateDetails.setSubmittedOn(new Date(metricsubmitted.getSubmittedAt()).toString());
            userEmailTemplateDetails.setStatus(metricsubmitted.getSubmitStatus().name());
            userEmailTemplateDetails.setEmailId(userObj.get().getEmailAddress());
            userEmailTemplateDetails.setReviewedBy(metricsubmitted.getReviewers().get(0).getReviewerName());
            userEmailTemplateDetails.setReviewOn(new Date(System.currentTimeMillis()).toString());
            if (reviewerObj.isPresent()) {
                String[] s = {reviewerObj.get().getEmailAddress()};
                userEmailTemplateDetails.setReviewerEmails(s);
            }

            emailConversationUtils.sendEmailNotificationForMetricsReviewed(userEmailTemplateDetails);
        }
        log.debug("MetricSubmitServiceImpl::submittedMetric()::{}", metricsubmitted);
        log.debug("MetricSubmitServiceImpl::submittedMetric::End");
        return metricsubmitted;
    }

    private void updateQuestionCommentInSubmittedMetric(MetricSubmitted metricsubmitted, ReviewerQuestionComment questionComment) {
        metricsubmitted.getProjectCategory().stream()
                .flatMap(category -> category.getTemplateQuestionnaire().stream())
                .filter(answer -> answer.getQuestionId().equals(questionComment.getQuestionId()))
                .findFirst()
                .ifPresent(answer -> {
                    answer.setReviewerComment(questionComment.getReviewerComment());
                });
    }

    private void updateReviewerWeightage(MetricSubmitted metricsubmitted, ReviewerQuestionWeightage questionWeightage) {
        metricsubmitted.getProjectCategory().stream()
                .flatMap(category -> category.getTemplateQuestionnaire().stream())
                .filter(answer -> answer.getQuestionId().equals(questionWeightage.getQuestionId()))
                .findFirst()
                .ifPresent(answer -> {
                    answer.setReviewerWeightage(questionWeightage.getReviewerWeightage());
                });
    }

    public double scoreCalculation(List<ReviewerQuestionWeightage> weightages, MetricSubmitted metricSubmitted) {
        if (!AssessmentStatus.SUBMITTED.equals(metricSubmitted.getSubmitStatus()) || weightages == null || weightages.isEmpty()) {
            return NumberUtils.DOUBLE_ZERO;
        }

        double totalScore = 0.0;
        int noOfQuestions = 0;
        List<CategoryWiseScore> categoryScores = new ArrayList<>();

        for (MetricSubmitProjectCategory projectCategory : metricSubmitted.getProjectCategory()) {
            double categoryScore = 0.0;
            int totalQuestionsInCategory = 0;

            for (MetricSubmitTemplateQuestionnaire questionAnswer : projectCategory.getTemplateQuestionnaire()) {
                for (ReviewerQuestionWeightage weightage : weightages) {
                    if (questionAnswer.getQuestionId().equals(weightage.getQuestionId())) {
                        Double numericWeightage = convertWeightageToNumeric(weightage.getReviewerWeightage());
                        if (numericWeightage != null && numericWeightage != -1) {
                            categoryScore += numericWeightage;
                            totalScore += numericWeightage;
                            totalQuestionsInCategory++;
                            noOfQuestions++;
                        }
                    }
                }
            }

            if (totalQuestionsInCategory > 0) {
                categoryScores.add(new CategoryWiseScore(projectCategory.getCategoryName(), (categoryScore * 100.0) / totalQuestionsInCategory, totalQuestionsInCategory));
            } else {
                categoryScores.add(new CategoryWiseScore(projectCategory.getCategoryName(), 0, 0));
            }
            metricSubmitted.setCategorywiseScores(categoryScores);
        }

        if (noOfQuestions > 0) {
            return totalScore * 100 / noOfQuestions;
        } else {
            return NumberUtils.DOUBLE_ZERO;
        }
    }

    private Double convertWeightageToNumeric(String weightage) {
        if (StringUtils.isBlank(weightage)) {
            return null;
        }
        return WEIGHTAGE_MAP.get(weightage);
    }

    private void populateSaveMetricReviewerCommentByMetricId(MetricSubmitted metricsubmitted, String reviewerid, String comment, String reviewerName) {
        if (CollectionUtils.isEmpty(metricsubmitted.getReviewers())) {
            metricsubmitted.setReviewers(new ArrayList<>());
        }
        Optional<Reviewer> metricreviewercommentobj = metricsubmitted.getReviewers().stream()
                .filter(metricreviewercomment -> metricreviewercomment.getReviewerId().equals(reviewerid)).findFirst();
        if (metricreviewercommentobj.isPresent()) {
            metricreviewercommentobj.get().setComment(comment);
            metricreviewercommentobj.get().setReviewerAt(System.currentTimeMillis());
            metricreviewercommentobj.get().setReviewerName(reviewerName);
            return;
        }
        if (metricreviewercommentobj.isEmpty()) {
            Reviewer reviewer = new Reviewer();
            reviewer.setComment(comment);
            reviewer.setReviewerAt(System.currentTimeMillis());
            reviewer.setReviewerId(reviewerid);
            reviewer.setReviewerName(reviewerName);
            metricsubmitted.getReviewers().add(reviewer);
        }
    }

    @Override
    public MetricSubmitted saveOrSubmitMetric(MetricSubmittedDto requestPayload) {
        logger.debug("MetricSubmitServiceImpl::saveOrSubmitMetric()::Start");
        if (Objects.isNull(requestPayload)) {
            throw new IllegalArgumentException("Request do not validated");
        }
        Optional<MetricSubmitted> metricSubmitted = Optional.empty();
        Optional<MetricTemplate> template = metricTemplateRepository.findById(requestPayload.getMetricTemplateId());
        if (template.isEmpty()) {
            throw new TemplateNotFoundException(METRIC_TEMPLATE_NOT_FOUND);
        }
        if (Objects.nonNull(requestPayload.getMetricId())) {
            metricSubmitted = metricSubmittedRepository.findById(requestPayload.getMetricId());
        }
        if (metricSubmitted.isPresent()) {
            metricSubmitted.get().setProjectCategory(requestPayload.getProjectCategory());
            metricSubmitted.get().setSubmitStatus(requestPayload.getSubmitStatus());
            metricSubmitted.get().setSubmittedAt(
                    this.isSubmited(metricSubmitted, requestPayload) ? System.currentTimeMillis() : null);
            metricSubmitted.get().setDescription(requestPayload.getDescription());
            metricSubmitted.get().setUpdatedAt(System.currentTimeMillis());
            if (AssessmentStatus.SUBMITTED.equals(metricSubmitted.get().getSubmitStatus())) {

                metricSubmitted.get().setFrequencyReminderDate(TemplateFrequencyReminder.NA.equals(metricSubmitted.get().getTemplate().getTemplateFrequency()) ? 0 : setEmailRemainderDate(template.get().getTemplateFrequency(), sendReminderAtBefore));
                metricSubmitted.get().setFrequencyOverDueRemindersDate(TemplateFrequencyReminder.NA.equals(metricSubmitted.get().getTemplate().getTemplateFrequency()) ? List.of() : setEmailOverDueRemainderDates(template.get().getTemplateFrequency(), overDueReminders, thresholdForOverDueReminders));
                metricSubmitted.get().setIsFrequencyRequired(true);

            }
            metricSubmittedRepository.save(metricSubmitted.get());
            if (AssessmentStatus.SUBMITTED.equals(metricSubmitted.get().getSubmitStatus())) {
                prepareAndSendEmailNotifications(metricSubmitted.get());
                checkAndDeActiveOldTemplate(metricSubmitted.get());

            }
            return metricSubmitted.get();
        }
        Account account = accountRepository.findById(requestPayload.getAccountId())
                .orElseThrow(() -> new AccountsNotFoundException("Account not found with ID: " + requestPayload.getAccountId()));

	/*	BusinessUnit businessUnit = businessUnitRepository.findById(requestPayload.getBusinessUnitId())
				.orElseThrow(() -> new ResourceNotFoundException("BusinessUnit not found with ID: " + requestPayload.getBusinessUnitId()));
*/
        ProjectType projectType = projectTypeRepository.findById(requestPayload.getProjectTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectType not found with ID: " + requestPayload.getProjectTypeId()));

        Project project = projectRepository.findById(requestPayload.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + requestPayload.getProjectId()));

        metricSubmitted = Optional.of(conversationService.toMetricSubmitted(requestPayload, template.get(), account,
                projectType, project));
        metricSubmitted.get()
                .setSubmittedAt(this.isSubmited(metricSubmitted, requestPayload) ? System.currentTimeMillis() : null);
        if (AssessmentStatus.SUBMITTED.equals(metricSubmitted.get().getSubmitStatus())) {

            metricSubmitted.get().setFrequencyReminderDate(TemplateFrequencyReminder.NA.equals(metricSubmitted.get().getTemplate().getTemplateFrequency()) ? 0 : setEmailRemainderDate(template.get().getTemplateFrequency(), sendReminderAtBefore));
            metricSubmitted.get().setFrequencyOverDueRemindersDate(TemplateFrequencyReminder.NA.equals(metricSubmitted.get().getTemplate().getTemplateFrequency()) ? List.of() : setEmailOverDueRemainderDates(template.get().getTemplateFrequency(), overDueReminders, thresholdForOverDueReminders));
            metricSubmitted.get().setIsFrequencyRequired(true);

        }
        metricSubmittedRepository.save(metricSubmitted.get());
        if (AssessmentStatus.SUBMITTED.equals(metricSubmitted.get().getSubmitStatus())) {
            prepareAndSendEmailNotifications(metricSubmitted.get());
            checkAndDeActiveOldTemplate(metricSubmitted.get());

        }
        logger.debug("MetricSubmitServiceImpl::saveOrSubmitMetric()::metricSubmitted::{}", metricSubmitted);
        logger.debug("MetricSubmitServiceImpl::saveOrSubmitMetric()::End");
        return metricSubmitted.get();
    }

    private void checkAndDeActiveOldTemplate(MetricSubmitted metricSubmitted) {
        metricSubmittedRepository.findBySubmittedByAndTemplateIdAndProjectIdAndUpdateisFrequencyRequired(metricSubmitted.getSubmittedBy(), metricSubmitted.getTemplate().getId(), metricSubmitted.getProject().getId(), false, metricSubmitted.getId());
    }

    private void prepareAndSendEmailNotifications(MetricSubmitted metricSubmitted) {
        logger.debug("MetricSubmitServiceImpl::prepareAndSendEmailNotifications()::metricSubmitted::{}", metricSubmitted);
        if (StringUtils.isBlank(metricSubmitted.getSubmittedBy())) {
            return;
        }
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(metricSubmitted.getSubmittedBy()));
        logger.debug("MetricSubmitServiceImpl::prepareAndSendEmailNotifications()::userObj::{}", userObj);
        if (userObj.isEmpty() || StringUtils.isBlank(userObj.get().getEmailAddress())) {
            return;
        }
        UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
        userEmailTemplateDetails.setUserName(userObj.get().getName());
        userEmailTemplateDetails.setProjectName(metricSubmitted.getProject().getProjectName());
        userEmailTemplateDetails.setProjectCode(metricSubmitted.getProject().getProjectCode());
        userEmailTemplateDetails.setProjectType(metricSubmitted.getProjectType().getProjectTypeName());
        userEmailTemplateDetails.setAccountName(metricSubmitted.getAccount().getAccountName());
        userEmailTemplateDetails.setSubmittedOn(new Date(metricSubmitted.getSubmittedAt()).toString());
        userEmailTemplateDetails.setEmailId(userObj.get().getEmailAddress());
        userEmailTemplateDetails.setTemplateName(metricSubmitted.getTemplate().getTemplateName());
        emailConversationUtils.sendEmailNotificationForMetricSubmittedConfirmation(userEmailTemplateDetails);
        userEmailTemplateDetails.setSubmittedBy(metricSubmitted.getSubmitterName());
        prepareAndSendNotificationToReviewer(userEmailTemplateDetails);
    }

    private void prepareAndSendNotificationToReviewer(UserEmailTemplateDetails userEmailTemplateDetails) {
        Optional<List<User>> listOfReviewers = userRepository.findByRoleIn(List.of(Roles.Reviewer));
        if (listOfReviewers.isEmpty() || CollectionUtils.isEmpty(listOfReviewers.get())) {
            return;
        }
        List<String> emailIdsList = listOfReviewers.get().stream().map(User::getEmailAddress)
                .filter(emailAddress -> !StringUtils.equalsAnyIgnoreCase(userEmailTemplateDetails.getEmailId(), emailAddress)).toList();
        if (!CollectionUtils.isEmpty(emailIdsList)) {
            String[] s = emailIdsList.toArray(new String[0]);
            userEmailTemplateDetails.setReviewerEmails(s);
            emailConversationUtils.sendEmailNotificationForMetricSubmittedToReviewer(userEmailTemplateDetails);
        }
    }

    private boolean isSubmited(Optional<MetricSubmitted> metricSubmitted, MetricSubmittedDto requestPayload) {
        return metricSubmitted.isPresent() && Objects.nonNull(requestPayload)
                && requestPayload.getSubmitStatus().equals(AssessmentStatus.SUBMITTED);
    }

    @Override
    public MetricSubmitted findMetricById(Long metricId) {
        logger.debug("MetricSubmitServiceImpl::findMetricById() - Start: Searching for metricId with ID {}", metricId);

        if (metricId == null || metricId <= 0) {
            logger.error("MetricSubmitServiceImpl::findAssessmentById() - metric ID Not Found: {}", metricId);
            throw new IllegalArgumentException("metricId ID not found: " + metricId);
        }

        Optional<MetricSubmitted> metricSubmitte = metricSubmittedRepository.findById(metricId);

        if (!metricSubmitte.isPresent()) {
            logger.warn("MetricSubmitServiceImpl::findMetricById() - MetricSubmitted with ID {} not found", metricId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "MetricSubmitted with ID " + metricId + " not found");
        }

        logger.debug("MetricSubmitServiceImpl::findMetricById() - End: Found MetricSubmitted with ID {}", metricId);
        return metricSubmitte.get();
    }

    @Override
    public List<LineChartProjection> submitMetricLineChartStartAndEndDates(Long startDate, Long endDate, String filterName, String filterValue) {
        log.debug("MetricSubmitServiceImpl::submitMetricLineChartStartAndEndDates()::Start");
        List<LineChartProjection> submitmetriclinechartdatalist;
        List<Long> filterValues = new ArrayList<>();
        if (StringUtils.isNotBlank(filterValue)) {
            filterValues = Arrays.stream(filterValue.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();
        }
        switch (filterName) {
            case ServiceConstants.PROJECT ->
                    submitmetriclinechartdatalist = metricSubmittedRepository.submitMetricLineChartStartAndEndDatesAndProjectId(new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(), new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1).getMillis(), filterValues, List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));

            case ServiceConstants.PROJECT_TYPE ->
                    submitmetriclinechartdatalist = metricSubmittedRepository.submitMetricLineChartStartAndEndDatesProjectTypeId(new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(), new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1).getMillis(), Long.valueOf(filterValue), List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
            case ServiceConstants.ACCOUNT ->
                    submitmetriclinechartdatalist = metricSubmittedRepository.submitMetricLineChartStartAndEndDatesAndAccountId(new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(), new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1).getMillis(), Long.valueOf(filterValue), List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
            default ->
                    submitmetriclinechartdatalist = metricSubmittedRepository.submitMetricLineChartStartandEndDates(new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(), new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1).getMillis(), List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        }

        if (CollectionUtils.isEmpty(submitmetriclinechartdatalist)) {
            throw new SubmitMetricLineChartDataNotFoundException(SUBMITMETRIC_DATA_NOT_FOUND);
        }
        assessmentService.getDateRangeBetween(startDate, endDate, submitmetriclinechartdatalist);
        this.sortLineChartProject(submitmetriclinechartdatalist);
        log.debug("MetricSubmitServiceImpl::submitMetricLineChartStartAndEndDates()::End");
        return submitmetriclinechartdatalist;
    }

    private void sortLineChartProject(List<LineChartProjection> submitmetriclinechartdatalist) {
        if (!CollectionUtils.isEmpty(submitmetriclinechartdatalist)) {
            submitmetriclinechartdatalist.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
        }
    }

    @Override
    public List<MetricSubmitted> getAllPendingReviewMetrics(String currentUserId) {
        log.debug("MetricSubmitServiceImpl::getAllPendingReviewMetrics()::call stared");
        List<MetricSubmitted> metricSubmittedList = metricSubmittedRepository.findAllBySubmittedByNotAndSubmitStatusOrderByUpdatedAtDesc(currentUserId, AssessmentStatus.SUBMITTED.name(), Sort.by(Sort.Order.desc("updatedAt")));
        log.debug("Found {} metrics", metricSubmittedList.size());
        if (CollectionUtils.isEmpty(metricSubmittedList)) {
            logger.debug(" Metrics not found from DB for userId {}", currentUserId);
            throw new MetricNotFoundException(METRIC_NOT_FOUND);
        }
        log.debug("MetricSubmitServiceImpl::getAllPendingReviewMetrics()::call ended");
        return metricSubmittedList;
    }

    @Override
    public PiechartDashboardDto calculatePercentageForMetricDashboardPieChart() {
        log.debug("MetricSubmitServiceImpl::calculatePercentageForMetricDashboardPieChart() call started");
        Integer totalMetric = metricSubmittedRepository.countBySubmitStatusIn(List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        log.debug("calculatePercentageForMetricDashboardPieChart() -Total Metrics: {}", totalMetric);
        if (totalMetric == 0) {
            log.error("calculatePercentageForMetricDashboardPieChart() - No Metrics found");
            throw new MetricNotFoundException("No Metrics in DB");
        }
        Integer totalMetricSubmitted = metricSubmittedRepository.countBySubmitStatusIn(List.of(AssessmentStatus.SUBMITTED));
        Integer totalMetricReviewed = metricSubmittedRepository.countBySubmitStatusIn(List.of(AssessmentStatus.REVIEWED));

        String submittedPercentage = String.format("%.2f%%", ((double) totalMetricSubmitted / totalMetric) * 100);
        log.debug("calculatePercentageForMetricDashboardPieChart() - Submitted Metrics Percentage calculated: {}%", submittedPercentage);
        String reviewedPercentage = String.format("%.2f%%", ((double) totalMetricReviewed / totalMetric) * 100);
        log.debug("calculatePercentageForMetricDashboardPieChart() - Reviewed Metrics Percentage calculated: {}%", reviewedPercentage);

        log.debug("MetricSubmitServiceImpl::calculatePercentageForMetricDashboardPieChart() call ended");
        return new PiechartDashboardDto(submittedPercentage, totalMetricSubmitted, reviewedPercentage, totalMetricReviewed, UNIT);
    }

    @Override
    public List<MetricAndAssessmentDetailsDto> getAllMetricDetails(String submittedBy) {
        log.debug("MetricSubmitServiceImpl :: getAllMetricDetails() call started");

        List<MetricSubmitted> metricSubmitteds = metricSubmittedRepository.findAllBySubmittedByOrderByUpdatedAtDesc(submittedBy);
        if (CollectionUtils.isEmpty(metricSubmitteds)) {
            log.debug("MetricDetails not from DB {} ", metricSubmitteds);
            throw new CustomException("MetricDetails not found From DB ", HttpStatus.OK);
        }
        List<MetricAndAssessmentDetailsDto> metricDetails = conversationService.toMetricSubmitted(metricSubmitteds);
        log.debug("MetricAndAssessmentDetailsDto from DB {} ", metricSubmitteds);
        log.debug("MetricSubmitServiceImpl :: getAllMetricDetails() call ended");
        return metricDetails;
    }

    @Override
    public List<MetricAndAssessmentReportDetails> getMetricReportDetails() {
        log.debug("MetricSubmitServiceImpl :: getMetricReportDetails() call started");
        List<MetricSubmitted> metricSubmittedData = metricSubmittedRepository.findAllBySubmitStatusNotOrderByUpdatedAtDesc(AssessmentStatus.SAVE, Sort.by(Sort.Order.desc("updatedAt")));
        ;
        if (CollectionUtils.isEmpty(metricSubmittedData)) {
            log.debug("metricSubmittedData not found from DB {} ", metricSubmittedData);
            throw new CustomException("Metric Report Details not found From DB ", HttpStatus.OK);
        }
        List<MetricAndAssessmentReportDetails> metricReportDetails = conversationService.toMetricReportDetails(metricSubmittedData);
        log.debug("MetricSubmitServiceImpl :: getMetricReportDetails() call ended");
        return metricReportDetails;
    }

    @Override
    public List<MetricSubmitted> getTop10MetricForDashboard() {
        logger.debug("MetricSubmitServiceImpl::getTop10MetricForDashboard()::Start");
        List<MetricSubmitted> top10MetricForDashboard = metricSubmittedRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
        if (CollectionUtils.isEmpty(top10MetricForDashboard)) {
            log.error("Metric not found from DB ");
            throw new MetricNotFoundException(METRIC_NOT_FOUND);
        } else {
            logger.debug("Top10Assessments data:: {} ", top10MetricForDashboard);
            logger.debug("MetricSubmitServiceImpl::getTop10MetricForDashboard()::End");
            return top10MetricForDashboard;
        }
    }

    @Override
    public PiechartDashboardDto calculatePercentageForPieChartWithFilters(Filters filterName, String filterValue) {
        log.debug("MetricSubmitServiceImpl::calculatePercentageForPieChartWithFilters() call started");
        List<MetricSubmitted> metricSubmittedList;
        if (filterName != null && filterValue != null) {
            List<Long> filterValues = Arrays.stream(filterValue.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            metricSubmittedList = switch (filterName) {
                case PR -> metricSubmittedRepository.findByProjectIds(filterValues);
                case PT -> metricSubmittedRepository.findByProjectTypeId(Long.parseLong(filterValue));
                case AC -> metricSubmittedRepository.findByAccountId(Long.parseLong(filterValue));
            };
        } else {
            metricSubmittedList = metricSubmittedRepository.findBySubmitStatusIn(
                    List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        }
        log.debug("MetricSubmitServiceImpl::calculatePercentageForPieChartWithFilters() call ended");
        return calculatePercentages(metricSubmittedList);

    }

    private PiechartDashboardDto calculatePercentages(List<MetricSubmitted> metricSubmittedList) {
        log.debug("Calculating percentages for the pie chart");
        Long reviewed = getStatusCount(AssessmentStatus.REVIEWED, metricSubmittedList);
        Long submitted = getStatusCount(AssessmentStatus.SUBMITTED, metricSubmittedList);
        Long totalMetrics = reviewed + submitted;
        return new PiechartDashboardDto(calculatePercentage(submitted, totalMetrics), Math.toIntExact(submitted),
                calculatePercentage(reviewed, totalMetrics), Math.toIntExact(reviewed),
                UNIT);
    }

    private String calculatePercentage(Long status, Long totalMetrics) {
        log.debug("Calculating percentage: status={}, totalMetrics={}", status, totalMetrics);
        return String.format("%.2f%%", ((double) status / totalMetrics) * 100);
    }

    private Long getStatusCount(AssessmentStatus status, List<MetricSubmitted> metricSubmittedList) {
        log.debug("Counting metrics with status: {}", status);
        return metricSubmittedList.stream().filter(metrics -> status.equals(metrics.getSubmitStatus())).count();
    }

    @Override
    public List<MetricSubmitted> getTop10MetricsForDashboardFilters(Filters filterName,
                                                                    String filterValue) {
        log.debug("MetricSubmitServiceImpl :: getTop10MetricsForDashboardFilters() call started");
        List<MetricSubmitted> metricSubmittedList;
        if (filterName != null && StringUtils.isNotBlank(filterValue)) {
            List<Long> filterValues = Arrays.stream(filterValue.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();
            log.debug("FilterName: {}, FilterValue: {}", filterName, filterValue);
            metricSubmittedList = switch (filterName) {

                case AC ->
                        metricSubmittedRepository.findTop10ByAccountIdAndSubmitStatusInOrderByUpdatedAtDesc(Long.parseLong(filterValue), List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
                case PR ->
                        metricSubmittedRepository.findTop10ByProjectIdInAndSubmitStatusInOrderByUpdatedAtDesc(filterValues, List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
                case PT ->
                        metricSubmittedRepository.findTop10ByProjectTypeIdAndSubmitStatusInOrderByUpdatedAtDesc(Long.parseLong(filterValue), List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
            };
        } else {
            log.debug("FilterName or FilterValue is null");
            metricSubmittedList = metricSubmittedRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(
                    List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED)
            );
        }
        log.debug("MetricSubmitServiceImpl :: getTop10MetricsForDashboardFilters() call End");
        return metricSubmittedList;
    }

    @Override
    public List<MetricSubmitted> getReviewedMetricsForReviewer(String reviewerId) {
        logger.debug("MetricSubmitServiceImpl::getReviewedMetricsForReviewer() - Started");
        if (StringUtils.isBlank(reviewerId)) {
            logger.error("MetricSubmitServiceImpl::getReviewedMetricsForReviewer{}", reviewerId);
            throw new CustomException("Invalid reviewerId Id : " + reviewerId, HttpStatus.BAD_REQUEST);
        }
        List<MetricSubmitted> metrics = metricSubmittedRepository.findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(
                AssessmentStatus.REVIEWED, reviewerId);
        if (CollectionUtils.isEmpty(metrics)) {
            throw new MetricNotFoundException(METRIC_NOT_FOUND);
        }
        logger.debug("MetricSubmitServiceImpl::getReviewedMetricsForReviewer() - Ended");
        return metrics;
    }


    @Override
	public MetricSubmitted inactiveMetricById(Long metricId) {

		logger.debug("MetricSubmitServiceImpl::inactiveMetricById()::Start");
		Optional<MetricSubmitted> metricsubmittedobj = metricSubmittedRepository.findById(metricId);
		MetricSubmitted metricSubmitted = metricsubmittedobj
				.orElseThrow(() -> new MetricNotFoundException(METRIC_NOT_FOUND));
		if (AssessmentStatus.REVIEWED.equals(metricsubmittedobj.get().getSubmitStatus())
				|| AssessmentStatus.SUBMITTED.equals(metricsubmittedobj.get().getSubmitStatus())) {
			metricSubmitted.setSubmitStatus(AssessmentStatus.INACTIVE);
			metricSubmitted.setIsFrequencyRequired(false);
			
		}
		 else {
        	 throw new CustomException(String.format("Metric not in REVIEWED/SUBMITTED status, can not be Deleted"), HttpStatus.OK);
        }
		metricSubmittedRepository.save(metricSubmitted);
		logger.debug("MetricSubmitServiceImpl::inactiveMetricById()::End");
		return metricSubmitted;
	}

}