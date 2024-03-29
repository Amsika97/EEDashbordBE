package com.maveric.digital.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.maveric.digital.responsedto.ScoreCategoryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.ScoreCategoryNotFoundException;
import com.maveric.digital.model.ScoreCategory;
import com.maveric.digital.repository.ScoreCategoryRepository;
@Service
public class ScoreCategoryServiceImpl implements ScoreCategoryService {
	Logger logger = LoggerFactory.getLogger(ScoreCategoryServiceImpl.class);
	public static final String SCORE_CATEGORY_NOT_FOUND = "Score Category not found";
	private final ObjectMapper mapper;
	private final ScoreCategoryRepository scoreCategoryRepository;
	
	
	
	@Autowired
	public ScoreCategoryServiceImpl(ObjectMapper mapper, ScoreCategoryRepository scoreCategoryRepository) {
		this.mapper = mapper;
		this.scoreCategoryRepository = scoreCategoryRepository;
	}
	@Override
	public List<ScoreCategory> getAllScoreCategories() throws JsonProcessingException {
		logger.debug("ScoreCategoryServiceImpl::getAllScoreCategories()::Start");
		List<ScoreCategory> list = scoreCategoryRepository.findAll();
		mapper.writeValueAsString(list);
		logger.debug("ScoreCategoryServiceImpl::getAllScoreCategories()::list::{}",list);
		logger.debug("ScoreCategoryServiceImpl::getAllScoreCategories()::End");
		return list;
	}
	
	@Override
	public ScoreCategory getScoreCategoryById(Long scoreCategoryId) throws JsonProcessingException {
		logger.debug("ScoreCategoryServiceImpl::getScoreCategoryById()::Start");
		Optional<ScoreCategory> scoreCategory = scoreCategoryRepository.findById(scoreCategoryId);
		if(scoreCategory.isEmpty()) {
		 throw new ScoreCategoryNotFoundException(SCORE_CATEGORY_NOT_FOUND);
		}
		mapper.writeValueAsString(scoreCategory.get());
		logger.debug("ScoreCategoryServiceImpl::getScoreCategoryById()::scoreCategory::{}",scoreCategory.get());
		logger.debug("ScoreCategoryServiceImpl::getScoreCategoryById()::End");
		return scoreCategory.get();
	}

	@Override
	public ScoreCategory addScoreCategory(ScoreCategoryDto request) {
		logger.debug("ScoreCategoryServiceImpl::addScoreCategory()::Start");
		if (Objects.isNull(request)) {
			logger.error("request is not valid {}",request);
			throw new IllegalArgumentException("Not Valid request");

		}
		ScoreCategory scoreCategory = new ScoreCategory();
		scoreCategory.setCategoryName(request.getCategoryName());
		scoreCategory.setCategoryOptions(request.getCategoryOptions());
		scoreCategory.setCreatedBy(request.getCreatedBy());
		scoreCategory.setCreatedAt(request.getCreatedAt());
		scoreCategory.setUpdatedAt(request.getUpdatedAt());
		logger.debug("ScoreCategoryServiceImpl::addScoreCategory()::end");
		return scoreCategoryRepository.save(scoreCategory);
		}

}
