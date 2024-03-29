package com.maveric.digital.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.ScoreScaleNotFoundException;
import com.maveric.digital.model.ScoringScale;
import com.maveric.digital.repository.ScoreScaleRepository;
import com.maveric.digital.responsedto.ScoreScaleDto;
import com.maveric.digital.validate.ValidateScoreRange;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreScaleServiceImpl implements ScoreScaleService {
	private static final String SCORE_NOT_FOUND = "Score Scale not found";
	private final ScoreScaleRepository scoreRepository;
	private final ObjectMapper mapper;
	private final ValidateScoreRange validateScoreRange;

	@Override
	public List<ScoringScale> getScoreScale() throws JsonProcessingException {
		log.debug("ScoreScaleServiceImpl::getScoreScale()::Start");
		List<ScoringScale> scores = scoreRepository.findAll();
		if (CollectionUtils.isEmpty(scores)) {
			throw new ScoreScaleNotFoundException(SCORE_NOT_FOUND);
		}
		mapper.writeValueAsString(scores);
		log.debug("ScoreScaleServiceImpl::getScoreScale()::scores::{}", scores);
		log.debug("ScoreScaleServiceImpl::getScoreScale()::End");
		return scores;
	}

	@Override
	public ScoringScale addScoreScale(ScoreScaleDto dto) throws ResourceCreationException {
		try {
			log.debug("ScoreScaleServiceImpl::addScoreScale()::Start");
			if (validateScoreRange.duplicateScoreScale(dto)) {
				log.error("Duplicate Score Scale Range!! Given Scale range is already present, dto-{}", dto);
				throw new CustomException("Duplicate Score Scale Range!! Given Scale range is already present",
						HttpStatus.BAD_REQUEST);
			}
			if (validateScoreRange.validateScoreScale(dto)) {
				log.error("Invalid Score Scale Range!! From values should be less than To values, dto-{}", dto);
				throw new CustomException("Invalid Score Scale Range!! From values should be less than To values",
						HttpStatus.BAD_REQUEST);
			}			
			if (validateScoreRange.inBetweenScoreScale(dto)) {
				log.error("Score Scale Range is not in proper order!!, dto-{}", dto);
				throw new CustomException("Score Scale Range is not in proper order!!", HttpStatus.BAD_REQUEST);
			}
			if (validateScoreRange.invalidJson(dto)) {
				log.error("Invalid JSON, dto-{}", dto);
				throw new CustomException("Invalid Json for the given scoreScaletype!!", HttpStatus.BAD_REQUEST);
			}
			ScoringScale scoringScale = new ScoringScale();
			scoringScale.setName(dto.getName());
			scoringScale.setCreatedUserId(dto.getCreatedUserId());
			scoringScale.setScoreScaleType(dto.getScoreScaleType());
			scoringScale.setRange(dto.getRange());

			scoringScale = scoreRepository.save(scoringScale);
			log.debug("Score scale data saved successfully, scoringScale-{}", scoringScale);
			log.debug("ScoreScaleServiceImpl::addScoreScale()::End");
			return scoringScale;
		} catch (DataIntegrityViolationException | ConstraintViolationException | CustomException e) {
			throw e;
		} catch (Exception ex) {
			throw new ResourceCreationException(String.format("error {%s} saving score scale {%s}", ex, dto));
		}
	}
}