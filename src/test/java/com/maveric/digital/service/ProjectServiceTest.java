package com.maveric.digital.service;

import com.maveric.digital.exceptions.AccountsNotFoundException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ProjectAlreadyExistsException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.model.Account;
import com.maveric.digital.model.Project;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.responsedto.ProjectInfo;
import com.maveric.digital.utils.ProjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProjectServiceTest {
    @MockBean
    ProjectRepository projectRepository;

    @Autowired
    ProjectService projectService;
    @MockBean
    ProjectMapper projectMapper;

    private ProjectDto projectDto;

    @Autowired
    private ProjectServiceImpl projectServiceImpl;

    @MockBean
    private AccountRepository accountRepository;

    @BeforeEach
    private void prepareData() {
        projectDto = new ProjectDto(1L, "Digital", "Ram", "Bu", LocalDate.now(), LocalDate.now(), "Ramss", true, LocalDate.now(), LocalDate.now(),1L,"C5689");

    }


    @Test
    void createProjectTest() {
        var project = new Project();
        BeanUtils.copyProperties(projectDto, project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.convertToProjectDto(project)).thenReturn(projectDto);
        var projectDto1 = projectService.createProject(projectDto);
        assertEquals(projectDto, projectDto1);
    }


    @Test
    void ThrowingConstraintViolationExceptionForCreateProject() {
        var project = new Project();
        BeanUtils.copyProperties(projectDto, project);
        when(projectRepository.save(any(Project.class))).thenThrow(ConstraintViolationException.class);
        when(projectMapper.convertToProjectDto(project)).thenReturn(projectDto);
        assertThrows(ConstraintViolationException.class, () -> projectService.createProject(projectDto));
    }


    @Test
    void ThrowingDataIntegrityViolationExceptionForCreateProjectTest() {
        var project = new Project();
        BeanUtils.copyProperties(projectDto, project);
        when(projectRepository.save(any(Project.class))).thenThrow(DataIntegrityViolationException.class);
        when(projectMapper.convertToProjectDto(project)).thenReturn(projectDto);
        assertThrows(DataIntegrityViolationException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    void ThrowingCustomExceptionForCreateProjectTest() {
        var project = new Project();
        BeanUtils.copyProperties(projectDto, project);
        when(projectRepository.save(any(Project.class))).thenThrow(ResourceCreationException.class);
        when(projectMapper.convertToProjectDto(project)).thenReturn(projectDto);
        assertThrows(ResourceCreationException.class, () -> projectService.createProject(projectDto));
    }






    @Test
    void testGetProjectsInfoByAccountIdConstraintViolationException() {
        Optional<List<Project>> ofResult = Optional.of(new ArrayList<>());
        when(projectRepository.findByStatusTrueAndAccountId(1l,Sort.by("projectName"))).thenReturn(ofResult);
        List<ProjectInfo> actualProjectsInfoByAccountId = projectServiceImpl.getProjectsInfoByAccountId(1L);
        verify(projectRepository).findByStatusTrueAndAccountId(1l,Sort.by("projectName"));
        assertTrue(actualProjectsInfoByAccountId.isEmpty());
    }

    @Test
    void testCreateProjectonAccountIdProjectAlreadyExistsException() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setAccountId(1L);
        projectDto.setProjectName("Existing Project");
        projectDto.setProjectCode("TP123");

        Account account = new Account();
        account.setId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(projectRepository.existsByProjectNameAndAccountId("Existing Project", 1L)).thenReturn(true);

        // Act and Assert
        assertThrows(ProjectAlreadyExistsException.class, () -> projectService.createProjectonAccountId(projectDto));
    }

    @Test
    void testCreateProjectonAccountIdResourceCreationException() {


        Account account = new Account();
        account.setAccountName("Madhu");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        Project project = new Project();
        project.setAccount(account);
        project.setCreatedAt(LocalDate.of(2023, 12, 9));
        project.setEndDate(LocalDate.of(2023, 12, 9));
        project.setId(1L);
        project.setManagerName("ProjectServiceImpl :: getProjectsInfoByAccountId() call started");
        project.setProjectName("ProjectServiceImpl :: getProjectsInfoByAccountId() call started");
        project.setStartDate(LocalDate.of(2023, 12, 9));
        project.setStatus(true);
        project.setUpdatedAt(LocalDate.of(2023, 12, 9));
        project.setUpdatedBy("2023-12-9");



        Account account2 = new Account();
        account2.setAccountName("Madhu");
        account2.setCreatedAt(0L);
        account2.setId(2L);
        account2.setUpdatedAt(0L);

        Project project2 = new Project();
        project2.setAccount(account2);
        project2.setCreatedAt(LocalDate.of(2023, 12, 9));
        project2.setEndDate(LocalDate.of(2023, 12, 9));
        project2.setId(2L);
        project2.setManagerName("ProjectList from DB: {}");
        project2.setProjectName("ProjectList from DB: {}");
        project2.setStartDate(LocalDate.of(2023, 12, 9));
        project2.setStatus(false);
        project2.setUpdatedAt(LocalDate.of(2023, 12, 9));
        project2.setUpdatedBy("2023/12/9");

        ArrayList<Project> projectList = new ArrayList<>();
        projectList.add(project2);
        projectList.add(project);
        Optional<List<Project>> ofResult = Optional.of(projectList);
        when(projectRepository.findByStatusTrueAndAccountId(1l,Sort.by("projectName"))).thenReturn(ofResult);
        List<ProjectInfo> actualProjectsInfoByAccountId = projectServiceImpl.getProjectsInfoByAccountId(1L);
        verify(projectRepository).findByStatusTrueAndAccountId(1l,Sort.by("projectName"));
        ProjectInfo getResult = actualProjectsInfoByAccountId.get(0);
        assertEquals("ProjectList from DB: {}", getResult.getProjectName());
        ProjectInfo getResult2 = actualProjectsInfoByAccountId.get(1);
        assertEquals("ProjectServiceImpl :: getProjectsInfoByAccountId() call started", getResult2.getProjectName());
        assertEquals(1L, getResult2.getId().longValue());
        assertEquals(2, actualProjectsInfoByAccountId.size());
        assertEquals(2L, getResult.getId().longValue());
    }
    @Test
    void testCreateProjectonAccountIdAccountsNotFoundException() {
        when(projectRepository.findByStatusTrueAndAccountId(1l,Sort.by("projectName")))
                .thenThrow(new ConstraintViolationException(new HashSet<>()));
        assertThrows(ConstraintViolationException.class, () -> projectServiceImpl.getProjectsInfoByAccountId(1L));
        verify(projectRepository).findByStatusTrueAndAccountId(1l,Sort.by("projectName"));
    }
    @Test
    void testCreateProjectonAccountIdSuccess() {


        Account account = new Account();
        account.setAccountName("Madhu");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);
        Optional<Account> ofResult = Optional.of(account);

        when(accountRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(projectRepository.existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any()))
                .thenReturn(false);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setAccountId(1L);
        projectDto.setProjectName("TestProject");
        projectDto.setProjectCode("TP001");
        Project result = projectServiceImpl.createProjectonAccountId(projectDto);
        verify(projectRepository).existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any());
        verify(accountRepository).findById(Mockito.<Long>any());
    }

    @Test
    void testCreateProjectonAccountIdDataIntegrityViolationException() {

        when(accountRepository.findById(Mockito.<Long>any())).thenReturn(Optional.of(new Account()));
        when(projectRepository.existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any()))
                .thenReturn(false);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setAccountId(1L);
        projectDto.setProjectName("TestProject");
        projectDto.setProjectCode("TP001");

        when(projectRepository.save(any(Project.class)))
                .thenThrow(new DataIntegrityViolationException("Integrity violation"));
        assertThrows(DataIntegrityViolationException.class, () -> projectServiceImpl.createProjectonAccountId(projectDto));
        verify(projectRepository).existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any());
        verify(accountRepository).findById(Mockito.<Long>any());
    }

    @Test
    void testCreateProjectonAccountIdConstraintViolationException() {
        when(accountRepository.findById(Mockito.<Long>any())).thenReturn(Optional.of(new Account()));
        when(projectRepository.existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any()))
                .thenReturn(false);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setAccountId(1L);
        projectDto.setProjectName("TestProject");
        projectDto.setProjectCode("TP001");
        when(projectRepository.save(any(Project.class)))
                .thenThrow(new ConstraintViolationException("Constraint violation", null));
        assertThrows(ConstraintViolationException.class, () -> projectServiceImpl.createProjectonAccountId(projectDto));
        verify(projectRepository).existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any());
        verify(accountRepository).findById(Mockito.<Long>any());
    }
    @Test
    void testGetProjectsByBusinessUnitIdEmptyResult() {

        Account account = new Account();
        account.setAccountName("Madhu");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);
        Optional<Account> ofResult = Optional.of(account);
        when(accountRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(projectRepository.existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any()))
                .thenThrow(new RuntimeException("ProjectServiceImpl::createProject()::Start"));
        assertThrows(ResourceCreationException.class, () -> projectServiceImpl.createProjectonAccountId(new ProjectDto()));
        verify(projectRepository).existsByProjectNameAndAccountId(Mockito.<String>any(), Mockito.<Long>any());
        verify(accountRepository).findById(Mockito.<Long>any());
    }

    @Test
    void testGetProjectsByBusinessUnitIdConstraintViolationExceptionWithEmptyResult() {
        Optional<Account> emptyResult = Optional.empty();
        when(accountRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);
        assertThrows(AccountsNotFoundException.class, () -> projectServiceImpl.createProjectonAccountId(new ProjectDto()));
        verify(accountRepository).findById(Mockito.<Long>any());
    }

    @Test
    void testGetProjectsInfosByBusinessUnitIdsSuccessWithEmptyResult() {
        ProjectDto projectDto = mock(ProjectDto.class);
        when(projectDto.getAccountId()).thenThrow(new RuntimeException(""));
        assertThrows(ResourceCreationException.class, () -> projectServiceImpl.createProjectonAccountId(projectDto));
        verify(projectDto).getAccountId();
    }

}
