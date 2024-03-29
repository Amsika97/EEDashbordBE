package com.maveric.digital.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.*;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.*;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.responsedto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.*;

import static com.maveric.digital.service.AccountServiceImpl.ACCOUNT_NOT_FOUND;
import static com.maveric.digital.service.ConversationService.PROJECT_NOT_FOUND;
import static com.maveric.digital.service.ConversationService.PROJECT_TYPE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {ConversationService.class, ModelMapper.class})
@ExtendWith(MockitoExtension.class)
class ConversationServiceTests {

    private static final String SCORE_NOT_FOUND = "Score Scale not found";
    private static final String TEMPLATE_NOT_FOUND = "Template not found";
    private static final String ASSESSMENT_NOT_FOUND = "Assessment not found";
    private static final String SCORE_CATEGORY_NOT_FOUND = "Score Category not found";


    @Mock
    ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    void AllExceptions() {
        List<Template> emptyTemplates = List.of();
        List<Assessment> assessmentDtoList = List.of();
        List<ScoringScale> scoreScaleDtoList = List.of();
        List<ScoreCategory> scoreCategoryList = List.of();
        List<ProjectTypeDto> projectTypeDtoList=List.of();
        List<AccountDto> accountList=List.of();
        List<ProjectDto> projectDtoList=List.of();
        TemplateNotFoundException templateNotFoundExceptionDto = assertThrows(
            TemplateNotFoundException.class,
            () -> conversationService.toTemplateDto(null));
        TemplateNotFoundException templateNotFoundExceptionDtos = assertThrows(
            TemplateNotFoundException.class,
            () -> conversationService.toTemplateDtos(emptyTemplates));
        ScoreScaleNotFoundException scoreScaleNotFoundExceptionDto = assertThrows(
            ScoreScaleNotFoundException.class,
            () -> conversationService.toScoreDto(null));
        ScoreScaleNotFoundException scoreScaleNotFoundExceptionDtos = assertThrows(
            ScoreScaleNotFoundException.class,
            () -> conversationService.toScoreDtos(scoreScaleDtoList));

        AssessmentNotFoundException assessmentNotFoundExceptionDto = assertThrows(
            AssessmentNotFoundException.class,
            () -> conversationService.toAssessmentDto(null));
        AssessmentNotFoundException assessmentNotFoundExceptionDtos = assertThrows(
            AssessmentNotFoundException.class,
            () -> conversationService.toAssessmentDtos(assessmentDtoList));

        ScoreCategoryNotFoundException scoreCategoryNotFoundExceptionDto = assertThrows(
            ScoreCategoryNotFoundException.class,
            () -> conversationService.toScoreCategoryDto(null));
        ScoreCategoryNotFoundException scoreCategoryNotFoundExceptionDtos = assertThrows(
            ScoreCategoryNotFoundException.class,
            () -> conversationService.toScoreCategoryDtos(scoreCategoryList));
        CustomException customException = assertThrows(
                CustomException.class,
                () -> conversationService.toProjectTypeList(projectTypeDtoList));

        AssessmentNotFoundException  assessmentNotFoundException = assertThrows(
                AssessmentNotFoundException.class,
                () -> conversationService.toAssessmentsSubmittedDashboardDtos(assessmentDtoList));

        AccountsNotFoundException accountsNotFoundException = assertThrows(
                AccountsNotFoundException.class,
                () -> conversationService.toAccountList(accountList));
        TemplateNotFoundException  templateNotFoundException = assertThrows(
                TemplateNotFoundException.class,
                () -> conversationService.toAssessmentTemplateDtos(emptyTemplates));

        TemplateNotFoundException  templateNotFound = assertThrows(
                TemplateNotFoundException.class,
                () -> conversationService.toAssessmentTemplateDto(null));
        ProjectNotFoundException  projectNotFoundException = assertThrows(
                ProjectNotFoundException.class,
                () -> conversationService.toProjectList(projectDtoList));
        AssessmentNotFoundException  assessmentNotFound = assertThrows(
                AssessmentNotFoundException.class,
                () -> conversationService.toAssessmentReviewDto(null));
        AssessmentNotFoundException  assessmentNotFoundEx = assertThrows(
                AssessmentNotFoundException.class,
                () -> conversationService.toAssessmentReviewDtos(null));
        assertEquals(TEMPLATE_NOT_FOUND, templateNotFoundExceptionDto.getMessage());
        assertEquals(TEMPLATE_NOT_FOUND, templateNotFoundExceptionDtos.getMessage());
        assertEquals(SCORE_NOT_FOUND, scoreScaleNotFoundExceptionDto.getMessage());
        assertEquals(SCORE_NOT_FOUND, scoreScaleNotFoundExceptionDtos.getMessage());
        assertEquals(ASSESSMENT_NOT_FOUND, assessmentNotFoundExceptionDto.getMessage());
        assertEquals(ASSESSMENT_NOT_FOUND, assessmentNotFoundExceptionDtos.getMessage());
        assertEquals(SCORE_CATEGORY_NOT_FOUND, scoreCategoryNotFoundExceptionDto.getMessage());
        assertEquals(SCORE_CATEGORY_NOT_FOUND, scoreCategoryNotFoundExceptionDtos.getMessage());
        assertEquals(PROJECT_TYPE_NOT_FOUND, customException.getMessage());
        assertEquals(ASSESSMENT_NOT_FOUND, assessmentNotFoundException.getMessage());
        assertEquals(ACCOUNT_NOT_FOUND, accountsNotFoundException.getMessage());
        assertEquals(TEMPLATE_NOT_FOUND, templateNotFoundException.getMessage());
        assertEquals(TEMPLATE_NOT_FOUND, templateNotFound.getMessage());
        assertEquals(PROJECT_NOT_FOUND, projectNotFoundException.getMessage());
        assertEquals(ASSESSMENT_NOT_FOUND, assessmentNotFound.getMessage());
        assertEquals(ASSESSMENT_NOT_FOUND, assessmentNotFoundEx.getMessage());
    }

    @Test
    void testToAssessmentDto() {

        TemplateQuestionnaire templateQuestionnaire1=new TemplateQuestionnaire();
        templateQuestionnaire1.setQuestionId(1);
        TemplateQuestionnaire templateQuestionnaire2=new TemplateQuestionnaire();
        templateQuestionnaire2.setQuestionId(2);
        List<TemplateQuestionnaire> tplist=Arrays.asList(templateQuestionnaire1,templateQuestionnaire2);
        QuestionAnswer questionAnswer1=new QuestionAnswer();
        questionAnswer1.setQuestionId(1);
        QuestionAnswer questionAnswer2=new QuestionAnswer();
        questionAnswer2.setQuestionId(2);
        List<QuestionAnswer> qlist=Arrays.asList(questionAnswer1,questionAnswer2);

        List<AssessmentProjectCategory> list=new ArrayList<>();
        AssessmentProjectCategory p1=new AssessmentProjectCategory();
        p1.setTemplateQuestionnaire(qlist);
        list.add(p1);
        List<ProjectCategory> tlist=new ArrayList<>();
        ProjectCategory tp1=new ProjectCategory();
        tp1.setTemplateQuestionnaire(tplist);
        tlist.add(tp1);
        Assessment assessment = new Assessment();
        assessment.setId(1L);
        Template template = new Template();

        template.setId(2L);
        assessment.setTemplate(template);
        ScoreCategory s1=new ScoreCategory();
        s1.setId(1L);
        List<ScoreCategory> slist=Arrays.asList(s1);
        assessment.getTemplate().setScore(new ScoringScale());
        assessment.getTemplate().setScoreCategories(slist);

        assessment.setProjectCategory(list);
        assessment.getTemplate().setProjectCategory(tlist);

        assessment.setAssessmentDescription("testDescription");

        Project project=new Project();
        project.setProjectName("testProjectName");
        project.setId(1L);
        assessment.setProject(project);


        Account account=new Account();
        account.setAccountName("testAccountName");
        account.setId(1L);
        assessment.setAccount(account);

        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setAssessmentId(1L);
        CategoryWiseScore categoryWiseScore=new CategoryWiseScore();
        List<CategoryWiseScore> categoryWiseScores=new ArrayList<>();
        categoryWiseScores.add(categoryWiseScore);
        assessment.setCategoryScores(categoryWiseScores);
        assessment.setProjectId(111L);
        assessment.setProjectType(new ProjectType());
        assessmentDto = conversationService.toAssessmentDto(assessment);

        assertNotNull(assessmentDto);
        assertEquals(assessment.getId(), assessmentDto.getAssessmentId());
        assertEquals(assessment.getTemplate().getTemplateName(),assessmentDto.getTemplateName());
        assertEquals(assessment.getAssessmentDescription(),assessmentDto.getAssessmentDescription());
        assertEquals(assessment.getProject().getProjectName(),assessmentDto.getProjectName());
        assertEquals(assessment.getAccount().getAccountName(),assessmentDto.getAccountName());

    }


    @Test
    void testtoLineChartDtos() {
        ConversationService conversationService = new ConversationService();
        List<LineChartDto> list = conversationService.toLineChartDtos(
            this.populateLineChartProjection());
        assertEquals(list.size(), Integer.valueOf(2));
        assertEquals(list.get(0).getCount(), this.populateLineChartProjection().get(0).getCount());
        assertEquals(list.get(0).getDate(), this.populateLineChartProjection().get(0).getId());
    }

    private List<LineChartProjection> populateLineChartProjection() {

        LineChartProjection projection = new LineChartProjection();
        projection.setCount(5);
        projection.setId("31-10-2023");
        LineChartProjection projection1 = new LineChartProjection();
        projection1.setCount(2);
        projection1.setId("30-10-2023");
        return List.of(projection, projection1);
    }

    @Test
    void testConvertToUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUserName("testUser");
        userDto.setRole("developer");
        User user = new User();
        user.setId(1L);

        user = conversationService.convertToUser(userDto);

        assertEquals(user.getUserName(), userDto.getUserName());
        assertEquals(user.getRole(), userDto.getRole());
    }

    @Test
    void testConvertToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testUser");
        user.setRole("developer");
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        user.setCreatedDate(Instant.now());
        user.setLastLoginTime(Instant.now());
        userDto = conversationService.convertToUserDto(user);
        user.setCreatedDate(null);
        conversationService.convertToUserDto(user);
        assertEquals("testUser", userDto.getUserName());
        assertEquals("developer", userDto.getRole());
    }

    @Test
    void testConvertToUserList() {
        UserDto userDto1 = new UserDto();
        userDto1.setEmailAddress("user1@example.com");
        UserDto userDto2 = new UserDto();
        userDto2.setEmailAddress("user2@example.com");
        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);
        List<User> user = conversationService.convertToUserList(userDtoList);
        assertEquals(userDto1.getEmailAddress(), user.get(0).getEmailAddress());
    }

    @Test
     void testConvertToUserDtoListWithEmptyList() {
        List<User> users = Collections.emptyList();
        assertThrows(ResourceNotFoundException.class, () -> conversationService.convertToUserDtoList(users));
    }

    @Test
    void testConvertToUserDtoList() {
        User user1 = new User();
        user1.setLastLoginTime(Instant.now());
        user1.setCreatedDate(Instant.now());
        user1.setEmailAddress("user1@example.com");
        User user2 = new User();
        user2.setEmailAddress("user2@example.com");
        user2.setCreatedDate(Instant.now());
        user2.setLastLoginTime(Instant.now());
        user2.setEmailAddress("user1@example.com");
        List<User> userList = Arrays.asList(user1, user2);
        List<UserDto> userDtoList = conversationService.convertToUserDtoList(userList);
        assertEquals(user1.getEmailAddress(), userDtoList.get(0).getEmailAddress());
    }

    @Test
    void testConvertToUserListWithEmptyList() {
        List<UserDto> userDtos = Collections.emptyList();
        assertThrows(ResourceNotFoundException.class, () -> conversationService.convertToUserList(userDtos));
    }

    @Test
    void testToAuditDto() {
        Audit audit = new Audit();
        audit.setId(1L);
        audit.setUpdatedByName("testUser");
        AuditDto auditDto = conversationService.toAuditDto(audit);

        assertEquals("testUser", auditDto.getUpdatedByName());

    }

    @Test
    void testToAuditDtoList() {
        Audit audit1 = new Audit();
        audit1.setUpdatedByName("testUser1");
        Audit audit2 = new Audit();
        audit2.setUpdatedByName("testUser2");
        List<Audit> auditList = Arrays.asList(audit1, audit2);
        List<AuditDto> auditDtoList = conversationService.toAuditDtoList(auditList);
        assertEquals(audit1.getUpdatedByName(), auditDtoList.get(0).getUpdatedByName());
    }

    @Test
    void testToTemplateDto() {
        ScoreCategory scoreCategory=new ScoreCategory();
        List<ScoreCategory> scoreCategoryList=new ArrayList<>();
        scoreCategoryList.add(scoreCategory);
        Template template = new Template();
        template.setId(1L);
        template.setTemplateName("T1");
        template.setScore(new ScoringScale());
        template.setScoreCategories(scoreCategoryList);

       TemplateDto templateDto = conversationService.toTemplateDto(template);
        assertNotNull(templateDto);
        assertEquals(template.getId(),templateDto.getTemplateId());
}

    @Test
    void testToTemplateDtos() {
        ScoreCategory scoreCategory=new ScoreCategory();
        List<ScoreCategory> scoreCategoryList=new ArrayList<>();
        scoreCategoryList.add(scoreCategory);
        Template template1 = new Template();
        template1.setId(1L);
        template1.setTemplateName("T1");
        template1.setScore(new ScoringScale());
        template1.setScoreCategories(scoreCategoryList);
        Template template2 = new Template();
        template2.setId(2L);
        template2.setTemplateName("T2");
        template2.setScore(new ScoringScale());
        template2.setScoreCategories(scoreCategoryList);
        List<Template> templateList=Arrays.asList(template1,template2);

        List<TemplateDto> resultDtoList = conversationService.toTemplateDtos(templateList);
        assertNotNull(resultDtoList);
        assertEquals(2,resultDtoList.size());
    }
    @Test
     void testToScoreDto() {
        ScoringScale domain = new ScoringScale();
        domain.setId(1L);
        domain.setName("Test Scale");
        domain.setCreatedUserId(100L);
        domain.setScoreScaleType("Type1");

        ScoreScaleDto result = conversationService.toScoreDto(domain);

        assertNotNull(result);
        assertEquals(domain.getId(), result.getId());
        assertEquals(domain.getRange(), result.getRange());
        assertEquals(domain.getName(), result.getName());
        assertEquals(domain.getCreatedUserId(), result.getCreatedUserId());
        assertEquals(domain.getScoreScaleType(), result.getScoreScaleType());
    }

    @Test
     void testToScoreDtos_NonEmptyList() {

        ScoringScale domain1 = new ScoringScale();
        ScoringScale domain2 = new ScoringScale();
        List<ScoringScale> domains = Arrays.asList(domain1, domain2);

        List<ScoreScaleDto> result = conversationService.toScoreDtos(domains);

        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    void testToAssessmentProjectCategories() {

        AssessmentProjectCategoryDto dto1 = mock(AssessmentProjectCategoryDto.class);
        AssessmentProjectCategoryDto dto2 = mock(AssessmentProjectCategoryDto.class);
        List<AssessmentProjectCategoryDto> dtoList = Arrays.asList(dto1, dto2);

        List<AssessmentProjectCategory> result = conversationService.toAssessmentProjectCategories(dtoList);

        assertNotNull(result);
        assertEquals(2, result.size());
    }





    @Test
     void testConvertToProjectTypeDto() {
        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        projectType.setProjectTypeName("testName");

        ProjectTypeDto resultDto = conversationService.convertToProjectTypeDto(projectType);

        assertNotNull(resultDto);
        assertEquals(projectType.getId(), resultDto.getId());
        assertEquals(projectType.getProjectTypeName(), resultDto.getProjectTypeName());
    }

    @Test
    void testConvertToProjectTypeDtoList() {
        ProjectType projectType1=new ProjectType();
        projectType1.setProjectTypeName("testName1");
        ProjectType projectType2=new ProjectType();
        projectType2.setProjectTypeName("testName2");
        List<ProjectType> projectTypes = Arrays.asList(projectType1, projectType1);

        List<ProjectTypeDto> resultDtos = conversationService.convertToProjectTypeDto(projectTypes);

        assertNotNull(resultDtos);
        assertEquals(2, resultDtos.size());
        assertEquals(projectType1.getId(), resultDtos.get(0).getId());
        assertEquals(projectType1.getProjectTypeName(), resultDtos.get(0).getProjectTypeName());
    }


    @Test
    void testToAssessmentSubmitedByDto() {
        Assessment assessment=new Assessment();
        Template template=new Template();
        List<Reviewer> reviewerList=new ArrayList<>();
        Reviewer reviewer1=new Reviewer();
        reviewer1.setReviewerName("testName");
        reviewerList.add(reviewer1);
        List<Project> projects=new ArrayList<>();
        Project project=new Project();
        project.setId(2L);
        projects.add(project);
        assessment.setId(1L);
        assessment.setProjectId(2L);
        assessment.setSubmitStatus(AssessmentStatus.SAVE);
        assessment.setTemplate(template);
        assessment.setReviewers(reviewerList);

        AssessmentSubmitedByDto resultDto = conversationService.toAssessmentSubmitedByDto(assessment);
        assessment.setReviewers(new ArrayList<>());
        conversationService.toAssessmentSubmitedByDto(assessment);

        assertNotNull(resultDto);
        assertEquals(1L, resultDto.getAssessmentId());
        assertEquals("citi", resultDto.getClientName());
        assertEquals(2L, resultDto.getProjectId());
    }

    @Test
    void testToTemplateDtoFromJsonString_Success() throws JsonProcessingException {
        String templateJsonString = "{\"templateId\":1, \"templateName\":\"Sample Template\"}";
        TemplateDto mockDto = new TemplateDto();
        when(objectMapper.readValue(templateJsonString, TemplateDto.class)).thenReturn(mockDto);

        TemplateDto result = conversationService.toTemplateDtoFromJsonString(templateJsonString);

        assertNotNull(result);
    }

    @Test
    void testToAssessmentSubmitedByDtos() {
        Assessment assessment1 =new Assessment();
        Assessment assessment2 =new Assessment();
        Template template=new Template();
        List<Reviewer> reviewerList=new ArrayList<>();
        Reviewer reviewer1=new Reviewer();
        reviewer1.setReviewerName("testName");
        reviewerList.add(reviewer1);
        List<Project> projects=new ArrayList<>();
        Project project=new Project();
        project.setId(2L);
        projects.add(project);
        assessment1.setId(1L);
        assessment1.setProjectId(2L);
        assessment1.setSubmitStatus(AssessmentStatus.SAVE);
        assessment1.setTemplate(template);
        assessment1.setReviewers(reviewerList);
        assessment2.setId(2L);
        assessment2.setProjectId(2L);
        assessment2.setSubmitStatus(AssessmentStatus.SAVE);
        assessment2.setTemplate(template);
        assessment2.setReviewers(reviewerList);

        List<Assessment>  assessmentList=Arrays.asList(assessment1,assessment2);

        List<AssessmentSubmitedByDto> resultDto = conversationService.toAssessmentSubmitedByDtos(assessmentList);

        assertNotNull(resultDto);
        assertEquals(1L, resultDto.get(0).getAssessmentId());
    }

    @Test
    void testToAssessmentSubmitedByDtosThrowsException() {
        List<Assessment> assessments = Collections.emptyList();
        assertThrows(AssessmentNotFoundException.class, () -> conversationService.toAssessmentSubmitedByDtos(assessments));
    }

    @Test
     void testToTemplateInfo() {
     Template template=new Template();
     template.setId(1L);
     template.setTemplateName("t1");
     template.setIsActive(true);

        TemplateInfo result = conversationService.toTemplateInfo(template);

        assertNotNull(result);
        assertEquals(template.getId(), result.getId());
        assertEquals(template.getTemplateName(), result.getTemplateName());
        assertEquals(template.getIsActive(), result.getIsActive());
    }

    @Test
     void testToTemplateInfoThrowsException() {
        List<Template> templates = Collections.emptyList();
        assertThrows(TemplateNotFoundException.class, () -> conversationService.toTemplateInfo(templates));

    }

    @Test
     void testToTemplateInfoList() {
        Template template1 =new Template();
        template1.setId(1L);
        template1.setTemplateName("t1");
        template1.setIsActive(true);
        Template template2 =new Template();
        template2.setId(2L);
        template2.setTemplateName("t2");
        template2.setIsActive(false);
        List<Template> templateList=Arrays.asList(template1,template2);

        List<TemplateInfo> result = conversationService.toTemplateInfo(templateList);

        assertNotNull(result);
        assertEquals(template1.getId(), result.get(0).getId());
        assertEquals(template2.getTemplateName(), result.get(1).getTemplateName());
        assertEquals(template1.getIsActive(), result.get(0).getIsActive());
    }

    @Test
    void testToQuestionAnswer() {
        QuestionAnswerDto dto = new QuestionAnswerDto();
        dto.setAnswerOptionIndex(1);
        dto.setComment("Test Comment");
        dto.setFileUri("http://example.com/file");
        dto.setMimeType("image/jpeg");
        dto.setQuestionId(1);
        dto.setQuestionDescription("Test Description");

        QuestionAnswer result = conversationService.toQuestionAnswer(dto);

        assertNotNull(result);
        assertEquals(dto.getAnswerOptionIndex(), result.getAnswerOptionIndex());
        assertEquals(dto.getComment(), result.getComment());
        assertEquals(dto.getFileUri(), result.getFileUri());
        assertEquals(dto.getMimeType(), result.getMimeType());
        assertEquals(dto.getQuestionId(), result.getQuestionId());
        assertEquals(dto.getQuestionDescription(), result.getQuestionDescription());
    }

    @Test
    void testToAssessment() {
        AssessmentDto dto = new AssessmentDto();
        Template template=new Template();
        Account account=new Account();
        ProjectType projectType=new ProjectType();
        Project project=new Project();
        Reviewer reviewer1=new Reviewer();
        Reviewer reviewer2=new Reviewer();
        List<Reviewer> reviewerList=Arrays.asList(reviewer1,reviewer2);

        dto.setProjectId(123L);
        dto.setReviewers(reviewerList);
        dto.setSubmitedAt(123456789L);
        dto.setSubmitedBy("a2f876d4-9269-11ee-b9d1-0242ac120002");
        dto.setSubmitStatus(AssessmentStatus.SAVE);
        dto.setAssessmentDescription("Test Description");

        List<AssessmentProjectCategoryDto> projectCategoryDtos = new ArrayList<>();
        dto.setProjectCategory(projectCategoryDtos);

        Assessment result = conversationService.toAssessment(dto, template,account,projectType,project);

        assertNotNull(result);
        assertEquals(dto.getProjectId(), result.getProjectId());
        assertEquals(dto.getReviewers(), result.getReviewers());
        assertEquals(dto.getSubmitedAt(), result.getSubmitedAt());
        assertEquals(dto.getSubmitedBy(), result.getSubmitedBy());
        assertEquals(dto.getSubmitStatus(), result.getSubmitStatus());

    }


    @Test
    void testToTemplate() {
        TemplateDto templateDto = new TemplateDto();
        templateDto.setTemplateName("T1");

        Template result = conversationService.toTemplate(templateDto);

        assertNotNull(result);
        assertEquals(templateDto.getTemplateName(),result.getTemplateName());
    }



    @Test
    void testPopulateTemplateForSaveTemplate() {
        ScoringScale score = new ScoringScale();
        List<ScoreCategory> scoreCategories = Arrays.asList(new ScoreCategory());
        List<Project> projects = Arrays.asList(new Project());
        List<ProjectType> projectTypes = Arrays.asList(new ProjectType());
        TemplateSaveRequestDto templateSaveRequest = new TemplateSaveRequestDto();
        templateSaveRequest.setTemplateName("Template Name");
        templateSaveRequest.setTemplateUploadedUserId("a2f876d4-9269-11ee-b9d1-0242ac120002");
        templateSaveRequest.setTemplateUploadedUserName("Uploader Name");
        TemplateQuestionnaire templateQuestionnaire1=new TemplateQuestionnaire();
        templateQuestionnaire1.setQuestionId(1);
        TemplateQuestionnaire templateQuestionnaire2=new TemplateQuestionnaire();
        templateQuestionnaire2.setQuestionId(2);
        List<TemplateQuestionnaire> tplist=Arrays.asList(templateQuestionnaire1,templateQuestionnaire2);
        List<ProjectCategory> projectCategoryList=new ArrayList<>();
        ProjectCategory tp1=new ProjectCategory();
        tp1.setTemplateQuestionnaire(tplist);
        projectCategoryList.add(tp1);
        Template template = new Template();
        template.setProjectCategory(projectCategoryList);

        conversationService.populateTemplateForSaveTemplate(score, scoreCategories, projects, projectTypes, templateSaveRequest, template);

        assertEquals(projects, template.getProjects());
        assertEquals(projectTypes, template.getProjectTypes());
        //assertEquals(score, template.getScore());
        assertEquals(scoreCategories, template.getScoreCategories());
        assertEquals(templateSaveRequest.getTemplateName(), template.getTemplateName());
        assertEquals(templateSaveRequest.getTemplateUploadedUserId(), template.getTemplateUploadedUserId());
        assertEquals(templateSaveRequest.getTemplateUploadedUserName(), template.getTemplateUploadedUserName());

    }
    @Test
    void toMetricAndAssessmentDetailsDtoSuccess() {
        Assessment assessment = new Assessment();
        assessment.setId(1L);
        assessment.setUpdatedAt(System.currentTimeMillis());
        assessment.setSubmitterName("john");
        assessment.setSubmitedBy("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        Project project = new Project();
        project.setProjectName("Development");
        project.setProjectCode("175DEV01");
        assessment.setProject(project);
        Account account = new Account();
        account.setAccountName("citi");
        assessment.setAccount(account);
        assessment.setSubmitStatus(AssessmentStatus.SUBMITTED);
        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerName("Ravi");
        reviewer.setReviewerAt(System.currentTimeMillis());
        assessment.setReviewers(Arrays.asList(reviewer));
        assessment.setSubmitStatus(AssessmentStatus.SAVE);
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDtoWithSavedStatus = conversationService.toMetricAndAssessmentDetailsDto(assessment);
        assessment.setSubmitStatus(AssessmentStatus.SUBMITTED);

        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDtoWithSubmittedStatus = conversationService.toMetricAndAssessmentDetailsDto(assessment);
        assessment.setSubmitStatus(AssessmentStatus.REVIEWED);

        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDtoWithReviewedStatus = conversationService.toMetricAndAssessmentDetailsDto(assessment);


        assertNotNull(metricAndAssessmentDetailsDtoWithSavedStatus);
        assertEquals(assessment.getId(), metricAndAssessmentDetailsDtoWithSavedStatus.getId());
        assertEquals(assessment.getProject().getProjectName(), metricAndAssessmentDetailsDtoWithSavedStatus.getProjectName());
        assertEquals(assessment.getAccount().getAccountName(), metricAndAssessmentDetailsDtoWithSavedStatus.getAccountName());
        assertEquals(AssessmentStatus.SUBMITTED.name(), metricAndAssessmentDetailsDtoWithSubmittedStatus.getStatus());
        assertEquals(AssessmentStatus.REVIEWED.name(), metricAndAssessmentDetailsDtoWithReviewedStatus.getStatus());

    }
    @Test
    void toMetricAndAssessmentDetailsDtosSuccess() {
        Assessment assessment1 = new Assessment();
        assessment1.setId(1L);
        assessment1.setUpdatedAt(System.currentTimeMillis());
        assessment1.setSubmitterName("john");
        assessment1.setSubmitedBy("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        Project project1 = new Project();
        project1.setProjectName("Development");
        project1.setProjectCode("175DEV01");
        assessment1.setProject(project1);
        Account account1 = new Account();
        account1.setAccountName("citi");
        assessment1.setAccount(account1);
        assessment1.setSubmitStatus(AssessmentStatus.SUBMITTED);
        Reviewer reviewer1 = new Reviewer();
        reviewer1.setReviewerName("Ravi");
        reviewer1.setReviewerAt(System.currentTimeMillis());
        assessment1.setReviewers(Arrays.asList(reviewer1));
        Assessment assessment2 = new Assessment();
        assessment2.setId(2L);
        assessment2.setUpdatedAt(System.currentTimeMillis());
        assessment2.setSubmitterName("Steve");
        assessment2.setSubmitedBy("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        Project project2 = new Project();
        project2.setProjectName("Testing");
        project2.setProjectCode("175TES01");
        assessment2.setProject(project2);
        Account account2 = new Account();
        account2.setAccountName("sbi");
        assessment2.setAccount(account2);
        assessment2.setSubmitStatus(AssessmentStatus.SAVE);
        Reviewer reviewer2 = new Reviewer();
        reviewer2.setReviewerName("Anil");
        reviewer2.setReviewerAt(System.currentTimeMillis());
        assessment2.setReviewers(Arrays.asList(reviewer2));
        List<Assessment> assessments = Arrays.asList(assessment1, assessment2);

        List<MetricAndAssessmentDetailsDto> result = conversationService.toMetricAndAssessmentDetailsDto(assessments);

        assertNotNull(result);
        assertEquals(assessment1.getId(), result.get(0).getId());
        assertEquals(assessment1.getProject().getProjectName(), result.get(0).getProjectName());
        assertEquals(assessment2.getAccount().getAccountName(), result.get(1).getAccountName());
    }
    @Test
    void toMetricAndAssessmentDetailsDtoWithEmptyList() {
        List<Assessment> assessments = Collections.emptyList();
        assertThrows(AssessmentNotFoundException.class, () -> conversationService.toMetricAndAssessmentDetailsDto(assessments));
    }
    @Test
    void toProjectDtoSuccessful() {
        Project project = new Project();
        project.setId(1L);
        project.setProjectName("Sample Project");
        project.setManagerName("Manager Name");
        project.setUpdatedBy("User123");
        project.setStatus(true);
        Account account = new Account();
        account.setId(2L);
        project.setAccount(account);
        ProjectDto result = conversationService.toProjectDto(project);

        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
        assertEquals(project.getProjectName(), result.getProjectName());
        assertEquals(project.getManagerName(), result.getManagerName());
        assertEquals(project.getAccount().getId(), result.getAccountId());
    }
    @Test
    void testToAssessmentReviewDto() {
        Assessment assessment = new Assessment();
        assessment.setId(1L);
        assessment.setUpdatedAt(System.currentTimeMillis());
        assessment.setSubmitterName("john");
        assessment.setSubmitedBy("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        Project project = new Project();
        project.setProjectName("Development");
        project.setProjectCode("175DEV01");
        assessment.setProject(project);
        Account account = new Account();
        account.setAccountName("citi");
        assessment.setAccount(account);
        assessment.setSubmitStatus(AssessmentStatus.SUBMITTED);
        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerName("Ravi");
        reviewer.setReviewerAt(System.currentTimeMillis());
        assessment.setReviewers(Arrays.asList(reviewer));
        AssessmentReviewDto result = conversationService.toAssessmentReviewDto(assessment);

        assertNotNull(result);
        assertEquals(assessment.getId(), result.getId());
        assertEquals(assessment.getAccount().getAccountName(), result.getAccountName());
        assertEquals(assessment.getProject().getProjectName(), result.getProjectName());
        assertEquals(assessment.getSubmitedAt(), result.getSubmittedAt());
        assertEquals(assessment.getSubmitterName(), result.getSubmitterName());
        assertEquals(assessment.getProject().getProjectCode(), result.getProjectCode());
    }
    @Test
    void testToAssessmentReviewDtosSuccess() {
        Assessment assessment1 = new Assessment();
        assessment1.setId(1L);
        assessment1.setUpdatedAt(System.currentTimeMillis());
        assessment1.setSubmitterName("john");
        assessment1.setSubmitedBy("7e2313c6-1c9f-472a-a92d-76f165e08bd3");

        Project project = new Project();
        project.setProjectName("Development");
        project.setProjectCode("175DEV01");
        assessment1.setProject(project);
        Account account = new Account();
        account.setAccountName("citi");
        assessment1.setAccount(account);
        assessment1.setSubmitStatus(AssessmentStatus.SUBMITTED);
        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerName("Ravi");
        reviewer.setReviewerAt(System.currentTimeMillis());
        assessment1.setReviewers(Arrays.asList(reviewer));

        Assessment assessment2 = new Assessment();
        assessment2.setId(1L);
        assessment2.setUpdatedAt(System.currentTimeMillis());
        assessment2.setAccount(account);
        assessment2.setSubmitterName("john");
        assessment2.setSubmitedBy("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        assessment2.setProject(project);
        assessment2.setSubmitStatus(AssessmentStatus.SUBMITTED);
        reviewer.setReviewerAt(System.currentTimeMillis());
        assessment2.setReviewers(Arrays.asList(reviewer));
        List<Assessment> assessments=Arrays.asList(assessment1,assessment2);
        List<AssessmentReviewDto> result = conversationService.toAssessmentReviewDtos(assessments);

        assertNotNull(result);
        assertEquals(assessment1.getId(), result.get(0).getId());
        assertEquals(assessment1.getAccount().getAccountName(), result.get(0).getAccountName());
        assertEquals(assessment1.getProject().getProjectName(), result.get(0).getProjectName());
        assertEquals(assessment2.getSubmitterName(), result.get(1).getSubmitterName());
    }

    @Test
    void testToReportDetailsDtos(){
        List<Assessment> assessments=new ArrayList<>();
        Assessment assessment=new Assessment();
        Template template=new Template();
        List<Reviewer> reviewerList=new ArrayList<>();
        Reviewer reviewer1=new Reviewer();
        reviewer1.setReviewerName("testName");
        reviewerList.add(reviewer1);
        List<Project> projects=new ArrayList<>();
        Project project=new Project();
        project.setId(2L);
        projects.add(project);
        assessment.setAccount(new Account());
        assessment.setProject(new Project());
        assessment.setId(1L);
        assessment.setProjectId(2L);
        assessment.setSubmitStatus(AssessmentStatus.SAVE);
        assessment.setTemplate(template);
        assessment.setReviewers(reviewerList);
        assessments.add(assessment);
        conversationService.toReportDetailsDtos(assessments);

    }
    @Test
    void testToReportDetailsDtosWithEmptyList() {
        List<Assessment> assessments = Collections.emptyList();
        assertThrows(AssessmentNotFoundException.class, () -> conversationService.toReportDetailsDtos(assessments));
    }
    @Test
    void testToReportDetailsDto(){
        Assessment assessment=new Assessment();
        Template template=new Template();
        List<Reviewer> reviewerList=new ArrayList<>();
        Reviewer reviewer1=new Reviewer();
        reviewer1.setReviewerName("testName");
        reviewerList.add(reviewer1);
        List<Project> projects=new ArrayList<>();
        Project project=new Project();
        project.setId(2L);
        projects.add(project);
        assessment.setAccount(new Account());
        assessment.setProject(new Project());
        assessment.setId(1L);
        assessment.setProjectId(2L);
        assessment.setSubmitStatus(AssessmentStatus.SAVE);
        assessment.setTemplate(template);
        assessment.setReviewers(reviewerList);
        conversationService.toReportDetailsDto(assessment);

    }

    @Test
    void testToReportDetailsDtosWithNullObject() {
        assertThrows(AssessmentNotFoundException.class, () -> conversationService.toReportDetailsDto(null));
    }






    @Test
    void testToProjectFromMetaDataWithFoundAccount() {
        CsvMetaDataDto dto = mock(CsvMetaDataDto.class);
        when(dto.getAccountId()).thenReturn(1L);
        when(dto.getDeliveryManager()).thenReturn("Barani");

        Map<Long, Account> accountById = new HashMap<>();
        Account foundAccount = new Account();
        accountById.put(1L, foundAccount);
        Long expectedId = 123L;

        Project result = conversationService.toProjectFromMetaData(accountById, dto, expectedId);

        assertNotNull(result);
        assertEquals(expectedId, result.getId());
        assertEquals(foundAccount, result.getAccount());
        assertEquals("Barani", result.getManagerName());
    }

    @Test
    void testToProjectFromMetaDataWithNotFoundAccount() {
        CsvMetaDataDto dto = mock(CsvMetaDataDto.class);
        when(dto.getAccountId()).thenReturn(999L);
        Map<Long, Account> accountById = new HashMap<>();
        Long expectedId = 123L;

        Project result = conversationService.toProjectFromMetaData(accountById, dto, expectedId);

        assertNotNull(result);
        assertNull(result.getAccount());
        assertEquals(expectedId, result.getId());
    }


    @Test
    void testToAssessmentDtos() {
        TemplateQuestionnaire templateQuestionnaire1=new TemplateQuestionnaire();
        templateQuestionnaire1.setQuestionId(1);
        TemplateQuestionnaire templateQuestionnaire2=new TemplateQuestionnaire();
        templateQuestionnaire2.setQuestionId(2);
        List<TemplateQuestionnaire> tplist=Arrays.asList(templateQuestionnaire1,templateQuestionnaire2);
        QuestionAnswer questionAnswer1=new QuestionAnswer();
        questionAnswer1.setQuestionId(1);
        QuestionAnswer questionAnswer2=new QuestionAnswer();
        questionAnswer2.setQuestionId(2);
        List<QuestionAnswer> qlist=Arrays.asList(questionAnswer1,questionAnswer2);

        List<AssessmentProjectCategory> list=new ArrayList<>();
        AssessmentProjectCategory p1=new AssessmentProjectCategory();
        p1.setTemplateQuestionnaire(qlist);
        list.add(p1);
        List<ProjectCategory> tlist=new ArrayList<>();
        ProjectCategory tp1=new ProjectCategory();
        tp1.setTemplateQuestionnaire(tplist);
        tlist.add(tp1);
        Assessment assessment = new Assessment();
        assessment.setId(1L);
        Template template = new Template();

        template.setId(2L);
        assessment.setTemplate(template);
        ScoreCategory s1=new ScoreCategory();
        s1.setId(1L);
        List<ScoreCategory> slist=Arrays.asList(s1);
        assessment.getTemplate().setScore(new ScoringScale());
        assessment.getTemplate().setScoreCategories(slist);

        assessment.setProjectCategory(list);
        assessment.getTemplate().setProjectCategory(tlist);

        assessment.setAssessmentDescription("testDescription");

        Project project=new Project();
        project.setProjectName("testProjectName");
        project.setId(1L);
        assessment.setProject(project);


        Account account=new Account();
        account.setAccountName("testAccountName");
        account.setId(1L);
        assessment.setAccount(account);

        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setAssessmentId(1L);
        assessmentDto.setAssessmentId(1L);
        assessmentDto.setProjectTypeId(111L);
        List<Assessment> assessments=new ArrayList<>();
        assessments.add(assessment);
        conversationService.toAssessmentDtos(assessments);

    }




    @Test
    void toLoginDto() {
        User user = new User();
        user.setId(111L);
        user.setRole("SE");
        user.setEmailAddress("aaa@maveric-systems.com");
        user.setUserName("maveric");
        user.setLastLoginTime(Instant.now());
        conversationService.toLoginDto(user);
    }

    @Test

    void toAccountList() {
        List<AccountDto> accountList = new ArrayList<>();
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(111L);
        accountDto.setAccountName("A-1");
        accountList.add(accountDto);
        conversationService.toAccountList(accountList);

    }

    @Test
    void toProjectTypeList() {
        List<ProjectTypeDto> projectDtoList = new ArrayList<>();
        ProjectTypeDto projectTypeDto = new ProjectTypeDto();
        projectTypeDto.setId(111L);
        projectTypeDto.setProjectTypeName("P-1");
        projectDtoList.add(projectTypeDto);
        conversationService.toProjectTypeList(projectDtoList);
    }

    @Test
    void toAssessmentsSubmittedDashboardDtos() {
        List<Assessment> assessmentList = new ArrayList<>();
        Assessment assessment = new Assessment();
        assessment.setId(111L);
        assessment.setCreatedAt(1111111111L);
        assessment.setProject(new Project());
        assessment.setAccount(new Account());
        assessmentList.add(assessment);
        conversationService.toAssessmentsSubmittedDashboardDtos(assessmentList);

    }

    @Test
    void toProjectTypeFromMetaData() {
        CsvMetaDataDto csvMetaDataDto = new CsvMetaDataDto();
        csvMetaDataDto.setProject("P-1");
        csvMetaDataDto.setId(111L);
        conversationService.toProjectTypeFromMetaData(csvMetaDataDto);
    }

    @Test
    void toAssessmentTemplateDtos(){
        List<Template> templateList=new ArrayList<>();
        Template template=new Template();
        template.setId(111L);
        Project project=new Project();
        project.setId(111L);
        List<Project> projects=List.of(project);
        template.setProjects(projects);
        ProjectType projectType=new ProjectType();
        projectType.setId(111L);
        List<ProjectType> projectTypes=List.of(projectType);

        template.setProjectTypes(projectTypes);
        template.setScoreCategories(new ArrayList<>());

        templateList.add(template);
        conversationService.toAssessmentTemplateDtos(templateList);
    }

    @Test
    void toProjectList() {
        List<ProjectDto> projectDtoList = new ArrayList<>();
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setProjectName("P-1");
        projectDto.setStatus(true);
        projectDtoList.add(projectDto);
        conversationService.toProjectList(projectDtoList);
    }

    @Test
    void toBusinessUnitFromMetaData() {
        CsvMetaDataDto dto = new CsvMetaDataDto();
        dto.setId(111L);
        dto.setProject("P-1");
    }

    @Test
    void toAccountFromMetaData() {
        CsvMetaDataDto dto = new CsvMetaDataDto();
        dto.setId(111L);
        dto.setProject("P-1");
        conversationService.toAccountFromMetaData(dto);

    }

    @Test
    void toProjectFromMetaData() {
        Map<Long, Account> accountById = new HashMap<>();
        CsvMetaDataDto dto = new CsvMetaDataDto();
        dto.setId(111L);
        dto.setProject("P-1");
        conversationService.toProjectFromMetaData(accountById, dto, 111L);

    }
    @Test
    void parseToCsvMetaDataDto(){
        Reader reader=new InputStreamReader(InputStream.nullInputStream());
        conversationService.parseToCsvMetaDataDto(reader);
    }

    @Test
    void toSubmissionFilterDtoFromJsonString() throws IOException {
        SubmissionFilterDto submissionFilterDto=new SubmissionFilterDto();
        submissionFilterDto.setFromDate(System.currentTimeMillis());
        submissionFilterDto.setSubmittedBy("U-1");
        when(objectMapper.readValue("",SubmissionFilterDto.class)).thenReturn(submissionFilterDto);
        conversationService.toSubmissionFilterDtoFromJsonString("");
        verify(objectMapper).readValue("",SubmissionFilterDto.class);
    }

    @Test
    void validatedAssessment(){
        assertThrows(AssessmentNotFoundException.class, () -> conversationService.validatedAssessment(null));
        Assessment  assessment=new Assessment();
        assertThrows(TemplateNotFoundException.class, () -> conversationService.validatedAssessment(assessment));
        assessment.setTemplate(new Template());
        assessment.getTemplate().setScore(new ScoringScale());
        assertThrows(AssessmentProjectCategoryNotFoundException.class, () -> conversationService.validatedAssessment(assessment));
        AssessmentProjectCategory assessmentProjectCategory=new AssessmentProjectCategory();
        assessment.setProjectCategory(List.of(assessmentProjectCategory));
        assertThrows(TemplateProjectCategoryNotFoundException.class, () -> conversationService.validatedAssessment(assessment));
    }
    @Test
    void populateProjectNameByProjectId(){
        List<Project> projects=new ArrayList<>();
        Project project=new Project();
        project.setId(111L);
        project.setProjectName("P-1");
        projects.add(project);
        conversationService.populateProjectNameByProjectId(projects,111L);
    }


    @Test
    void toAssessmentsSubmittedDashboardDto(){
        Assessment assessment=new Assessment();
        Template template=new Template();
        List<Reviewer> reviewerList=new ArrayList<>();
        Reviewer reviewer1=new Reviewer();
        reviewer1.setReviewerName("testName");
        reviewerList.add(reviewer1);
        Project project=new Project();
        project.setId(2L);
        project.setProjectName("P-1");
        Account account=new Account();
        account.setAccountName("A");
        assessment.setAccount(account);

        assessment.setProject(project);
        assessment.setId(1L);
        assessment.setProjectId(2L);
        assessment.setSubmitStatus(AssessmentStatus.SAVE);
        assessment.setTemplate(template);
        assessment.setReviewers(reviewerList);
        conversationService.toAssessmentsSubmittedDashboardDto(assessment);
        assessment.setProject(null);
        assessment.setAccount(null);
        conversationService.toAssessmentsSubmittedDashboardDto(assessment);
    }

    @Test
    void populateTemplateQuestionnaireMap(){
        List<ProjectCategory> projectCategories=new ArrayList<>();
        conversationService.populateTemplateQuestionnaireMap(projectCategories);
    }
}
