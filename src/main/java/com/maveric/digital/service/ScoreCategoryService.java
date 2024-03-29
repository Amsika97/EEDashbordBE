package com.maveric.digital.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.model.ScoreCategory;
import com.maveric.digital.responsedto.ScoreCategoryDto;

public interface ScoreCategoryService {
	List<ScoreCategory> getAllScoreCategories() throws JsonProcessingException;
	ScoreCategory getScoreCategoryById(Long scoreCategoryId) throws JsonProcessingException;
	
	ScoreCategory addScoreCategory(ScoreCategoryDto request);

}
