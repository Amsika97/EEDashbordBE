package com.maveric.digital.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.maveric.digital.model.ProjectType;

public interface ProjectTypeRepository extends MongoRepository<ProjectType, Long> {
	List<ProjectType> findByIdIn(List<Long> projectTypeIds);

}

