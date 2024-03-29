package com.maveric.digital.service.file;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.Account;
import com.maveric.digital.model.Project;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.responsedto.ProjectDto;
import com.maveric.digital.responsedto.ProjectTypeDto;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.utils.FileContextConstants;
import com.maveric.digital.utils.parsingutils.CSVParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileConversionServiceImpl implements FileConvertorService {

    private final ProjectRepository projectRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final AccountRepository accountRepository;
    private final ConversationService conversationService;


    @Override
    public Object uploadCSVFile(MultipartFile file, String fileContext) throws IOException {
        try {
            log.debug("FileConversionServiceImpl :: uploadCSVFile() : call started ");

            String endLogMsg = "FileConversionServiceImpl :: uploadCSVFile() : call ended";
            switch (fileContext) {
                case FileContextConstants.CONTEXT_PROJECT -> {
                    List<ProjectDto> projectList = CSVParser.readCSV(file, ProjectDto.class);
                    if (CollectionUtils.isEmpty(projectList)){
                        log.debug("No record found in file");
                        throw new CustomException("File Should not be empty",HttpStatus.OK);
                    }
                    log.debug("Converted file to projectList - {}",projectList);
                    List<Long> accountIds = projectList.stream()
                            .map(ProjectDto::getAccountId)
                            .toList();
                    log.debug("AccountIds - {}",accountIds);

                    //Mapping project id with Account
                    Map<Long, Account> mapProjectIdWithAccount = mapProjectIdWithAccount(accountIds, projectList);
                    List<Project> projects = conversationService.toProjectList(projectList);
                    for (Project p : projects) {
                        p.setAccount(mapProjectIdWithAccount.get(p.getId()));
                        p.setCreatedAt(LocalDate.now());
                    }
                    projectRepository.insert(projects);
                    log.debug("Projects stored in DB Successfully");
                    log.debug(endLogMsg);
                    return projectList;
                }

                case FileContextConstants.CONTEXT_ACCOUNT -> {
                    List<AccountDto> accountList = CSVParser.readCSV(file, AccountDto.class);
                    if (CollectionUtils.isEmpty(accountList)){
                        log.debug("No record found in file");
                        throw new CustomException("File Should not be empty",HttpStatus.OK);
                    }
                    log.debug("Converted file to accountList - {}",accountList);
                    List<Account> accounts = conversationService.toAccountList(accountList);
                    for (Account a : accounts) {
                        a.setCreatedAt(System.currentTimeMillis());
                    }
                    accountRepository.insert(accounts);
                    log.debug("AccountList stored in DB Successfully");
                    log.debug(endLogMsg);
                    return accountList;
                }
                case FileContextConstants.CONTEXT_PROJECT_TYPE -> {
                    List<ProjectTypeDto> projectTypeList = CSVParser.readCSV(file, ProjectTypeDto.class);
                    if (CollectionUtils.isEmpty(projectTypeList)){
                        log.debug("No record found in file");
                        throw new CustomException("File Should not be empty",HttpStatus.OK);
                    }
                    log.debug("Converted file to projectTypeList - {}",projectTypeList);
                    List<ProjectType> projectTypes = conversationService.toProjectTypeList(projectTypeList);
                    projectTypeRepository.insert(projectTypes);
                    log.debug("ProjectTypeList stored in DB Successfully");
                    log.debug(endLogMsg);
                    return projectTypeList;
                }
                default ->
                        throw new CustomException(String.format("Please provide valid fileContext  %s", fileContext), HttpStatus.BAD_REQUEST);
            }
        } catch (DuplicateKeyException throwable) {
            String duplicateKey = filterDuplicateIdFromException(throwable);
            log.error("Duplicate Key found - {}",duplicateKey);
            throw new CustomException(StringUtils.isNotBlank(duplicateKey) ? String.format("Provided Duplicate key for - %s- CSV  - id : %s ", fileContext,duplicateKey) : "Provided Duplicate key", HttpStatus.CONFLICT);
        } catch (Throwable throwable) {
            log.error("Exception Occurred while Converting CSVFile to JSON");
            throw new CustomException(String.format("Exception Occurred while Converting CSVFile to JSON %s ", throwable.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public Map<Long, Account> findAccountsByIdIn(List<Long> ids) {
        //Mapping account id with Account
        List<Account> accounts = accountRepository.findAccountsByIdIn(ids);
        if (CollectionUtils.isEmpty(accounts)) {
            throw new CustomException(String.format("Accounts  Not found for account-IDs %s", ids), HttpStatus.BAD_REQUEST);
        }
        return accounts.stream().collect(Collectors.toMap(Account::getId, account -> account));
    }

    public Map<Long, Account> mapProjectIdWithAccount(List<Long> ids, List<ProjectDto> list) {
        //Mapping account id with Account
        Map<Long, Account> findAccountsByIdIn = findAccountsByIdIn(ids);
        //Mapping project id with account
        Map<Long, Account> mapProjectIdWithAccount = new HashMap<>();
        list.forEach(projectDto -> {
            if (!findAccountsByIdIn.containsKey(projectDto.getAccountId())) {
                log.debug("Account Not found for account-ID: {}",projectDto.getAccountId());
                throw new CustomException(String.format("Account Not found for account-ID %s", projectDto.getAccountId()), HttpStatus.BAD_REQUEST);
            } else {
                mapProjectIdWithAccount.put(projectDto.getId(), findAccountsByIdIn.get(projectDto.getAccountId()));

            }
        });
        return mapProjectIdWithAccount;
    }


    private String filterDuplicateIdFromException(Throwable throwable) {
        Pattern pattern = Pattern.compile("_id: (\\d+)");
        Matcher matcher = pattern.matcher(throwable.getMessage());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
