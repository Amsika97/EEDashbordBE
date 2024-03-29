package com.maveric.digital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.maveric.digital.model.ScoringScale;


@Repository
public interface ScoreScaleRepository extends MongoRepository<ScoringScale, Long> {
	
}
