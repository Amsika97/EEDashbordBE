package com.maveric.digital.controller;


import com.maveric.digital.responsedto.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.TemplateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping({"/v1"})
@RequiredArgsConstructor
@Slf4j
public class TemplateController {
    private final TemplateService templateService;
    private final ConversationService conversationService;

    @Operation(
            summary = "Create a Template"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Created a Template successfully"
    ), @ApiResponse(
            responseCode = "400",
            description = "Provided Template data is invalid"
    )})
    @PostMapping({"/template/create"})
    public ResponseEntity<TemplateDto> createTemplate(@RequestBody @Valid TemplateSaveRequestDto templateSaveRequest) throws URISyntaxException {
        log.debug("TemplateController::createTemplate() call started");
        log.debug("createTemplate {}", templateSaveRequest);
        TemplateDto template = this.templateService.createTemplate(templateSaveRequest);
        log.debug("TemplateController::createTemplate call ended");
        return ResponseEntity.ok().body(template);
    }
    @Operation(
            summary = "Update a Template"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Updated a Template successfully"
    ), @ApiResponse(
            responseCode = "400",
            description = "Template data is invalid"
    )})
    @PostMapping({"/template/update"})
    public ResponseEntity<TemplateDto> updateTemplate(@RequestBody  TemplateSaveRequestDto templateSaveRequest)  {
        log.debug("TemplateController::updateTemplate() call started");
        log.debug("update Template request data : {}", templateSaveRequest);
        TemplateDto template = this.templateService.updateTemplate(templateSaveRequest);
        log.debug(" Latest version template data : {}", template);
        log.debug("TemplateController::updateTemplate call ended ");
        return ResponseEntity.ok().body(template);
    }


    @Operation(
            summary = "Get Template by templateName"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns template for template name"
    ), @ApiResponse(
            responseCode = "404",
            description = "Template not found"
    )})
    @GetMapping({"/template/{templateName}"})
    public ResponseEntity<TemplateDto> getTemplateByTemplateName(@PathVariable String templateName) {
        log.debug("TemplateController::getTemplateByTemplateName call started");
        log.debug("templateName {}", templateName);
        TemplateDto projectDtosRes = this.templateService.getTemplateByTemplateName(templateName);
        log.debug("TemplateController::getTemplateByTemplateName call ended");
        return ResponseEntity.ok().body(projectDtosRes);
    }

    @Operation(
            summary = "Get Templates by projectType"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns Collection of Templates for projectType"
    ), @ApiResponse(
            responseCode = "404",
            description = "Template not found"
    )})
    @GetMapping({"/template/projectType/{projectTypeId}"})
    public ResponseEntity<List<TemplateDto>> getTemplatesByProjectType(@PathVariable Long projectTypeId) {
        log.debug("TemplateController::getTemplateByTemplateName call started");
        log.debug("projectType {}", projectTypeId);
        List<TemplateDto> templateByProjectType = this.templateService.getTemplatesByProjectType(projectTypeId);
        log.debug("TemplateController::getTemplateByProjectType call ended");
        return ResponseEntity.ok().body(templateByProjectType);
    }

    @Operation(
            summary = "Get TemplateInfo  by projectTypeId"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns Collection of TemplateInfo for projectType"
    ), @ApiResponse(
            responseCode = "404",
            description = "Template not found"
    )})
    @GetMapping({"/template/info/{projectTypeId}"})
    public ResponseEntity<List<TemplateInfo>> getTemplateInfoByProjectType(@PathVariable Long projectTypeId) {
        log.debug("TemplateController::getTemplateInfoByProjectType call started");
        log.debug("projectType {}", projectTypeId);
        List<TemplateInfo> templateByProjectType = this.templateService.getTemplateInfoByProjectType(projectTypeId);
        log.debug("TemplateController::getTemplateInfoByProjectType call ended");
        return ResponseEntity.ok().body(templateByProjectType);
    }
    @Operation(summary = "Get a Template  by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Return a Template for id"),
            @ApiResponse(responseCode = "404",
                    description = "Template not found"
            )})
    @GetMapping({"/template/id/{id}"})
    public ResponseEntity<TemplateDto> getTemplateById(@PathVariable Long id) {
        log.debug("TemplateController::getTemplateById call started");
        log.debug("template id {}", id);
        TemplateDto templateDto = this.templateService.getTemplateById(id);
        log.debug("TemplateController::getTemplateById call ended");

        return ResponseEntity.ok().body(templateDto);
    }
    @Operation(summary = "Get TemplateInfo  by selected businessUnitId,projectId and projectTypeId")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Return a TemplateInfo for selected businessUnitId,projectId and projectTypeId"),
            @ApiResponse(responseCode = "204",
                    description = "TemplateInfo not found"
            )})
    @GetMapping({"/template/info/{projectId}/{projectTypeId}"})
    public ResponseEntity<List<TemplateInfo>> getTemplateBySelectedFilters(@PathVariable Long projectId,@PathVariable Long projectTypeId) {
        log.debug("TemplateController::getTemplateBySelectedFilters call started");
        log.debug("projectId,projectTypeId ,{} {}",projectId,projectTypeId);
        List<TemplateInfo> templateByProjectType = this.templateService.getTemplateInfoBySelectedFilters(projectId,projectTypeId);
        log.debug("TemplateController::getTemplateBySelectedFilters call ended");
        return ResponseEntity.ok().body(templateByProjectType);
    }
    @GetMapping(path="/template/info/allTemplates")
    public ResponseEntity<List<TemplateInfo>> getAllTemplateInfos(){
        log.debug("TemplateController::getAllTemplateInfos call started");
        return ResponseEntity.ok().body(conversationService.toTemplateInfo(templateService.getAllTemplateInfos()));
    }
    @Operation(summary = "Discard a Template  by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Discard Template info for id"),
            @ApiResponse(responseCode = "404",
                    description = "Template not found"
            )})
    @PutMapping("/template/discard/{id}")
    public ResponseEntity<TemplateInfo> discardTemplate(@RequestParam Boolean isActive,@PathVariable Long id){
        TemplateInfo templateStatus = this.templateService.discardTemplate(isActive,id);
        log.debug("TemplateController::discardTemplate call started");
        return ResponseEntity.ok().body(templateStatus);
    }

    @Operation(
            summary = "Get all Assessment Templates"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns all Assessment Templates"
    ), @ApiResponse(
            responseCode = "404",
            description = "Template not found"
    )})
    @GetMapping(path="/templates")
    public ResponseEntity<List<AssessmentTemplateDto>> getAllAssessmentTemplates(){
        log.debug("TemplateController::getAllAssessmentTemplates call started");
        return ResponseEntity.ok().body(templateService.getAssessmentTemplates());
    }

    @Operation(
            summary = "Template Preview"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Template Preview is Successful"
    ), @ApiResponse(
            responseCode = "200",
            description = "Provided Preview data is invalid"
    )})
    @PostMapping({"/template/preview"})
    public ResponseEntity<TemplateDto> createPreview(@RequestBody @Valid TemplateSaveRequestDto templateSaveRequest) throws URISyntaxException {
        log.debug("TemplateController::createPreview() call started");
        log.debug("createPreview {}", templateSaveRequest);
        TemplateDto template = this.templateService.createPreview(templateSaveRequest);
        log.debug("TemplateController::createPreview call ended");
        return ResponseEntity.ok().body(template);
    }
    @Operation(
            summary = "Get unique TemplateDisplayName"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Returns all unique templateDisplayNames based on filterName"
    )})
    @GetMapping(path="/template/distinct/template-display-names")
    public ResponseEntity<List<TemplateDisplayDto>> getUniqueTemplateDisplayNames(@RequestParam(name = "filterName", required = false) String filterName){
        log.info("TemplateController::getUniqueTemplateDisplayNames()");
        return ResponseEntity.ok(templateService.getUniqueTemplateDisplayNames(filterName));
    }

}
