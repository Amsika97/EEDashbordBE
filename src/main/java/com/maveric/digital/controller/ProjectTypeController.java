package com.maveric.digital.controller;

import com.maveric.digital.responsedto.ProjectTypeDto;
import com.maveric.digital.service.ProjectTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class ProjectTypeController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectTypeController.class);
    private final ProjectTypeService projectTypeService;

    @Autowired
    public ProjectTypeController(ProjectTypeService projectTypeService) {
        this.projectTypeService = projectTypeService;
    }

    @GetMapping("/projectTypes")
    @Operation(description = "getallprojectType")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "getall projectType successfully"), @ApiResponse(responseCode = "400", description = "Provided data is invalid")})
    public List<ProjectTypeDto> getAll () {
        logger.info("ProjectTypeController::getAllProjectTypes call started");
        return new ResponseEntity<>(projectTypeService.getAll(), HttpStatus.OK).getBody();
    }

    @GetMapping("/filtered/projectTypes")
    @Operation(description = "getallprojectType")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "getall projectType successfully"), @ApiResponse(responseCode = "400", description = "Provided data is invalid")})
    public List<ProjectTypeDto> getAllprojectTypes () {
        logger.info("ProjectTypeController::getAllProjectTypes call started");
        return new ResponseEntity<>(projectTypeService.getAllfilteredprojectTypes(), HttpStatus.OK).getBody();
    }


}
