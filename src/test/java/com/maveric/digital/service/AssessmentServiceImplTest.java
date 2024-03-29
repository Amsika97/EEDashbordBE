package com.maveric.digital.service;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.maveric.digital.exceptions.*;

import com.maveric.digital.utils.ServiceConstants;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;

import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.*;
import com.maveric.digital.repository.*;
import com.maveric.digital.responsedto.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.projection.LineChartProjection;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AssessmentServiceImplTest {
    private static final String TEMPLATE_NOT_FOUND = "Template not found";
    private static final String QUESTION_VALIDATE = "templateQuestionnaire do not validated";
    private static final String SCORE_CATEGORY_VALIDATE = "score category name do not validated in templateQuestionnaire ";
    @MockBean
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentServiceImpl assessmentService;
    @MockBean
    private ConversationService conversationService;
    @MockBean
    private TemplateRepository templateRepository;

    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private ProjectTypeRepository projectTypeRepository;
    @MockBean
    private UserRepository userRepository;
    private AssessmentDto assessmentDto;
    private ObjectMapper objectMapper;
    private File jsonFile;
    private File saveReviewerCommentjsonFile;
    private ReviewerCommentDto reviewerCommentDto;


    private List<Assessment> assessments;
    private    List<AssessmentProjectCategory> assessmentProjectCategories;
    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        jsonFile = new ClassPathResource("sample/AssessmentDto.json").getFile();
        saveReviewerCommentjsonFile = new ClassPathResource("sample/saveReviewerComment-request.json").getFile();
        File businessUnitJsonFile =new ClassPathResource("sample/businessUnit-id-response.json").getFile();
        File accountJsonFile =new ClassPathResource("sample/Account-response.json").getFile();
        File projectJsonFile= new ClassPathResource("sample/Project-response.json").getFile();
        assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        Account account=objectMapper.readValue(Files.readString(accountJsonFile.toPath()), Account.class);
        Project project =objectMapper.readValue(Files.readString(projectJsonFile.toPath()), Project.class);
        assessments=new ArrayList<>();
        Assessment  assessment=new Assessment();
        BeanUtils.copyProperties(assessmentDto,assessment);
        assessment.setAccount(account);
        assessment.setProject(project);
        assessments.add(assessment);

        List<QuestionAnswer> questionAnswers =new ArrayList<>();
        QuestionAnswer questionAnswer1=new QuestionAnswer();
        questionAnswer1.setQuestionDescription("");
        questionAnswer1.setQuestionId(1);
        questionAnswer1.setAnswerOptionIndex(1);
        QuestionAnswer questionAnswer2=new QuestionAnswer();
        questionAnswer2.setQuestionDescription("");
        questionAnswer2.setQuestionId(2);
        questionAnswer2.setAnswerOptionIndex(1);
        QuestionAnswer questionAnswer3=new QuestionAnswer();
        questionAnswer3.setQuestionDescription("");
        questionAnswer3.setQuestionId(1);
        questionAnswer3.setAnswerOptionIndex(1);
        questionAnswers.add(questionAnswer1);
        questionAnswers.add(questionAnswer2);
        questionAnswers.add(questionAnswer3);
        assessmentProjectCategories=new ArrayList<>();
        AssessmentProjectCategory assessmentProjectCategory =new AssessmentProjectCategory();
        assessmentProjectCategory.setCategoryName("");
        assessmentProjectCategory.setTemplateQuestionnaire(questionAnswers);
        assessmentProjectCategory.setCategoryDescription("");
        assessmentProjectCategories.add(assessmentProjectCategory);
        reviewerCommentDto = objectMapper.readValue(Files.readString(saveReviewerCommentjsonFile.toPath()), ReviewerCommentDto.class);
    }
    @Test
    void testScoreCalculationWithValidDatascore() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        double result = assessmentService.scoreCalculation(assessmentDto);
        double expectedScore = 58.00000000000001;
        assertEquals(expectedScore, result, 0.01);
    }
    @Test
    void testScoreCalculationWithValidDatascore0() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentScoreCategoryNotMatch.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        double result = assessmentService.scoreCalculation(assessmentDto);
        double expectedScore = 0.0;
        assertEquals(expectedScore, result, 0.01);
    }
    @Test
    void testScoreCalculationWithDiffScoreCategories() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample1.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        double result = assessmentService.scoreCalculation(assessmentDto);
        double expectedScore = 66.5;
        assertEquals(expectedScore, result, 0.01);
    }
    @Test
    void testScoreCalculationWithDiffScoreCategoriesJumbledOptions() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample1.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        double result = assessmentService.scoreCalculation(assessmentDto);
        double expectedScore = 66.5;
        assertEquals(expectedScore, result, 0.01);
    }

    @Test
    void testGetAssessmentsBySubmitedBySuccess() {
        String submitedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        when(assessmentRepository.findBySubmitedByOrderByUpdatedAtDesc(submitedBy)).thenReturn(assessments);

        List<Assessment> result = assessmentService.getAssessmentsBySubmitedBy(submitedBy);

        assertEquals(assessments, result);
    }
    @Test
    void testGetAssessmentsBySubmitedByFail() {
        String submitedBy = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                            assessmentService.getAssessmentsBySubmitedBy(submitedBy);});

        assertEquals("Invalid SubmittedBy Id : " + submitedBy, exception.getMessage());

    }

    @Test
    void testFindAssessmentByIdSuccess() {
        Long assessmentId = 1L;
        Assessment assessment = new Assessment();
        assessment.setId(assessmentId);

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        Assessment result = assessmentService.findAssessmentById(assessmentId);

        assertTrue(Objects.nonNull(result));
        assertEquals(assessment, result);
    }

    @Test
    void testFindAssessmentByIdInvalidId() {
        Long assessmentId = -1L;

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            assessmentService.findAssessmentById(assessmentId);
                        });

        assertEquals("Assessment ID not found: " + assessmentId, exception.getMessage());
    }
    @Test
    void testFindAssessmentByIdInvalidIdnull() {
        Long assessmentId = null;

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            assessmentService.findAssessmentById(assessmentId);
                        });

        assertEquals("Assessment ID not found: " + assessmentId, exception.getMessage());
    }

    @Test
    void testFindAssessmentByIdNotFound() {
        Long assessmentId = 1L;

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            assessmentService.findAssessmentById(assessmentId);
                        });

        assertEquals(NOT_FOUND, exception.getStatusCode());
        assertEquals("Assessment with ID " + assessmentId + " not found", exception.getReason());
    }

    @Test
    void testFindAllAssessmentsSuccess() {
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        when(assessmentRepository.findAll()).thenReturn(assessments);

        List<Assessment> result = assessmentService.findAllAssessments();

        assertEquals(assessments, result);
    }

    @Test
    void testGetAllSubmittedAssessmentsSuccess() {
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        when(assessmentRepository.findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(assessments);

        List<Assessment> result = assessmentService.getAllSubmittedAssessments();

        assertEquals(assessments, result);

    }
    @Test
    void testGetAllSubmittedAssessmentsFail() {
        List<Assessment> assessments = new ArrayList<>();

        when(assessmentRepository.findAllSubmittedAssessmentOrderByUpdatedAtDesc(Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(Collections.emptyList());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            assessmentService.getAllSubmittedAssessments();});

    }


    @Test
    void testFindAssessmentsBySubmittedByAndStatusSuccess() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        String submitStatus = "SUBMITTED";
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        when(assessmentRepository.findBySubmitedByAndSubmitStatus(submittedBy, submitStatus)).thenReturn(assessments);

        List<Assessment> result = assessmentService.getAssessmentsBySubmittedByAndStatus(submittedBy, submitStatus);

        assertEquals(assessments, result);
    }
    @Test
    void testFindAssessmentsBySubmittedByAndStatusSuccessIllegalArgsException() {
        String submittedBy = null;
        String submitStatus = "SUBMITTED";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assessmentService.getAssessmentsBySubmittedByAndStatus(submittedBy,submitStatus);});

        assertEquals("Invalid SubmittedBy Id : " + submittedBy, exception.getMessage());

    }
    @Test
    void testFindAssessmentsBySubmittedByAndStatusSuccessIllegalArgsExceptionInvalidSubmittedStatus() {
        String submittedBy = "1";
        String submitStatus = "";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assessmentService.getAssessmentsBySubmittedByAndStatus(submittedBy,submitStatus);});

        assertEquals("Invalid submitStatus  :" + submitStatus, exception.getMessage());

    }
    @Test
    void testFindAssessmentsBySubmittedByAndStatusSuccessIllegalArgsExceptionInvalidSubmittedBy() {
        String submittedBy = "";
        String submitStatus = "SUBMITTED";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assessmentService.getAssessmentsBySubmittedByAndStatus(submittedBy,submitStatus);});

        assertEquals("Invalid SubmittedBy Id : " + submittedBy, exception.getMessage());

    }



    @Test
    void testFindAssessmentsBySubmittedByAndStatusNotFound() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        String submitStatus = "SUBMITTED";
        when(assessmentRepository.findBySubmitedByAndSubmitStatus(submittedBy, submitStatus))
                .thenReturn(Collections.emptyList());

        CustomException exception =
                assertThrows(
                        CustomException.class,
                        () -> {
                            assessmentService.getAssessmentsBySubmittedByAndStatus(submittedBy, submitStatus);
                        });

        assertEquals(new CustomException("Assessment not found", HttpStatus.NO_CONTENT).getMessage(), exception.getMessage());
    }


    @Test
    void testFindAllAssessmentsEmpty() {
        List<Assessment> assessments = new ArrayList<>();

        when(assessmentRepository.findAll()).thenReturn(assessments);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> {
                            assessmentService.findAllAssessments();
                        });

        assertEquals(NOT_FOUND, exception.getStatusCode());
        assertEquals("No Assessment present", exception.getReason());
    }

    private ProjectCategory populateProjectCategory() {
        ProjectCategory projectCategory = new ProjectCategory();
        projectCategory.setCategoryName("initial");
        projectCategory.setTemplateQuestionnaire(List.of(this.populateTemplateQuestionnaire()));
        return projectCategory;
    }

    private TemplateQuestionnaire populateTemplateQuestionnaire() {
        TemplateQuestionnaire templateQuestionnaire = new TemplateQuestionnaire();
        templateQuestionnaire.setQuestionId(1);
        templateQuestionnaire.setFieldType("radio");
        templateQuestionnaire.setQuestion("1. How well-defined are the project objectives? ");
        templateQuestionnaire.setScoreCategory("importance");
        return templateQuestionnaire;
    }

    @Test
    void testSaveOrSubmitAssessment1() {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Account account=new Account();
        ProjectType projectType=new ProjectType();
        Project project=new Project();
        Assessment assessment = new Assessment();
        assessmentDto.setAssessmentId(1l);
        when(assessmentRepository.findById(1l)).thenReturn(Optional.of(assessment));
        when(templateRepository.findById(anyLong())).thenReturn(Optional.of(template));
        when(conversationService.toAssessment(assessmentDto, template,account,projectType,project)).thenReturn(assessment);
        when(assessmentRepository.save(assessment)).thenReturn(assessment);
        when(conversationService.toAssessmentProjectCategories(assessmentDto.getProjectCategory())).thenReturn(List.of(new AssessmentProjectCategory()));
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));

        Assessment assessmentObj = assessmentService.saveOrSubmitAssessment(assessmentDto);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }
    @Test
    void testSaveOrSubmitAssessment() throws IOException {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        template.setTemplateFrequency(TemplateFrequencyReminder.NA);

        Account account=new Account();
        account.setId(1L);

        ProjectType projectType=new ProjectType();
        projectType.setId(1L);
        Project project=new Project();
        project.setId(1L);
        Assessment assessment = new Assessment();
        assessment.setTemplate(template);
        assessment.setAccount(account);
        assessment.setProject(project);
        assessment.setProjectType(projectType);
        assessment.setSubmitStatus(AssessmentStatus.SUBMITTED);
        AssessmentDto assessmentDto11 = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);

        when(templateRepository.findById(assessmentDto11.getTemplateId())).thenReturn(Optional.of(template));
        when(assessmentRepository.findById(assessmentDto11.getAssessmentId())).thenReturn(Optional.empty());
        when(accountRepository.findById(assessmentDto11.getAccountId())).thenReturn(Optional.of(account));
        when(projectTypeRepository.findById(assessmentDto11.getProjectTypeId())).thenReturn(Optional.of(projectType));
        when(projectRepository.findById(assessmentDto11.getProjectId())).thenReturn(Optional.of(project));
        when(conversationService.toAssessment(assessmentDto11, template,account,projectType,project)).thenReturn(assessment);
        when(assessmentRepository.save(assessment)).thenReturn(assessment);
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setProjectCategory(assessmentProjectCategories);
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));

        Assessment assessmentObj = assessmentService.saveOrSubmitAssessment(assessmentDto11);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }
    @Test
    void testSaveOrSubmitAssessmentObjectnull() throws IOException {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        template.setTemplateFrequency(TemplateFrequencyReminder.NA);
        Account account=new Account();
        account.setId(1L);

        ProjectType projectType=new ProjectType();
        projectType.setId(1L);
        Project project=new Project();
        project.setId(1L);
        Assessment assessment = new Assessment();
        assessment.setTemplate(template);
        assessment.setAccount(account);
        assessment.setProject(project);
        assessment.setProjectType(projectType);
        assessment.setSubmitStatus(AssessmentStatus.SUBMITTED);
        File jsonFile1 = new ClassPathResource("sample/AssessmentDto.json").getFile();
        AssessmentDto assessmentDto11 = objectMapper.readValue(Files.readString(jsonFile1.toPath()), AssessmentDto.class);

        when(templateRepository.findById(assessmentDto11.getTemplateId())).thenReturn(Optional.of(template));
        when(assessmentRepository.findById(assessmentDto11.getAssessmentId())).thenReturn(Optional.empty());
        when(accountRepository.findById(assessmentDto11.getAccountId())).thenReturn(Optional.of(account));
        when(projectTypeRepository.findById(assessmentDto11.getProjectTypeId())).thenReturn(Optional.of(projectType));
        when(projectRepository.findById(assessmentDto11.getProjectId())).thenReturn(Optional.of(project));
        when(conversationService.toAssessment(assessmentDto11, template,account,projectType,project)).thenReturn(assessment);
        when(assessmentRepository.save(assessment)).thenReturn(assessment);
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));
        Assessment assessmentObj = assessmentService.saveOrSubmitAssessment(assessmentDto11);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }

    @Test
    void testNewSaveOrSubmitAssessment() {
        Template template = new Template();
        template.setId(1L);
        template.setTemplateName("T1");
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Account account=new Account();
        ProjectType projectType=new ProjectType();
        Project project=new Project();
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        assessmentDto.setAssessmentId(1l);
        when(assessmentRepository.findById(1l)).thenReturn(Optional.of(assessment));
        when(templateRepository.findById(anyLong())).thenReturn(Optional.of(template));
        when(conversationService.toAssessment(assessmentDto, template,account,projectType,project)).thenReturn(assessment);
        when(conversationService.toAssessmentProjectCategories(assessmentDto.getProjectCategory())).thenReturn(List.of(new AssessmentProjectCategory()));
        //doNothing().when(eventPublisher).publishEvent(assessment,anyLong(),anyString(),anyString(),anyString(),null);
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));

        Assessment assessmentObj = assessmentService.saveOrSubmitAssessment(assessmentDto);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }

    @Test
    void testScoreCalculationWithValidDataNoQuestions() {
        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setSubmitStatus(AssessmentStatus.SUBMITTED);
        AssessmentProjectCategoryDto category = new AssessmentProjectCategoryDto();
        category.setCategoryName("Sample Category");
        assessmentDto.setProjectCategory(Collections.singletonList(category));
        List<QuestionAnswerDto> questions = new ArrayList<>();
        QuestionAnswerDto dto = new QuestionAnswerDto();
        dto.setQuestion("");
        dto.setQuestionId(1);
        dto.setScoreCategory(null);
        dto.setAnswerOptionIndex(1);
        questions.add(dto);
        category.setTemplateQuestionnaire(questions);
        double expectedScore = 0.0;
        double result = assessmentService.scoreCalculation(assessmentDto);
        assertEquals(expectedScore, result, 0.01);
    }
    @Test
    void testScoreCalculationWithNoScoreCategory() {
        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setSubmitStatus(AssessmentStatus.SUBMITTED);
        AssessmentProjectCategoryDto category = new AssessmentProjectCategoryDto();
        category.setCategoryName("Sample Category");
        assessmentDto.setProjectCategory(Collections.singletonList(category));
        List<QuestionAnswerDto> questions = new ArrayList<>();
        QuestionAnswerDto dto = new QuestionAnswerDto();
        dto.setQuestion("");
        dto.setQuestionId(1);
        dto.setScoreCategory("");
        dto.setAnswerOptionIndex(1);
        questions.add(dto);
        category.setTemplateQuestionnaire(questions);
        double expectedScore = 0.0;
        double result = assessmentService.scoreCalculation(assessmentDto);
        assertEquals(expectedScore, result, 0.01);
    }


    @Test
    void testValidateQuestionAnswer() {
        assessmentDto.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).setQuestionId(15);
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Account account=new Account();
        ProjectType projectType=new ProjectType();
        Project project=new Project();
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        when(assessmentRepository.findById(1l)).thenReturn(Optional.of(assessment));
        when(templateRepository.findById(anyLong())).thenReturn(Optional.of(template));
        when(conversationService.toAssessment(assessmentDto, template,account,projectType,project)).thenReturn(assessment);
        when(conversationService.toAssessmentProjectCategories(assessmentDto.getProjectCategory())).thenReturn(List.of(new AssessmentProjectCategory()));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.saveOrSubmitAssessment(assessmentDto));
        assertEquals(QUESTION_VALIDATE, illegalArgumentException.getMessage());

    }

    @Test
    void testValidateScoreCategory() {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        template.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).setScoreCategory("satisfactory");
        Account account=new Account();
        ProjectType projectType=new ProjectType();
        Project project=new Project();
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        when(assessmentRepository.findById(1l)).thenReturn(Optional.of(assessment));
        when(templateRepository.findById(anyLong())).thenReturn(Optional.of(template));
        when(conversationService.toAssessment(assessmentDto, template,account,projectType,project)).thenReturn(assessment);
        when(conversationService.toAssessmentProjectCategories(assessmentDto.getProjectCategory())).thenReturn(List.of(new AssessmentProjectCategory()));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.saveOrSubmitAssessment(assessmentDto));
        assertEquals(SCORE_CATEGORY_VALIDATE, illegalArgumentException.getMessage());

    }

    @Test
    void testSaveOrSubmitAssessmentIllegalArgumentException() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.saveOrSubmitAssessment(null));

        TemplateNotFoundException templateNotFoundException = assertThrows(TemplateNotFoundException.class,
                () -> assessmentService.saveOrSubmitAssessment(assessmentDto));
        assertEquals("Request do not validated", illegalArgumentException.getMessage());
        assertEquals(TEMPLATE_NOT_FOUND, templateNotFoundException.getMessage());

    }


    @Test
    void testSaveReviewerComment() {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        assessment.setReviewers(new ArrayList<>(List.of(this.populateReviewer())));
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.of(assessment));
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));
        ReviewerQuestionComment reviewerQuestionComment=new ReviewerQuestionComment();
        reviewerQuestionComment.setReviewerComment("Need improvement");
        reviewerCommentDto.setReviewerQuestionComment(Arrays.asList(reviewerQuestionComment));
        assessment.setProjectCategory(assessmentProjectCategories);
        when(userRepository.findByOidIn(Collections.singletonList(Mockito.<UUID>any()))).thenReturn(Optional.empty());
        reviewerCommentDto.setReviewerId(String.valueOf(UUID.randomUUID()));
        Assessment assessmentObj = assessmentService.saveReviewerComment(reviewerCommentDto);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }
    @Test
    void testSaveReviewerCommentSatusReviewed() {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        assessment.setReviewers(new ArrayList<>(List.of(this.populateReviewer())));
        reviewerCommentDto.setStatus(AssessmentStatus.REVIEWED);
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.of(assessment));
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));
        assessment.setProjectCategory(assessmentProjectCategories);
        ReviewerQuestionComment reviewerQuestionComment=new ReviewerQuestionComment();
        when(userRepository.findByOidIn(Collections.singletonList(Mockito.<UUID>any()))).thenReturn(Optional.empty());
        reviewerCommentDto.setReviewerId(String.valueOf(UUID.randomUUID()));
        reviewerQuestionComment.setReviewerComment("Need improvement");
        reviewerCommentDto.setReviewerQuestionComment(Arrays.asList(reviewerQuestionComment));
        Assessment assessmentObj = assessmentService.saveReviewerComment(reviewerCommentDto);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }

    @Test
    void testSaveReviewerCommentSatusReviewersDiff() {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        assessment.setReviewers(new ArrayList<>(List.of(this.populateReviewer())));
        reviewerCommentDto.setStatus(AssessmentStatus.SAVE);
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.of(assessment));
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));
        assessment.setProjectCategory(assessmentProjectCategories);
        ReviewerQuestionComment reviewerQuestionComment=new ReviewerQuestionComment();
        reviewerQuestionComment.setReviewerComment("Need improvement");
        reviewerCommentDto.setReviewerQuestionComment(Arrays.asList(reviewerQuestionComment));
        when(userRepository.findByOidIn(Collections.singletonList(Mockito.<UUID>any()))).thenReturn(Optional.empty());
        reviewerCommentDto.setReviewerId(String.valueOf(UUID.randomUUID()));

        Assessment assessmentObj = assessmentService.saveReviewerComment(reviewerCommentDto);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }
    @Test
    void testSaveReviewerCommentSatusReviewersDiffNo() {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        assessment.setReviewers(new ArrayList<>(List.of(this.populateReviewer1())));
        reviewerCommentDto.setStatus(AssessmentStatus.SAVE);
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.of(assessment));
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));
        assessment.setProjectCategory(assessmentProjectCategories);
        ReviewerQuestionComment reviewerQuestionComment=new ReviewerQuestionComment();
        reviewerQuestionComment.setReviewerComment("Need improvement");
        reviewerCommentDto.setReviewerQuestionComment(Arrays.asList(reviewerQuestionComment));
        when(userRepository.findByOidIn(Collections.singletonList(Mockito.<UUID>any()))).thenReturn(Optional.empty());
        reviewerCommentDto.setReviewerId(String.valueOf(UUID.randomUUID()));

        Assessment assessmentObj = assessmentService.saveReviewerComment(reviewerCommentDto);
        assertEquals(assessment.getId(), assessmentObj.getId());

    }
    private Reviewer populateReviewer1() {
        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerId("1");
        return reviewer;
    }


    @Test
     void testGetDateRangeBetween() {
        ArrayList<LineChartProjection> lineChartDataList = new ArrayList<>();
        List<LineChartProjection> actualDateRangeBetween = assessmentService.getDateRangeBetween(1L, 1L,
                lineChartDataList);
        Assertions.assertSame(lineChartDataList, actualDateRangeBetween);
        LineChartProjection getResult = actualDateRangeBetween.get(0);
        assertEquals("01-01-1970", getResult.getId());
    }
    @Test
    void testGetDateRangeBetween3() {
        ArrayList<LineChartProjection> lineChartDataList = new ArrayList<>();
        List<LineChartProjection> actualDateRangeBetween = assessmentService.getDateRangeBetween(-1L, 1L,
                lineChartDataList);
        Assertions.assertSame(lineChartDataList, actualDateRangeBetween);
        LineChartProjection getResult = actualDateRangeBetween.get(0);
        assertEquals("31-12-1969", getResult.getId());
        LineChartProjection getResult2 = actualDateRangeBetween.get(1);
        assertEquals("01-01-1970", getResult2.getId());

    }

    @Test
     void testGetDateRangeBetween5() {
        ArrayList<LineChartProjection> lineChartDataList = new ArrayList<>();
        List<LineChartProjection> actualDateRangeBetween = assessmentService.getDateRangeBetween(Long.MAX_VALUE,
                Long.MIN_VALUE, lineChartDataList);
        Assertions.assertSame(lineChartDataList, actualDateRangeBetween);
        LineChartProjection getResult = actualDateRangeBetween.get(0);
        assertEquals("17-08-292278994", getResult.getId());
    }

    @Test
     void testGetDateRangeBetween2() {
        ArrayList<LineChartProjection> lineChartDataList = new ArrayList<>();
        List<LineChartProjection> actualDateRangeBetween = assessmentService.getDateRangeBetween(0L, 1L,
                lineChartDataList);
        Assertions.assertSame(lineChartDataList, actualDateRangeBetween);
        LineChartProjection getResult = actualDateRangeBetween.get(0);
        assertEquals("01-01-1970", getResult.getId());
    }
    @Test
    void testReviewerOrAssessmentNotFoundException() {
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.empty());
        AssessmentNotFoundException assessmentNotFoundException = assertThrows(AssessmentNotFoundException.class,
                () -> assessmentService.saveReviewerComment(reviewerCommentDto));
        assertEquals(AssessmentServiceImpl.ASSESSMENT_NOT_FOUND, assessmentNotFoundException.getMessage());

    }

    @Test
    void testgetLineChartDataByStartAndEndDates() {
        when(assessmentRepository.findLineChartDataByStartAndEndDates(anyLong(), anyLong(),anyList())).thenReturn(this.populateLineChartProjection());
            List<LineChartProjection> lineChartDataList = assessmentService.getLineChartDataByStartAndEndDates(1L,1L,"All","1,2,3");
        assertEquals(lineChartDataList.size(), Integer.valueOf(3));
        assertEquals(lineChartDataList.get(1), this.populateLineChartProjection().get(0));

    }

    @Test
    void testLineChartDataNotFoundException() {
        when(assessmentRepository.findLineChartDataByStartAndEndDates(anyLong(), anyLong(),anyList())).thenReturn(Collections.emptyList());
        LineChartDataNotFoundException lineChartDataNotFoundException = assertThrows(LineChartDataNotFoundException.class,
                () -> assessmentService.getLineChartDataByStartAndEndDates(1L, 1L,"All","1,2,3"));
        assertEquals(AssessmentServiceImpl.LINE_CHART_DATA_NOT_FOUND, lineChartDataNotFoundException.getMessage());

    }

    private List<LineChartProjection> populateLineChartProjection() {
        List<LineChartProjection> list = new ArrayList<>(); 
        LineChartProjection projection = new LineChartProjection();
        projection.setCount(5);
        projection.setId("31-10-2023");
        LineChartProjection projection1 = new LineChartProjection();
        projection1.setCount(2);
        projection1.setId("30-10-2023");
        list.add(projection1);
        list.add(projection);
        return list;
    }

    private Reviewer populateReviewer() {
        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerId("a2f876d4-9269-11ee-b9d1-0242ac120002");
        return reviewer;
    }

    @Test
    void testPopulateReviewrCommentByReviewerId() {
        Template template = new Template();
        template.setProjectCategory(List.of(this.populateProjectCategory()));
        Assessment assessment = new Assessment();
        assessment.setId(1l);
        assessment.setProjectCategory(assessmentProjectCategories);
        when(assessmentRepository.findById(anyLong())).thenReturn(Optional.of(assessment));
        when(userRepository.findByOid(Mockito.<UUID>any())).thenReturn(Optional.empty());
        assessment.setSubmitedBy(String.valueOf(UUID.randomUUID()));
        ReviewerQuestionComment reviewerQuestionComment=new ReviewerQuestionComment();
        reviewerQuestionComment.setReviewerComment("Need improvement");
        reviewerCommentDto.setReviewerQuestionComment(Arrays.asList(reviewerQuestionComment));
        when(userRepository.findByOidIn(Collections.singletonList(Mockito.<UUID>any()))).thenReturn(Optional.empty());
        reviewerCommentDto.setReviewerId(String.valueOf(UUID.randomUUID()));
        Assessment assessmentObj = assessmentService.saveReviewerComment(reviewerCommentDto);
        assertEquals(assessment.getId(), assessmentObj.getId());
    }

    @Test
    void testFindFiveAssessmentsBySubmittedBy() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        when(assessmentRepository.findTop5ByOrderByCreatedAtDesc(submittedBy)).thenReturn(assessments);

        List<Assessment> result = assessmentService.findLastFiveAssessments(submittedBy);

        assertEquals(assessments, result);
    }

    @Test
    void testFindFiveAssessmentsBySubmittedByFailure() {
        String submittedBy = null;
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.findLastFiveAssessments(submittedBy));

        assertEquals("Invalid submittedBy: null", illegalArgumentException.getMessage());
    }

    @Test
    void testFindFiveAssessmentsBySubmittedByFailureNull() {
        String submittedBy = "";
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.findLastFiveAssessments(submittedBy));

        assertEquals("Invalid submittedBy: ", illegalArgumentException.getMessage());
    }

    @Test
    void testFindLast5AssessmentsNoAssessmentsFound() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        when(assessmentRepository.findTop5ByOrderByCreatedAtDesc(submittedBy)).thenReturn(Collections.emptyList());
        List<Assessment> result = assessmentService.findLastFiveAssessments(submittedBy);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetAssessmentByStatusListAndSubmittedBy() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentStatus> assessmentsList = new ArrayList<>();
        assessmentsList.add(AssessmentStatus.REVIEWED);
        List<Assessment> assessments = new ArrayList<>();
        assessments.add(new Assessment());
        assessments.add(new Assessment());
        when(assessmentRepository.findBySubmitStatusInAndSubmitedBy(assessmentsList, submittedBy)).thenReturn(assessments);
        List<Assessment> result = assessmentService.getAssessmentsByStatus(assessmentsList, submittedBy);
        assertEquals(assessments, result);
    }

    @Test
    void testGetAssessmentByStatusListAndSubmittedByInvalidSubmittedBy() {
        String submittedBy = null;
        List<AssessmentStatus> assessmentsList = new ArrayList<>();
        assessmentsList.add(AssessmentStatus.REVIEWED);
        assessmentsList.add(AssessmentStatus.SUBMITTED);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.getAssessmentsByStatus(assessmentsList, submittedBy));

        assertEquals("Invalid data given in arguments", illegalArgumentException.getMessage());
    }

    @Test
    void testGetAssessmentByStatusListAndSubmittedByInvalidSubmittedByNull() {
        String submittedBy = null;
        List<AssessmentStatus> assessmentsList = Collections.emptyList();
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.getAssessmentsByStatus(assessmentsList, submittedBy));

        assertEquals("Invalid data given in arguments", illegalArgumentException.getMessage());
    }

    @Test
    void testGetAssessmentByStatusListAndSubmittedByEmptyStatusList() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentStatus> assessmentsList = new ArrayList<>();
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.getAssessmentsByStatus(assessmentsList, submittedBy));

        assertEquals("Invalid data given in arguments", illegalArgumentException.getMessage());
    }
    @Test
    void testGetAssessmentByStatusListAndSubmittedByEmptyStatusListAndEmptySubmittedBy() {
        String submittedBy = "";
        List<AssessmentStatus> assessmentsList = new ArrayList<>();
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> assessmentService.getAssessmentsByStatus(assessmentsList, submittedBy));

        assertEquals("Invalid data given in arguments", illegalArgumentException.getMessage());
    }

    @Test
    void testGetAssessmentByStatusListAndSubmittedByNoAssessments() {
        String submittedBy = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        List<AssessmentStatus> assessmentsList = new ArrayList<>();
        assessmentsList.add(AssessmentStatus.REVIEWED);
        assessmentsList.add(AssessmentStatus.SUBMITTED);
        when(assessmentRepository.findBySubmitStatusInAndSubmitedBy(assessmentsList, submittedBy))
                .thenReturn(Collections.emptyList());

        AssessmentNotFoundException exception = assertThrows(AssessmentNotFoundException.class,
                () -> {
                    assessmentService.getAssessmentsByStatus(assessmentsList, submittedBy);
                });

        assertEquals(new AssessmentNotFoundException("Assessment not found").getMessage(), exception.getMessage());
    }







    @Test
    void getTop10AssessmentsForDashboardTest() {

        List<Assessment> expectedAssessments = new ArrayList<>();

        Assessment assessment = new Assessment();
        assessment.setId(1L);
        assessment.setUpdatedAt(1L);
        assessment.setSubmitStatus(AssessmentStatus.REVIEWED);

        Assessment assessment1 = new Assessment();
        assessment1.setId(2L);
        assessment1.setUpdatedAt(2L);
        assessment1.setSubmitStatus(AssessmentStatus.SUBMITTED);

        expectedAssessments.add(assessment);
        expectedAssessments.add(assessment1);

        Mockito.when(assessmentRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(
                        List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED)))
                .thenReturn(expectedAssessments);

        List<Assessment> actualAssessments = assessmentService.getTop10AssessmentsForDashboard();
        assertEquals(expectedAssessments, actualAssessments);
    }


    @Test
    void getTop10AssessmentsForDashboardTestNoAssessments() {
        List<Assessment> assessments = new ArrayList<>();
        List<AssessmentStatus> assessmentStatusList=new ArrayList<>();
        assessmentStatusList.add(AssessmentStatus.SUBMITTED);
        assessmentStatusList.add(AssessmentStatus.REVIEWED);
        when(assessmentRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(assessmentStatusList)).thenReturn(Collections.emptyList());
        AssessmentNotFoundException exception = assertThrows(AssessmentNotFoundException.class,
                () -> {
                    assessmentService.getTop10AssessmentsForDashboard();
                });

        assertEquals(new AssessmentNotFoundException("Assessment not found").getMessage(), exception.getMessage());
    }

    @Test
    void getAllAssessmentsDetails() throws Exception {
        List<MetricAndAssessmentDetailsDto> metricAndAssessmentDetailsDtos=new ArrayList<>();
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto=new MetricAndAssessmentDetailsDto();
        metricAndAssessmentDetailsDto.setReviewerName("RAM");
        metricAndAssessmentDetailsDto.setId(1L);
        Mockito.when(this.assessmentRepository.findAllBySubmitedByOrderByUpdatedAtDesc("a2f876d4-9269-11ee-b9d1-0242ac120002")).thenReturn(assessments);
        Mockito.when(conversationService.toMetricAndAssessmentDetailsDto(assessments)).thenReturn(metricAndAssessmentDetailsDtos);
        List<MetricAndAssessmentDetailsDto> templateDtos = this.assessmentService.getAllAssessmentsDetails("a2f876d4-9269-11ee-b9d1-0242ac120002");
        Assertions.assertEquals(metricAndAssessmentDetailsDtos, templateDtos);
        Mockito.verify(this.assessmentRepository).findAllBySubmitedByOrderByUpdatedAtDesc(anyString());
        Mockito.verify(this.conversationService).toMetricAndAssessmentDetailsDto(assessments);

    }

    @Test
    void ThrowingCustomExceptionForGetAllAssessmentsDetails() {
        Mockito.when(this.assessmentRepository.findAllBySubmitedByOrderByUpdatedAtDesc(anyString())).thenThrow(CustomException.class);
        Assertions.assertThrows(CustomException.class, () ->
                this.assessmentService.getAllAssessmentsDetails("a2f876d4-9269-11ee-b9d1-0242ac120002"));
        Mockito.verify(this.assessmentRepository).findAllBySubmitedByOrderByUpdatedAtDesc(anyString());

    }

    @Test
    void ThrowingCustomExceptionForGetAllAssessmentsDetailsWithEmptyAssessments() {
        Mockito.when(this.assessmentRepository.findAllBySubmitedByOrderByUpdatedAtDesc(anyString())).thenReturn(Collections.emptyList());
        Assertions.assertThrows(CustomException.class, () ->
                this.assessmentService.getAllAssessmentsDetails("a2f876d4-9269-11ee-b9d1-0242ac120002"));
        Mockito.verify(this.assessmentRepository).findAllBySubmitedByOrderByUpdatedAtDesc(anyString());

    }

    @Test
    void getAllPendingReviewAssessmentsShouldReturnListOfAssessments() {
        String userId="a2f876d4-9269-11ee-b9d1-0242ac120002";
        Assessment assessment1 = new Assessment();
        assessment1.setId(1L);
        assessment1.setSubmitStatus(AssessmentStatus.SAVE);
        assessment1.setSubmitterName("testName");
        assessment1.setProjectId(1L);
        Assessment assessment2 = new Assessment();
        assessment2.setId(2L);
        assessment2.setSubmitStatus(AssessmentStatus.SUBMITTED);
        assessment2.setSubmitterName("testName1");
        assessment2.setProjectId(2L);
        List<Assessment> assessments = Arrays.asList(assessment1,assessment2);
        Mockito.when(assessmentRepository.findAllBySubmitedByNotAndSubmitStatusOrderByUpdatedAtDesc(anyString(),anyString(),Mockito.any(Sort.class)))
            .thenReturn(assessments);
        List<Assessment> result = assessmentService.getAllPendingReviewAssessments(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testName",result.get(0).getSubmitterName());
        Mockito.verify(assessmentRepository).findAllBySubmitedByNotAndSubmitStatusOrderByUpdatedAtDesc(anyString(),anyString(),Mockito.any(Sort.class));
    }

    @Test
    void getAllPendingReviewAssessmentsShouldThrowExceptionWhenNoAssessmentsFound() {
        String userId="a2f876d4-9269-11ee-b9d1-0242ac120002";
        Mockito.when(assessmentRepository.findAllBySubmitedByNotAndSubmitStatusOrderByUpdatedAtDesc(anyString(),anyString(),Mockito.any(Sort.class)))
            .thenReturn(Collections.emptyList());
        AssessmentNotFoundException assessmentNotFoundException=assertThrows(AssessmentNotFoundException.class,
            ()-> assessmentService.getAllPendingReviewAssessments(userId));

        assertEquals("Assessment not found",assessmentNotFoundException.getMessage());
    }
    
    @Test
    void testAssessmentReport() {
    	when(assessmentRepository.findBySubmitStatusIn(
				List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED,AssessmentStatus.INACTIVE),
				Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(assessments);
    	List<Assessment> assessments = assessmentService.assessmentReport();
    	Assessment assessment =assessments.get(0);
    	assertNotNull(assessment.getAccount().getAccountName());
    	assertNotNull(assessment.getProject().getProjectName());
    	assertEquals(1, assessments.size());
    
    
    }
    
    @Test
    void testAssessmentReportEmptyAssessments() {
    	when(assessmentRepository.findBySubmitStatusIn(
				List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED, AssessmentStatus.INACTIVE),
				Sort.by(Sort.Order.desc("updatedAt")))).thenReturn(null);
    	List<Assessment> assessments = assessmentService.assessmentReport();
    	assertEquals(0, assessments.size());
    }


    private List<Assessment> createSampleAssessments() {

        Account account = new Account();
        account.setAccountName("name");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        Project project = new Project();
        project.setAccount(account);
        project.setCreatedAt(LocalDate.of(2024, 1, 1));
        project.setEndDate(LocalDate.of(2024, 1, 1));
        project.setId(1L);
        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        projectType.setProjectTypeName("name");

        List<TemplateQuestionnaire> templateQuestionnaires =new ArrayList<>();
        TemplateQuestionnaire templateQuestionnaire=new TemplateQuestionnaire();
        templateQuestionnaire.setQuestionDescription("");
        templateQuestionnaire.setQuestionId(1);
        templateQuestionnaire.setQuestion("");
        TemplateQuestionnaire templateQuestionnaire1=new TemplateQuestionnaire();
        templateQuestionnaire1.setQuestionDescription("");
        templateQuestionnaire1.setQuestionId(2);
        templateQuestionnaire1.setQuestion("");
        TemplateQuestionnaire templateQuestionnaire2=new TemplateQuestionnaire();
        templateQuestionnaire2.setQuestionDescription("");
        templateQuestionnaire2.setQuestionId(1);
        templateQuestionnaire2.setQuestion("");
        templateQuestionnaires.add(templateQuestionnaire);
        templateQuestionnaires.add(templateQuestionnaire1);
        templateQuestionnaires.add(templateQuestionnaire2);

        List<ProjectCategory> projectCategories=new ArrayList<>();
        ProjectCategory projectCategory=new ProjectCategory();
        projectCategory.setCategoryDescription("");
        projectCategory.setCategoryName("");
        projectCategory.setTemplateQuestionnaire(templateQuestionnaires);
        projectCategories.add(projectCategory);

        Template template = new Template();
        template.setAssessmentDescription("desc");
        template.setCreatedAt(1L);
        template.setId(1L);
        template.setIsActive(true);
        template.setProjectCategory(projectCategories);

        List<QuestionAnswer> questionAnswers =new ArrayList<>();
        QuestionAnswer questionAnswer1=new QuestionAnswer();
        questionAnswer1.setQuestionDescription("");
        questionAnswer1.setQuestionId(1);
        questionAnswer1.setAnswerOptionIndex(1);
        QuestionAnswer questionAnswer2=new QuestionAnswer();
        questionAnswer2.setQuestionDescription("");
        questionAnswer2.setQuestionId(2);
        questionAnswer2.setAnswerOptionIndex(1);
        QuestionAnswer questionAnswer3=new QuestionAnswer();
        questionAnswer3.setQuestionDescription("");
        questionAnswer3.setQuestionId(1);
        questionAnswer3.setAnswerOptionIndex(1);
        questionAnswers.add(questionAnswer1);
        questionAnswers.add(questionAnswer2);
        questionAnswers.add(questionAnswer3);

        List<AssessmentProjectCategory> assessmentProjectCategories=new ArrayList<>();
        AssessmentProjectCategory assessmentProjectCategory =new AssessmentProjectCategory();
        assessmentProjectCategory.setCategoryName("");
        assessmentProjectCategory.setTemplateQuestionnaire(questionAnswers);
        assessmentProjectCategory.setCategoryDescription("");
        assessmentProjectCategories.add(assessmentProjectCategory);

        Assessment assessment = new Assessment();
        assessment.setAccount(account);
        assessment.setAssessmentDescription("desc");

        assessment.setCategoryScores(new ArrayList<>());
        assessment.setCreatedAt(1L);
        assessment.setId(1L);
        assessment.setProject(project);
        assessment.setProjectCategory(assessmentProjectCategories);
        assessment.setProjectId(1L);
        assessment.setProjectType(projectType);
        assessment.setReviewers(new ArrayList<>());
        assessment.setScore(10.0d);
        assessment.setSubmitStatus(AssessmentStatus.REVIEWED);
        assessment.setSubmitedAt(1L);
        assessment.setSubmitedBy("A");
        assessment.setSubmitterName("Ravi");
        assessment.setTemplate(template);
        assessment.setUpdatedAt(1L);
        return List.of(assessment);

    }
    public List<AssessmentDto> createSampleAssessmentsDto() {

        List<QuestionAnswerDto> templateQuestionnaire=new ArrayList<>();
        QuestionAnswerDto templateQuestionnaire1=new QuestionAnswerDto();
        templateQuestionnaire1.setQuestionId(1);
        templateQuestionnaire1.setQuestion("");
        templateQuestionnaire1.setQuestionDescription("");
        templateQuestionnaire1.setAnswerOptionIndex(1);

        QuestionAnswerDto templateQuestionnaire2=new QuestionAnswerDto();
        templateQuestionnaire2.setQuestionId(2);
        templateQuestionnaire2.setQuestion("");
        templateQuestionnaire2.setQuestionDescription("");
        templateQuestionnaire2.setAnswerOptionIndex(1);

        QuestionAnswerDto templateQuestionnaire3=new QuestionAnswerDto();
        templateQuestionnaire3.setQuestionId(1);
        templateQuestionnaire3.setQuestion("");
        templateQuestionnaire3.setQuestionDescription("");
        templateQuestionnaire3.setAnswerOptionIndex(1);

        templateQuestionnaire.add(templateQuestionnaire1);
        templateQuestionnaire.add(templateQuestionnaire2);
        templateQuestionnaire.add(templateQuestionnaire3);

        List<AssessmentProjectCategoryDto> projectCategory= new ArrayList<>();
        AssessmentProjectCategoryDto projectCategory1=new AssessmentProjectCategoryDto();
        projectCategory1.setTemplateQuestionnaire(templateQuestionnaire);
        projectCategory1.setCategoryDescription("");
        projectCategory1.setCategoryName("");
        projectCategory.add(projectCategory1);

        List<AssessmentDto> assessments = new ArrayList<>();
        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setAssessmentId(1L);
        assessmentDto.setTemplateId(1L);
        assessmentDto.setTemplateName("");
        assessmentDto.setSubmitterName(null);
        assessmentDto.setSubmitedAt(1L);
        assessmentDto.setProjectType(null);
        assessmentDto.setTemplateUploadedUserId("0cda4d72");
        assessmentDto.setSubmitedBy ("A");
        assessmentDto.setProjectId (1L);
        assessmentDto.setTemplateUploadedUserName ("Manoj Viswanathan");
        assessmentDto.setProjectCategory(projectCategory);
        assessmentDto.setSubmitStatus(AssessmentStatus.valueOf("REVIEWED"));
        assessments.add(assessmentDto);
        return assessments;

    }
    @Test
    void getCountOfUserResponse() {
        Long templateId = 1L;
        Long projectId = 1L;
        Integer questionId = 1;
        Map<String, Map<Integer, Long>> expectedMap = new HashMap<>();
        Map<Integer, Long> nestedMap = new HashMap<>();
        nestedMap.put(1, 2L);
        expectedMap.put("", nestedMap);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(templateRepository.existsById(templateId)).thenReturn(true);
        List<Assessment> assessments = createSampleAssessments();
        when(assessmentRepository.findByTemplateIdAndProjectId(templateId, projectId)).thenReturn(assessments);
        List<AssessmentDto> assessmentDto1=createSampleAssessmentsDto();
        when(conversationService.toAssessmentDtos(assessments)).thenReturn(assessmentDto1);
        Map<String, Map<Integer, Long>> result = assessmentService.getCountOfUserResponse(templateId, projectId, questionId);
        assertEquals(expectedMap, result);
        if (questionId != null) {
            assertTrue(result.entrySet().stream().allMatch(entry ->
                    entry.getValue().keySet().stream().allMatch(key -> key.equals(questionId))
            ));
        }
    }
    @Test
    void getCountOfUserResponseNullQuestionId() {
        Long templateId = 1L;
        Long projectId = 1L;
        Integer questionId = null;
        Map<String, Map<Integer, Long>> expectedMap = new HashMap<>();
        Map<Integer, Long> nestedMap = new HashMap<>();
        nestedMap.put(1, 3L);
        expectedMap.put("", nestedMap);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(templateRepository.existsById(templateId)).thenReturn(true);
        List<Assessment> assessments = createSampleAssessments();
        when(assessmentRepository.findByTemplateIdAndProjectId(templateId, projectId)).thenReturn(assessments);
        List<AssessmentDto> assessmentDto1=createSampleAssessmentsDto();
        when(conversationService.toAssessmentDtos(assessments)).thenReturn(assessmentDto1);
        Map<String, Map<Integer, Long>> result = assessmentService.getCountOfUserResponse(templateId, projectId,questionId);
        assertEquals(expectedMap, result);
    }


    @Test
    void getCountOfUserResponseEmpty() {
        Long templateId = 1L;
        Long projectId = 1L;
        Integer questionId = 1;
        Map<String, Map<Integer, Long>> expectedMap = new HashMap<>();
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(templateRepository.existsById(templateId)).thenReturn(true);
        when(assessmentRepository.findByTemplateIdAndProjectId(templateId, projectId)).thenReturn(assessments);
        when(conversationService.toAssessmentDtos(assessments)).thenReturn(new ArrayList<>());
        Map<String, Map<Integer, Long>> result = assessmentService.getCountOfUserResponse(templateId, projectId, questionId);
        assertEquals(expectedMap,result);
    }
    @Test
    void getCountOfUserResponseWithoutQuestionId() {
        Long templateId = 1L;
        Long projectId = 1L;
        Integer questionId = null;
        Map<String, Map<Integer, Long>> expectedMap = new HashMap<>();
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(templateRepository.existsById(templateId)).thenReturn(true);
        when(assessmentRepository.findByTemplateIdAndProjectId(templateId, projectId)).thenReturn(assessments);
        when(conversationService.toAssessmentDtos(assessments)).thenReturn(new ArrayList<>());
        Map<String, Map<Integer, Long>> result = assessmentService.getCountOfUserResponse(templateId, projectId, questionId);
        assertEquals(result, expectedMap);
    }
    @Test
    void getCountOfUserResponseProjectNotFoundException() {
        Long templateId = 1L;
        Long projectId = 1L;
        Integer questionId = 1;
        when(projectRepository.existsById(projectId)).thenReturn(false);
        when(templateRepository.existsById(templateId)).thenReturn(false);
        assertThrows(ProjectNotFoundException.class, () ->
                assessmentService.getCountOfUserResponse(templateId, projectId, questionId));

    }
    @Test
    void getCountOfUserResponseTemplateNotFoundException() {
        Long templateId = 1L;
        Long projectId = 1L;
        Integer questionId = 1;
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(templateRepository.existsById(templateId)).thenReturn(false);
        assertThrows(TemplateNotFoundException.class, () ->
                assessmentService.getCountOfUserResponse(templateId, projectId, questionId));

    }
    @Test
    void getCountOfUserResponseAssessmentNotFoundException() {
        Long templateId = 1L;
        Long projectId = 1L;
        Integer questionId = 1;
        List<Assessment> assessmentList=new ArrayList<>();
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(templateRepository.existsById(templateId)).thenReturn(true);
        when(assessmentRepository.findByTemplateIdAndProjectId(1L,1L)).thenReturn(assessmentList);
        assertThrows(AssessmentNotFoundException.class, () ->
                assessmentService.getCountOfUserResponse(templateId, projectId, questionId));

    }
    private AssessmentProjectCategory asspopulateProjectCategory() {
        AssessmentProjectCategory projectCategory = new AssessmentProjectCategory();
        projectCategory.setCategoryName("initial");
        projectCategory.setTemplateQuestionnaire(List.of(this.populateTemplateQuestionnairee()));
        return projectCategory;
    }

    private QuestionAnswer populateTemplateQuestionnairee() {
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setQuestionId(1);
        questionAnswer.setFileUri("");
        return questionAnswer;
    }
    private AssessmentProjectCategory asspopulateProjectCategorynull() {
        AssessmentProjectCategory projectCategory = new AssessmentProjectCategory();
        projectCategory.setCategoryName("initial");
        projectCategory.setTemplateQuestionnaire(List.of(this.populateTemplateQuestionnaireUriNull()));
        return projectCategory;
    }
    private QuestionAnswer populateTemplateQuestionnaireUriNull() {
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setQuestionId(1);
        questionAnswer.setFileUri(null);
        return questionAnswer;
    }
    @Test
    void removeFileUriShouldRemoveFileFromAssessment() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        Assessment assessment = new Assessment();
        BeanUtils.copyProperties(assessmentDto, assessment);
        assessment.setId(1L);
        assessment.setProjectCategory(List.of(asspopulateProjectCategory()));
        String folderName = "";
        String fileName = "";
        String regexPattern = Pattern.quote(folderName) + ".*" + Pattern.quote(fileName);
        when(assessmentRepository.findAssessmentWithFileUri(regexPattern)).thenReturn(Optional.of(assessment));
        assessmentService.removeFileUri(fileName, folderName);
        verify(assessmentRepository).findAssessmentWithFileUri(regexPattern);
        verify(assessmentRepository).save(assessment);
        assertNull(assessment.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).getFileUri());
    }
    @Test
    void removeFileUriShouldRemoveFileFromAssessmentFileURINUll() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        Assessment assessment = new Assessment();
        BeanUtils.copyProperties(assessmentDto, assessment);
        assessment.setId(1L);
        assessment.setProjectCategory(List.of(asspopulateProjectCategorynull()));
        String folderName = "";
        String fileName = "";
        String regexPattern = Pattern.quote(folderName) + ".*" + Pattern.quote(fileName);
        when(assessmentRepository.findAssessmentWithFileUri(regexPattern)).thenReturn(Optional.of(assessment));
        assessmentService.removeFileUri(fileName, folderName);
        verify(assessmentRepository).findAssessmentWithFileUri(regexPattern);

        assertNull(assessment.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).getFileUri());
    }
    @Test
    void removeFileUriShouldRemoveFileFromAssessmentFileURINUllFoldernameFilename() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        Assessment assessment = new Assessment();
        BeanUtils.copyProperties(assessmentDto, assessment);
        assessment.setId(1L);
        assessment.setProjectCategory(List.of(asspopulateProjectCategorynull()));
        assessment.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).setFileUri("http://test.com/test");
        String folderName = "null";
        String fileName = "null";
        String regexPattern = Pattern.quote(folderName) + ".*" + Pattern.quote(fileName);
        when(assessmentRepository.findAssessmentWithFileUri(regexPattern)).thenReturn(Optional.of(assessment));
        assessmentService.removeFileUri(fileName, folderName);
        verify(assessmentRepository).findAssessmentWithFileUri(regexPattern);

        assertNotNull(assessment.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).getFileUri());
    }
    @Test
    void removeFileUriShouldRemoveFileFromAssessmentFileURIFoldernameNull() throws IOException {

        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        Assessment assessment = new Assessment();
        BeanUtils.copyProperties(assessmentDto, assessment);
        assessment.setId(1L);
        assessment.setProjectCategory(List.of(asspopulateProjectCategorynull()));
        assessment.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).setFileUri("http://test.com/test");
        String folderName = "";
        String fileName = "null";
        String regexPattern = Pattern.quote(folderName) + ".*" + Pattern.quote(fileName);
        when(assessmentRepository.findAssessmentWithFileUri(regexPattern)).thenReturn(Optional.of(assessment));
        assessmentService.removeFileUri(fileName, folderName);
        verify(assessmentRepository).findAssessmentWithFileUri(regexPattern);

        assertNotNull(assessment.getProjectCategory().get(0).getTemplateQuestionnaire().get(0).getFileUri());
    }
    @Test
    void removeFileUriShouldRemoveFileAssessmentNotFound() throws IOException {
        File jsonFile = new ClassPathResource("sample/AssessmentDtoSample.json").getFile();
        AssessmentDto assessmentDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), AssessmentDto.class);
        Assessment assessment = new Assessment();
        BeanUtils.copyProperties(assessmentDto, assessment);
        assessment.setId(1L);
        String folderName = "";
        String fileName = "";
        String regexPattern = "";
        when(assessmentRepository.findAssessmentWithFileUri(regexPattern)).thenReturn(Optional.of(assessment));
        AssessmentNotFoundException assessmentNotFoundException=assertThrows(AssessmentNotFoundException.class,
                ()-> assessmentService.removeFileUri(fileName,folderName));
    }
    List<Assessment> populateAssessmentList(){

        Account account=new Account();
        account.setId(1L);
        Project project=new Project();
        project.setId(1L);
        ProjectType projectType=new ProjectType();
        projectType.setId(1L);
        Assessment assessmentObj=new Assessment();
        assessmentObj.setAccount(account);
        assessmentObj.setProject(project);
        assessmentObj.setProjectType(projectType);
        assessmentObj.setSubmitStatus(AssessmentStatus.SUBMITTED);

        Assessment assessmentObj1=new Assessment();
        assessmentObj1.setAccount(account);
        assessmentObj1.setProject(project);
        assessmentObj1.setProjectType(projectType);
        assessmentObj1.setSubmitStatus(AssessmentStatus.REVIEWED);

        List<Assessment> assessmentList=new ArrayList<>();
        assessmentList.add(assessmentObj);
        assessmentList.add(assessmentObj1);
        return assessmentList;
    }


    @Test
    void testTop10AssessmentsAC(){
        Filters filterName=Filters.AC;
        String filterValue="1";
        when(assessmentRepository.findTop10ByAccountIdAndSubmitStatusInOrderByUpdatedAtDesc(Long.parseLong(filterValue), List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED)))
                .thenReturn(populateAssessmentList());
        List<Assessment> result=assessmentService.getTop10AssessmentsForDashboardFilters(filterName,filterValue);
        assertEquals(populateAssessmentList(),result);

    }
    @Test
    void testTop10AssessmentsPR(){
        Filters filterName=Filters.PR;
        String filterValue="1,2,3";
        List<Long> filterValues = Arrays.stream(filterValue.split(","))
            .map(String::trim)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        when(assessmentRepository.findTop10ByProjectIdInAndSubmitStatusInOrderByUpdatedAtDesc(filterValues, List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED)))
                .thenReturn(populateAssessmentList());
        List<Assessment> result=assessmentService.getTop10AssessmentsForDashboardFilters(filterName,filterValue);
        assertEquals(populateAssessmentList(),result);

    }
    @Test
    void testTop10AssessmentsPT(){
        Filters filterName=Filters.PT;
        String filterValue="1";
        when(assessmentRepository.findTop10ByProjectTypeIdAndSubmitStatusInOrderByUpdatedAtDesc(Long.parseLong(filterValue), List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED)))
                .thenReturn(populateAssessmentList());
        List<Assessment> result=assessmentService.getTop10AssessmentsForDashboardFilters(filterName,filterValue);
        assertEquals(populateAssessmentList(),result);

    }
    @Test
    void testTop10AssessmentsALL(){
        Filters filterName=null;
        String filterValue=null;
        when(assessmentRepository.findTop10BySubmitStatusInOrderByUpdatedAtDesc(List.of(AssessmentStatus.REVIEWED, AssessmentStatus.SUBMITTED)))
                .thenReturn(populateAssessmentList());
        List<Assessment> result=assessmentService.getTop10AssessmentsForDashboardFilters(filterName,filterValue);
        assertEquals(populateAssessmentList(),result);

    }

    @Test
    void testGetLineChartDataByStartAndEndDatesWithProjectFilter() {
        Long startDate = 1609459200000L;
        Long endDate = 1612137600000L;
        String filterName = ServiceConstants.PROJECT;
        String filterValue = "123";

        List<LineChartProjection> expectedList = new ArrayList<>();
        LineChartProjection lineChartProjection1=new LineChartProjection();
        lineChartProjection1.setId("123");
        LineChartProjection lineChartProjection2=new LineChartProjection();
        lineChartProjection2.setId("456");
        expectedList.add(lineChartProjection1);
        expectedList.add(lineChartProjection2);

        when(assessmentRepository.findLineChartDataByStartAndEndDatesInAndProjectId(
            anyLong(), anyLong(), anyList(),anyList())).thenReturn(expectedList);

        List<LineChartProjection> actualList =
            assessmentService.getLineChartDataByStartAndEndDates(startDate, endDate, filterName, filterValue);

        assertEquals(expectedList, actualList);
        verify(assessmentRepository).findLineChartDataByStartAndEndDatesInAndProjectId(
            anyLong(), anyLong(), anyList(),anyList());
    }
    @Test
    void testGetLineChartDataByStartAndEndDatesWithProjectTypeFilter() {
        Long startDate = 1609459200000L;
        Long endDate = 1612137600000L;
        String filterName = ServiceConstants.PROJECT_TYPE;
        String filterValue = "123";

        List<LineChartProjection> expectedList = new ArrayList<>();
        LineChartProjection lineChartProjection1=new LineChartProjection();
        lineChartProjection1.setId("123");
        LineChartProjection lineChartProjection2=new LineChartProjection();
        lineChartProjection2.setId("456");
        expectedList.add(lineChartProjection1);
        expectedList.add(lineChartProjection2);

        when(assessmentRepository.findLineChartDataByStartAndEndDatesAndProjectTypeId(
            anyLong(), anyLong(), anyLong(),anyList())).thenReturn(expectedList);

        List<LineChartProjection> actualList =
            assessmentService.getLineChartDataByStartAndEndDates(startDate, endDate, filterName, filterValue);

        assertEquals(expectedList, actualList);
        verify(assessmentRepository).findLineChartDataByStartAndEndDatesAndProjectTypeId(
            anyLong(), anyLong(), anyLong(),anyList());
    }
    @Test
    void testGetLineChartDataByStartAndEndDatesWithAccountFilter() {
        Long startDate = 1609459200000L;
        Long endDate = 1612137600000L;
        String filterName = ServiceConstants.ACCOUNT;
        String filterValue = "123";

        List<LineChartProjection> expectedList = new ArrayList<>();
        LineChartProjection lineChartProjection1=new LineChartProjection();
        lineChartProjection1.setId("123");
        LineChartProjection lineChartProjection2=new LineChartProjection();
        lineChartProjection2.setId("456");
        expectedList.add(lineChartProjection1);
        expectedList.add(lineChartProjection2);

        when(assessmentRepository.findLineChartDataByStartAndEndDatesAndAccount(
            anyLong(), anyLong(), anyLong(),anyList())).thenReturn(expectedList);

        List<LineChartProjection> actualList =
            assessmentService.getLineChartDataByStartAndEndDates(startDate, endDate, filterName, filterValue);

        assertEquals(expectedList, actualList);
        verify(assessmentRepository).findLineChartDataByStartAndEndDatesAndAccount(
            anyLong(), anyLong(), anyLong(),anyList());
    }
    @Test
    void testGetAssessmentsByReviewerIdSuccess() {
        String reviewerId = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        Assessment assessment1=new Assessment();
        assessment1.setId(1L);
        assessment1.setSubmitterName("darshan");
        Assessment assessment2=new Assessment();
        assessment2.setId(2L);
        assessment2.setSubmitterName("ravi");
        List<Assessment> assessments = Arrays.asList(assessment1,assessment2);
        when(assessmentRepository.findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(AssessmentStatus.REVIEWED
                ,reviewerId)).thenReturn(assessments);
        List<Assessment> result = assessmentService.getReviewedAssessmentsForReviewer(reviewerId);

        assertEquals(assessments, result);
        assertEquals(assessment1.getSubmitterName(),result.get(0).getSubmitterName());
    }
    @Test
    void testGetAssessmentsByReviewerIdFail() {
        String reviewerId = null;
        CustomException exception = assertThrows(CustomException.class, () -> {
            assessmentService.getReviewedAssessmentsForReviewer(reviewerId);});

        assertEquals("Invalid reviewerId Id : " + reviewerId, exception.getMessage());
    }
    @Test
    void testGetAssessmentsByReviewerIdNotFound() {
        String reviewerId = "a2f876d4-9269-11ee-b9d1-0242ac120002";
        when(assessmentRepository.findBySubmitStatusAndReviewersReviewerIdOrderByUpdatedAtDesc(AssessmentStatus.REVIEWED
                ,reviewerId))
            .thenReturn(Collections.emptyList());
        AssessmentNotFoundException exception =
            assertThrows(
                AssessmentNotFoundException.class,
                () -> {
                    assessmentService.getReviewedAssessmentsForReviewer(reviewerId);
                });
    }
    
    @Test
    public void testInactiveAssessmentById() {
        Long assessmentId = 1L;
        Assessment assessment = new Assessment();
        assessment.setId(assessmentId);
        assessment.setSubmitStatus(AssessmentStatus.SUBMITTED);

        Optional<Assessment> optionalAssessment = Optional.of(assessment);

        when(assessmentRepository.findById(assessmentId)).thenReturn(optionalAssessment);

        Assessment result = assessmentService.inactiveAssessmentById(assessmentId);

        assertEquals(AssessmentStatus.INACTIVE, result.getSubmitStatus());
        assertEquals(false, result.getIsFrequencyRequired());
    }
    
    @Test
    public void testInactiveAssessmentById_AssessmentNotFound() {
        Long assessmentId = 1L;
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.empty());
        AssessmentNotFoundException assessmentNotFoundException=assertThrows(AssessmentNotFoundException.class, () -> {
            assessmentService.inactiveAssessmentById(assessmentId);
        });
        assertEquals(AssessmentServiceImpl.ASSESSMENT_NOT_FOUND, assessmentNotFoundException.getMessage());
    }
    
    @Test
    void testInactiveAssessmentByIdFail() {
    	Long assessmentId = 1L;
    	Assessment assessment = new Assessment();
        assessment.setId(assessmentId);
        assessment.setSubmitStatus(AssessmentStatus.SAVE);

        Optional<Assessment> optionalAssessment = Optional.of(assessment);

        when(assessmentRepository.findById(assessmentId)).thenReturn(optionalAssessment);

        CustomException exception = assertThrows(CustomException.class, () -> {
            assessmentService.inactiveAssessmentById(assessmentId);});

        assertEquals("Assessment not in REVIEWED/SUBMITTED status, can not be Deleted", exception.getMessage());
    }
}
