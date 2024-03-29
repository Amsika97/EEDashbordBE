package com.maveric.digital.service;

import com.maveric.digital.model.Account;
import com.maveric.digital.model.Project;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.repository.AccountRepository;
import com.maveric.digital.repository.ProjectRepository;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.responsedto.CsvMetaDataDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	private final ConversationService conversationService;
	private final AccountRepository accountRepository;
	private final ProjectRepository projectRepository;
	private final ProjectTypeRepository projectTypeRepository;
	private SecureRandom random = new SecureRandom();
	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
	@Override
	public void importCsvMetaData(List<CsvMetaDataDto> metaDataList) {
		logger.info("AdminServiceImpl::importCsvMetaData() Start");
		this.populateMetaDataId(metaDataList);
		Map<Long, Account> accountById = new HashMap<>();
		Map<String,Long> accountNameById = new HashMap<>();
		Map<Long, Project> projectById = new HashMap<>();
		Map<String, ProjectType> projectTypeById = new HashMap<>();

		this.populateAccountMap(accountById,  metaDataList,accountNameById);
		this.populateProjectMap(accountById, projectById, metaDataList);
		this.populateProjectTypeMap(projectTypeById, metaDataList);

		if (!CollectionUtils.isEmpty(accountById.values())) {
			accountRepository.saveAll(accountById.values());
			logger.info("account inserted::::{}",accountById.values());
		}

		if (!CollectionUtils.isEmpty(projectById.values())) {
			projectRepository.insert(projectById.values());
			logger.info("project inserted::::{}",projectById.values());
		}


		if (!CollectionUtils.isEmpty(projectTypeById.values())) {
			//projectTypeRepository.insert(projectTypeById.values());
			logger.info("projectType inserted::::{}",projectTypeById.values());
		}

		logger.info("AdminServiceImpl::importCsvMetaData() End");

	}


	private void populateProjectTypeMap(Map<String, ProjectType> projectTypeById, List<CsvMetaDataDto> metaDataList) {
		metaDataList.forEach(obj -> {
			ProjectType projectType = conversationService.toProjectTypeFromMetaData(obj);
			projectTypeById.put(projectType.getProjectTypeName(), projectType);
		});
	}

	private void populateProjectMap(Map<Long, Account> accountById, Map<Long, Project> projectById,
									List<CsvMetaDataDto> metaDataList) {
		metaDataList.forEach(obj -> {
			Project project = conversationService.toProjectFromMetaData(accountById, obj,
					Math.abs(random.nextLong(1000000000)));
			projectById.put(project.getId(), project);

		});
	}


	private void populateAccountMap(Map<Long, Account> accountById,
									List<CsvMetaDataDto> metaDataList, Map<String, Long> accountNameById) {
		List<Account> accounts=accountRepository.findAll();
		Map<String,Account> accountNameWithIdMapFromDB= accounts.stream().collect(Collectors.toMap(Account::getAccountName, account -> account, (a, b) -> b));
		metaDataList.forEach(obj -> {
			Account account = conversationService.toAccountFromMetaData(obj);
			if (accountNameWithIdMapFromDB.get(account.getAccountName()) != null) {
				obj.setAccountId(accountNameWithIdMapFromDB.get(account.getAccountName()).getId());
				accountById.put(accountNameWithIdMapFromDB.get(account.getAccountName()).getId(), accountNameWithIdMapFromDB.get(account.getAccountName()));
			} else if (accountNameById.get(account.getAccountName()) != null) {
				obj.setAccountId(accountNameById.get(account.getAccountName()));
				accountById.put(accountNameById.get(account.getAccountName()), account);
			} else {
				Long id = Math.abs(random.nextLong(1000000000));
				accountNameById.put(account.getAccountName(), id);
				obj.setAccountId(id);
				accountById.put(id, account);
			}
		});
	}

	private void populateMetaDataId(List<CsvMetaDataDto> metaDataList) {
		if (CollectionUtils.isEmpty(metaDataList)) {
			return;
		}
		metaDataList.forEach(obj -> obj.setId(Math.abs(random.nextLong(1000000000))));
	}

}
