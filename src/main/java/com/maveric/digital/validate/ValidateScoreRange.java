package com.maveric.digital.validate;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.maveric.digital.model.embedded.ScoreRange;
import com.maveric.digital.responsedto.ScoreScaleDto;

@Component
public class ValidateScoreRange {
	
	 private static final String MULTI_VALUE = "multiValue";
	 private static final String SINGLE_VALUE = "singleValue";
	static Logger logger = LoggerFactory.getLogger(ValidateScoreRange.class);

	public boolean validateScoreScale(ScoreScaleDto dto) {
		if (MULTI_VALUE.equals(dto.getScoreScaleType())) {
			logger.debug("Validate::validateScoreScale()::Start");

			List<ScoreRange> ranges = dto.getRange();
			for (ScoreRange range : ranges) {
				if (range.getFrom() >= range.getTo()) {
					logger.warn("From range is greater than To range");
					return true;
				}}}
		logger.debug("Validate::validateScoreScale()::End");
		return false;
	}

	public boolean duplicateScoreScale(ScoreScaleDto dto) {
		if ((SINGLE_VALUE.equals(dto.getScoreScaleType())) || (MULTI_VALUE.equals(dto.getScoreScaleType()))) {
			logger.debug("Validate::duplicateScoreScale()::Start");
			Set<Integer> uniqueFromValues = new HashSet<>();
			Set<Integer> uniqueToValues = new HashSet<>();
			List<ScoreRange> ranges = dto.getRange();
			for (ScoreRange range : ranges) {

					int from = range.getFrom();
					int to = range.getTo();

					if (uniqueFromValues.contains(from) && uniqueToValues.contains(to)) {
						logger.error("Duplicate values found!!");
						return true;
					}
				
					logger.info("No Duplicate range found, Values added in the list !!");
					uniqueFromValues.add(from);
					uniqueToValues.add(to);
				}}
		logger.debug("Validate::duplicateScoreScale()::End");
		return false;
	}

	public boolean inBetweenScoreScale(ScoreScaleDto dto) {
		if (MULTI_VALUE.equals(dto.getScoreScaleType())) {
			logger.debug("Validate::inBetweenScoreScale()::Start");
			int previousTo = Integer.MIN_VALUE;
			List<ScoreRange> ranges = dto.getRange();
			Collections.sort(ranges, Comparator.comparingInt(ScoreRange::getFrom));
			for (ScoreRange range : ranges) {
				int from = range.getFrom();
				int to = range.getTo();
				if (from >= to || from < previousTo) {
					logger.error("Score Range is not in proper order");
					return true;
				}
				previousTo = to;

			}
		}
		logger.debug("Validate::InBetweenScoreScale()::End");
		return false;
	}

	public boolean invalidJson(ScoreScaleDto dto) {
		if (SINGLE_VALUE.equals(dto.getScoreScaleType())) {
			logger.debug("Validate::invalidJson()::Start");

			List<ScoreRange> ranges = dto.getRange();
			for (ScoreRange range : ranges) {
					if (!(range.getFrom().equals( range.getTo()))) {
					logger.warn("Invalid Json ");
					return true;
				}
				logger.debug("Validate::invalidJson()::End");
			}}
		return false;
}}
