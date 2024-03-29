package com.maveric.digital.service;


import java.io.Reader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import java.util.stream.Collectors;

import com.maveric.digital.exceptions.*;
import com.maveric.digital.model.*;
import com.maveric.digital.responsedto.*;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.model.embedded.AssessmentProjectCategory;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.ProjectCategory;
import com.maveric.digital.model.embedded.QuestionAnswer;
import com.maveric.digital.model.embedded.Reviewer;
import com.maveric.digital.model.embedded.TemplateQuestionnaire;
import com.maveric.digital.projection.LineChartProjection;
import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.responsedto.AssessmentDto;
import com.maveric.digital.responsedto.AssessmentProjectCategoryDto;
import com.maveric.digital.responsedto.AssessmentReviewDto;
import com.maveric.digital.responsedto.AssessmentSubmitedByDto;
import com.maveric.digital.responsedto.AssessmentTemplateDto;
import com.maveric.digital.responsedto.AssessmentsSubmittedDashboardDto;
import com.maveric.digital.responsedto.AuditDto;
import com.maveric.digital.responsedto.CsvMetaDataDto;
import com.maveric.digital.responsedto.LineChartDto;
import com.maveric.digital.responsedto.MetricAndAssessmentDetailsDto;
import com.maveric.digital.responsedto.MetricAndAssessmentReportDetails;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.responsedto.ProjectTypeDto;
import com.maveric.digital.responsedto.QuestionAnswerDto;
import com.maveric.digital.responsedto.ScoreCategoryDto;
import com.maveric.digital.responsedto.ScoreScaleDto;
import com.maveric.digital.responsedto.TemplateDto;
import com.maveric.digital.responsedto.TemplateInfo;
import com.maveric.digital.responsedto.TemplateSaveRequestDto;
import com.maveric.digital.responsedto.UserDto;
import com.opencsv.bean.CsvToBeanBuilder;

import static com.maveric.digital.service.AccountServiceImpl.ACCOUNT_NOT_FOUND;
import static com.maveric.digital.utils.ServiceConstants.Login_success;

@Service
public class ConversationService {

	Logger logger = LoggerFactory.getLogger(ConversationService.class);
	private static final String SCORE_NOT_FOUND = "Score Scale not found";
	public static final String TEMPLATE_NOT_FOUND = "Template not found";
	public static final String ASSESSMENT_NOT_FOUND = "Assessment not found";
	private static final String SCORE_CATEGORY_NOT_FOUND = "Score Category not found";
	private static final String TEMPLATE_PROJECT_CATEGORY_NOT_FOUND = "Template project Category not found";
	private static final String ASSESSMENT_PROJECT_CATEGORY_NOT_FOUND = "Assessment project Category not found";
	private static final String USER_NOT_FOUND = "User Not Found";
	private static final String CATEGORY_NOT_FOUND = "Category not Found";

	public static final String PROJECT_TYPE_NOT_FOUND = "PROJECT TYPE NOT FOUND";
	public static final String PROJECT_NOT_FOUND = "PROJECT NOT FOUND";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy||HH:mm:ss a");

	private final Random random = new Random();

	@Autowired
	private ModelMapper modelMapper;
	

	@Autowired
	private ObjectMapper objectMapper;
	public Template toTemplate(TemplateDto templateDto) {
		modelMapper=new ModelMapper();
		this.logger.debug("start::ConversationService::toTemplateDto");
		Template template = modelMapper.map(templateDto, Template.class);
		this.logger.debug("end::ConversationService::toTemplateDto");
		return template;
	}


	public TemplateDto toTemplateDto(Template domain) {
		if(Objects.isNull(domain)) {
			throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
		}
		logger.debug("ConversationService::toTemplateDto()::Start");
		TemplateDto dto =  TemplateDto.builder().build();
		dto.setTemplateName(domain.getTemplateName());
		dto.setTemplateId(domain.getId());
		dto.setTemplateUploadedUserName(domain.getTemplateUploadedUserName());
		dto.setProjectCategory(domain.getProjectCategory());
		dto.setTemplateFrequency(domain.getTemplateFrequency());
		dto.setTemplateUploadedUserId(domain.getTemplateUploadedUserId());
		dto.setScoreCategories(this.toScoreCategoryDtos(domain.getScoreCategories()));
		dto.setAssessmentDescription(domain.getAssessmentDescription());
		dto.setTemplateDisplayName(domain.getTemplateDisplayName());
		logger.debug("ConversationService::toTemplateDto()::End");
		return dto;
	}

	public AssessmentTemplateDto toAssessmentTemplateDto(Template domain){
		if(Objects.isNull(domain)) {
			throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
		}
		logger.debug("ConversationService::toAssessmentTemplateDto()::Start");
		AssessmentTemplateDto dto= AssessmentTemplateDto.builder().build();
		dto.setTemplateId(domain.getId());
		dto.setTemplateName(domain.getTemplateName());
		dto.setProjectType(domain.getProjectTypes().stream().map(ProjectType::getProjectTypeName).toList());
		dto.setTemplateUploadedUserName(domain.getTemplateUploadedUserName());
		dto.setCreatedOn(domain.getCreatedAt());
		dto.setIsActive(domain.getIsActive());
		dto.setTemplateDisplayName(domain.getTemplateDisplayName());
		dto.setTemplateFrequency(domain.getTemplateFrequency());
		logger.debug("ConversationService::toAssessmentTemplateDto()::End");
		return dto;
	}


	public List<AssessmentTemplateDto> toAssessmentTemplateDtos(List<Template> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toAssessmentTemplateDto).toList();
	}

	public ScoreScaleDto toScoreDto(ScoringScale domain) {
		logger.debug("ConversationService::toScoreDto()::Start");
		if (Objects.isNull(domain)) {
			logger.debug("ConversationService::toScoreDto()::End");
			throw new ScoreScaleNotFoundException(SCORE_NOT_FOUND);
		}
		ScoreScaleDto dto = new ScoreScaleDto();
		dto.setId(domain.getId());
		dto.setRange(domain.getRange());
		dto.setName(domain.getName());
		dto.setCreatedUserId(domain.getCreatedUserId());
		dto.setScoreScaleType(domain.getScoreScaleType());
		logger.debug("ConversationService::toScoreDto()::End");
		return dto;
	}

	public List<ScoreScaleDto> toScoreDtos(List<ScoringScale> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new ScoreScaleNotFoundException(SCORE_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toScoreDto).toList();
	}

	public List<TemplateDto> toTemplateDtos(List<Template> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toTemplateDto).toList();
	}

	public AssessmentDto toAssessmentDto(Assessment domain) {
		logger.debug("ConversationService::toAssessmentDto()::Start");
		this.validatedAssessment(domain);
		AssessmentDto dto = new AssessmentDto();
		dto.setAssessmentId(domain.getId());
		dto.setProjectId(domain.getProjectId());
		if(domain.getProject()!=null){
			dto.setProjectName(domain.getProject().getProjectName());
		}
		dto.setReviewers(domain.getReviewers());
		dto.setSubmitedAt(domain.getSubmitedAt());
		dto.setSubmitedBy(domain.getSubmitedBy());
		if (domain.getAccount() != null) {
			dto.setAccountId(domain.getAccount().getId());
			dto.setAccountName(domain.getAccount().getAccountName());
		}

		if (domain.getProjectType() != null) {
			dto.setProjectTypeId(domain.getProjectType().getId());
		}
		dto.setSubmitStatus(domain.getSubmitStatus());
		dto.setTemplateId(domain.getTemplate().getId());
		dto.setTemplateName(domain.getTemplate().getTemplateName());
		dto.setBusinessUnitName(domain.getProject().getBusinessUnit());
		dto.setTemplateUploadedUserId(domain.getTemplate().getTemplateUploadedUserId());
		dto.setTemplateUploadedUserName(domain.getTemplate().getTemplateUploadedUserName());
		dto.setProjectCategory(this.populateAssessmentProjectCategoryDtos(domain.getProjectCategory(),
				this.populateTemplateQuestionnaireMap(domain.getTemplate().getProjectCategory())));
		dto.setScoreCategories(this.toScoreCategoryDtos(domain.getTemplate().getScoreCategories()));
		dto.setScore(domain.getScore());
		if(!CollectionUtils.isEmpty(domain.getCategoryScores())) {
			dto.setCategorywiseScores(domain.getCategoryScores());
		}
		dto.setAssessmentDescription(domain.getAssessmentDescription());
		dto.setIsEdited(domain.getIsEdited());
		logger.debug("ConversationService::toAssessmentDto()::End");
		return dto;

	}

	public List<AssessmentProjectCategory> toAssessmentProjectCategories(List<AssessmentProjectCategoryDto> dtos) {

		return dtos.stream().filter(Objects::nonNull).map(this::toAssessmentProjectCategory)
				.toList();

	}

	public AssessmentProjectCategory toAssessmentProjectCategory(AssessmentProjectCategoryDto dto) {
		AssessmentProjectCategory embeded = new AssessmentProjectCategory();
		embeded.setCategoryName(dto.getCategoryName());
		embeded.setTemplateQuestionnaire(this.toQuestionAnswers(dto.getTemplateQuestionnaire()));
		embeded.setCategoryDescription(dto.getCategoryDescription());
		return embeded;

	}

	public List<QuestionAnswer> toQuestionAnswers(List<QuestionAnswerDto> dtos) {

		return dtos.stream().filter(Objects::nonNull).map(this::toQuestionAnswer).toList();

	}

	public QuestionAnswer toQuestionAnswer(QuestionAnswerDto dto) {
		QuestionAnswer embeded = new QuestionAnswer();
		embeded.setAnswerOptionIndex(dto.getAnswerOptionIndex());
		embeded.setComment(dto.getComment());
		embeded.setFileUri(dto.getFileUri());
		embeded.setMimeType(dto.getMimeType());
		embeded.setQuestionId(dto.getQuestionId());
		embeded.setQuestionDescription(dto.getQuestionDescription());
		embeded.setQuestionSubText(dto.getQuestionSubText());
		embeded.setExtraInfo(dto.getExtraInfo());
		embeded.setReviewerComment(dto.getReviewerComment());
		return embeded;

	}



	public void validatedAssessment(Assessment domain) {
		if (Objects.isNull(domain)) {
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		if (Objects.isNull(domain.getTemplate())) {
			throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
		}
		/*
		 * if (Objects.isNull(domain.getTemplate().getScore())) { throw new
		 * ScoreScaleNotFoundException(SCORE_NOT_FOUND); }
		 */
		if (CollectionUtils.isEmpty(domain.getProjectCategory())) {
			throw new AssessmentProjectCategoryNotFoundException(ASSESSMENT_PROJECT_CATEGORY_NOT_FOUND);
		}
		if (CollectionUtils.isEmpty(domain.getTemplate().getProjectCategory())) {
			throw new TemplateProjectCategoryNotFoundException(TEMPLATE_PROJECT_CATEGORY_NOT_FOUND);
		}
	}

	private List<AssessmentProjectCategoryDto> populateAssessmentProjectCategoryDtos(
			List<AssessmentProjectCategory> projectCategories,
			Map<Integer, TemplateQuestionnaire> templateQuestionnaireByQuestionId) {
		return projectCategories.stream()
				.map(obj -> this.populateAssessmentProjectCategoryDto(obj, templateQuestionnaireByQuestionId))
				.toList();
	}

	private AssessmentProjectCategoryDto populateAssessmentProjectCategoryDto(AssessmentProjectCategory embaded,
																			  Map<Integer, TemplateQuestionnaire> templateQuestionnaireByQuestionId) {
		AssessmentProjectCategoryDto dto = new AssessmentProjectCategoryDto();
		dto.setCategoryName(embaded.getCategoryName());
		dto.setCategoryDescription(embaded.getCategoryDescription());
		dto.setTemplateQuestionnaire(
				this.populateQuestionAnswerDtos(embaded.getTemplateQuestionnaire(), templateQuestionnaireByQuestionId));
		return dto;
	}

	private List<QuestionAnswerDto> populateQuestionAnswerDtos(List<QuestionAnswer> questionAnswers,
															   Map<Integer, TemplateQuestionnaire> templateQuestionnaireByQuestionId) {

		return questionAnswers.stream()
				.map(obj -> this.populateQuestionAnswerDto(obj, templateQuestionnaireByQuestionId.get(obj.getQuestionId())))
				.toList();

	}

	public Map<Integer, TemplateQuestionnaire> populateTemplateQuestionnaireMap(List<ProjectCategory> projectCategories) {
		if (CollectionUtils.isEmpty(projectCategories)) {
			return Map.of();
		}
		return projectCategories.stream()
				.filter(obj -> Objects.nonNull(obj) && !CollectionUtils.isEmpty(obj.getTemplateQuestionnaire()))
				.flatMap(obj -> obj.getTemplateQuestionnaire().stream()).filter(Objects::nonNull)
				.collect(Collectors.toMap(TemplateQuestionnaire::getQuestionId, obj -> obj));
	}

	private QuestionAnswerDto populateQuestionAnswerDto(QuestionAnswer questionAnswer,
														TemplateQuestionnaire templateQuestionnaire) {
		QuestionAnswerDto dto = new QuestionAnswerDto();
		dto.setAnswerOptionIndex(questionAnswer.getAnswerOptionIndex());
		dto.setComment(questionAnswer.getComment());
		dto.setFieldType(templateQuestionnaire.getFieldType());
		dto.setFileUri(questionAnswer.getFileUri());
		dto.setMimeType(questionAnswer.getMimeType());
		dto.setQuestion(templateQuestionnaire.getQuestion());
		dto.setQuestionSubText(templateQuestionnaire.getQuestionSubText());
		dto.setQuestionId(questionAnswer.getQuestionId());
		dto.setScoreCategory(templateQuestionnaire.getScoreCategory());
		dto.setQuestionDescription(questionAnswer.getQuestionDescription());
		dto.setExtraInfo(questionAnswer.getExtraInfo());
		dto.setReviewerComment(questionAnswer.getReviewerComment());
		return dto;

	}

	public List<AssessmentDto> toAssessmentDtos(List<Assessment> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toAssessmentDto).toList();
	}

	public List<ScoreCategoryDto> toScoreCategoryDtos(List<ScoreCategory> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new ScoreCategoryNotFoundException(SCORE_CATEGORY_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toScoreCategoryDto).toList();
	}

	public ScoreCategoryDto toScoreCategoryDto(ScoreCategory domain) {
		logger.debug("ConversationService::toScoreCategoryDto()::Start");
		ScoreCategoryDto dto = new ScoreCategoryDto();
		if (Objects.isNull(domain)) {
			logger.debug("ConversationService::toScoreCategoryDto()::End");
			throw new ScoreCategoryNotFoundException(SCORE_CATEGORY_NOT_FOUND);
		}
		dto.setCategoryId(domain.getId());
		dto.setCategoryName(domain.getCategoryName());
		dto.setCategoryOptions(domain.getCategoryOptions());
		dto.setCreatedBy(domain.getCreatedBy());
		dto.setUpdatedAt(domain.getUpdatedAt());
		dto.setCreatedAt(domain.getCreatedAt());
		logger.debug("ConversationService::toScoreCategoryDto()::End");
		return dto;
	}


	public Assessment toAssessment(AssessmentDto dto,Template template,Account account,ProjectType projectType,Project project) {
		logger.debug("ConversationService::toAssessment()::Start");
		Assessment domain = new Assessment();
		domain.setProjectId(dto.getProjectId());
		domain.setReviewers(dto.getReviewers());
		domain.setSubmitedAt(dto.getSubmitedAt());
		domain.setSubmitedBy(dto.getSubmitedBy());
		domain.setSubmitterName(dto.getSubmitterName());
		domain.setSubmitStatus(dto.getSubmitStatus());
		domain.setTemplate(template);
		domain.setProjectCategory(this.toAssessmentProjectCategories(dto.getProjectCategory()));
		domain.setCreatedAt(System.currentTimeMillis());
		domain.setUpdatedAt(System.currentTimeMillis());
		domain.setAssessmentDescription(dto.getAssessmentDescription());
		domain.setProjectType(projectType);
		domain.setAccount(account);
		domain.setProject(project);
		logger.debug("ConversationService::toAssessment()::End");
		return domain;

	}

	public ProjectTypeDto convertToProjectTypeDto(ProjectType projectType) {
		ProjectTypeDto projectTypeDto= new ProjectTypeDto();
		BeanUtils.copyProperties(projectType, projectTypeDto);
		logger.info("converting projecttype to projecttypedto : {}" , projectTypeDto);
		return projectTypeDto;
	}

	public List<ProjectTypeDto> convertToProjectTypeDto(List<ProjectType> projectTypes) {
		List<ProjectTypeDto> projectTypeDtos = new ArrayList<>();
		for (ProjectType project2 : projectTypes) {
			projectTypeDtos.add(convertToProjectTypeDto(project2));
		}
		logger.info("converting projecttype to projecttypedtos : {}" , projectTypeDtos);
		return projectTypeDtos;
	}

	public TemplateDto toTemplateDtoFromJsonString(String templateJsonString) throws JsonProcessingException {
		this.logger.debug("start::ConversationService::toTemplateDto::templateJsonString::{}",templateJsonString);
		TemplateDto dto = objectMapper.readValue(templateJsonString, TemplateDto.class);
		this.logger.debug("end::ConversationService::toTemplateDto");
		return dto;
	}

	public void populateTemplateForSaveTemplate(ScoringScale score, List<ScoreCategory> scoreCategories,
												List<Project> projects, List<ProjectType> projectTypes,TemplateSaveRequestDto templateSaveRequest,
												Template template) {
		template.setProjects(projects);
		template.setProjectTypes(projectTypes);
//		template.setScore(score);
		template.setScoreCategories(scoreCategories);
		template.setTemplateName(templateSaveRequest.getTemplateName());
		template.setTemplateUploadedUserId(templateSaveRequest.getTemplateUploadedUserId());
		template.setTemplateUploadedUserName(templateSaveRequest.getTemplateUploadedUserName());
		this.populateTemplateQuestionnaireList(template.getProjectCategory());
	}
	private void populateTemplateQuestionnaireList(List<ProjectCategory> projectCategory) {
		projectCategory.stream().flatMap(obj->obj.getTemplateQuestionnaire().stream()).forEach(this::populateTemplateQuestionnaire);
	}

	private void populateTemplateQuestionnaire(TemplateQuestionnaire templateQuestionnaire) {
		templateQuestionnaire.setFieldType("radio");
		templateQuestionnaire.setQuestionId(Math.abs(random.nextInt(1000000000)));
	}

	public List<AssessmentSubmitedByDto> toAssessmentSubmitedByDtos(List<Assessment> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toAssessmentSubmitedByDto)
				.collect(Collectors.toList());

	}

	public AssessmentSubmitedByDto toAssessmentSubmitedByDto(Assessment assessment) {

		AssessmentSubmitedByDto dto = new AssessmentSubmitedByDto();
		dto.setAssessmentId(assessment.getId());
		dto.setClientName("citi");
		dto.setSubmitedAt(assessment.getSubmitStatus().equals(AssessmentStatus.SAVE) ? assessment.getCreatedAt() : assessment.getSubmitedAt());
		dto.setProjectId(assessment.getProjectId());
		dto.setTemplateId(assessment.getTemplate().getId());
		dto.setSubmitedBy(assessment.getSubmitedBy());
		dto.setSubmitStatus(assessment.getSubmitStatus());
		List<Reviewer> reviewers = assessment.getReviewers();
		if (reviewers != null && !reviewers.isEmpty()) {
			Reviewer firstReviewer = reviewers.get(0);
			dto.setReviewerAt(firstReviewer.getReviewerAt());
			dto.setReviewerName(firstReviewer.getReviewerName());
		} else {
			dto.setReviewerAt(0L);
			dto.setReviewerName("");
		}
		dto.setProjectName(this.populateProjectNameByProjectId(assessment.getTemplate
				().getProjects(), assessment.getProjectId()));
		return dto;
	}

	public LoginDto toLoginDto(User user){
		LoginDto dto=new LoginDto();
		dto.setLoginMessage(Login_success);
		dto.setRole(user.getRole());
		dto.setOid(user.getOid());
		dto.setUserName(user.getUserName());
		dto.setUserName(user.getUserName());
		dto.setEmailAddress(user.getEmailAddress());
		dto.setUserFirstAndLastName(user.getName());
		if(Objects.nonNull(user.getLastLoginTime())){
			dto.setLastLoginTime(formatter.format(user.getLastLoginTime().atZone(ZoneId.of("Asia/Kolkata"))));
		}
		return dto;
	}

	public String populateProjectNameByProjectId(List<Project> projects, Long projectId) {
		if (CollectionUtils.isEmpty(projects)) {
			return "";
		}
		Optional<String> projectName = projects.stream().filter(project -> project.getId().equals(projectId))
				.map(Project::getProjectName).findFirst();
        return projectName.orElse("");
	}
	public List<TemplateInfo> toTemplateInfo(List<Template> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toTemplateInfo).collect(Collectors.toList());
	}

	public TemplateInfo toTemplateInfo(Template domain) {
		TemplateInfo dto = new TemplateInfo();
		dto.setId(domain.getId());
		dto.setTemplateName(domain.getTemplateName());
		dto.setIsActive(domain.getIsActive());
		return dto;
	}

	public List<LineChartDto> toLineChartDtos(List<LineChartProjection> projections) {

		return projections.stream().filter(Objects::nonNull).map(this::toLineChartDto)
				.collect(Collectors.toList());

	}

	public LineChartDto toLineChartDto(LineChartProjection projection) {
		LineChartDto dto = new LineChartDto();
		dto.setCount(projection.getCount());
		dto.setDate(projection.getId());
		return dto;
	}

	public UserDto convertToUserDto(User user) {
		logger.debug("ConversationService::convertToUserDto()::Start");
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(user, userDto);
		if(user.getLastLoginTime().equals(user.getCreatedDate())) {
			Instant lastLoginTime = user.getLastLoginTime();
			userDto.setLastLoginTime("User Activated:"+formatter.format(lastLoginTime.atZone(ZoneId.of("Asia/Kolkata"))));
		}else {
			Instant lastLoginTime = user.getLastLoginTime();
			userDto.setLastLoginTime(formatter.format(lastLoginTime.atZone(ZoneId.of("Asia/Kolkata"))));
		}
		logger.debug("Converted to UserDto {}",userDto);
		logger.debug("ConversationService::convertToUserDto()::End");
		return userDto;
	}

	public User convertToUser(UserDto userDto) {
		logger.debug("ConversationService::convertToUser()::Start");
		User user = new User();
		BeanUtils.copyProperties(userDto, user);
		logger.debug("Converted to User {}",user);
		logger.debug("ConversationService::convertToUser()::End");
		return user;
	}

	public List<UserDto> convertToUserDtoList(List<User> userList){
		logger.debug("ConversationService::convertToUserDtoList()::Start");
		if (CollectionUtils.isEmpty(userList)) {
			logger.error("No User found in the list: {}",userList);
			throw new ResourceNotFoundException("User not found");
		}
		List<UserDto> userDtos=userList.stream().filter(Objects::nonNull).map(this::convertToUserDto).toList();
		logger.debug("Converted to UserDtos {}",userDtos);
		logger.debug("ConversationService::convertToUserDtoList()::End");
		return userDtos;
	}

	public List<User> convertToUserList(List<UserDto> userDtoList){
		logger.debug("ConversationService::convertToUserList()::Start");
		if (CollectionUtils.isEmpty(userDtoList)) {
			logger.error("No User found in the list: {}",userDtoList);
			throw new ResourceNotFoundException("User not found");
		}
		List<User> users=userDtoList.stream().filter(Objects::nonNull).map(this::convertToUser).toList();
		logger.debug("Converted to Users {}",users);
		logger.debug("ConversationService::convertToUserList()::End");
		return users;
	}


	public AuditDto toAuditDto(Audit audit) {
		logger.debug("ConversationService::toAuditDto(): started");
		AuditDto auditDto = new AuditDto();
		BeanUtils.copyProperties(audit, auditDto);
		logger.debug("Converted to auditDto {}", auditDto);
		logger.debug("ConversationService::toAuditDto(): ended");
		return auditDto;

	}

	public List<AuditDto> toAuditDtoList(List<Audit> auditList) {
		logger.debug("ConversationService::toAuditDtoList(): started");
		List<AuditDto> auditDtoList = auditList.stream().filter(Objects::nonNull).map(this::toAuditDto).toList();
		logger.debug("Converted to toAuditDtoList {}", auditDtoList);
		logger.debug("ConversationService:: toAuditDtoList(): ended");
		return auditDtoList;

	}

	public SubmissionFilterDto toSubmissionFilterDtoFromJsonString(String submissionFilterDto) throws JsonProcessingException {
		logger.debug("toSubmissionFilterDtoFromJsonString:: started{}", submissionFilterDto);
		SubmissionFilterDto dto = objectMapper.readValue(submissionFilterDto, SubmissionFilterDto.class);
		this.logger.debug("toSubmissionFilterDtoFromJsonString:: ended");
		return dto;
	}
	public List<AssessmentsSubmittedDashboardDto> toAssessmentsSubmittedDashboardDtos(List<Assessment> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toAssessmentsSubmittedDashboardDto)
				.collect(Collectors.toList());

	}

	public AssessmentsSubmittedDashboardDto toAssessmentsSubmittedDashboardDto(Assessment assessment) {

		AssessmentsSubmittedDashboardDto dto = new AssessmentsSubmittedDashboardDto();
		dto.setSubmitterName(assessment.getSubmitterName());
		dto.setId(assessment.getId());
		dto.setSubmitterId(assessment.getSubmitedBy());
		Template template = assessment.getTemplate();
		if(template != null) dto.setTemplateName(template.getTemplateName());
		List<Reviewer> reviewers = assessment.getReviewers();
		if (reviewers != null && !reviewers.isEmpty()) {
			Reviewer firstReviewer = reviewers.get(0);
			dto.setReviewerName(firstReviewer.getReviewerName());
			dto.setReviewerId(firstReviewer.getReviewerId());
		} else {
			dto.setReviewerName("");
		}
		dto.setSubmitStatus(assessment.getSubmitStatus());
		dto.setScore(assessment.getScore());
		dto.setSubmitedAt(assessment.getSubmitedAt());
		Project project= assessment.getProject();
		if(project!=null){
			dto.setProjectName(project.getProjectName());
			dto.setBusinessUnitName(assessment.getProject().getBusinessUnit());
			dto.setProjectCode(project.getProjectCode());
		}else {
			dto.setProjectName("");
		}
		Account account=assessment.getAccount();
		if(account!=null){
			dto.setAccountName(account.getAccountName());
		}else {
			dto.setAccountName("");
		}
		return dto;
	}

	public AssessmentReviewDto toAssessmentReviewDto(Assessment domain) {
		logger.debug("ConversationService::toAssessmentReviewDto():: call started");
		if (Objects.isNull(domain)) {
			logger.error("ConversationService::toAssessmentReviewDto():: No Assessment found");
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		AssessmentReviewDto dto = new AssessmentReviewDto();
		dto.setId(domain.getId());
		dto.setAccountName(domain.getAccount().getAccountName());
		dto.setProjectName(domain.getProject().getProjectName());
		dto.setSubmittedAt(domain.getSubmitedAt());
		dto.setSubmitterName(domain.getSubmitterName());
		dto.setProjectCode(domain.getProject().getProjectCode());
		dto.setDeliveryUnit(domain.getProject().getBusinessUnit());
		dto.setScore(domain.getScore());
		dto.setSubmitterId(domain.getSubmitedBy());;
		if(Objects.nonNull(domain.getProjectType()) && Objects.nonNull(domain.getProjectType().getProjectTypeName())) {
			dto.setProjectType(domain.getProjectType().getProjectTypeName());	
		}
		
		logger.debug("ConversationService::toAssessmentReviewDto()::call ended");
		return dto;
	}
	public List<AssessmentReviewDto> toAssessmentReviewDtos(List<Assessment> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			logger.error("No Assessment found in the list: {}",domains);
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toAssessmentReviewDto).toList();
	}

	public MetricAndAssessmentDetailsDto toMetricAndAssessmentDetailsDto(Assessment assessment) {
		logger.debug("ConversationService::toMetricAndAssessmentDetailsDto()::call started");
		MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto = new MetricAndAssessmentDetailsDto();
		metricAndAssessmentDetailsDto.setId(assessment.getId());
		if (assessment.getProject() != null) {
			metricAndAssessmentDetailsDto.setProjectName(assessment.getProject().getProjectName());
			metricAndAssessmentDetailsDto.setProjectCode(assessment.getProject().getProjectCode());
		}
		if (assessment.getAccount() != null) {
			metricAndAssessmentDetailsDto.setAccountName(assessment.getAccount().getAccountName());
		}
		metricAndAssessmentDetailsDto.setStatus(assessment.getSubmitStatus().name());
		metricAndAssessmentDetailsDto.setSubmittedByName(assessment.getSubmitterName());
		metricAndAssessmentDetailsDto.setSubmittedBy(assessment.getSubmitedBy());
		metricAndAssessmentDetailsDto.setSubmittedAt(assessment.getSubmitedAt());
		Template template = assessment.getTemplate();
		if(template != null) metricAndAssessmentDetailsDto.setTemplateName(template.getTemplateName());
		if (AssessmentStatus.SAVE.equals(assessment.getSubmitStatus())) {
			metricAndAssessmentDetailsDto.setStatus(AssessmentStatus.SAVE.name());
		} else if (AssessmentStatus.SUBMITTED.equals(assessment.getSubmitStatus())) {
			metricAndAssessmentDetailsDto.setStatus(AssessmentStatus.SUBMITTED.name());
		} else if (AssessmentStatus.REVIEWED.equals(assessment.getSubmitStatus())) {
			metricAndAssessmentDetailsDto.setStatus(AssessmentStatus.REVIEWED.name());
			assessment.getReviewers().stream().filter(Objects::nonNull)
			.forEach(reviewer -> metricAndAssessmentDetailsDto.setReviewerId(reviewer.getReviewerId()));
		}
		metricAndAssessmentDetailsDto.setLastUpdateAt(assessment.getUpdatedAt());
		if (!CollectionUtils.isEmpty(assessment.getReviewers())){
			Reviewer reviewer=assessment.getReviewers().get(0);
			metricAndAssessmentDetailsDto.setReviewerName(reviewer.getReviewerName());
			metricAndAssessmentDetailsDto.setReviewerAt(reviewer.getReviewerAt());
			metricAndAssessmentDetailsDto.setReviewerId(reviewer.getReviewerId());
		}
		if(Objects.nonNull(assessment.getScore())) {
			metricAndAssessmentDetailsDto.setScore(assessment.getScore());
		}
		logger.debug("Converted to toMetricAndAssessmentDetailsDto {} ",metricAndAssessmentDetailsDto);
		logger.debug("ConversationService::toMetricAndAssessmentDetailsDto()::call ended");
		return metricAndAssessmentDetailsDto;
	}

	public List<MetricAndAssessmentDetailsDto> toMetricAndAssessmentDetailsDto(List<Assessment> assessments) {
		if (CollectionUtils.isEmpty(assessments)) {
			logger.error("No Assessment found in the list: {}", assessments);
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		return assessments.stream().filter(Objects::nonNull).map(this::toMetricAndAssessmentDetailsDto).toList();
	}

	public List<CsvMetaDataDto> parseToCsvMetaDataDto(Reader reader) {
		return new CsvToBeanBuilder<CsvMetaDataDto>(reader).withType(CsvMetaDataDto.class).build().parse().stream()
				.skip(1).toList();
	}



	public Account toAccountFromMetaData( CsvMetaDataDto dto) {
		Account domain = new Account();
		domain.setAccountName(dto.getAccountName());
		domain.setCreatedAt(System.currentTimeMillis());
		domain.setUpdatedAt(System.currentTimeMillis());
		domain.setAccountCode(dto.getAccountCode());
		domain.setAccountManager(dto.getAccountManager());
		return domain;

	}

	public Project toProjectFromMetaData(Map<Long, Account> accountById,CsvMetaDataDto dto,Long id) {
		Project domain = new Project();
		domain.setId(id);
		domain.setAccount(accountById.get(dto.getAccountId()));
		domain.setStartDate(dto.getStartDate());
		domain.setEndDate(dto.getEndDate());
		domain.setProjectName(dto.getProject());
		domain.setManagerName(StringUtils.isBlank(dto.getDeliveryManager())?"NA":dto.getDeliveryManager());
		domain.setCreatedAt(LocalDate.now());
		domain.setUpdatedAt(LocalDate.now());
		domain.setPortfolio(dto.getPortfolio());
		domain.setDeliveryPartner(dto.getDeliveryPartner());
		domain.setStatus(true);
		domain.setGrowthPartner(dto.getGrowthPartner());
		domain.setGrowthPartnerEmail(dto.getGrowthPartnerEmail());
		domain.setProjectBillingType(dto.getProjectBillingType());
		domain.setEngagementType(dto.getEngagementType());
		domain.setDeliveryManager(dto.getDeliveryManager());
		domain.setDeliveryManagerEmail(dto.getDeliveryManagerEmail());
		domain.setAccountManager(dto.getAccountManager());
		domain.setAccountManagerEmail(dto.getAccountManagerEmail());
		domain.setProjectCode(StringUtils.isBlank(dto.getProjectCode())?"NA":dto.getProjectCode());
		domain.setUpdatedBy("System Admin");
		return domain;
	}

	public ProjectType toProjectTypeFromMetaData(CsvMetaDataDto dto) {
		ProjectType domain = new ProjectType();
		domain.setProjectTypeName(dto.getProjectBillingType());
		return domain;
	}

	public Project toProject(ProjectDto projectDto) {
		logger.debug("ConversationService::toProject()::call started");
		Project project = modelMapper.map(projectDto, Project.class);
		logger.debug("Converted to project- {}", project);
		logger.debug("ConversationService::toProject()::call ended");
		return project;
	}

	public List<Project> toProjectList(List<ProjectDto> projectDto) {
		logger.debug("ConversationService::toProjectList()::call started");
		if (CollectionUtils.isEmpty(projectDto)) {
			throw new ProjectNotFoundException(PROJECT_NOT_FOUND);
		}
		List<Project> toProjectList = projectDto.stream().filter(Objects::nonNull).map(this::toProject).toList();
		logger.debug("Converted to toProjectList- {}", toProjectList);
		logger.debug("ConversationService::toProjectList()::call ended");
		return toProjectList;
	}
	public FAQCategoryDto toFAQCategoryDto(FAQCategory faqCategory) {
		logger.debug("ConversationService::toFAQCategoryDto()::call started");
		FAQCategoryDto faqCategorydto = modelMapper.map(faqCategory, FAQCategoryDto.class);
		logger.debug("Converted toFAQCategoryDto- {}", faqCategorydto);
		logger.debug("ConversationService::toFAQCategoryDto()::call ended");
		return faqCategorydto;
	}

	public List<FAQCategoryDto> toFAQCategoryDtoList(List<FAQCategory> faqCategories) {
		logger.debug("ConversationService::toFAQCategoryDtoList()::call started");
		if (CollectionUtils.isEmpty(faqCategories)) {
			throw new CustomException(CATEGORY_NOT_FOUND,HttpStatus.NOT_FOUND);
		}
		List<FAQCategoryDto> toFAQCategoryDtoList = faqCategories.stream().filter(Objects::nonNull).map(this::toFAQCategoryDto).toList();
		logger.debug("Converted to toFAQCategoryDtoList- {}", toFAQCategoryDtoList);
		logger.debug("ConversationService::toFAQCategoryDtoList()::call ended");
		return toFAQCategoryDtoList;
	}

	public ProjectType toProjectType(ProjectTypeDto projectTypeDto) {
		logger.debug("ConversationService::toProjectType()::call started");
		ProjectType projectType = modelMapper.map(projectTypeDto, ProjectType.class);
		logger.debug("Converted to projectType- {}", projectType);
		logger.debug("ConversationService::projectType()::call ended");
		return projectType;
	}

	public List<ProjectType> toProjectTypeList(List<ProjectTypeDto> projectTypeDtos) {
		logger.debug("ConversationService::toProjectTypeList()::call started");
		if (CollectionUtils.isEmpty(projectTypeDtos)) {
			throw new CustomException(PROJECT_TYPE_NOT_FOUND, HttpStatus.OK);
		}
		List<ProjectType> toProjectTypeList = projectTypeDtos.stream().filter(Objects::nonNull).map(this::toProjectType).toList();
		logger.debug("Converted to toProjectTypeList- {}", toProjectTypeList);
		logger.debug("ConversationService::toProjectTypeList()::call ended");
		return toProjectTypeList;
	}


	public Account toAccount(AccountDto projectTypeDto) {
		logger.debug("ConversationService::toAccount()::call started");

		Account account = modelMapper.map(projectTypeDto, Account.class);
		logger.debug("Converted to toAccount- {}", account);
		logger.debug("ConversationService::toAccount()::call ended");
		return account;
	}

	public List<Account> toAccountList(List<AccountDto> accountList) {
		logger.debug("ConversationService::toAccountList()::call started");

		if (CollectionUtils.isEmpty(accountList)) {
			throw new AccountsNotFoundException(ACCOUNT_NOT_FOUND);
		}
		List<Account> toAccountList = accountList.stream().filter(Objects::nonNull).map(this::toAccount).toList();
		logger.debug("Converted to toAccountList- {}", toAccountList);
		logger.debug("ConversationService::toAccountList()::call ended");
		return toAccountList;
	}




	public ProjectDto toProjectDto(Project project) {
		ProjectDto dto = new ProjectDto();
		dto.setId(project.getId());
		dto.setProjectName(project.getProjectName());
		dto.setManagerName(project.getManagerName());
		dto.setStartDate(project.getStartDate());
		dto.setEndDate(project.getEndDate());
		dto.setUpdatedBy(project.getUpdatedBy());
		dto.setStatus(project.isStatus());
		dto.setCreatedAt(project.getCreatedAt());
		dto.setAccountId(project.getAccount().getId());
		return dto;
	}


	public List<MetricAndAssessmentReportDetails> toReportDetailsDtos(List<Assessment> domains) {
		logger.debug("ConversationService::toReportDetailsDtos()::call started");
		if (CollectionUtils.isEmpty(domains)) {
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		logger.debug("ConversationService::toReportDetailsDtos()::call end");
		return domains.stream().filter(Objects::nonNull).map(this::toReportDetailsDto).toList();
	}
	public MetricAndAssessmentReportDetails toReportDetailsDto(Assessment assessment) {
		logger.debug("ConversationService::toReportDetailsDto()::call started");
		if(Objects.isNull(assessment)) {
			throw new AssessmentNotFoundException(ASSESSMENT_NOT_FOUND);
		}
		MetricAndAssessmentReportDetails dto = new MetricAndAssessmentReportDetails();
		dto.setId(assessment.getId());
		dto.setAccountName(assessment.getAccount().getAccountName());
		dto.setProjectCode(assessment.getProject().getProjectCode());
		dto.setProjectName(assessment.getProject().getProjectName());
		if(!CollectionUtils.isEmpty(assessment.getReviewers())) {
			dto.setReviewerName(assessment.getReviewers().get(0).getReviewerName());
		}
		dto.setStatus(assessment.getSubmitStatus());
		dto.setSubmittedAt(assessment.getSubmitedAt());
		dto.setTemplateName(assessment.getTemplate().getTemplateName());
		dto.setScore(assessment.getScore());
		dto.setTemplateDisplayName(assessment.getTemplate().getTemplateDisplayName());
		dto.setSubmittedBy(assessment.getSubmitterName());
		dto.setSubmitterId(assessment.getSubmitedBy());
		if(assessment.getSubmitStatus().equals(AssessmentStatus.REVIEWED)) {
			assessment.getReviewers().stream().filter(Objects::nonNull)
			.forEach(reviewer -> dto.setReviewerId(reviewer.getReviewerId()));
		}
		logger.debug("ConversationService::toReportDetailsDto()::Data::{}",dto);
		logger.debug("ConversationService::toReportDetailsDto()::call end");
		return dto;
	}
	public List<UserFilterDto> toUserFilterDtos(List<User> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new UserNotFoundException(USER_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toUserFilterDto)
				.toList();

	}
	public UserFilterDto toUserFilterDto(User user){
		UserFilterDto userFilterDto=new UserFilterDto();
		userFilterDto.setName(user.getName());
		userFilterDto.setOid(user.getOid());
		userFilterDto.setRole(user.getRole());
		return userFilterDto;
	}

}
