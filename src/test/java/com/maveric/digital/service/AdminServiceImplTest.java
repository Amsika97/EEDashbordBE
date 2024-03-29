package com.maveric.digital.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.maveric.digital.model.Account;
import com.maveric.digital.model.Project;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.responsedto.CsvMetaDataDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AdminServiceImpl.class})
@ExtendWith(SpringExtension.class)
class AdminServiceImplTest {
    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private AdminServiceImpl adminServiceImpl;



    @MockBean
    private ConversationService conversationService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectTypeRepository projectTypeRepository;

    @Test
    void testImportCsvMetaData() {

        Account account = new Account();
        account.setAccountName("Account name");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);


        Account account2 = new Account();
        account2.setAccountName("Account name");
        account2.setCreatedAt(1L);
        account2.setId(1L);
        account2.setUpdatedAt(1L);

        Project project = new Project();
        project.setAccount(account2);
        project.setCreatedAt(LocalDate.of(2023, 12, 22));
        project.setEndDate(LocalDate.of(2023, 12, 22));
        project.setId(1L);
        project.setManagerName("Manager Name");
        project.setProjectCode("Project Code");
        project.setProjectName("Project Name");
        project.setStartDate(LocalDate.of(2023, 12, 22));
        project.setStatus(true);
        project.setUpdatedAt(LocalDate.of(2023, 12, 22));
        project.setUpdatedBy("2023-12-22");

        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        projectType.setProjectTypeName("Project Type Name");
        when(conversationService.toProjectTypeFromMetaData(Mockito.<CsvMetaDataDto>any())).thenReturn(projectType);
        when(conversationService.toProjectFromMetaData(Mockito.<Map<Long, Account>>any(), Mockito.<CsvMetaDataDto>any(),
                Mockito.<Long>any())).thenReturn(project);
        when(conversationService.toAccountFromMetaData(
                Mockito.<CsvMetaDataDto>any())).thenReturn(account);
        when(accountRepository.insert(Mockito.<Iterable<Account>>any())).thenReturn(new ArrayList<>());
        when(projectRepository.insert(Mockito.<Iterable<Project>>any())).thenReturn(new ArrayList<>());
        when(projectTypeRepository.insert(Mockito.<Iterable<ProjectType>>any())).thenReturn(new ArrayList<>());

        CsvMetaDataDto csvMetaDataDto = new CsvMetaDataDto();
        csvMetaDataDto.setAccountId(1L);
        csvMetaDataDto.setDeliveryManager("AdminServiceImpl::importCsvMetaData() Start");
        csvMetaDataDto.setEndDate(LocalDate.of(2023, 12, 22));
        csvMetaDataDto.setId(1L);
        csvMetaDataDto.setProject("AdminServiceImpl::importCsvMetaData() Start");
        csvMetaDataDto.setProjectBillingType("AdminServiceImpl::importCsvMetaData() Start");
        csvMetaDataDto.setStartDate(LocalDate.of(2023, 12, 22));

        ArrayList<CsvMetaDataDto> metaDataList = new ArrayList<>();
        metaDataList.add(csvMetaDataDto);

        // Act
        adminServiceImpl.importCsvMetaData(metaDataList);
        verify(conversationService).toProjectFromMetaData(Mockito.<Map<Long, Account>>any(), Mockito.<CsvMetaDataDto>any(),
                Mockito.<Long>any());
        verify(conversationService).toProjectTypeFromMetaData(Mockito.<CsvMetaDataDto>any());
        verify(accountRepository).saveAll(Mockito.<Iterable<Account>>any());
        verify(projectRepository).insert(Mockito.<Iterable<Project>>any());
      //  verify(projectTypeRepository).insert(Mockito.<Iterable<ProjectType>>any());
    }
}
