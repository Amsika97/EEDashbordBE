package com.maveric.digital.controller;

import java.util.Collections;
import java.util.List;

import com.maveric.digital.responsedto.MetricTemplateInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maveric.digital.responsedto.MetricTemplateDetailsDto;
import com.maveric.digital.responsedto.MetricTemplateDto;
import com.maveric.digital.responsedto.MetricTemplateSaveRequestDto;
import com.maveric.digital.service.MetricTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping({"/v1"})
@RequiredArgsConstructor
@Slf4j
public class MetricTemplateController {

    private final MetricTemplateService metricTemplateService;

	@Operation(
            summary = "Create a Metric Template"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Created a Metric Template successfully"
    ), @ApiResponse(
            responseCode = "204",
            description = "Provided Metric Template data is invalid"
    )})

    @PostMapping({"/metric/template/create"})
    public ResponseEntity<MetricTemplateDto> createMetricTemplate(@RequestBody @Valid MetricTemplateSaveRequestDto metricTemplateSaveRequestDto) {
        log.debug("TemplateController::createMetricTemplate() call started");
        log.debug("metricTemplateSaveRequestDto details {}", metricTemplateSaveRequestDto);
        MetricTemplateDto metricTemplate = metricTemplateService.createMetricTemplate(metricTemplateSaveRequestDto);
        log.debug("TemplateController::createMetricTemplate() call ended");
        return ResponseEntity.ok().body(metricTemplate);
    }


	@Operation(
			summary = "Get all Metric Templates"
	)
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200",
			description = "Returns all Metric Templates"
	), @ApiResponse(
			responseCode = "404",
			description = "Metric not found"
	)})
	@GetMapping(path="/metric/templates")
	public ResponseEntity<List<MetricTemplateDetailsDto>> getAllMetricTemplates(){
		log.debug("TemplateController::getAllMetricTemplates call started");
		return ResponseEntity.ok().body(metricTemplateService.getMetricTemplates());
	}
	@Operation(
			summary = "Enable or disable metric template "
	)
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200",
			description = "updates isActive and returns template"
	), @ApiResponse(
			responseCode = "200",
			description = "Metric not found"
	)})
	@PutMapping("/metric/template/updateMetricTemplateStatus/{templateId}")
	public ResponseEntity<MetricTemplateDto> updateMetricTemplateStatus(@PathVariable Long templateId,@RequestParam Boolean isActive){
		log.debug("MetricTemplateController::updateMetricTemplateStatus::call started");
		MetricTemplateDto templateStatus = metricTemplateService.updateMetricTemplateStatus(templateId,isActive);
		return ResponseEntity.ok(templateStatus);
	}


	@Operation(summary = "Get MetricTemplate by metricTemplateId")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns MetricTemplate by metricTemplateId"),
			@ApiResponse(responseCode = "200", description = "MetricTemplate not found")})
	@GetMapping("/metric/templateId/{metricTemplateId}")
	public ResponseEntity<MetricTemplateDto> getMetricTemplateById(@PathVariable("metricTemplateId") Long metricTemplateId) {
		log.debug("MetricTemplateServiceImpl :: getMetricTemplateById() call started");
		MetricTemplateDto metricTemplateDto = metricTemplateService.getMetricTemplateById(metricTemplateId);
		log.debug("MetricTemplateServiceImpl :: getMetricTemplateById() call ended");
		return ResponseEntity.ok().body((metricTemplateDto));
	}


	@GetMapping("/metricTemplate/info/projectTypeId/{projectTypeId}")
	public ResponseEntity<List<MetricTemplateInfo>> getMetricTemplateInfoByProjectTypeId(@PathVariable Long projectTypeId) {
		log.debug("MetricTemplateController::getMetricTemplateInfoByProjectTypeId() call started");
		List<MetricTemplateInfo> metricTemplateInfo = metricTemplateService.getMetricTemplateInfoByProjectTypeId(projectTypeId)
				.orElse(Collections.emptyList());

		if (metricTemplateInfo.isEmpty()) {
			log.warn("No Metric Template found for projectTypeId: {}", projectTypeId);
			return ResponseEntity.noContent().build();
		}

		log.debug("MetricTemplateController::getMetricTemplateInfoByProjectTypeId() call ended");
		return ResponseEntity.ok().body(metricTemplateInfo);
	}

	@Operation(
			summary = "Preview a Metric Template"
	)
	@ApiResponses(value = {@ApiResponse(
			responseCode = "200",
			description = "Found Metric Template successfully"
	), @ApiResponse(
			responseCode = "200",
			description = "Provided Metric Template data is invalid"
	)})

	@PostMapping({"/metric/template/preview"})
	public ResponseEntity<MetricTemplateDto> previewMetricTemplate(@RequestBody @Valid MetricTemplateSaveRequestDto metricTemplateSaveRequestDto) {
		log.debug("TemplateController::previewMetricTemplate() call started");
		log.debug("metricTemplateSaveRequestDto details {}", metricTemplateSaveRequestDto);
		MetricTemplateDto metricTemplate = metricTemplateService.previewMetricTemplate(metricTemplateSaveRequestDto);
		log.debug("TemplateController::previewMetricTemplate() call ended");
		return ResponseEntity.ok().body(metricTemplate);
	}
}