package com.maveric.digital.controller;

import com.maveric.digital.exceptions.ErrorDetails;
import com.maveric.digital.model.Project;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.responsedto.ProjectInfo;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Slf4j
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ConversationService conversationService;



    @Operation(summary = "Create a Project")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created a Project successfully"),
            @ApiResponse(responseCode = "400", description = "Provided Project data is invalid")})
    @PostMapping("/project/create")
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) throws URISyntaxException {
        log.info("ProjectController::createProject() call started");
        log.info("projectDto {}", projectDto);
        ProjectDto projectDtoRes = projectService.createProject(projectDto);
        log.info("ProjectController::createProject call ended");
        return ResponseEntity.created(new URI("/v1/project/create/" + projectDtoRes.getId())).body(projectDtoRes);
    }


    @Operation(summary = "Get ProjectInfos by AccountId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns Collection of ProjectInfos for Account Id"),
            @ApiResponse(responseCode = "204", description = "No projects found for the given Account Id")
    })
    @GetMapping("/project/info/accountId/{accountId}")
    public ResponseEntity<?> getProjectsInfosByAccountId(@PathVariable Long accountId) {
        log.debug("ProjectController::getProjectsInfosByAccountId() call started");
        log.debug("accountId {}", accountId);

        List<ProjectInfo> projectInfos = projectService.getProjectsInfoByAccountId(accountId);

        log.debug("ProjectController::getProjectsInfosByAccountId call ended");

        if (projectInfos.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(
                    new Date(),
                    String.format("No projects found for accountId: %d", accountId),
                    "uri=/ee-dashboard/api/v1/project/info/accountId/" + accountId);

            return ResponseEntity.ok().body(Collections.singletonList(errorDetails));
        }

        return ResponseEntity.ok().body(projectInfos);
    }

    @Operation(summary = "Create a Project by Account Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created a Project successfully"),
            @ApiResponse(responseCode = "400", description = "Provided Project data is invalid")})
    @PostMapping("/project/createOnAccount")
    public ResponseEntity<ProjectDto> createProjectOnAccountId(@RequestBody ProjectDto projectDto) {
        log.debug("ProjectController::createProject() call started");
        log.debug("projectDto {}", projectDto);

        Project createdProject = projectService.createProjectonAccountId(projectDto);
        ProjectDto createdProjectDto = conversationService.toProjectDto(createdProject);
        log.debug("ProjectController::createProject call ended");
        return new ResponseEntity<>(createdProjectDto, HttpStatus.CREATED);
    }


}
