package com.maveric.digital.service;

import com.maveric.digital.exceptions.*;
import com.maveric.digital.model.Account;
import com.maveric.digital.model.Project;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.responsedto.ProjectInfo;
import com.maveric.digital.utils.ProjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private AccountRepository accountRepository;


    @Override
    public ProjectDto createProject(ProjectDto projectDto) {
        try {
            log.info("ProjectServiceImpl :: createProject() call started");
            Project project = new Project();
            BeanUtils.copyProperties(projectDto, project);
            project.setCreatedAt(LocalDate.now());
            project = projectRepository.save(project);
            log.info("Project data saved in DB{}", project);
            projectDto = projectMapper.convertToProjectDto(project);
            log.info("Converted ProjectDto {}", projectDto);
            log.info("ProjectServiceImpl :: createProject() call ended {}", projectDto);
            return projectDto;
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResourceCreationException(String.format("Error  Occurs while saving  Project : exception-{%s} , projectDto-{%s}", ex, projectDto));
        }
    }


    @Override
    public List<ProjectInfo> getProjectsInfoByAccountId(Long accountId) {
        log.info("ProjectServiceImpl :: getProjectsInfoByAccountId() call started");

        List<Project> projects = projectRepository.findByStatusTrueAndAccountId(accountId, Sort.by("projectName"))
                .orElseGet(Collections::emptyList);

        log.info("ProjectList from DB: {}", projects);

        if (projects.isEmpty()) {
            log.info("No projects found for account ID {}", accountId);
            return Collections.emptyList();
        }

        List<ProjectInfo> projectInfoList = projects.stream()
                .map(project -> new ProjectInfo(project.getId(), project.getProjectName()))
                .collect(Collectors.toList());

        log.info("Converted ProjectInfo List: {}", projectInfoList);
        log.info("ProjectServiceImpl :: getProjectsInfoByAccountId() call ended");

        return projectInfoList;
    }

    @Override
    @Transactional
    public Project createProjectonAccountId(ProjectDto projectDto) {
        log.debug("ProjectServiceImpl::createProject()::Start");

        try {
            log.info("ProjectServiceImpl::createProject() call started");

            Account account = accountRepository.findById(projectDto.getAccountId())
                    .orElseThrow(() -> new AccountsNotFoundException("Account not found with ID: " + projectDto.getAccountId()));

            if (projectRepository.existsByProjectNameAndAccountId(projectDto.getProjectName(), projectDto.getAccountId())) {
                throw new ProjectAlreadyExistsException("Project with the same name and account ID already exists");
            }

            Project project = new Project();
            BeanUtils.copyProperties(projectDto, project);
            project.setCreatedAt(LocalDate.now());
            project.setAccount(account);
            project.setUpdatedAt(LocalDate.now());
            project.setProjectCode(projectDto.getProjectCode());
            project = projectRepository.save(project);

            log.debug("Project data saved in DB: {}", project);
            ProjectDto createdProjectDto = projectMapper.convertToProjectDto(project);
            log.debug("Converted ProjectDto: {}", createdProjectDto);

            log.debug("ProjectServiceImpl::createProject() call ended");

            return project;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.error("Error occurred while saving Project", e);
            throw e;
        } catch (AccountsNotFoundException | InvalidAccountException e) {
            log.error("Error occurred while processing account", e);
            throw e;
        } catch (ProjectAlreadyExistsException e) {
            log.error("Error occurred while creating project", e);
            throw e;
        } catch (Exception ex) {
            log.error("Error occurred while saving Project", ex);
            throw new ResourceCreationException("Error occurred while saving Project", ex);
        }
    }


}
