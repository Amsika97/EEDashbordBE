package com.maveric.digital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.maveric.digital.model.ScoreCategory;


@Repository
public interface ScoreCategoryRepository extends MongoRepository<ScoreCategory, Long> {
	Optional<ScoreCategory> findById(Long scoreCategoryId);
	List<ScoreCategory> findByCategoryNameIn(List<String> categoryNames);


}
