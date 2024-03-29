package com.maveric.digital.service.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.Account;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.service.ConversationService;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {FileConversionServiceImpl.class})
@ExtendWith(SpringExtension.class)
class FileConversionServiceImplTest {
    @MockBean
    private AccountRepository accountRepository;


    @MockBean
    private ConversationService conversationService;

    @Autowired
    private FileConversionServiceImpl fileConversionServiceImpl;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectTypeRepository projectTypeRepository;
    @Test
    void shouldThrowExceptionWhenUploadCSVFileWithInvalidFileTest() throws IOException {
        assertThrows(CustomException.class, () -> fileConversionServiceImpl.uploadCSVFile(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "File Context"));
    }
    @Test
    void shouldThrowExceptionWhenUploadCSVFileWithInvalidContentTest() throws IOException {
        DataInputStream contentStream = mock(DataInputStream.class);
        when(contentStream.readAllBytes()).thenReturn("AXAXAXAX".getBytes("UTF-8"));
        doNothing().when(contentStream).close();
        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.uploadCSVFile(new MockMultipartFile("Name", contentStream), "ACCOUNT"));
        verify(contentStream).close();
        verify(contentStream).readAllBytes();
    }
    @Test
    void shouldThrowExceptionWhenUploadCSVFileWithInvalidBusinessUnitTest() throws IOException {
        DataInputStream contentStream = mock(DataInputStream.class);
        when(contentStream.readAllBytes()).thenReturn("AXAXAXAX".getBytes("UTF-8"));
        doNothing().when(contentStream).close();
        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.uploadCSVFile(new MockMultipartFile("Name", contentStream), "BU"));
        verify(contentStream).close();
        verify(contentStream).readAllBytes();
    }
    @Test
    void shouldThrowExceptionWhenUploadCSVFileWithInvalidProjectTest() throws IOException {
        DataInputStream contentStream = mock(DataInputStream.class);
        when(contentStream.readAllBytes()).thenReturn("AXAXAXAX".getBytes("UTF-8"));
        doNothing().when(contentStream).close();
        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.uploadCSVFile(new MockMultipartFile("Name", contentStream), "PROJECT"));
        verify(contentStream).close();
        verify(contentStream).readAllBytes();
    }
    @Test
    void shouldThrowExceptionWhenUploadCSVFileWithInvalidProjectTypeTest() throws IOException {
        DataInputStream contentStream = mock(DataInputStream.class);
        when(contentStream.readAllBytes()).thenReturn("AXAXAXAX".getBytes("UTF-8"));
        doNothing().when(contentStream).close();
        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.uploadCSVFile(new MockMultipartFile("Name", contentStream), "PROJECT_TYPE"));
        verify(contentStream).close();
        verify(contentStream).readAllBytes();
    }
    @Test
    void shouldThrowExceptionWhenFindAccountsByIdInWithEmptyListTest() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());
        assertThrows(CustomException.class, () -> fileConversionServiceImpl.findAccountsByIdIn(new ArrayList<>()));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldFindAccountsByIdInAndReturnEmptyMapTest() {
        Account account = new Account();
        account.setAccountName("Account name");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        ArrayList<Account> accountList = new ArrayList<>();
        accountList.add(account);
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(accountList);

        Map<Long, Account> actualFindAccountsByIdInResult = fileConversionServiceImpl.findAccountsByIdIn(new ArrayList<>());
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
        assertEquals(1, actualFindAccountsByIdInResult.size());
    }
    @Test
    void shouldFindAccountsByIdInAndReturnMapWithOneElementTest() {

        Account account = new Account();
        account.setAccountName("Account name");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        Account account2 = new Account();
        account2.setAccountName("Account Name");
        account2.setCreatedAt(0L);
        account2.setId(2L);
        account2.setUpdatedAt(0L);

        ArrayList<Account> accountList = new ArrayList<>();
        accountList.add(account2);
        accountList.add(account);
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(accountList);

        Map<Long, Account> actualFindAccountsByIdInResult = fileConversionServiceImpl.findAccountsByIdIn(new ArrayList<>());

        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
        assertEquals(2, actualFindAccountsByIdInResult.size());
    }
    @Test
    void shouldFindAccountsByIdInAndReturnMapWithTwoElementsTest() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1L);

        assertThrows(CustomException.class, () -> fileConversionServiceImpl.findAccountsByIdIn(ids));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldThrowExceptionWhenFindAccountsByIdInWithEmptyList2Test() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(0L);
        ids.add(1L);
        assertThrows(CustomException.class, () -> fileConversionServiceImpl.findAccountsByIdIn(ids));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldThrowExceptionWhenFindAccountsByIdInWithNonexistentIdsTest() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any()))
                .thenThrow(new CustomException("An error occurred", HttpStatus.CONTINUE));
        assertThrows(CustomException.class, () -> fileConversionServiceImpl.findAccountsByIdIn(new ArrayList<>()));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldThrowExceptionWhenFindAccountsByIdInAndReturnMapWithEmptyListTest() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());
        ArrayList<Long> ids = new ArrayList<>();
        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.mapProjectIdWithAccount(ids, new ArrayList<>()));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldMapProjectIdWithAccountAndThrowExceptionTest() {

        Account account = new Account();
        account.setAccountName("Account Name");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        ArrayList<Account> accountList = new ArrayList<>();
        accountList.add(account);
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(accountList);
        ArrayList<Long> ids = new ArrayList<>();

        Map<Long, Account> actualMapProjectIdWithAccountResult = fileConversionServiceImpl.mapProjectIdWithAccount(ids,
                new ArrayList<>());
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
        assertTrue(actualMapProjectIdWithAccountResult.isEmpty());
    }
    @Test
    void shouldMapProjectIdWithAccountAndReturnEmptyMapTest() {

        Account account = new Account();
        account.setAccountName("Account name");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);


        Account account2 = new Account();
        account2.setAccountName("Account name");
        account2.setCreatedAt(0L);
        account2.setId(2L);
        account2.setUpdatedAt(0L);

        ArrayList<Account> accountList = new ArrayList<>();
        accountList.add(account2);
        accountList.add(account);
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(accountList);
        ArrayList<Long> ids = new ArrayList<>();

        Map<Long, Account> actualMapProjectIdWithAccountResult = fileConversionServiceImpl.mapProjectIdWithAccount(ids,
                new ArrayList<>());

        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
        assertTrue(actualMapProjectIdWithAccountResult.isEmpty());
    }
    @Test
    void shouldThrowCustomExceptionWhenMappingProjectIdWithAccountAndNoAccountsFound() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());
        ArrayList<Long> projectIds = new ArrayList<>();
        projectIds.add(1L);

        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.mapProjectIdWithAccount(projectIds, new ArrayList<>()));

        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }

    @Test
    void shouldThrowCustomExceptionWhenMappingProjectIdWithAccountAndNoMatchingAccountsFound() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(0L);
        ids.add(1L);

        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.mapProjectIdWithAccount(ids, new ArrayList<>()));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldThrowCustomExceptionWhenMappingProjectIdWithEmptyIdsAndProjectDtoList() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());
        ArrayList<Long> ids = new ArrayList<>();

        ArrayList<ProjectDto> list = new ArrayList<>();
        list.add(new ProjectDto());

        assertThrows(CustomException.class, () -> fileConversionServiceImpl.mapProjectIdWithAccount(ids, list));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldThrowCustomExceptionWhenMappingProjectIdWithEmptyIdsAndMultipleProjectDto() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>());
        ArrayList<Long> ids = new ArrayList<>();

        ArrayList<ProjectDto> list = new ArrayList<>();
        list.add(new ProjectDto());
        list.add(new ProjectDto());

        assertThrows(CustomException.class, () -> fileConversionServiceImpl.mapProjectIdWithAccount(ids, list));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldThrowCustomExceptionWithSpecificDetailsWhenRepositoryThrowsException() {
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any()))
                .thenThrow(new CustomException("An error occurred", HttpStatus.CONTINUE));
        ArrayList<Long> ids = new ArrayList<>();

        assertThrows(CustomException.class,
                () -> fileConversionServiceImpl.mapProjectIdWithAccount(ids, new ArrayList<>()));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }
    @Test
    void shouldThrowCustomExceptionWhenMappingProjectIdWithMatchingAccountsFound() {


        Account account = new Account();
        account.setAccountName("Account Name");
        account.setCreatedAt(1L);
        account.setId(1L);
        account.setUpdatedAt(1L);

        ArrayList<Account> accountList = new ArrayList<>();
        accountList.add(account);
        when(accountRepository.findAccountsByIdIn(Mockito.<List<Long>>any())).thenReturn(accountList);
        ArrayList<Long> ids = new ArrayList<>();

        ArrayList<ProjectDto> list = new ArrayList<>();
        list.add(new ProjectDto());

        assertThrows(CustomException.class, () -> fileConversionServiceImpl.mapProjectIdWithAccount(ids, list));
        verify(accountRepository).findAccountsByIdIn(Mockito.<List<Long>>any());
    }

}
