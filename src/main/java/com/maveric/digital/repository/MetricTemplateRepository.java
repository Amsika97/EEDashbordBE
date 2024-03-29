package com.maveric.digital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.maveric.digital.model.MetricTemplate;

@Repository
public interface MetricTemplateRepository extends MongoRepository<MetricTemplate, Long> {
	List<MetricTemplate> findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(Long projectTypeId);

	MetricTemplate findMetricTemplateByTemplateNameAndIsActiveTrue(String templateName);

	@Query("{'id' : ?0}")
	@Update("{'$set': {'isActive': ?1}}")
	void deActivateTemplate(Long id, Boolean isActive);

	List<MetricTemplate> findByIsActiveTrue();

	Optional<List<MetricTemplate>> findByIsActiveTrueAndProjectTypes(Long projectTypeId);

	List<MetricTemplate> findDistinctByTemplateDisplayNameIsNotNull();
}
