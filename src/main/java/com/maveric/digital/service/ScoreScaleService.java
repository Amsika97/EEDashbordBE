package com.maveric.digital.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.model.ScoringScale;
import com.maveric.digital.responsedto.ScoreScaleDto;

public interface ScoreScaleService {
	List<ScoringScale> getScoreScale() throws JsonProcessingException;
	ScoringScale addScoreScale(ScoreScaleDto dto);

}
