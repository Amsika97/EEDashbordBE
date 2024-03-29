package com.maveric.digital.service;
import com.maveric.digital.model.ProjectType;
import com.maveric.digital.repository.ProjectTypeRepository;
import com.maveric.digital.responsedto.ProjectTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class ProjectTypeServiceImpl implements ProjectTypeService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectTypeServiceImpl.class);
    private final ProjectTypeRepository projectTypeRepository;
    private final static String SELECT_ALL="Select All";

    @Autowired
    private  ConversationService conversationService;
    @Autowired
    public ProjectTypeServiceImpl(ProjectTypeRepository projectTypeRepository) {
        this.projectTypeRepository = projectTypeRepository;
    }
    @Override
    public List<ProjectTypeDto> getAll() {
        logger.debug("ProjecttypeService- getAll() call started ");
        List<ProjectType> projectTypes = projectTypeRepository.findAll(Sort.by("projectTypeName"));
        if (!CollectionUtils.isEmpty(projectTypes)) {
        	logger.debug("Fetched projectTypes list from db::{}",projectTypes);
            return conversationService.convertToProjectTypeDto(this.moveSellectAllToTop(projectTypes));
        }
        logger.debug("Fetched projecttypelist from db : {} ", projectTypes);
        logger.debug("BusinessUnitService::getBusinessUnitById() call completed");
        return Collections.emptyList();
    }


    @Override
    public List<ProjectTypeDto> getAllfilteredprojectTypes() {
        logger.debug("ProjecttypeService- getAll() call started ");
        List<ProjectType> projectTypes = projectTypeRepository.findAll(Sort.by("projectTypeName"));
        if (!CollectionUtils.isEmpty(projectTypes)) {
        List<ProjectTypeDto> allProjectTypes = conversationService.convertToProjectTypeDto(projectTypes);
        List<ProjectTypeDto> filteredProjectTypes = allProjectTypes.stream()
                .filter(projectType -> !projectType.getProjectTypeName().equals("All"))
                .collect(Collectors.toList());
        return filteredProjectTypes;
        }
        logger.debug("Fetched projecttypelist from db : {} ", projectTypes);
        logger.debug("BusinessUnitService::getBusinessUnitById() call completed");
        return Collections.emptyList();

    }

    
	private List<ProjectType> moveSellectAllToTop(List<ProjectType> projectTypes) {
		Optional<ProjectType> projectType = projectTypes.stream().filter(p -> p.getProjectTypeName().equals(SELECT_ALL))
				.findFirst();
		if(projectType.isPresent()) {
			projectTypes.remove(projectType.get());
			projectTypes.add(0,projectType.get());
		}
		return projectTypes;

	}

}