package com.maveric.digital.service;

import java.util.*;
import java.util.stream.Collectors;

import com.maveric.digital.responsedto.*;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.MetricNotFoundException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.model.MetricTemplate;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.model.embedded.MetricProjectCategory;
import com.maveric.digital.model.embedded.MetricTemplateQuestionnaire;
import com.maveric.digital.model.embedded.ProjectCategory;
import com.maveric.digital.model.embedded.TemplateQuestionnaire;
import com.maveric.digital.repository.MetricTemplateRepository;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.utils.ServiceConstants;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
@RequiredArgsConstructor
@Slf4j
public class MetricTemplateServiceImpl implements MetricTemplateService {

    private final MetricConversationService metricConversationService;
    private final MetricTemplateRepository metricTemplateRepository;
    private final ProjectTypeRepository projectTypeRepository;
    public static final String METRIC_NOT_FOUND = "Metric Not Found";
    @Value("${id}")
    private Long projectTypeId2;
    private final Random random = new Random();


    @Override
    public MetricTemplateDto createMetricTemplate(MetricTemplateSaveRequestDto metricTemplateSaveRequestDto) {
        try {
            log.debug("MetricTemplateServiceImpl::createMetricTemplate() call started");
            MetricTemplateDto metricTemplateDto = metricConversationService.toMetricTemplateDtoFromJsonString(metricTemplateSaveRequestDto.getTemplateData());
            log.debug("MetricTemplateDto from metricTemplateJson Data :: {}", metricTemplateDto);
            MetricTemplate metricTemplate = this.metricConversationService.toMetricTemplate(metricTemplateDto);
            log.debug("Converted to MetricTemplate::{}", metricTemplate);
            if (metricTemplateRepository.findMetricTemplateByTemplateNameAndIsActiveTrue(metricTemplateSaveRequestDto.getTemplateName()) != null) {
                log.debug("Duplicate MetricTemplate name found -{}", metricTemplate.getTemplateName());
                throw new CustomException(String.format("Duplicate MetricTemplate name found for template name {%s}", metricTemplateSaveRequestDto.getTemplateName()), HttpStatus.OK);
            }
            metricTemplate.setVersion(ServiceConstants.DEFAULT_TEMPLATE_VERSION);
            metricTemplate.setIsActive(true);
            metricTemplate.setTemplateFrequency(metricTemplateSaveRequestDto.getTemplateFrequency());
            prepareMetricTemplate(metricTemplateSaveRequestDto, metricTemplate);
            metricTemplate = metricTemplateRepository.save(metricTemplate);
            log.debug("MetricTemplate after saving in  DB {}", metricTemplate);
            MetricTemplateDto dto = this.metricConversationService.toMetricTemplateDto(metricTemplate);
            log.debug("Converted to MetricTemplateDto {}", dto);
            log.debug("MetricTemplateServiceImpl::createTemplate() call ended");
            return dto;
        } catch (DataIntegrityViolationException | ConstraintViolationException | CustomException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResourceCreationException(String.format("Error {%s} saving Metric Template {%s}", ex, metricTemplateSaveRequestDto));

        }
    }


    private void prepareMetricTemplate(MetricTemplateSaveRequestDto metricTemplateSaveRequestDto, MetricTemplate metricTemplate) {
        List<ProjectType> projectTypes = this.projectTypeRepository.findByIdIn(metricTemplateSaveRequestDto.getProjectTypes());
        if (CollectionUtils.isEmpty(projectTypes)) {
            log.debug("ProjectType list from DB  is empty");
            throw new CustomException(String.format("ProjectType list is Empty for projectTypes :{%s}", metricTemplateSaveRequestDto.getProjectTypes()), HttpStatus.OK);
        }
        log.debug(" projectTypes list getting from DB : {}", projectTypes);
        metricConversationService.populateMetricTemplateForSaveTemplate(projectTypes, metricTemplateSaveRequestDto, metricTemplate);
        this.populateMetricTemplateQuestionnaireList(metricTemplate.getProjectCategory());

    }
    
	

	private void populateMetricTemplateQuestionnaireList(List<MetricProjectCategory> projectCategory) {
		projectCategory.stream().flatMap(obj -> obj.getTemplateQuestionnaire().stream())
				.forEach(this::populateMetricTemplateQuestionnaire);
	}

	private void populateMetricTemplateQuestionnaire(MetricTemplateQuestionnaire templateQuestionnaire) {
		templateQuestionnaire.setQuestionId(Math.abs(random.nextInt(1000000000)));
	}


  @Override
  public List<MetricTemplateDetailsDto> getMetricTemplates() {
    log.debug("MetricTemplateServiceImpl::getMetricTemplates() call started");
      Sort sortByCreatedAtDesc = Sort.by(Sort.Order.desc("createdAt"));
      List<MetricTemplate> metricTemplateList = metricTemplateRepository.findAll(sortByCreatedAtDesc);
    log.debug("templateList::{}", metricTemplateList);
    if (CollectionUtils.isEmpty(metricTemplateList)) {
      log.error("MetricTemplateList not found ");
      throw new MetricNotFoundException(METRIC_NOT_FOUND);
    }
    List<MetricTemplateDetailsDto> metricTemplateDetailsDtoList = metricConversationService.toMetricTemplateDetailsDtos(metricTemplateList);
    log.debug("Convert to toMetricTemplateDetailsDtos::{}", metricTemplateDetailsDtoList);
    log.debug("MetricTemplateServiceImpl::getMetricTemplates() call ended");
    return metricTemplateDetailsDtoList;
  }


    @Override
    public MetricTemplateDto updateMetricTemplateStatus(Long id, Boolean isActive) {
        log.debug("MetricTemplateServiceImpl::updateMetricTemplateStatus() call started");
        Optional<MetricTemplate> template = metricTemplateRepository.findById(id);
        if (template.isEmpty()) {
            log.error("Template not found for id  -{}", id);
            throw new TemplateNotFoundException("Template not found for id-" + id);
        }
        MetricTemplate metricTemplate = template.get();
        metricTemplate.setIsActive(isActive);
        metricTemplateRepository.save(metricTemplate);
        log.debug("MetricTemplateServiceImpl::updateMetricTemplateStatus() call end");
        return metricConversationService.toMetricTemplateDto(metricTemplate);
    }

    @Override
    public MetricTemplateDto getMetricTemplateById(Long metricTemplateId) {
        log.debug("MetricTemplateServiceImpl :: getMetricTemplateById() call started");

        Optional<MetricTemplate> metricTemplateOpt = metricTemplateRepository.findById(metricTemplateId);
        if (metricTemplateOpt.isEmpty()) {
            throw new CustomException(String.format("MetricTemplate  not found for metricTemplateId-%s", metricTemplateId), HttpStatus.OK);
        }
        MetricTemplate metricTemplate = metricTemplateOpt.get();
        log.debug("MetricTemplate from DB {} ", metricTemplate);
        MetricTemplateDto metricTemplateDto = metricConversationService.toMetricTemplateDto(metricTemplate);
        log.debug("Converted into MetricTemplateDto {}", metricTemplateDto);
        log.debug("MetricTemplateServiceImpl :: getMetricTemplateById() call ended");
        return metricTemplateDto;
    }
    @Override
    public Optional<List<MetricTemplateInfo>> getMetricTemplateInfoByProjectTypeId(Long projectTypeId) {
        log.debug("MetricTemplateServiceImpl :: getMetricTemplateInfoByProjectTypeId() call started");
        if (Objects.isNull(projectTypeId)) {
            throw new IllegalArgumentException("ProjectTypeId cannot be null");
        }
        List<MetricTemplate> finalList=new ArrayList<>();
        Optional<List<MetricTemplate>> metricTemplateList=callRepo(projectTypeId);
        Optional<List<MetricTemplate>> metricTemplateList1=callRepo(projectTypeId2);
        if(metricTemplateList.isPresent() && !CollectionUtils.isEmpty(metricTemplateList.get())) {
        	finalList.addAll(metricTemplateList.get());	
        }
        if(metricTemplateList1.isPresent() && !CollectionUtils.isEmpty(metricTemplateList1.get())) {
        	finalList.addAll(metricTemplateList1.get());	
        }
        
        if(CollectionUtils.isEmpty(finalList)) {
            throw new MetricNotFoundException(METRIC_NOT_FOUND);
        }
        log.debug("MetricTemplate from DB {}", finalList);
        Set<MetricTemplateInfo> uniqueTemplates = finalList.stream()
                .map(metricTemplate -> new MetricTemplateInfo(metricTemplate.getId(), metricTemplate.getTemplateName()))
                .sorted(Comparator.comparing(MetricTemplateInfo::getTemplateName,String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<MetricTemplateInfo> sortedTemplateList = List.copyOf(uniqueTemplates);

        log.debug("Converted TemplateInfo List {}", sortedTemplateList);
        log.debug("ProjectServiceImpl :: getProjectsInfoByAccountId() call ended");

        return Optional.of(sortedTemplateList);
    }
    private Optional<List<MetricTemplate>> callRepo(Long projectTypeId){
        return metricTemplateRepository.findByIsActiveTrueAndProjectTypes(projectTypeId);
    }


    @Override
    public MetricTemplateDto previewMetricTemplate(MetricTemplateSaveRequestDto metricTemplateSaveRequestDto) {
        try {
            log.debug("MetricTemplateServiceImpl::previewMetricTemplate() call started");
            MetricTemplateDto metricTemplateDto = metricConversationService.toMetricTemplateDtoFromJsonString(metricTemplateSaveRequestDto.getTemplateData());
            log.debug("MetricTemplateDto from metricTemplateJson Data :: {}", metricTemplateDto);
            MetricTemplate metricTemplate = this.metricConversationService.toMetricTemplate(metricTemplateDto);
            log.debug("Converted to MetricTemplate::{}", metricTemplate);
            MetricTemplateDto dto = this.metricConversationService.toMetricTemplateDto(metricTemplate);
            log.debug("Converted to MetricTemplateDto {}", dto);
            log.debug("MetricTemplateServiceImpl::reviewMetricTemplate() call ended");
            return dto;
        } catch (DataIntegrityViolationException | ConstraintViolationException | CustomException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResourceCreationException(String.format("Error {%s} review Metric Template {%s}", ex, metricTemplateSaveRequestDto));

        }
    }


}