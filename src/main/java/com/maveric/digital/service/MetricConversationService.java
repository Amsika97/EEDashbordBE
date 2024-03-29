package com.maveric.digital.service;

import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.model.embedded.Reviewer;
import com.maveric.digital.projection.LineChartProjection;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.responsedto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.maveric.digital.exceptions.AssessmentNotFoundException;
import com.maveric.digital.exceptions.AssessmentProjectCategoryNotFoundException;
import com.maveric.digital.exceptions.TemplateNotFoundException;

import com.maveric.digital.model.Account;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.MetricTemplate;
import com.maveric.digital.model.Project;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricConversationService {

    public static final String METRIC_TEMPLATE_NOT_FOUND = "Metric Template not found";
    public static final String METRIC_SUBMIT_NOT_FOUND = "Submitted Metric  not found";
    private static final String PROJECT_CATEGORY_NOT_FOUND = "project Category not found";

    private final ObjectMapper mapper;



    public MetricTemplateDto toMetricTemplateDtoFromJsonString(String templateDescriptionJsonString) throws JsonProcessingException {
        log.debug("MetricConversationService::toMetricTemplateDtoFromJsonString() call started");
        log.debug("templateDescriptionJsonString {} ", templateDescriptionJsonString);
        MetricTemplateDto metricTemplateDto = mapper.readValue(templateDescriptionJsonString, MetricTemplateDto.class);
        log.debug("converted to metricTemplateDto {} ", metricTemplateDto);
        log.debug("MetricConversationService::toMetricTemplateDtoFromJsonString()call ended");
        return metricTemplateDto;
    }

    public MetricTemplate toMetricTemplate(MetricTemplateDto metricTemplateDto) {
        log.debug("MetricConversationService::toMetricTemplate() ::call started::{}", metricTemplateDto);
        MetricTemplate metricTemplate = mapper.convertValue(metricTemplateDto, MetricTemplate.class);
        log.debug("converted to MetricTemplate {} ", metricTemplate);
        log.debug("MetricConversationService::toMetricTemplate() ::call ended");
        return metricTemplate;
    }
	public MetricSubmittedDto toMetricSubmitDto(MetricSubmitted domain) {
		log.debug("MetricConversationService::toMetricSubmitDto()::Start");
		this.validatedMetricSubmit(domain);
		MetricSubmittedDto dto = new MetricSubmittedDto();
		dto.setMetricId(domain.getId());
		dto.setProjectId(domain.getProject().getId());
		dto.setReviewers(domain.getReviewers());
		dto.setSubmittedAt(domain.getSubmittedAt());
		dto.setSubmittedBy(domain.getSubmittedBy());
		dto.setSubmitterName(domain.getSubmitterName());
		dto.setSubmitStatus(domain.getSubmitStatus());
    if(!CollectionUtils.isEmpty(domain.getCategorywiseScores())) {
      dto.setCategorywiseScores(domain.getCategorywiseScores());
    }
    dto.setScore(domain.getScore());
		dto.setMetricTemplateId(domain.getTemplate().getId());
		dto.setTemplateName(domain.getTemplate().getTemplateName());
		dto.setTemplateUploadedUserId(domain.getTemplate().getTemplateUploadedUserId());
		dto.setTemplateUploadedUserName(domain.getTemplate().getTemplateUploadedUserName());
		dto.setProjectCategory(domain.getProjectCategory());
		dto.setDescription(domain.getDescription());
		dto.setAccountId(domain.getAccount().getId());
		//dto.setBusinessUnitId(domain.getBusinessUnit().getId());
		dto.setProjectTypeId(domain.getProjectType().getId());
        dto.setProjectName(domain.getProject().getProjectName());
        dto.setAccountName(domain.getAccount().getAccountName());
        dto.setBusinessUnitName(domain.getProject().getBusinessUnit());
        dto.setTemplateDisplayName(domain.getTemplate().getTemplateDisplayName());
        dto.setIsEdited(domain.getIsEdited());
		log.debug("MetricConversationService::toMetricSubmitDto()::End");
		return dto;

	}
	
	public MetricSubmitted toMetricSubmitted(MetricSubmittedDto dto,MetricTemplate metricTemplate,Account account
			,ProjectType projectType,Project project) {
		log.debug("MetricConversationService::toMetricSubmitted()::Start");
		MetricSubmitted domain = new MetricSubmitted();
		domain.setReviewers(dto.getReviewers());
		domain.setSubmittedAt(dto.getSubmittedAt());
		domain.setSubmittedBy(dto.getSubmittedBy());
		domain.setSubmitterName(dto.getSubmitterName());
		domain.setSubmitStatus(dto.getSubmitStatus());
    domain.setScore(dto.getScore());
		domain.setProjectCategory(dto.getProjectCategory());
		domain.setCreatedAt(System.currentTimeMillis());
		domain.setUpdatedAt(System.currentTimeMillis());
		domain.setDescription(dto.getDescription());
		domain.setTemplate(metricTemplate);
		domain.setProjectType(projectType);
		domain.setAccount(account);
		domain.setProject(project);
		log.debug("MetricConversationService::toMetricSubmitted()::End");
		return domain;



    }

    public MetricTemplateDto toMetricTemplateDto(MetricTemplate domain) {
        if (Objects.isNull(domain)) {
            throw new TemplateNotFoundException(METRIC_TEMPLATE_NOT_FOUND);
        }
        log.debug("MetricConversationService::toMetricTemplateDto()::Start");
        MetricTemplateDto dto = MetricTemplateDto.builder().build();
        dto.setTemplateName(domain.getTemplateName());
        dto.setTemplateDisplayName(domain.getTemplateDisplayName());
        dto.setTemplateId(domain.getId());
        dto.setTemplateUploadedUserName(domain.getTemplateUploadedUserName());
        dto.setProjectCategory(domain.getProjectCategory());
        dto.setTemplateUploadedUserId(domain.getTemplateUploadedUserId());
        dto.setDescription(domain.getDescription());
        log.debug("ConversationService::toMetricTemplateDto()::End");
        dto.setIsActive(domain.getIsActive());
        log.debug("ConversationService::toMetricTemplateDto()::End");
        return dto;
    }

    public List<MetricTemplateDto> toMetricTemplateDtos(List<MetricTemplate> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            throw new TemplateNotFoundException(METRIC_TEMPLATE_NOT_FOUND);
        }
        return domains.stream().filter(Objects::nonNull).map(this::toMetricTemplateDto).toList();
    }



    public void validatedMetricSubmit(MetricSubmitted domain) {
        if (Objects.isNull(domain)) {
            throw new AssessmentNotFoundException(METRIC_SUBMIT_NOT_FOUND);
        }
        if (Objects.isNull(domain.getTemplate())) {
            throw new TemplateNotFoundException(METRIC_TEMPLATE_NOT_FOUND);
        }
        if (CollectionUtils.isEmpty(domain.getProjectCategory())) {
            throw new AssessmentProjectCategoryNotFoundException(PROJECT_CATEGORY_NOT_FOUND);
        }
    }


    public void populateMetricTemplateForSaveTemplate(List<ProjectType> projectTypes, MetricTemplateSaveRequestDto metricTemplateSaveRequestDto, MetricTemplate metricTemplate) {
        metricTemplate.setDescription(metricTemplateSaveRequestDto.getDescription());
        metricTemplate.setCreatedAt(System.currentTimeMillis());
        metricTemplate.setTemplateUploadedUserId(metricTemplateSaveRequestDto.getTemplateUploadedUserId());
        metricTemplate.setTemplateUploadedUserName(metricTemplateSaveRequestDto.getTemplateUploadedUserName());
        metricTemplate.setProjectTypes(projectTypes);
        metricTemplate.setTemplateName(metricTemplateSaveRequestDto.getTemplateName());
    }

    public MetricTemplateDetailsDto toMetricTemplateDetailsDto(MetricTemplate domain) {
        if (Objects.isNull(domain)) {
            throw new MetricNotFoundException(METRIC_TEMPLATE_NOT_FOUND);
        }
        log.debug("MetricConversationService::toMetricTemplateDetailsDto()::Start");
        MetricTemplateDetailsDto dto = MetricTemplateDetailsDto.builder().build();
        dto.setTemplateName(domain.getTemplateName());
        dto.setTemplateId(domain.getId());
        dto.setProjectType(domain.getProjectTypes().stream().map(ProjectType::getProjectTypeName).toList());
        dto.setTemplateUploadedUserName(domain.getTemplateUploadedUserName());
        dto.setCreatedOn(domain.getCreatedAt());
        dto.setIsActive(domain.getIsActive());
        dto.setTemplateDisplayName(domain.getTemplateDisplayName());
        dto.setTemplateFrequency(domain.getTemplateFrequency());
        log.debug("MetricConversationService::toMetricTemplateDetailsDto()::End");
        return dto;

    }

    public List<MetricTemplateDetailsDto> toMetricTemplateDetailsDtos(List<MetricTemplate> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            throw new MetricNotFoundException(METRIC_TEMPLATE_NOT_FOUND);
        }
        return domains.stream().filter(Objects::nonNull).map(this::toMetricTemplateDetailsDto).toList();
    }

	public List<LineChartDto>  tosubmitmetriclinechartdto(List<LineChartProjection> projections){
		return projections.stream().filter(Objects::nonNull).map(this::toSubmitMetricLineChartdto)
				.collect(Collectors.toList());
	}
	public LineChartDto toSubmitMetricLineChartdto(LineChartProjection projection){
		LineChartDto dto = new LineChartDto();
		dto.setCount(projection.getCount());
		dto.setDate(projection.getId());
		return dto;
	}
  public MetricReviewDto toMetricReviewDto(MetricSubmitted domain){
    log.debug("MetricConversationService::toMetricReviewDto():: call started");
    if (Objects.isNull(domain)) {
      log.error("MetricConversationService::toMetricReviewDto():: No Metric found");
      throw new MetricNotFoundException(METRIC_SUBMIT_NOT_FOUND);
    }
    MetricReviewDto dto=new MetricReviewDto();
    dto.setId(domain.getId());
    dto.setSubmitterName(domain.getSubmitterName());
    dto.setAccountName(domain.getAccount().getAccountName());
    dto.setProjectName(domain.getProject().getProjectName());
    dto.setSubmittedAt(domain.getSubmittedAt());
    dto.setProjectCode(domain.getProject().getProjectCode());
    dto.setDeliveryUnit(domain.getProject().getBusinessUnit());
    dto.setSubmitterId(domain.getSubmittedBy());
    if(Objects.nonNull(domain.getProjectType()) && Objects.nonNull(domain.getProjectType().getProjectTypeName())) {
    	dto.setProjectType(domain.getProjectType().getProjectTypeName());	
    }
    log.debug("MetricConversationService::toMetricReviewDto():: call ended");
    return dto;
  }

  public List<MetricReviewDto> toMetricReviewDtos(List<MetricSubmitted> domains) {
    if (CollectionUtils.isEmpty(domains)) {
      log.error("No Metric found in the list: {}",domains);
      throw new MetricNotFoundException(METRIC_SUBMIT_NOT_FOUND);
    }
    return domains.stream().filter(Objects::nonNull).map(this::toMetricReviewDto).toList();
  }

    public MetricAndAssessmentDetailsDto toMetricAndAssessmentDetailsDto(MetricSubmitted metricSubmitted) {
        log.debug("MetricConversationService::toMetricAndAssessmentDetailsDto():: call started");
        MetricAndAssessmentDetailsDto metricAndAssessmentDetailsDto = new MetricAndAssessmentDetailsDto();
        metricAndAssessmentDetailsDto.setId(metricSubmitted.getId());
        metricAndAssessmentDetailsDto.setProjectCode(metricSubmitted.getProject().getProjectCode());
        metricAndAssessmentDetailsDto.setProjectName(metricSubmitted.getProject().getProjectName());
        metricAndAssessmentDetailsDto.setAccountName(metricSubmitted.getAccount().getAccountName());
        metricAndAssessmentDetailsDto.setStatus(metricSubmitted.getSubmitStatus().name());
        metricAndAssessmentDetailsDto.setSubmittedBy(metricSubmitted.getSubmittedBy());
        metricAndAssessmentDetailsDto.setSubmittedAt(metricSubmitted.getSubmittedAt());
        metricAndAssessmentDetailsDto.setSubmittedByName(metricSubmitted.getSubmitterName());
        metricAndAssessmentDetailsDto.setScore(metricSubmitted.getScore());
        MetricTemplate template = metricSubmitted.getTemplate();
        if(template != null) metricAndAssessmentDetailsDto.setTemplateName(template.getTemplateName());
        if (AssessmentStatus.SAVE.equals(metricSubmitted.getSubmitStatus())) {
            metricAndAssessmentDetailsDto.setStatus(AssessmentStatus.SAVE.name());
        } else if (AssessmentStatus.SUBMITTED.equals(metricSubmitted.getSubmitStatus())) {
            metricAndAssessmentDetailsDto.setStatus(AssessmentStatus.SUBMITTED.name());
        }
        else if (AssessmentStatus.REVIEWED.equals(metricSubmitted.getSubmitStatus())) {
            metricAndAssessmentDetailsDto.setStatus(AssessmentStatus.REVIEWED.name());
            metricSubmitted.getReviewers().stream().filter(Objects::nonNull)
            .forEach(reviewer -> metricAndAssessmentDetailsDto.setReviewerId(reviewer.getReviewerId()) );
        }
        metricAndAssessmentDetailsDto.setLastUpdateAt(metricSubmitted.getUpdatedAt());
        if (!CollectionUtils.isEmpty(metricSubmitted.getReviewers())){
            Reviewer reviewer=metricSubmitted.getReviewers().get(0);
            metricAndAssessmentDetailsDto.setReviewerName(reviewer.getReviewerName());
            metricAndAssessmentDetailsDto.setReviewerAt(reviewer.getReviewerAt());
            metricAndAssessmentDetailsDto.setReviewerId(reviewer.getReviewerId());
        }
        log.debug("MetricAndAssessmentDetailsDto {}", metricAndAssessmentDetailsDto);
        log.debug("MetricConversationService::toMetricAndAssessmentDetailsDto():: call ended");
        return metricAndAssessmentDetailsDto;
    }

    public List<MetricAndAssessmentDetailsDto> toMetricSubmitted(List<MetricSubmitted> metricSubmitteds) {
        if (CollectionUtils.isEmpty(metricSubmitteds)) {
            log.error("No MetricSubmitted found in the list: {}", metricSubmitteds);
            throw new MetricNotFoundException("No MetricSubmitted found");	
        }
        return metricSubmitteds.stream().filter(Objects::nonNull).map(this::toMetricAndAssessmentDetailsDto).toList();
    }

    public MetricAndAssessmentReportDetails toMetricReportDetail(MetricSubmitted metricSubmitted) {
        log.debug("MetricConversationService::toMetricReportDetail():: call started");
        MetricAndAssessmentReportDetails metricAndAssessmentReportDetails = new MetricAndAssessmentReportDetails();
        metricAndAssessmentReportDetails.setId(metricSubmitted.getId());
        if (ObjectUtils.isNotEmpty(metricSubmitted.getProject())) {
            metricAndAssessmentReportDetails.setProjectCode(metricSubmitted.getProject().getProjectCode());
            metricAndAssessmentReportDetails.setProjectName(metricSubmitted.getProject().getProjectName());
        }
        if (ObjectUtils.isNotEmpty(metricSubmitted.getAccount())) {
            metricAndAssessmentReportDetails.setAccountName(metricSubmitted.getAccount().getAccountName());
        }
        metricAndAssessmentReportDetails.setStatus(metricSubmitted.getSubmitStatus());
        metricAndAssessmentReportDetails.setSubmittedAt(metricSubmitted.getSubmittedAt());
        if (!CollectionUtils.isEmpty(metricSubmitted.getReviewers())) {
            Reviewer reviewer = metricSubmitted.getReviewers().get(0);
            metricAndAssessmentReportDetails.setReviewerName(reviewer.getReviewerName());
        }

        if (ObjectUtils.isNotEmpty(metricSubmitted.getTemplate())) {
            metricAndAssessmentReportDetails.setTemplateName(metricSubmitted.getTemplate().getTemplateName());
            metricAndAssessmentReportDetails.setTemplateDisplayName(metricSubmitted.getTemplate().getTemplateDisplayName());
            
        }
        metricAndAssessmentReportDetails.setScore(metricSubmitted.getScore());
        metricAndAssessmentReportDetails.setSubmittedBy(metricSubmitted.getSubmitterName());
        metricAndAssessmentReportDetails.setSubmitterId(metricSubmitted.getSubmittedBy());
        if(metricSubmitted.getSubmitStatus().equals(AssessmentStatus.REVIEWED)) {
        	metricSubmitted.getReviewers().stream().filter(Objects::nonNull)
        	.forEach(reviewer -> metricAndAssessmentReportDetails.setReviewerId(reviewer.getReviewerId()));
        }
        log.debug("MetricAndAssessmentReportDetails {}", metricAndAssessmentReportDetails);
        log.debug("MetricConversationService::toMetricReportDetail():: call ended");
        return metricAndAssessmentReportDetails;
    }

    public List<MetricAndAssessmentReportDetails> toMetricReportDetails(List<MetricSubmitted> metricSubmitteds) {
        log.debug("MetricConversationService::toMetricReportDetails():: call started");
        if (CollectionUtils.isEmpty(metricSubmitteds)) {
            log.error("No MetricSubmitted found in the list: {}", metricSubmitteds);
            throw new MetricNotFoundException(METRIC_SUBMIT_NOT_FOUND);
        }

        List<MetricAndAssessmentReportDetails> metricAndAssessmentReportDetails = metricSubmitteds.stream().filter(Objects::nonNull).map(this::toMetricReportDetail).toList();
        log.debug("MetricAndAssessmentReportDetails {}", metricAndAssessmentReportDetails);
        log.debug("MetricConversationService::toMetricReportDetails():: call ended");
        
        return metricAndAssessmentReportDetails;
    }
    
    public List<AssessmentsSubmittedDashboardDto> toMetricSubmittedSubmittedDashboardDtos(List<MetricSubmitted> domains) {
		if (CollectionUtils.isEmpty(domains)) {
			throw new AssessmentNotFoundException(METRIC_SUBMIT_NOT_FOUND);
		}
		return domains.stream().filter(Objects::nonNull).map(this::toMetricSubmittedSubmittedDashboardDto)
				.collect(Collectors.toList());

	}

	public AssessmentsSubmittedDashboardDto toMetricSubmittedSubmittedDashboardDto(MetricSubmitted metricSubmitted) {

		AssessmentsSubmittedDashboardDto dto = new AssessmentsSubmittedDashboardDto();
		dto.setId(metricSubmitted.getId());
		dto.setSubmitterName(metricSubmitted.getSubmitterName());
		dto.setSubmitterId(metricSubmitted.getSubmittedBy());
		MetricTemplate template = metricSubmitted.getTemplate();
		if(template != null) dto.setTemplateName(template.getTemplateName());
		List<Reviewer> reviewers = metricSubmitted.getReviewers();
		if (reviewers != null && !reviewers.isEmpty()) {
			Reviewer firstReviewer = reviewers.get(0);
			dto.setReviewerName(firstReviewer.getReviewerName());
			dto.setReviewerId(firstReviewer.getReviewerId());
		} else {
			dto.setReviewerName("");
		}

	/*	BusinessUnit businessUnit = metricSubmitted.getBusinessUnit();
		if (businessUnit != null) {
			dto.setBusinessUnitName(businessUnit.getName());
		} else {
			dto.setBusinessUnitName("");
		}*/
		dto.setSubmitStatus(metricSubmitted.getSubmitStatus());
		dto.setSubmitedAt(metricSubmitted.getSubmittedAt());
        dto.setScore(metricSubmitted.getScore());
		Project project= metricSubmitted.getProject();
		if(project!=null){
			dto.setProjectName(project.getProjectName());
			dto.setProjectCode(project.getProjectCode());
		}else {
			dto.setProjectName("");
		}
		Account account=metricSubmitted.getAccount();
		if(account!=null){
			dto.setAccountName(account.getAccountName());
		}else {
			dto.setAccountName("");
		}
		return dto;
	}

}
