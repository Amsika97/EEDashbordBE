package com.maveric.digital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.maveric.digital.model.Project;
import com.maveric.digital.responsedto.ProjectInfo;

@Repository
public interface ProjectRepository extends MongoRepository<Project, Long> {

    List<Project> findByIdIn(List<Long> projectIds);
    List<Project> findAll();
    Optional<List<Project>> findByStatusTrueAndAccountId(Long accountId, Sort sort);
    @Query(value = "{'status':?0}",fields ="{account:1}" )
    Optional<List<Project>> findByStatus(boolean status);

    boolean existsByProjectNameAndAccountId(String projectName, Long accountId);

}

