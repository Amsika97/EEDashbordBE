package com.maveric.digital.service;

import com.maveric.digital.exceptions.*;
import com.maveric.digital.model.*;
import com.maveric.digital.model.embedded.ProjectCategory;
import com.maveric.digital.model.embedded.TemplateQuestionnaire;
import com.maveric.digital.repository.*;
import com.maveric.digital.responsedto.*;
import com.maveric.digital.utils.ServiceConstants;
import jakarta.validation.ConstraintViolationException;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.maveric.digital.service.ConversationService.TEMPLATE_NOT_FOUND;
import static com.maveric.digital.utils.ServiceConstants.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;
    private final ConversationService conversationService;
    private final ScoreScaleRepository scoreScaleRepository;
    private final ScoreCategoryRepository scoreCategoryRepository;
    private final ProjectRepository projectRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final MetricTemplateRepository metricTemplateRepository;
    private final MetricConversationService metricConversationService;

    public static final String AUDIT_MESSAGE="Template is successfully created";
    @Value("${id}")
    private Long projectTypeId2;

    public TemplateDto createTemplate(TemplateSaveRequestDto templateSaveRequest) {
        try {
            log.debug("TemplateServiceImpl::createTemplate() call Started");
            Template template = this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequest.getTemplateData()));
            log.debug(" converted to template::{}", template);
            if (templateRepository.findTemplateByTemplateNameAndIsActiveTrue(templateSaveRequest.getTemplateName()) != null) {
                log.error(" Duplicate Template name found -{}", template.getTemplateName());
                throw new CustomException(String.format("Duplicate Template name found for template name {%s}", templateSaveRequest.getTemplateName()), HttpStatus.BAD_REQUEST);
            }
            createTemplateData(templateSaveRequest, template);
            template.setVersion(ServiceConstants.DEFAULT_TEMPLATE_VERSION);
            template.setIsActive(true);
            template.setTemplateFrequency(templateSaveRequest.getTemplateFrequency());
            template.setCreatedAt(System.currentTimeMillis());
            template = templateRepository.save(template);
            log.debug(" Template saved in DB: Template-{}", template);
            TemplateDto dto = this.conversationService.toTemplateDto(template);
            log.debug(" convert to templateDto{}", dto);
            log.debug("ProjectController::createTemplate() call ended");
            return dto;
        } catch (DataIntegrityViolationException | ScoreScaleNotFoundException | ScoreCategoryNotFoundException |
                 ConstraintViolationException | CustomException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResourceCreationException(String.format("error {%s} saving Template {%s}", ex, templateSaveRequest));
        }
    }

    public TemplateDto updateTemplate(TemplateSaveRequestDto templateSaveRequest) {
        try {
            log.debug("TemplateServiceImpl::updateTemplate() call started");
            Template template = this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequest.getTemplateData()));
            log.debug(" Request data converted to template : {}", template);
            TemplateInfo oldActiveTemplateInfo = templateRepository.findTemplateInfoByTemplateNameAndIsActiveTrue(templateSaveRequest.getTemplateName());
            if ( Objects.isNull(oldActiveTemplateInfo)) {
                log.error("Template not found for templateName : {}", template);
                throw new TemplateNotFoundException("Template not found for templateName :" + templateSaveRequest.getTemplateName());
            }
            log.debug("oldActiveTemplateInfo {}: ", oldActiveTemplateInfo);
            createTemplateData(templateSaveRequest, template);
            template.setVersion(oldActiveTemplateInfo.getVersion()+ INCREMENT_TEMPLATE_VERSION);
            templateRepository.deActivateTemplate(oldActiveTemplateInfo.getId(), false);
            log.debug("Old template deActivated Successfully with version :{} ",oldActiveTemplateInfo.getVersion());
            template.setIsActive(true);
            template.setCreatedAt(System.currentTimeMillis());
            template = templateRepository.save(template);
            log.debug(" Template saved in DB with latest version {},{}",template.getTemplateName(), template.getVersion());
            TemplateDto dto = this.conversationService.toTemplateDto(template);
            log.debug(" Latest template convert into templateDto : {}", dto);
            log.debug("ProjectController::updateTemplate() call ended");
            return dto;
        } catch (DataIntegrityViolationException | ScoreScaleNotFoundException | ScoreCategoryNotFoundException |
                 ConstraintViolationException |TemplateNotFoundException | CustomException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResourceCreationException(String.format("error {%s} update Template {%s}", ex, templateSaveRequest));
        }
    }

    private void createTemplateData(TemplateSaveRequestDto templateSaveRequest, Template template) {
		/*
		 * Optional<ScoringScale> scoreScale =
		 * this.scoreScaleRepository.findById(templateSaveRequest.getScoreScaleId()); if
		 * (scoreScale.isEmpty()) { log.error(" ScoringScale is Empty for score id -{}",
		 * templateSaveRequest.getScoreScaleId()); throw new
		 * ScoreScaleNotFoundException(String.
		 * format("ScoringScale is Empty for score id {%s}",
		 * templateSaveRequest.getScoreScaleId())); }
		 */
       
        List<ProjectType> projectTypes = this.projectTypeRepository.findByIdIn(templateSaveRequest.getProjectTypes());
        if (CollectionUtils.isEmpty(projectTypes)) {
            log.error("projectTypes :{}", projectTypes);
            throw new CustomException(String.format("ProjectType list is Empty for projectTypes :{%s}", templateSaveRequest.getProjectTypes()),HttpStatus.BAD_REQUEST);
        }
        log.debug(" projectTypes list getting from DB : {}", projectTypes);
        List<ProjectCategory> projectCategory = template.getProjectCategory();
        log.debug(" ProjectCategory : {}", projectCategory);

        List<String> categoryNames = projectCategory.stream().flatMap(s -> s.getTemplateQuestionnaire().stream()).map(TemplateQuestionnaire::getScoreCategory).distinct().toList();
        log.debug("CategoryNames : {}", categoryNames);
        List<ScoreCategory> scoreCategories = scoreCategoryRepository.findByCategoryNameIn(categoryNames);
        if (CollectionUtils.isEmpty(scoreCategories)) {
            log.error("ScoreCategories is Empty for categoryNames :{}", categoryNames);
            throw new ScoreCategoryNotFoundException(String.format("ScoreCategories is Empty for categoryNames {%s}", categoryNames));
        }
        log.debug("ScoreCategories : {}", scoreCategories);
        conversationService.populateTemplateForSaveTemplate(null, scoreCategories,
                null, projectTypes, templateSaveRequest, template);
    }


    public TemplateDto getTemplateByTemplateName(String templateName) {
        log.debug("TemplateServiceImpl::getTemplateByTemplateName() call started");
        Template template = templateRepository.findTemplateByTemplateNameAndIsActiveTrue(templateName);
        log.debug(" template from DB  template-{}", template);
        if (Objects.isNull(template)) {
            log.error("Template not found for templateName  -{}", template);
            throw new TemplateNotFoundException("Template not found for templatename-" + templateName);
        } else {
            TemplateDto templateDto = this.conversationService.toTemplateDto(template);
            log.debug("converted to templateDto-{}", templateDto);
            log.debug("TemplateServiceImpl::getTemplateByTemplateName() call ended");
            return templateDto;
        }
    }

    @Override
    public TemplateDto getTemplateById(Long id) {
        log.debug("TemplateServiceImpl::getTemplateById() call started");
        Optional<Template> templateOpt = templateRepository.findById(id);
        log.debug(" template from DB  templateOpt-{}", templateOpt);
        if (templateOpt.isEmpty()) {
            log.error("Template not found for id  -{}", id);
            throw new TemplateNotFoundException("Template not found for id-" + id);
        } else {
            log.debug(" template from DB  template -{}", templateOpt.get());
            TemplateDto templateDto = this.conversationService.toTemplateDto(templateOpt.get());
            log.debug("converted to templateDto-{}", templateDto);
            log.debug("TemplateServiceImpl::getTemplateById() call ended");
            return templateDto;
        }
    }


    public List<TemplateDto> getTemplatesByProjectType(Long projectTypeId) {
        log.debug("TemplateServiceImpl::getTemplatesByProjectType() call started");
        List<Template> templateList = templateRepository.findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(projectTypeId);
        log.debug(" templateList::{}", templateList);
        if (CollectionUtils.isEmpty(templateList)) {
            log.error(" templateList Not found for projectType-{}", projectTypeId);
            throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
        }
        List<TemplateDto> templateDtoList = conversationService.toTemplateDtos(templateList);
        log.debug(" convert to templateDtoList::{}", templateDtoList);

        log.debug("ProjectController::getTemplatesByProjectType() call ended");
        return templateDtoList;

    }

    public List<AssessmentTemplateDto> getAssessmentTemplates(){
        log.debug("TemplateServiceImpl::getAssessmentTemplates() call started");
        Sort sortByCreatedAtDesc = Sort.by(Sort.Order.desc("createdAt"));
        List<Template> templateList = templateRepository.findAll(sortByCreatedAtDesc);
        log.debug("templateList::{}", templateList);
        if (CollectionUtils.isEmpty(templateList)) {
            log.error("TemplateList not found ");
            throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
        }
        List<AssessmentTemplateDto> assessmentTemplateDtoList = conversationService.toAssessmentTemplateDtos(templateList);
        log.debug("Convert to assessmentTemplateDtoList::{}", assessmentTemplateDtoList);
        log.debug("TemplateServiceImpl::getAssessmentTemplates() call ended");
        return assessmentTemplateDtoList;
    }
    @Override
    public List<TemplateInfo> getTemplateInfoByProjectType(Long projectTypeId) {
        log.debug("TemplateServiceImpl::getTemplateInfoByProjectType() call started");
        List<TemplateInfo> finalList=new ArrayList<>();
        List<Template> templateInfoList=this.templateRepository.findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(projectTypeId);
        List<Template> templateInfoList1=this.templateRepository.findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(projectTypeId2);
		if (!CollectionUtils.isEmpty(templateInfoList)) {
			finalList.addAll(conversationService.toTemplateInfo(templateInfoList));
		}
		if (!CollectionUtils.isEmpty(templateInfoList1)) {
			finalList.addAll(conversationService.toTemplateInfo(templateInfoList1));
		}

		 Set<TemplateInfo> uniqueTemplates = finalList.stream()
                .sorted(Comparator.comparing(TemplateInfo::getTemplateName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<TemplateInfo> sortedTemplateList = List.copyOf(uniqueTemplates);

        if (CollectionUtils.isEmpty(finalList)) {
            log.error(" TemplateInfoList Not found for projectTypeId-{}", projectTypeId);
            throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
        }
        log.debug("ProjectController::getTemplateInfoByProjectType() call ended");
        return sortedTemplateList;

    }
   
    @Override
    public List<TemplateInfo> getTemplateInfoBySelectedFilters( Long projectId, Long projectTypeId) {
        log.debug("TemplateServiceImpl::getTemplateBySelectedFilters() call started");
        List<TemplateInfo> templateInfoList = templateRepository.findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc( projectId, projectTypeId);
        log.debug("TemplateInfoList from DB {}", templateInfoList);
        if (CollectionUtils.isEmpty(templateInfoList)) {
            log.error("TemplateInfoList Not found for projectId :{},projectTypeId :{}",projectId,projectTypeId);
            throw new TemplateNotFoundException(TEMPLATE_NOT_FOUND);
        }
        log.debug("TemplateServiceImpl::getTemplateBySelectedFilters() call ended");
        return templateInfoList;
    }

	@Override
	public List<Template> getAllTemplateInfos() {
		log.debug("TemplateServiceImpl::getAllTemplateInfos() call started");
        Sort sortByCreatedAtDesc = Sort.by(Sort.Order.desc("createdAt"));
        List<Template> templates = templateRepository.findByIsActiveTrue(sortByCreatedAtDesc);
		log.debug("TemplateServiceImpl::getAllTemplateInfos() data::{}", templates);
		log.debug("TemplateServiceImpl::getAllTemplateInfos() call end");
		return templates;
	}
    @Override
    public TemplateInfo discardTemplate(Boolean isActive, Long id) {
        log.debug("TemplateServiceImpl::discardTemplate() call started");
        Optional<Template> templateOpt = templateRepository.findById(id);
        if(templateOpt.isEmpty()){
            log.error("Template not found for id  -{}", id);
            throw new TemplateNotFoundException("Template not found for id-" + id);
        }
        Template template = templateOpt.get();
        template.setIsActive(isActive);
        templateRepository.save(template);
        log.debug("TemplateServiceImpl::getAllTemplateInfos() call end");
        return this.conversationService.toTemplateInfo(template);
    }

    public TemplateDto createPreview(TemplateSaveRequestDto templateSaveRequest) {
        try {
            log.debug("TemplateServiceImpl::createPreview() call started");
            Template template = this.conversationService.toTemplate(conversationService.toTemplateDtoFromJsonString(templateSaveRequest.getTemplateData()));
            log.debug(" converted to template::{}", template);
            createTemplateData(templateSaveRequest, template);
            TemplateDto dto = this.conversationService.toTemplateDto(template);
            log.debug(" convert to templateDto{}", dto);
            log.debug("ProjectController::createPreview() call ended");
            return dto;
        } catch (DataIntegrityViolationException | ScoreScaleNotFoundException | ScoreCategoryNotFoundException |
                 ConstraintViolationException | CustomException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResourceCreationException(String.format("error {%s} saving Template {%s}", ex, templateSaveRequest));
        }
    }
    @Override
    public List<TemplateDisplayDto> getUniqueTemplateDisplayNames(final String  filterName) {
        log.info("TemplateServiceImpl::getUniqueTemplateDisplayNames() call started");
        List<TemplateDisplayDto> uniqueTemplateDisplayNamesList=new ArrayList<>();
        log.info("filterName {}",filterName);
        switch (filterName==null?BOTH:filterName.toLowerCase()) {
            case ASSESSMENTS -> getUniqueTemplateDisplayNamesAssessments(uniqueTemplateDisplayNamesList);
            case METRICS -> getUniqueMetricTemplateDisplayNames(uniqueTemplateDisplayNamesList);
            default -> {
                getUniqueTemplateDisplayNamesAssessments(uniqueTemplateDisplayNamesList);
                getUniqueMetricTemplateDisplayNames(uniqueTemplateDisplayNamesList);
            }
        }
        log.info("fetched unique template display names,count: {}",uniqueTemplateDisplayNamesList.size());
        log.info("TemplateServiceImpl::getUniqueTemplateDisplayNames() call ended");
        return uniqueTemplateDisplayNamesList;
    }

    public List<TemplateDisplayDto> getUniqueTemplateDisplayNamesAssessments(List<TemplateDisplayDto> uniqueTemplateDisplayNamesList){
        log.info("TemplateServiceImpl::getUniqueTemplateDisplayNamesAssessments() call started");
        List<Template> templates = templateRepository.findDistinctByTemplateDisplayNameIsNotNull();
        List<TemplateDto> templateDtoList=conversationService.toTemplateDtos(templates);
        if(!CollectionUtils.isEmpty(templates)) {
            uniqueTemplateDisplayNamesList.addAll(
                     templateDtoList.stream()
                             .map((TemplateDto templateDto)->new TemplateDisplayDto(templateDto.getTemplateId(),templateDto.getTemplateDisplayName())).toList());
        }
        log.info("TemplateServiceImpl::getUniqueTemplateDisplayNamesAssessments() call end");
        return uniqueTemplateDisplayNamesList;
    }
    public List<TemplateDisplayDto> getUniqueMetricTemplateDisplayNames(List<TemplateDisplayDto> uniqueTemplateDisplayNamesList){
        log.info("TemplateServiceImpl::getUniqueMetricTemplateDisplayNames() call started");
        List<MetricTemplate> metricTemplates = metricTemplateRepository.findDistinctByTemplateDisplayNameIsNotNull();
        List<MetricTemplateDto> metricTemplateDtoList=metricConversationService.toMetricTemplateDtos(metricTemplates);
        if(!CollectionUtils.isEmpty(metricTemplates)) {
            uniqueTemplateDisplayNamesList.addAll(metricTemplateDtoList.stream().map((MetricTemplateDto metricTemplateDto)->new TemplateDisplayDto(metricTemplateDto.getTemplateId(),metricTemplateDto.getTemplateDisplayName())).toList());
        }
        log.info("TemplateServiceImpl::getUniqueMetricTemplateDisplayNames() call end");
        return uniqueTemplateDisplayNamesList;
    }

}