package com.maveric.digital.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.exceptions.AssessmentProjectCategoryNotFoundException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.MetricSubmitProjectCategory;
import com.maveric.digital.model.embedded.Reviewer;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.responsedto.AssessmentsSubmittedDashboardDto;
import com.maveric.digital.responsedto.LineChartDto;
import com.maveric.digital.responsedto.MetricAndAssessmentDetailsDto;
import com.maveric.digital.responsedto.MetricAndAssessmentReportDetails;
import com.maveric.digital.responsedto.MetricReviewDto;
import com.maveric.digital.responsedto.MetricSubmittedDto;
import com.maveric.digital.responsedto.MetricTemplateDetailsDto;
import com.maveric.digital.responsedto.MetricTemplateDto;
import com.maveric.digital.responsedto.MetricTemplateSaveRequestDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MetricConversationService.class})
@SpringBootTest
class MetricConversationServiceTest {
    @Autowired
    private MetricConversationService metricConversationService;
    @MockBean
    MetricSubmitted metricSubmitted;
    @MockBean
    MetricSubmittedDto metricSubmittedDto;
    @MockBean
    private ObjectMapper objectMapper;


    @Test
     void testToMetricSubmitDto() {

        Account account = new Account();
        account.setAccountName("sri");
        account.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setProjectName("Project Name");

        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        projectType.setProjectTypeName("Project Type Name");

        MetricTemplate template = new MetricTemplate();
        template.setCreatedAt(1L);
        template.setDescription("Description");
        template.setId(1L);
        template.setIsActive(true);
        template.setProjectCategory(new ArrayList<>());
        template.setProjectTypes(new ArrayList<>());
        template.setTemplateName("Template Name");
        template.setTemplateUploadedUserId("42");
        template.setTemplateUploadedUserName("sri");
        template.setUpdatedAt(1L);

        MetricSubmitProjectCategory metricSubmitProjectCategory = new MetricSubmitProjectCategory();
        metricSubmitProjectCategory.setCategoryDescription("Description");
        metricSubmitProjectCategory.setCategoryName("categoryName");
        metricSubmitProjectCategory.setTemplateQuestionnaire(new ArrayList<>());

        ArrayList<MetricSubmitProjectCategory> metricSubmitProjectCategoryList = new ArrayList<>();
        metricSubmitProjectCategoryList.add(metricSubmitProjectCategory);


        MetricSubmitted metricSubmitted=new MetricSubmitted();

        metricSubmitted.setAccount(account);
        metricSubmitted.setCreatedAt(1L);
        metricSubmitted.setDescription("Description");
        metricSubmitted.setId(1L);
        metricSubmitted.setProject(project);
        metricSubmitted.setProjectCategory(metricSubmitProjectCategoryList);
        metricSubmitted.setProjectType(projectType);
        metricSubmitted.setReviewers(new ArrayList<>());
        metricSubmitted.setSubmitStatus(AssessmentStatus.SAVE);
        metricSubmitted.setSubmittedAt(1L);
        metricSubmitted.setSubmittedBy("Submitted By");
        metricSubmitted.setSubmitterName("Submitter Name");
        metricSubmitted.setTemplate(template);
        metricSubmitted.setUpdatedAt(1L);

        MetricSubmittedDto actualToMetricSubmitDtoResult = metricConversationService.toMetricSubmitDto(metricSubmitted);

        assertEquals(1L, actualToMetricSubmitDtoResult.getAccountId().longValue());
        assertEquals("Submitter Name", actualToMetricSubmitDtoResult.getSubmitterName());
        assertEquals("Submitted By", actualToMetricSubmitDtoResult.getSubmittedBy());
        assertEquals(1L, actualToMetricSubmitDtoResult.getSubmittedAt().longValue());
        assertEquals(AssessmentStatus.SAVE, actualToMetricSubmitDtoResult.getSubmitStatus());
        assertTrue(actualToMetricSubmitDtoResult.getReviewers().isEmpty());
        assertEquals(1L, actualToMetricSubmitDtoResult.getProjectTypeId().longValue());
        assertEquals("Project Name", actualToMetricSubmitDtoResult.getProjectName());
        assertEquals(1L, actualToMetricSubmitDtoResult.getProjectId().longValue());
        assertEquals(1, actualToMetricSubmitDtoResult.getProjectCategory().size());
        assertEquals("sri", actualToMetricSubmitDtoResult.getAccountName());
        assertEquals(1L, actualToMetricSubmitDtoResult.getMetricId().longValue());
        assertEquals("Description", actualToMetricSubmitDtoResult.getDescription());
    }
    @Test
    void toMetricAndAssessmentDetailsDtoSuccess() {
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setId(1L);
        metricSubmitted.setUpdatedAt(System.currentTimeMillis());

        Project project = new Project();
        project.setProjectCode("dev47cs");
        project.setProjectName("development");
        metricSubmitted.setProject(project);

        Account account = new Account();
        account.setAccountName("citi");
        metricSubmitted.setAccount(account);

        metricSubmitted.setSubmitStatus(AssessmentStatus.SUBMITTED);
        metricSubmitted.setSubmittedBy("mike");

        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerName("Manoj");
        reviewer.setReviewerAt(System.currentTimeMillis());
        metricSubmitted.setReviewers(Arrays.asList(reviewer));
        metricSubmitted.setSubmitStatus(AssessmentStatus.SAVE);
        MetricAndAssessmentDetailsDto metricSubmittedWithSaveStatus = metricConversationService.toMetricAndAssessmentDetailsDto(metricSubmitted);
        metricSubmitted.setSubmitStatus(AssessmentStatus.SUBMITTED);

        MetricAndAssessmentDetailsDto metricSubmittedWithSubmittedStatus = metricConversationService.toMetricAndAssessmentDetailsDto(metricSubmitted);

        metricSubmitted.setSubmitStatus(AssessmentStatus.REVIEWED);

        MetricAndAssessmentDetailsDto metricSubmittedWithReviewedStatus = metricConversationService.toMetricAndAssessmentDetailsDto(metricSubmitted);

        assertNotNull(metricSubmittedWithSaveStatus);
        assertEquals(metricSubmitted.getId(), metricSubmittedWithSaveStatus.getId());
        assertEquals(metricSubmitted.getProject().getProjectCode(), metricSubmittedWithSaveStatus.getProjectCode());
        assertEquals(metricSubmitted.getProject().getProjectName(), metricSubmittedWithSaveStatus.getProjectName());
        assertEquals(metricSubmitted.getAccount().getAccountName(), metricSubmittedWithSaveStatus.getAccountName());
        assertEquals(metricSubmitted.getSubmittedBy(), metricSubmittedWithSaveStatus.getSubmittedBy());
        assertEquals(AssessmentStatus.SAVE.name(), metricSubmittedWithSaveStatus.getStatus());

        assertEquals(AssessmentStatus.SUBMITTED.name(), metricSubmittedWithSubmittedStatus.getStatus());
        assertEquals(AssessmentStatus.REVIEWED.name(), metricSubmittedWithReviewedStatus.getStatus());

    }
    @Test
    void toMetricAndAssessmentDetailsDtosSuccess() {
        MetricSubmitted metricSubmitted1 = new MetricSubmitted();
        metricSubmitted1.setId(1L);
        metricSubmitted1.setUpdatedAt(System.currentTimeMillis());
        Project project1 = new Project();
        project1.setProjectCode("dev47cs");
        project1.setProjectName("development");
        metricSubmitted1.setProject(project1);
        Account account1 = new Account();
        account1.setAccountName("citi");
        metricSubmitted1.setAccount(account1);
        metricSubmitted1.setSubmitStatus(AssessmentStatus.SUBMITTED);
        metricSubmitted1.setSubmittedBy("mike");
        Reviewer reviewer1 = new Reviewer();
        reviewer1.setReviewerName("Manoj");
        reviewer1.setReviewerAt(System.currentTimeMillis());
        metricSubmitted1.setReviewers(Arrays.asList(reviewer1));

        MetricSubmitted metricSubmitted2 = new MetricSubmitted();
        metricSubmitted2.setId(2L);
        metricSubmitted2.setUpdatedAt(System.currentTimeMillis());
        Project project2 = new Project();
        project2.setProjectCode("test47cs");
        project2.setProjectName("testing");
        metricSubmitted2.setProject(project2);
        Account account2 = new Account();
        account2.setAccountName("sbi");
        metricSubmitted2.setAccount(account2);
        metricSubmitted2.setSubmitStatus(AssessmentStatus.SUBMITTED);
        metricSubmitted2.setSubmittedBy("mike");
        Reviewer reviewer2 = new Reviewer();
        reviewer2.setReviewerName("Ravi");
        reviewer2.setReviewerAt(System.currentTimeMillis());
        metricSubmitted2.setReviewers(Arrays.asList(reviewer2));
        List<MetricSubmitted> metrics= Arrays.asList(metricSubmitted1,metricSubmitted2);

        List<MetricAndAssessmentDetailsDto> result = metricConversationService.toMetricSubmitted(metrics);

        assertNotNull(result);
        assertEquals(metricSubmitted1.getId(), result.get(0).getId());
        assertEquals(metricSubmitted1.getProject().getProjectCode(), result.get(0).getProjectCode());
        assertEquals(metricSubmitted1.getProject().getProjectName(), result.get(0).getProjectName());
        assertEquals(metricSubmitted2.getAccount().getAccountName(), result.get(1).getAccountName());
        assertEquals(metricSubmitted2.getSubmitStatus().name(), result.get(1).getStatus());
        assertEquals(metricSubmitted2.getSubmittedBy(), result.get(1).getSubmittedBy());

    }
    @Test
    void toMetricAndAssessmentDetailsDtoWithEmptyList() {
        List<MetricSubmitted> metrics = Collections.emptyList();
        assertThrows(MetricNotFoundException.class, () -> metricConversationService.toMetricSubmitted(metrics));
    }
    @Test
    void ThrowingExceptionsForValidatedMetricSubmit() {
        assertThrows(AssessmentNotFoundException.class, () -> metricConversationService.validatedMetricSubmit(null));
        MetricSubmitted submitted=new MetricSubmitted();
        assertThrows(TemplateNotFoundException.class, () -> metricConversationService.validatedMetricSubmit(submitted));
        submitted.setTemplate(new MetricTemplate());
        assertThrows(AssessmentProjectCategoryNotFoundException.class, () -> metricConversationService.validatedMetricSubmit(submitted));
    }
    @Test
    void toMetricTemplateDtoFromJsonString_ValidJson() throws JsonProcessingException {
        String validJson = "{\"templateId\":1, \"templateName\":\"Sample Template\"}";
        MetricTemplateDto metricTemplateDto=new MetricTemplateDto();
        Mockito.when(objectMapper.readValue(validJson, MetricTemplateDto.class)).thenReturn(metricTemplateDto);
        MetricTemplateDto result = metricConversationService.toMetricTemplateDtoFromJsonString(validJson);
        assertNotNull(result);
        System.out.println(result);
    }
    @Test
    void toMetricSubmittedSuccess() {
        MetricSubmittedDto dto = new MetricSubmittedDto();
        dto.setMetricId(1L);
        dto.setSubmittedAt(System.currentTimeMillis());
        dto.setSubmittedBy("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        dto.setSubmitterName("john");
        dto.setSubmitStatus(AssessmentStatus.SUBMITTED);
        dto.setDescription("description");
        MetricTemplate metricTemplate = new MetricTemplate();
        metricTemplate.setId(1L);
        metricTemplate.setTemplateName("T1");


        Account account = new Account();
        account.setAccountName("citi");
        account.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setProjectName("testProjectName");

        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        projectType.setProjectTypeName("testProjectTypeName");

        MetricSubmitted result = metricConversationService.toMetricSubmitted(dto, metricTemplate, account, projectType, project);

        assertNotNull(result);
        assertEquals(dto.getSubmittedAt(), result.getSubmittedAt());
        assertEquals(dto.getSubmittedBy(), result.getSubmittedBy());
        assertEquals(dto.getSubmitterName(), result.getSubmitterName());
        assertEquals(dto.getSubmitStatus(), result.getSubmitStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(dto.getDescription(), result.getDescription());
    }
    @Test
    void toMetricTemplateDtoSuccess() {
        MetricTemplate template = new MetricTemplate();
        template.setId(1L);
        template.setTemplateName("Sample Template");
        template.setTemplateUploadedUserName("John Doe");
        template.setTemplateUploadedUserId("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        template.setDescription("Sample Description");
        template.setIsActive(true);

        MetricTemplateDto result = metricConversationService.toMetricTemplateDto(template);
        assertNotNull(result);
        assertEquals(template.getTemplateName(), result.getTemplateName());
        assertEquals(template.getId(), result.getTemplateId());
        assertEquals(template.getTemplateUploadedUserName(), result.getTemplateUploadedUserName());
        assertEquals(template.getTemplateUploadedUserId(), result.getTemplateUploadedUserId());
        assertEquals(template.getDescription(), result.getDescription());
        assertEquals(template.getIsActive(), result.getIsActive());
    }
    @Test
    void toMetricTemplateDtoWithNullObject() {
        assertThrows(TemplateNotFoundException.class, () -> metricConversationService.toMetricTemplateDto(null));
    }
    @Test
    void toMetricTemplateDtosSuccess() {
        MetricTemplate template1 = new MetricTemplate();
        template1.setId(1L);
        template1.setTemplateName("Sample Template");
        template1.setTemplateUploadedUserName("John Doe");
        template1.setTemplateUploadedUserId("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        template1.setDescription("Sample Description");
        template1.setIsActive(true);
        MetricTemplate template2 = new MetricTemplate();
        template2.setId(2L);
        template2.setTemplateName("Sample Template2");
        template2.setTemplateUploadedUserName("Mark");
        template2.setTemplateUploadedUserId("7e2313c6-1c9f-472a-a92d-76f165e08bd3");
        template2.setDescription("Sample Description1");
        template2.setIsActive(true);
        List<MetricTemplate> metricTemplates=Arrays.asList(template1,template2);

        List<MetricTemplateDto> result = metricConversationService.toMetricTemplateDtos(metricTemplates);
        assertNotNull(result);
        assertEquals(template1.getTemplateName(), result.get(0).getTemplateName());
        assertEquals(template1.getId(), result.get(0).getTemplateId());
        assertEquals(template1.getTemplateUploadedUserName(), result.get(0).getTemplateUploadedUserName());
        assertEquals(template2.getTemplateUploadedUserId(), result.get(1).getTemplateUploadedUserId());
        assertEquals(template2.getDescription(), result.get(1).getDescription());
        assertEquals(template2.getIsActive(), result.get(1).getIsActive());
    }
    @Test
    void toMetricTemplateDtosEmptyList() {
        List<MetricTemplate> domains = Collections.emptyList();
        assertThrows(TemplateNotFoundException.class, () -> metricConversationService.toMetricTemplateDtos(domains));
    }
    @Test
    void toMetricTemplateDetailsDtoSuccessfulConversion() {
        ProjectType projectType1=new ProjectType();
        projectType1.setProjectTypeName("projectName1");
        ProjectType projectType2=new ProjectType();
        projectType2.setProjectTypeName("projectName2");
        List<ProjectType> projectTypes=Arrays.asList(projectType1,projectType2);
        MetricTemplate domain = new MetricTemplate();
        domain.setId(1L);
        domain.setTemplateName("Sample Template");
        domain.setProjectTypes(projectTypes);
        domain.setCreatedAt(System.currentTimeMillis());
        domain.setIsActive(true);

        MetricTemplateDetailsDto result = metricConversationService.toMetricTemplateDetailsDto(domain);

        assertNotNull(result);
        assertEquals(domain.getTemplateName(), result.getTemplateName());
        assertEquals(domain.getId(), result.getTemplateId());
        assertEquals(domain.getProjectTypes().stream().map(ProjectType::getProjectTypeName).toList(), result.getProjectType());
        assertEquals(domain.getCreatedAt(), result.getCreatedOn());
        assertEquals(domain.getIsActive(), result.getIsActive());
    }
    @Test
    void toMetricTemplateDetailsDtoWithNullObject() {
        assertThrows(MetricNotFoundException.class, () -> metricConversationService.toMetricTemplateDetailsDto(null));
    }
    @Test
    void toMetricTemplateDetailsDtosSuccess() {
        ProjectType projectType1=new ProjectType();
        projectType1.setProjectTypeName("projectName1");
        ProjectType projectType2=new ProjectType();
        projectType2.setProjectTypeName("projectName2");
        List<ProjectType> projectTypes=Arrays.asList(projectType1,projectType2);
        MetricTemplate template1 = new MetricTemplate();
        template1.setId(1L);
        template1.setTemplateName("Sample Template");
        template1.setProjectTypes(projectTypes);
        template1.setCreatedAt(System.currentTimeMillis());
        template1.setIsActive(true);

        MetricTemplate template2 = new MetricTemplate();
        template2.setId(2L);
        template2.setTemplateName("Sample Template2");
        template2.setProjectTypes(projectTypes);
        template2.setCreatedAt(System.currentTimeMillis());
        template2.setIsActive(true);
        List<MetricTemplate> metricTemplates=Arrays.asList(template1,template2);

        List<MetricTemplateDetailsDto> result = metricConversationService.toMetricTemplateDetailsDtos(metricTemplates);

        assertNotNull(result);
        assertEquals(template1.getTemplateName(), result.get(0).getTemplateName());
        assertEquals(template1.getId(), result.get(0).getTemplateId());
        assertEquals(template1.getProjectTypes().stream().map(ProjectType::getProjectTypeName).toList(), result.get(0).getProjectType());
        assertEquals(template2.getCreatedAt(), result.get(1).getCreatedOn());
        assertEquals(template2.getIsActive(), result.get(1).getIsActive());
    }
    @Test
    void toMetricTemplateDetailsDtoThrowsException() {
        List<MetricTemplate> metricTemplates=Collections.emptyList();
        assertThrows(MetricNotFoundException.class, () -> {
            metricConversationService.toMetricTemplateDetailsDtos(metricTemplates);
        });
    }
    @Test
    void toMetricReviewDtoSuccessfulConversion() {
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setId(1L);
        metricSubmitted.setSubmitterName("John Doe");
        Account account = new Account();
        account.setAccountName("testAccount");
        metricSubmitted.setAccount(account);


        Project project = new Project();
        project.setProjectName("testProject");
        metricSubmitted.setProject(project);
        metricSubmitted.setSubmittedAt(System.currentTimeMillis());

        MetricReviewDto result = metricConversationService.toMetricReviewDto(metricSubmitted);

        assertNotNull(result);
        assertEquals(metricSubmitted.getId(), result.getId());
        assertEquals(metricSubmitted.getSubmitterName(), result.getSubmitterName());
        assertEquals(metricSubmitted.getAccount().getAccountName(), result.getAccountName());
        assertEquals(metricSubmitted.getProject().getProjectName(), result.getProjectName());
        assertEquals(metricSubmitted.getSubmittedAt(), result.getSubmittedAt());
    }
    @Test
    void toMetricReviewDto_NullDomainThrowsException() {
        assertThrows(MetricNotFoundException.class, () -> {
            metricConversationService.toMetricReviewDto(null);
        });
    }
    @Test
    void toMetricReviewDtosSuccess() {
        MetricSubmitted metricSubmitted1 = new MetricSubmitted();
        metricSubmitted1.setId(1L);
        metricSubmitted1.setSubmitterName("John Doe");
        Account account1 = new Account();
        account1.setAccountName("testAccount");
        metricSubmitted1.setAccount(account1);


        Project project1 = new Project();
        project1.setProjectName("testProject");
        metricSubmitted1.setProject(project1);
        metricSubmitted1.setSubmittedAt(System.currentTimeMillis());

        MetricSubmitted metricSubmitted2 = new MetricSubmitted();
        metricSubmitted2.setId(2L);
        metricSubmitted2.setSubmitterName("Mark Wood");
        Account account2 = new Account();
        account2.setAccountName("testAccount2");
        metricSubmitted2.setAccount(account2);


        Project project2 = new Project();
        project2.setProjectName("testProject2");
        metricSubmitted2.setProject(project2);
        metricSubmitted2.setSubmittedAt(System.currentTimeMillis());
        List<MetricSubmitted> metricSubmittedList=Arrays.asList(metricSubmitted1,metricSubmitted2);

        List<MetricReviewDto> result = metricConversationService.toMetricReviewDtos(metricSubmittedList);

        assertNotNull(result);
        assertEquals(metricSubmitted1.getId(), result.get(0).getId());
        assertEquals(metricSubmitted1.getSubmitterName(), result.get(0).getSubmitterName());
        assertEquals(metricSubmitted1.getAccount().getAccountName(), result.get(0).getAccountName());
        assertEquals(metricSubmitted2.getProject().getProjectName(), result.get(1).getProjectName());
        assertEquals(metricSubmitted2.getSubmittedAt(), result.get(1).getSubmittedAt());
    }
    @Test
    void toMetricReviewDtosThrowsException() {
        List<MetricSubmitted> metricSubmittedList=Collections.emptyList();
        assertThrows(MetricNotFoundException.class, () -> {
            metricConversationService.toMetricReviewDtos(metricSubmittedList);
        });
    }
    @Test
    void toSubmitMetricLineChartDtoSuccess() {
        LineChartProjection projection = new LineChartProjection();
        projection.setCount(10);
        projection.setId("2023-01-01");
        LineChartDto result = metricConversationService.toSubmitMetricLineChartdto(projection);

        assertNotNull(result);
        assertEquals(projection.getCount(), result.getCount());
        assertEquals(projection.getId(), result.getDate());
    }
    @Test
    void toSubmitMetricLineChartDtosSuccess() {
        LineChartProjection projection1 = new LineChartProjection();
        projection1.setCount(12);
        projection1.setId("2023-01-01");
        LineChartProjection projection2 = new LineChartProjection();
        projection2.setCount(10);
        projection2.setId("2023-05-11");
        List<LineChartProjection> projections=Arrays.asList(projection1,projection2);
        List<LineChartDto> result = metricConversationService.tosubmitmetriclinechartdto(projections);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(projections.get(0).getCount(), result.get(0).getCount());
        assertEquals(projections.get(1).getCount(), result.get(1).getCount());
        assertEquals(projections.get(0).getId(), result.get(0).getDate());
        assertEquals(projections.get(1).getId(), result.get(1).getDate());
    }
    @Test
    void toMetricReportDetailSuccess() {
        MetricSubmitted metricSubmitted = new MetricSubmitted();
        metricSubmitted.setId(1L);
        Project project=new Project();
        project.setProjectName("testProjectName");
        Account account=new Account();
        account.setAccountName("testAccountName");
        Reviewer reviewer1=new Reviewer();
        reviewer1.setReviewerName("test1");
        Reviewer reviewer2=new Reviewer();
        reviewer2.setReviewerName("test1");
        MetricTemplate template=new MetricTemplate();
        template.setTemplateName("T1");
        metricSubmitted.setProject(project);
        metricSubmitted.setAccount(account);
        metricSubmitted.setSubmitStatus(AssessmentStatus.SUBMITTED);
        metricSubmitted.setUpdatedAt(System.currentTimeMillis());
        metricSubmitted.setReviewers(Arrays.asList(reviewer1,reviewer2));
        metricSubmitted.setTemplate(template);

        MetricAndAssessmentReportDetails result = metricConversationService.toMetricReportDetail(metricSubmitted);

        assertNotNull(result);
        assertEquals(metricSubmitted.getId(), result.getId());
        assertEquals(metricSubmitted.getProject().getProjectCode(), result.getProjectCode());
        assertEquals(metricSubmitted.getProject().getProjectName(), result.getProjectName());
        assertEquals(metricSubmitted.getAccount().getAccountName(), result.getAccountName());
        assertEquals(metricSubmitted.getSubmitStatus(), result.getStatus());
        assertEquals(metricSubmitted.getSubmittedAt(), result.getSubmittedAt());
        assertEquals(metricSubmitted.getReviewers().get(0).getReviewerName(), result.getReviewerName());
        assertEquals(metricSubmitted.getTemplate().getTemplateName(), result.getTemplateName());
    }
    @Test
    void toMetricReportDetailsSuccess() {
        MetricSubmitted metricSubmitted1 = new MetricSubmitted();
        metricSubmitted1.setId(1L);
        Project project=new Project();
        project.setProjectName("testProjectName");
        Account account=new Account();
        account.setAccountName("testAccountName");
        Reviewer reviewer1=new Reviewer();
        reviewer1.setReviewerName("test1");
        Reviewer reviewer2=new Reviewer();
        reviewer2.setReviewerName("test1");
        MetricTemplate template=new MetricTemplate();
        template.setTemplateName("T1");
        metricSubmitted1.setProject(project);
        metricSubmitted1.setAccount(account);
        metricSubmitted1.setSubmitStatus(AssessmentStatus.SUBMITTED);
        metricSubmitted1.setUpdatedAt(System.currentTimeMillis());
        metricSubmitted1.setReviewers(Arrays.asList(reviewer1,reviewer2));
        metricSubmitted1.setTemplate(template);

        MetricSubmitted metricSubmitted2 = new MetricSubmitted();
        metricSubmitted2.setId(2L);
        Project project2=new Project();
        project2.setProjectName("testProjectName");
        Account account2=new Account();
        account2.setAccountName("testAccountName");
        MetricTemplate template2=new MetricTemplate();
        template2.setTemplateName("T2");
        metricSubmitted2.setProject(project);
        metricSubmitted2.setAccount(account);
        metricSubmitted2.setSubmitStatus(AssessmentStatus.SUBMITTED);
        metricSubmitted2.setUpdatedAt(System.currentTimeMillis());
        metricSubmitted2.setReviewers(Arrays.asList(reviewer1,reviewer2));
        metricSubmitted2.setTemplate(template);
        List<MetricSubmitted> metricSubmittedList=Arrays.asList(metricSubmitted1,metricSubmitted2);
        List<MetricAndAssessmentReportDetails> result = metricConversationService.toMetricReportDetails(metricSubmittedList);

        assertNotNull(result);
        assertEquals(metricSubmittedList.size(),result.size());
        assertEquals(metricSubmitted1.getId(), result.get(0).getId());
        assertEquals(metricSubmitted1.getProject().getProjectCode(), result.get(0).getProjectCode());
        assertEquals(metricSubmitted1.getProject().getProjectName(), result.get(0).getProjectName());
        assertEquals(metricSubmitted1.getAccount().getAccountName(), result.get(0).getAccountName());
        assertEquals(metricSubmitted1.getSubmitStatus(), result.get(0).getStatus());
        assertEquals(metricSubmitted2.getSubmittedAt(), result.get(1).getSubmittedAt());
        assertEquals(metricSubmitted2.getReviewers().get(0).getReviewerName(), result.get(1).getReviewerName());
        assertEquals(metricSubmitted2.getTemplate().getTemplateName(), result.get(1).getTemplateName());
    }
    @Test
    void toMetricReportDetailsThrowsException() {
        List<MetricSubmitted> metricSubmittedList=Collections.emptyList();
        assertThrows(MetricNotFoundException.class, () -> {
            metricConversationService.toMetricReportDetails(metricSubmittedList);
        });
    }
    @Test
    void populateMetricTemplateForSaveTemplate_Success() {
        ProjectType projectType1=new ProjectType();
        projectType1.setId(1L);
        projectType1.setProjectTypeName("Internal");
        ProjectType projectType2=new ProjectType();
        projectType2.setId(2L);
        projectType2.setProjectTypeName("Delivery");
        List<ProjectType> projectTypes = Arrays.asList(projectType1,projectType2);
        MetricTemplateSaveRequestDto metricTemplateSaveRequestDto=new MetricTemplateSaveRequestDto();
        metricTemplateSaveRequestDto.setTemplateName("T1");
        MetricTemplate metricTemplate = new MetricTemplate();
        metricConversationService.populateMetricTemplateForSaveTemplate(projectTypes, metricTemplateSaveRequestDto, metricTemplate);

        assertNotNull(metricTemplate);
        assertEquals(metricTemplateSaveRequestDto.getTemplateName(), metricTemplate.getTemplateName());
        assertEquals(projectTypes, metricTemplate.getProjectTypes());
        assertNotNull(metricTemplate.getCreatedAt());
    }

    @Test
    void toMetricTemplate(){
        MetricTemplateDto metricTemplateDto=new MetricTemplateDto();
        metricTemplateDto.setTemplateId(111L);
        metricTemplateDto.setTemplateName("T-1");
        metricTemplateDto.setIsActive(true);
        metricTemplateDto.setProjectType("PT-1");
        metricConversationService. toMetricTemplate(metricTemplateDto);
    }
    @Test
    void toMetricSubmittedSubmittedDashboardDtoSuccess() {
        MetricSubmitted metricSubmitted = mock(MetricSubmitted.class);
        when(metricSubmitted.getSubmitterName()).thenReturn("Ravi");
        Reviewer reviewer = mock(Reviewer.class);
        when(reviewer.getReviewerName()).thenReturn("Barani");
        List<Reviewer> reviewers = new ArrayList<>();
        reviewers.add(reviewer);
        when(metricSubmitted.getReviewers()).thenReturn(reviewers);
        Project project = mock(Project.class);
        when(project.getProjectName()).thenReturn("EED");
        when(project.getProjectCode()).thenReturn("CITI008");
        when(metricSubmitted.getProject()).thenReturn(project);
        Account account = mock(Account.class);
        when(account.getAccountName()).thenReturn("CITI");
        when(metricSubmitted.getAccount()).thenReturn(account);
        when(metricSubmitted.getSubmitStatus()).thenReturn(AssessmentStatus.SUBMITTED);
        when(metricSubmitted.getSubmittedAt()).thenReturn(1111111111L);

        AssessmentsSubmittedDashboardDto dto = metricConversationService.toMetricSubmittedSubmittedDashboardDto(metricSubmitted);

        assertEquals("Ravi", dto.getSubmitterName());
        assertEquals("Barani", dto.getReviewerName());
        assertEquals("EED", dto.getProjectName());
        assertEquals("CITI", dto.getAccountName());
    }
    @Test
    void toMetricSubmittedSubmittedDashboardDtoReviewerNull() {
        MetricSubmitted metricSubmitted = mock(MetricSubmitted.class);
        when(metricSubmitted.getReviewers()).thenReturn(null);
        AssessmentsSubmittedDashboardDto dto = metricConversationService.toMetricSubmittedSubmittedDashboardDto(metricSubmitted);
        assertEquals("", dto.getReviewerName());
    }
    @Test
    void toMetricSubmittedSubmittedDashboardDtosSuccess() {
        MetricSubmitted metricSubmitted1 = mock(MetricSubmitted.class);
        MetricSubmitted metricSubmitted2 = mock(MetricSubmitted.class);
        List<MetricSubmitted> domains = Arrays.asList(metricSubmitted1, metricSubmitted2);
        List<AssessmentsSubmittedDashboardDto> dtos = metricConversationService.toMetricSubmittedSubmittedDashboardDtos(domains);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
    }
    @Test
    void toMetricSubmittedSubmittedDashboardDtosThrowsException() {
        List<MetricSubmitted> metricSubmittedList=Collections.emptyList();
        assertThrows(AssessmentNotFoundException.class, () -> {
            metricConversationService.toMetricSubmittedSubmittedDashboardDtos(metricSubmittedList);
        });
    }

}
