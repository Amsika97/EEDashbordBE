package com.maveric.digital.service;
import com.maveric.digital.exceptions.*;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.*;
import com.maveric.digital.pushnotificationservice.EmailConversationUtils;
import com.maveric.digital.repository.*;
import com.maveric.digital.responsedto.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.maveric.digital.utils.ServiceConstants;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import com.maveric.digital.projection.LineChartProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.maveric.digital.utils.EmailReminderUtils.*;
import static com.maveric.digital.utils.ServiceConstants.REVIEWER;
import static java.util.stream.Collectors.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {
    private static final String TEMPLATE_NOT_FOUND = "Template not found";
    private static final String QUESTION_VALIDATE = "templateQuestionnaire do not validated";
    private static final String SCORE_CATEGORY_VALIDATE = "score category name do not validated in templateQuestionnaire ";
    public static final String ASSESSMENT_NOT_FOUND = "Assessment not found";
    public static final String REVIEWER_OR_ASSESSMENT_NOT_FOUND = "Reviewer or Assessment Not found";
    public static final String LINE_CHART_DATA_NOT_FOUND = "Line chart data not found";
    private static final int ASSESSMENT_COUNT_INCREMENT = 1;
    private static final String DOUBLE_FORMAT = "0.00";
    @Value("${overdue.reminders}")
    private int overDueReminders=3;
    @Value("${threshold.overdue.reminders}")
    private int thresholdForOverDueReminders=2;
    @Value("${send.reminder.days.before}")
    private int sendReminderAtBefore=1;

    private final AssessmentRepository assessmentRepository;
    private final ConversationService conversationService;
    private final TemplateRepository templateRepository;
    private final ProjectTypeRepository projectTyperepository;
    private final ProjectRepository projectrepository;
    private final AccountRepository accountrepository;
    private final EmailConversationUtils emailConversationUtils;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AssessmentServiceImpl.class);


    @Override
    public List<Assessment> getAssessmentsBySubmitedBy(String submitedBy) {
        logger.debug("AssessmentServiceImpl::getAssessmentsBySubmitedBy() - Start: Searching for assessment with submitedby{}", submitedBy);
        if (submitedBy == null) {
            logger.error("AssessmentServiceImpl::getAssessmentsBySubmitedBy{}", submitedBy);
            throw new IllegalArgumentException("Invalid SubmittedBy Id : " + submitedBy);
        }
        return assessmentRepository.findBySubmitedByOrderByUpdatedAtDesc(submitedBy);
    }

    @Override
    public List<Assessment> getAssessmentsBySubmittedByAndStatus(String submittedBy,
                                                                 String submitStatus) {
        log.debug("AssessmentServiceImpl::getAssessmentsBySubmittedByAndStatus() - Start");
        log.debug(" Searching for assessment with submittedBy {},submitStatus {}", submittedBy, submitStatus);
        if (submittedBy == null || submittedBy.isBlank()) {
            log.error(" submittedBy is null submittedBy {} ", submittedBy);
            throw new IllegalArgumentException("Invalid SubmittedBy Id : " + submittedBy);
        }

        boolean isValueInEnum = Arrays.stream(AssessmentStatus.values())
                .anyMatch(enumValue -> enumValue.name().equals(submitStatus));

        if (!isValueInEnum) {
            log.error("AssessmentServiceImpl::getAssessmentsBySubmittedByAndStatus() -  Invalid submitStatus {}", submitStatus);
            throw new IllegalArgumentException("Invalid submitStatus  :" + submitStatus);
        }

        List<Assessment> assessments = assessmentRepository.findBySubmitedByAndSubmitStatus(submittedBy, submitStatus);

        if (CollectionUtils.isEmpty(assessments)) {
            log.error("Assessments not found from DB for   submittedBy-{} submitStatus {}", submittedBy, submitStatus);
            throw new CustomException(ASSESSMENT_NOT_FOUND, HttpStatus.NO_CONTENT);
        }
        log.debug("Assessments from DB {}", assessments);
        log.debug("AssessmentServiceImpl::getAssessmentsBySubmittedByAndStatus() - End");
        return assessments;
    }

    @Override
    public Assessment findAssessmentById(@NotNull @Positive Long assessmentId) {
        logger.debug("AssessmentServiceImpl::findAssessmentById() - Start: Searching for assessment with ID {}", assessmentId);

        if (assessmentId == null || assessmentId <= 0) {
            logger.error("AssessmentServiceImpl::findAssessmentById() - assessment ID Not Found: {}", assessmentId);
            throw new IllegalArgumentException("Assessment ID not found: " + assessmentId);
        }

        Optional<Assessment> assessment = assessmentRepository.findById(assessmentId);

        if (!assessment.isPresent()) {
            logger.warn("AssessmentServiceImpl::findAssessmentById() - Assessment with ID {} not found", assessmentId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment with ID " + assessmentId + " not found");
        }

        logger.debug("AssessmentServiceImpl::findAssessmentById() - End: Found assessment with ID {}", assessmentId);
        return assessment.get();
    }

    @Override
    public List<Assessment> findAllAssessments() {
        logger.debug("AssessmentServiceImpl::findAllAssessments() - Start: Retrieving all assessments");

        List<Assessment> assessments = assessmentRepository.findAll();

        if (assessments.isEmpty()) {
            logger.debug("AssessmentServiceImpl::findAllAssessments() - Assessment empty");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Assessment present");
        }

        logger.debug("AssessmentServiceImpl::findAllAssessments() - End: Retrieved {} assessments", assessments.size());
        return assessments;
    }

    @Override
    public List<Assessment> getAllSubmittedAssessments() {
        logger.debug("AssessmentServiceImpl::getAllSubmittedAssessments() - Start: Retrieving all submitted assessments");

        List<Assessment> assessments = assessmentRepository.findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")));


        if (assessments.isEmpty()) {
            logger.debug("AssessmentServiceImpl::getAllSubmittedAssessments() - Assessment empty");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Assessment present");
        }

        logger.debug("AssessmentServiceImpl::getAllSubmittedAssessments() - End: Retrieved {} assessments", assessments.size());
        return assessments;
    }

    @Override
    public Assessment saveOrSubmitAssessment(AssessmentDto requestPayload) {
        logger.debug("AssessmentServiceImpl::AssessmentDto()::Start");
        if (Objects.isNull(requestPayload)) {
            throw new IllegalArgumentException("Request do not validated");
        }
        Optional<Assessment> assessment = Optional.empty();
        Optional<Template> template = templateRepository.findById(requestPayload.getTemplateId());
        if (template.isEmpty()) {
            throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
        }
        this.validateQuestionAnswer(requestPayload, template.get());
        if (Objects.nonNull(requestPayload.getAssessmentId())) {
            assessment = assessmentRepository.findById(requestPayload.getAssessmentId());
        }
        double score;
        //if the assessment was saved atleast once
        if (assessment.isPresent()) {
            assessment.get().setProjectCategory(
                    conversationService.toAssessmentProjectCategories(requestPayload.getProjectCategory()));
            assessment.get().setSubmitStatus(requestPayload.getSubmitStatus());
            assessment.get().setSubmitedAt(this.isSubmited(assessment, requestPayload) ? System.currentTimeMillis() : null);
            score = scoreCalculation(requestPayload);
            assessment.get().setScore(score);
            assessment.get().setCategoryScores(requestPayload.getCategorywiseScores());
            assessment.get().setAssessmentDescription(requestPayload.getAssessmentDescription());
            assessment.get().setUpdatedAt(System.currentTimeMillis());
            if (AssessmentStatus.SUBMITTED.equals(assessment.get().getSubmitStatus())) {
                assessment.get().setFrequencyReminderDate(TemplateFrequencyReminder.NA.equals(assessment.get().getTemplate().getTemplateFrequency()) ?0 : setEmailRemainderDate(template.get().getTemplateFrequency(),sendReminderAtBefore));
                assessment.get().setFrequencyOverDueRemindersDate(TemplateFrequencyReminder.NA.equals(assessment.get().getTemplate().getTemplateFrequency()) ? List.of() : setEmailOverDueRemainderDates(template.get().getTemplateFrequency(), overDueReminders, thresholdForOverDueReminders));
                assessment.get().setIsFrequencyRequired(true);
            }

            assessmentRepository.save(assessment.get());
            if (AssessmentStatus.SUBMITTED.equals(assessment.get().getSubmitStatus())) {
                prepareAndSendEmailNotification(assessment);
                checkAndDeActiveOldTemplate(assessment.get());

            }

            return assessment.get();
        }
        //If the saveandsubmitted is called for the first time
        Account account = accountrepository.findById(requestPayload.getAccountId()).get();
        ProjectType projectType = projectTyperepository.findById(requestPayload.getProjectTypeId()).get();
        Project project = projectrepository.findById(requestPayload.getProjectId()).get();
        assessment = Optional.of(conversationService.toAssessment(requestPayload, template.get(), account, projectType, project));

        assessment.get().setSubmitedAt(this.isSubmited(assessment, requestPayload) ? System.currentTimeMillis() : null);

        score = Double.parseDouble(new DecimalFormat(DOUBLE_FORMAT).format(scoreCalculation(requestPayload)));
        assessment.get().setScore(score);
        assessment.get().setCategoryScores(requestPayload.getCategorywiseScores());
        if (AssessmentStatus.SUBMITTED.equals(assessment.get().getSubmitStatus())) {
            assessment.get().setFrequencyReminderDate(TemplateFrequencyReminder.NA.equals(assessment.get().getTemplate().getTemplateFrequency()) ?0 : setEmailRemainderDate(template.get().getTemplateFrequency(),sendReminderAtBefore));
            assessment.get().setFrequencyOverDueRemindersDate(TemplateFrequencyReminder.NA.equals(assessment.get().getTemplate().getTemplateFrequency()) ? List.of() : setEmailOverDueRemainderDates(template.get().getTemplateFrequency(), overDueReminders, thresholdForOverDueReminders));
            assessment.get().setIsFrequencyRequired(true);

        }  assessmentRepository.save(assessment.get());
        if (AssessmentStatus.SUBMITTED.equals(assessment.get().getSubmitStatus())) {
            prepareAndSendEmailNotification(assessment);
            checkAndDeActiveOldTemplate(assessment.get());
        }

        logger.debug("AssessmentServiceImpl::AssessmentDto()::assessment::{}", assessment);
        logger.debug("AssessmentServiceImpl::AssessmentDto()::End");
        return assessment.get();
    }

    private void checkAndDeActiveOldTemplate(Assessment requestPayload) {
        assessmentRepository.findBySubmitedByAndTemplateIdAndProjectIdAndUpdateisFrequencyRequired(requestPayload.getSubmitedBy(),requestPayload.getTemplate().getId(),requestPayload.getProject().getId(),false,requestPayload.getId());
    }




    private void prepareAndSendEmailNotification(Optional<Assessment> assessment) {
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(assessment.get().getSubmitedBy()));
        if (userObj.isPresent()) {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            userEmailTemplateDetails.setUserName(userObj.get().getName());
            userEmailTemplateDetails.setProjectName(assessment.get().getProject().getProjectName());
            userEmailTemplateDetails.setProjectCode(assessment.get().getProject().getProjectCode());
            userEmailTemplateDetails.setAccountName(assessment.get().getAccount().getAccountName());
            userEmailTemplateDetails.setProjectType(assessment.get().getProjectType().getProjectTypeName());
            userEmailTemplateDetails.setTemplateName(assessment.get().getTemplate().getTemplateName());
            userEmailTemplateDetails.setSubmittedOn(new Date(System.currentTimeMillis()).toString());
            userEmailTemplateDetails.setEmailId(userObj.get().getEmailAddress());
            emailConversationUtils.sendEmailNotificationForAssessmentSubmittedConfirmation(userEmailTemplateDetails);
            prepareAndSendNotificationToReviewer( userEmailTemplateDetails);
        }
    }

    private void prepareAndSendNotificationToReviewer( UserEmailTemplateDetails userEmailTemplateDetails) {
        Optional<List<User>> listOfReviewers = userRepository.findByRoleIn(List.of(Roles.Reviewer));
        if (listOfReviewers.isEmpty() || CollectionUtils.isEmpty(listOfReviewers.get())){
            return;
        }
        List<String> emailIdsList = listOfReviewers.get().stream().map(User::getEmailAddress).
                filter(emailAddress -> !StringUtils.equalsAnyIgnoreCase(userEmailTemplateDetails.getEmailId(), emailAddress)).toList();

        if (!CollectionUtils.isEmpty(emailIdsList)) {
            String[] s = emailIdsList.toArray(new String[0]);
            userEmailTemplateDetails.setReviewerEmails(s);
            emailConversationUtils.sendEmailNotificationForAssessmentSubmittedToReviewer(userEmailTemplateDetails);
        }
    }

    public double scoreCalculation(AssessmentDto assessmentDto) {
        if (!AssessmentStatus.SUBMITTED.equals(assessmentDto.getSubmitStatus())) {
            return NumberUtils.DOUBLE_ZERO;
        }
        double questionScore = 0;
        int totalQuestions = 0;
        List<CategoryWiseScore> categoryScores = new ArrayList<>();
        Double optionscore;
        for (AssessmentProjectCategoryDto projectCategory : assessmentDto.getProjectCategory()) {
            double categoryScore = 0;
            int totalQuestionsInCategory = 0;
            for (QuestionAnswerDto questionAnswer : projectCategory.getTemplateQuestionnaire()) {
                int answerOptionIndex = questionAnswer.getAnswerOptionIndex();
                String scoreCategory = questionAnswer.getScoreCategory();
                if (scoreCategory != null && !scoreCategory.isEmpty()) {
                    optionscore = calculateOptionScore(assessmentDto, scoreCategory, answerOptionIndex);
                    questionScore += optionscore;
                    categoryScore += optionscore;
                    totalQuestionsInCategory++;
                    totalQuestions++;

                }
            }
            if (totalQuestionsInCategory > 0) {
                categoryScores.add(new CategoryWiseScore(projectCategory.getCategoryName(), Double.parseDouble(new DecimalFormat(DOUBLE_FORMAT).format((categoryScore * 100.0) / totalQuestionsInCategory)), totalQuestionsInCategory));
            } else {
                // Optionally handle the case where totalQuestionsInCategory is zero
                categoryScores.add(new CategoryWiseScore(projectCategory.getCategoryName(), 0, 0));
            }
            assessmentDto.setCategorywiseScores(categoryScores);
        }
        if (totalQuestions > 0) {
            return questionScore * 100 / totalQuestions;
        } else {
            return NumberUtils.DOUBLE_ZERO;
        }
    }

    private Double calculateOptionScore(AssessmentDto assessmentDto, String scoreCategory, int answerOptionIndex) {
        for (ScoreCategoryDto scoreCategoryDto : assessmentDto.getScoreCategories()) {
            if (scoreCategory.equals(scoreCategoryDto.getCategoryName())) {
                return scoreCategoryDto.getCategoryOptions().stream().filter(options -> options.getOptionIndex() == answerOptionIndex)
                        .map(Options::getOptionScore).findFirst().orElse(0.0);
            }
        }

        return 0.0;
    }


    private boolean isSubmited(Optional<Assessment> assessment, AssessmentDto requestPayload) {
        return assessment.isPresent() && Objects.nonNull(requestPayload)
                && requestPayload.getSubmitStatus().equals(AssessmentStatus.SUBMITTED);
    }

    private void validateQuestionAnswer(AssessmentDto requestPayload, Template template) {
        List<Integer> templateQuestionnaire = template.getProjectCategory().stream()
                .flatMap(obj -> obj.getTemplateQuestionnaire().stream()).map(TemplateQuestionnaire::getQuestionId).toList();
        List<QuestionAnswerDto> assessmentQuestionnaire = this.populateQuestionAnswerDto(requestPayload);


        if (!assessmentQuestionnaire.stream().map(QuestionAnswerDto::getQuestionId)
                .allMatch(templateQuestionnaire::contains)) {
            throw new IllegalArgumentException(QUESTION_VALIDATE);

        }
        this.validateScoreCategory(requestPayload, template);
    }

    private void validateScoreCategory(AssessmentDto requestPayload, Template template) {
        Set<String> scoreCategoryNameSet = this.populateScoreCategoryNameList(template);
        List<QuestionAnswerDto> assessmentQuestionnaire = this.populateQuestionAnswerDto(requestPayload);
        if (!assessmentQuestionnaire.stream().map(QuestionAnswerDto::getScoreCategory)
                .allMatch(scoreCategoryNameSet::contains)) {
            throw new IllegalArgumentException(SCORE_CATEGORY_VALIDATE);

        }
    }

    private List<QuestionAnswerDto> populateQuestionAnswerDto(AssessmentDto requestPayload) {
        return requestPayload.getProjectCategory().stream()
                .flatMap(obj -> obj.getTemplateQuestionnaire().stream()).toList();
    }

    private Set<String> populateScoreCategoryNameList(Template template) {
        return template.getProjectCategory().stream().flatMap(obj -> obj.getTemplateQuestionnaire().stream())
                .map(TemplateQuestionnaire::getScoreCategory).collect(toSet());
    }

    @Override
    public Assessment saveReviewerComment(ReviewerCommentDto request) {
        logger.debug("AssessmentServiceImpl::saveReviewerComment()::Start");
        Optional<Assessment> assessmentObj = assessmentRepository.findById(request.getAssessmentId());
        Assessment assessment = assessmentObj.orElseThrow(() -> new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND));
        this.populateReviewrCommentByReviewerId(assessment, request.getReviewerId(), request.getComment(), request.getReviewerName());
        
        request.getReviewerQuestionComment().stream()
        .filter(comment -> StringUtils.isNotBlank(comment.getReviewerComment()))
        .forEach(comment -> updateQuestionCommentInAssessment(assessment, comment));


        if (AssessmentStatus.REVIEWED.equals(request.getStatus())) {
            assessment.setSubmitStatus(request.getStatus());
            assessment.setUpdatedAt(System.currentTimeMillis());
        }
        assessmentRepository.save(assessment);
        Optional<User> userObj = userRepository.findByOid(UUID.fromString(assessment.getSubmitedBy()));
        Optional<User> reviewerObj = userRepository.findByOid(UUID.fromString(request.getReviewerId()));
        if (userObj.isPresent()) {
            UserEmailTemplateDetails userEmailTemplateDetails = new UserEmailTemplateDetails();
            userEmailTemplateDetails.setUserName(userObj.get().getName());
            userEmailTemplateDetails.setProjectName(assessment.getProject().getProjectName());
            userEmailTemplateDetails.setProjectCode(assessment.getProject().getProjectCode());
            userEmailTemplateDetails.setProjectType(assessment.getProjectType().getProjectTypeName());
            userEmailTemplateDetails.setAccountName(assessment.getAccount().getAccountName());
            userEmailTemplateDetails.setSubmittedOn(new Date(assessment.getSubmitedAt()).toString());
            userEmailTemplateDetails.setEmailId(userObj.get().getEmailAddress());
            userEmailTemplateDetails.setReviewedBy(request.getReviewerName());
            if (reviewerObj.isPresent()){
                String[] s = {reviewerObj.get().getEmailAddress()};
                userEmailTemplateDetails.setReviewerEmails(s);
            }
            userEmailTemplateDetails.setReviewOn(new Date(System.currentTimeMillis()).toString());
            userEmailTemplateDetails.setStatus(assessment.getSubmitStatus().name());
            emailConversationUtils.sendEmailNotificationForAssessmentReviewed(userEmailTemplateDetails);
        }
        logger.debug("AssessmentServiceImpl::saveReviewerComment()::{}",
                assessment);
        logger.debug("AssessmentServiceImpl::saveReviewerComment()::End");
        return assessment;
    }

    private void updateQuestionCommentInAssessment(Assessment assessment, ReviewerQuestionComment questionComment) {
        assessment.getProjectCategory().stream()
            .flatMap(category -> category.getTemplateQuestionnaire().stream())
            .filter(answer -> answer.getQuestionId().equals(questionComment.getQuestionId()))
            .findFirst()
            .ifPresent(answer -> {
                answer.setReviewerComment(questionComment.getReviewerComment());
            });
    }
    
    private void populateReviewrCommentByReviewerId(Assessment assessment, String reviewerid, String comment, String reviewerName) {
        if (CollectionUtils.isEmpty(assessment.getReviewers())) {
            assessment.setReviewers(new ArrayList<>());
        }
        Optional<Reviewer> reviewerObj = assessment.getReviewers().stream()
                .filter(reviewer -> reviewer.getReviewerId().equals(reviewerid)).findFirst();
        if (reviewerObj.isPresent()) {
            reviewerObj.get().setComment(comment);
            reviewerObj.get().setReviewerAt(System.currentTimeMillis());
            reviewerObj.get().setReviewerName(reviewerName);
            return;
        }

        if (reviewerObj.isEmpty()) {
            Reviewer reviewer = new Reviewer();
            reviewer.setComment(comment);
            reviewer.setReviewerAt(System.currentTimeMillis());
            reviewer.setReviewerId(reviewerid);
            reviewer.setReviewerName(reviewerName);
            assessment.getReviewers().add(reviewer);

        }

    }

    @Override
    public List<LineChartProjection> getLineChartDataByStartAndEndDates(Long startDate, Long endDate, String filterName, String filterValue) {
        logger.debug("AssessmentServiceImpl::getLineChartDataByStartAndEndDates()::Start");
        List<LineChartProjection> lineChartDataList;
        List<Long> filterValues=new ArrayList<>();
        if(StringUtils.isNotBlank(filterValue)){
            filterValues = Arrays.stream(filterValue.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        }
        switch (filterName) {
           case ServiceConstants.PROJECT -> {
                lineChartDataList = assessmentRepository.findLineChartDataByStartAndEndDatesInAndProjectId(
                        new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(),
                        new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1)
                                .getMillis(), filterValues,List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
            }
            case ServiceConstants.PROJECT_TYPE ->
                    lineChartDataList = assessmentRepository.findLineChartDataByStartAndEndDatesAndProjectTypeId(
                            new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(),
                            new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1)
                                    .getMillis(), Long.valueOf(filterValue),List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
            case ServiceConstants.ACCOUNT ->
                    lineChartDataList = assessmentRepository.findLineChartDataByStartAndEndDatesAndAccount(
                            new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(),
                            new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1)
                                    .getMillis(), Long.valueOf(filterValue),List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
            default -> lineChartDataList = assessmentRepository.findLineChartDataByStartAndEndDates(
                    new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().getMillis(),
                    new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1)
                            .getMillis(),List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED));
        }
        if (CollectionUtils.isEmpty(lineChartDataList)) {
            throw new LineChartDataNotFoundException(LINE_CHART_DATA_NOT_FOUND);
        }
        this.getDateRangeBetween(startDate, endDate, lineChartDataList);
        this.sortLineChartProject(lineChartDataList);
        logger.debug("AssessmentServiceImpl::getLineChartDataByStartAndEndDates()::End");
        return lineChartDataList;
    }

    public List<LineChartProjection> getDateRangeBetween(Long startDate, Long endDate,
                                                         List<LineChartProjection> lineChartDataList) {
        Integer noOfDays = Days
                .daysBetween(new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay(),
                        new DateTime(endDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay().plusDays(1))
                .getDays();
        List<DateTime> dateRang = new ArrayList<>();
        dateRang.add(new DateTime(startDate).withZone(DateTimeZone.forID("GMT")).withTimeAtStartOfDay());
        for (int i = 1; i < noOfDays; i++) {
            dateRang.add(dateRang.get(i - 1).plusDays(1));
        }
        List<String> dateRangList = dateRang.stream().map(this::parseToString).toList();

        return this.populateLineChartProjection(dateRangList, lineChartDataList);
    }

    private List<LineChartProjection> populateLineChartProjection(List<String> dateRangList,
                                                                  List<LineChartProjection> lineChartDataList) {
        Map<String, List<LineChartProjection>> map = lineChartDataList.stream()
                .collect(groupingBy(LineChartProjection::getId));
        for (String date : dateRangList) {
            if (!map.containsKey(date)) {
                LineChartProjection lineChartProjection = new LineChartProjection();
                lineChartProjection.setId(date);
                lineChartProjection.setCount(Integer.valueOf(0));
                lineChartDataList.add(lineChartProjection);
            }
        }
        return lineChartDataList;
    }

    private String parseToString(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-Y");
        return fmt.print(dateTime);
    }

    private void sortLineChartProject(List<LineChartProjection> lineChartDataList) {
        if (!CollectionUtils.isEmpty(lineChartDataList)) {
            lineChartDataList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
        }

    }

    @Override
    public List<Assessment> findLastFiveAssessments(String submittedBy) {
        logger.debug("AssessmentServiceImpl::findLastFiveAssessments()::Start");
        if (submittedBy == null || submittedBy.isBlank()) {
            throw new IllegalArgumentException("Invalid submittedBy: " + submittedBy);
        }
        List<Assessment> lastFiveAssessments = assessmentRepository.findTop5ByOrderByCreatedAtDesc(submittedBy);

        if (lastFiveAssessments.isEmpty()) {
            logger.debug("AssessmentService::findLast5Assessments::No last 5 assessments found");
            return Collections.emptyList();
        } else {
            logger.debug("AssessmentService::findLast5Assessments::Found {} last 5 assessments", lastFiveAssessments.size());
            logger.debug("AssessmentServiceImpl::findLast5Assessments()::End");
            return lastFiveAssessments;
        }
    }

    @Override
    public List<Assessment> getAssessmentsByStatus(List<AssessmentStatus> assessmentStatusList, String submittedBy) {
        logger.debug("AssessmentServiceImpl::getAssessmentsByStatus()::Start");
        if (submittedBy == null || submittedBy.isBlank() || assessmentStatusList.isEmpty()) {
            throw new IllegalArgumentException("Invalid data given in arguments");
        }
        List<Assessment> assessmentList = assessmentRepository.findBySubmitStatusInAndSubmitedBy(assessmentStatusList, submittedBy);
        if (CollectionUtils.isEmpty(assessmentList)) {
            log.error("Assessments not found from DB for submitStatus {} and submittedBy {}", assessmentStatusList, submittedBy);
            throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
        }
        logger.debug("AssessmentServiceImpl::getAssessmentsByStatus()::End");
        return assessmentList;
    }


    @Override
    public List<Assessment> getTop10AssessmentsForDashboard() {
        logger.debug("AssessmentServiceImpl::getTop10AssessmentsForDashboard()::Start");
        List<Assessment> top10AssessmentsForDashboard = assessmentRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
        if (CollectionUtils.isEmpty(top10AssessmentsForDashboard)) {
            log.error("Assessments not found from DB ");
            throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
        } else {
            logger.debug("Top10Assessments data:: {} ", top10AssessmentsForDashboard);
            logger.debug("AssessmentServiceImpl::getTop10AssessmentsForDashboard()::End");
            return top10AssessmentsForDashboard;
        }
    }

    @Override
    public List<Assessment> getAllPendingReviewAssessments(String currentUserId) {
        logger.debug("AssessmentServiceImpl::getAllPendingReviewAssessments()::call stared");
        List<Assessment> assessments = assessmentRepository.findAllBySubmitedByNotAndSubmitStatusOrderByUpdatedAtDesc(currentUserId, AssessmentStatus.SUBMITTED.name(), Sort.by(Sort.Order.desc("updatedAt")));
        logger.debug("Found {} assessments", assessments.size());
        if (CollectionUtils.isEmpty(assessments)) {
            logger.debug("Assessments not found from DB for userId {}", currentUserId);
            throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
        }
        logger.debug("AssessmentServiceImpl::getAllPendingReviewAssessments()::call ended");
        return assessments;
    }

    @Override
    public List<MetricAndAssessmentDetailsDto> getAllAssessmentsDetails(String submittedBy) {
        log.debug("AssessmentServiceImpl :: getAllAssessmentsDetails() call started");
        List<Assessment> assessments = assessmentRepository.findAllBySubmitedByOrderByUpdatedAtDesc(submittedBy);
        if (CollectionUtils.isEmpty(assessments)) {
            log.debug("Assessments not found  from DB {} for submittedBy", submittedBy);
            throw new CustomException(String.format("Assessments not found  from DB {} for submittedBy - %s", submittedBy), HttpStatus.OK);
        }
        log.debug("AssessmentDetails from DB {} ", assessments);
        List<MetricAndAssessmentDetailsDto> metricAndAssessmentDetailDtos = conversationService.toMetricAndAssessmentDetailsDto(assessments);
        log.debug("MetricAndAssessmentDetailDtos {} ", metricAndAssessmentDetailDtos);
        log.debug("AssessmentServiceImpl :: getAllAssessmentsDetails() call ended");
        return metricAndAssessmentDetailDtos;
    }

    @Override
    public void removeFileUri(String fileName, String folderName) {
        log.debug("AssessmentServiceImpl :: removeFileUri() call started");
        String regexPattern = Pattern.quote(folderName) + ".*" + Pattern.quote(fileName);
        Optional<Assessment> assessmentObj = assessmentRepository.findAssessmentWithFileUri(regexPattern);

        if (!assessmentObj.isPresent()) {
            log.error("Assessments not found from DB ");
            throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
        }
        Assessment assessment = assessmentObj.get();
        boolean updated = false;

        for (AssessmentProjectCategory category : assessment.getProjectCategory()) {
            for (QuestionAnswer questionnaire : category.getTemplateQuestionnaire()) {
                String fileUri = questionnaire.getFileUri();
                if (fileUri != null && fileUri.contains(folderName) && fileUri.endsWith(fileName)) {
                    questionnaire.setFileUri(null);
                    updated = true;
                }
            }
        }
        if (updated) {
            assessmentRepository.save(assessment);
        }
        log.debug("AssessmentServiceImpl :: removeFileUri() call ended");
    }

    @Override
    public Map<String, Map<Integer, Long>> getCountOfUserResponse(Long templateId, Long projectId, Integer questionId) {
        log.debug("AssessmentServiceImpl :: getCountOfUserResponse() call started");
        if (!projectrepository.existsById(projectId)) {
            log.error("projectId not found from DB {}", projectId);
            throw new ProjectNotFoundException("Project with ID " + projectId + " not found");
        }
        if (!templateRepository.existsById(templateId)) {
            log.error("templateId not found from DB {}", templateId);
            throw new TemplateNotFoundException("Template with ID " + templateId + " not found");
        }
        List<Assessment> assessmentsForTemplate = assessmentRepository.findByTemplateIdAndProjectId(templateId, projectId);
        if (assessmentsForTemplate.isEmpty()) {
            log.error("Assessments not found for Template with ID {} and Project with ID {}", templateId, projectId);
            throw new AssessmentNotFoundException("Assessments not found for Template with ID " + templateId + " and Project with ID " + projectId);
        }
        List<AssessmentDto> assessments = conversationService.toAssessmentDtos(assessmentsForTemplate);
        log.debug("AssessmentDto {} ", assessments);
        Map<String, Map<Integer, Long>> result = assessments.stream()
                .flatMap(assessment -> assessment.getProjectCategory().stream())
                .flatMap(category -> category.getTemplateQuestionnaire().stream())
                .filter(answer -> questionId == null || answer.getQuestionId().equals(questionId))
                .collect(groupingBy(QuestionAnswerDto::getQuestion,
                        groupingBy(QuestionAnswerDto::getAnswerOptionIndex, counting())
                ));
        log.debug("Processing assessments for questionId {}: {}", questionId, result);
        log.debug("AssessmentServiceImpl :: getCountOfUserResponse() call end");
        return result;
    }

    @Override
    public List<Assessment> assessmentReport() {
        log.debug("AssessmentServiceImpl :: assessmentReport() call started");
        List<Assessment> assessments = assessmentRepository.findBySubmitStatusIn(
                List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED,AssessmentStatus.INACTIVE),

                Sort.by(Sort.Order.desc("updatedAt")));
        if (CollectionUtils.isEmpty(assessments)) {
            return List.of();
        }
        log.debug("AssessmentServiceImpl :: assessmentReport() Data:::{}", assessments);
        log.debug("AssessmentServiceImpl :: assessmentReport() call End");
        return assessments;
    }

    @Override
    public List<Assessment> getTop10AssessmentsForDashboardFilters(Filters filterName, String filterValue) {
        log.debug("AssessmentServiceImpl :: getTop10AssessmentsForDashboardFilters() call started");
        List<Assessment> assessmentList;
        if (filterName != null && filterValue != null) {
            List<Long> filterValues = Arrays.stream(filterValue.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(toList());
            log.debug("FilterName: {}, FilterValue: {}", filterName, filterValue);
            log.debug("FilterName: {}, FilterValue: {}", filterName, filterValue);
            assessmentList = switch (filterName) {
                case AC -> assessmentRepository.findTop10ByAccountIdAndSubmitStatusInOrderByUpdatedAtDesc(Long.parseLong(filterValue), List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
              //case PR -> assessmentRepository.findTop10ByProjectIdAndSubmitStatusInOrderByUpdatedAtDesc(Long.parseLong(filterValue), List.of(AssessmentStatus.APPROVED, AssessmentStatus.SUBMITTED, AssessmentStatus.REJECTED));
                case PR -> assessmentRepository.findTop10ByProjectIdInAndSubmitStatusInOrderByUpdatedAtDesc(filterValues, List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
                case PT -> assessmentRepository.findTop10ByProjectTypeIdAndSubmitStatusInOrderByUpdatedAtDesc(Long.parseLong(filterValue), List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED));
            };
        } else {
            log.debug("FilterName or FilterValue is null");
            assessmentList = assessmentRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(
                    List.of(AssessmentStatus.REVIEWED

                            , AssessmentStatus.SUBMITTED)

            );
        }
        log.debug("AssessmentServiceImpl :: getTop10AssessmentsForDashboardFilters() call End");
        return assessmentList;
    }
    @Override
    public List<Assessment> getReviewedAssessmentsForReviewer(String reviewerId) {
        logger.debug("AssessmentServiceImpl::getReviewedAssessmentsForReviewer() - Started");
        if (StringUtils.isBlank(reviewerId)) {
            logger.error("AssessmentServiceImpl::getReviewedAssessmentsForReviewer {}", reviewerId);
            throw new CustomException("Invalid reviewerId Id : " + reviewerId,HttpStatus.BAD_REQUEST);
        }
        List<Assessment> assessments=assessmentRepository.findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(
                AssessmentStatus.REVIEWED,reviewerId);
        if(CollectionUtils.isEmpty(assessments)){
            throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
        }
        logger.debug("AssessmentServiceImpl::getReviewedAssessmentsForReviewer() - Ended");
        return assessments;
    }
    
    @Override
    public Assessment inactiveAssessmentById(Long assessmentId) {
    	logger.debug("AssessmentServiceImpl::inactiveAssessmentById()::Start");
        Optional<Assessment> assessmentObj = assessmentRepository.findById(assessmentId);
        Assessment assessment = assessmentObj.orElseThrow(() -> new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND));
       
        if (AssessmentStatus.REVIEWED.equals(assessmentObj.get().getSubmitStatus()) ||
        	    AssessmentStatus.SUBMITTED.equals(assessmentObj.get().getSubmitStatus())) {
        	    assessment.setSubmitStatus(AssessmentStatus.INACTIVE);
        	    assessment.setIsFrequencyRequired(false);   
        	    }
        else {
        	 throw new CustomException(String.format("Assessment not in REVIEWED/SUBMITTED status, can not be Deleted"), HttpStatus.OK);
        }
        assessmentRepository.save(assessment);
        logger.debug("AssessmentServiceImpl::saveReviewerComment()::End");
        return assessment;
    }
}