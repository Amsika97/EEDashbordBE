package com.maveric.digital.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.maveric.digital.model.Template;
import com.maveric.digital.responsedto.TemplateInfo;

@Repository
public interface TemplateRepository extends MongoRepository<Template, Long> {
	List<Template> findByProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc(Long projectTypeId);

	Template findTemplateByTemplateNameAndIsActiveTrue(String templateName);
	TemplateInfo findTemplateInfoByTemplateNameAndIsActiveTrue(String templateName);
	@Query("{'id' : ?0}")
	@Update("{'$set': {'isActive': ?1}}")
	void deActivateTemplate(Long id, Boolean isActive);

	List<Template>  findByIsActiveTrue(Sort sortByCreatedAtDesc);

	List<TemplateInfo> findTemplateInfoByProjects_idAndProjectTypes_idAndIsActiveTrueOrderByCreatedAtDesc( Long projectId, Long projectTypeId);

	List<Template> findDistinctByTemplateDisplayNameIsNotNull();
}
