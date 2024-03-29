package com.maveric.digital.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.exceptions.ScoreScaleNotFoundException;
import com.maveric.digital.model.ScoringScale;
import com.maveric.digital.model.embedded.ScoreRange;
import com.maveric.digital.repository.ScoreScaleRepository;
import com.maveric.digital.responsedto.ScoreScaleDto;
import com.maveric.digital.validate.ValidateScoreRange;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class ScoreScaleServiceImplTest {

    @Mock
    private ScoreScaleRepository scoreRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ValidateScoreRange validateScoreRange;

    @InjectMocks
    private ScoreScaleServiceImpl scoreScaleService;

    private ScoreScaleDto dto;
    private ScoringScale scoringScale;

    @BeforeEach
    public void init() {
        dto = new ScoreScaleDto();
        dto.setName("Importance");
        dto.setCreatedUserId(1L);
        List<ScoreRange> scoreRanges = new ArrayList<>();
        ScoreRange scoreRange1 = new ScoreRange();
        scoreRange1.setFrom(1);
        scoreRange1.setTo(5);
        scoreRange1.setOptionIndex(1);
        scoreRanges.add(scoreRange1);
        ScoreRange scoreRange2 = new ScoreRange();
        scoreRange2.setFrom(6);
        scoreRange2.setTo(10);
        scoreRange2.setOptionIndex(2);
        scoreRanges.add(scoreRange2);
        dto.setRange(scoreRanges);
        scoringScale = new ScoringScale();
        BeanUtils.copyProperties(dto, scoringScale);
    }

    @Test
    void testAddScoreScale() {
        when(scoreRepository.save(any(ScoringScale.class))).thenReturn(scoringScale);
        ScoringScale result = scoreScaleService.addScoreScale(dto);
        assertEquals(scoringScale, result);
    }

    @Test
    void testGetScoreScale() throws JsonProcessingException {
        List<ScoringScale> scoringScales = new ArrayList<>();
        scoringScales.add(scoringScale);
        when(scoreRepository.findAll()).thenReturn(scoringScales);
        List<ScoringScale> result = scoreScaleService.getScoreScale();
        verify(scoreRepository, times(1)).findAll();
        assertEquals(scoringScales, result);
    }

    @Test
    void testGetScoreScaleEmpty() {
        when(scoreRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ScoreScaleNotFoundException.class, () -> scoreScaleService.getScoreScale());
        Mockito.verify(this.scoreRepository).findAll();

    }

    @Test
    void throwingCustomExceptionForValidateScoreScale() {
        when(validateScoreRange.validateScoreScale(any(ScoreScaleDto.class))).thenThrow(CustomException.class);
        assertThrows(CustomException.class, () -> scoreScaleService.addScoreScale(dto));
        Mockito.verify(this.validateScoreRange).validateScoreScale(any(ScoreScaleDto.class));

    }

    @Test
    void throwingCustomExceptionForDuplicateScoreScale() {
        when(validateScoreRange.duplicateScoreScale(any(ScoreScaleDto.class))).thenThrow(CustomException.class);
        assertThrows(CustomException.class, () -> scoreScaleService.addScoreScale(dto));
        Mockito.verify(this.validateScoreRange).duplicateScoreScale(any(ScoreScaleDto.class));

    }

    @Test
    void throwingCustomExceptionForInBetweenScoreScale() {
        when(validateScoreRange.inBetweenScoreScale(any(ScoreScaleDto.class))).thenThrow(CustomException.class);
        assertThrows(CustomException.class, () -> scoreScaleService.addScoreScale(dto));
        Mockito.verify(this.validateScoreRange).inBetweenScoreScale(any(ScoreScaleDto.class));

    }

    @Test
    void throwingResourceCreationExceptionForAddScoreScale() {
        when(scoreRepository.save(any(ScoringScale.class))).thenThrow(ResourceCreationException.class);
        assertThrows(ResourceCreationException.class, () -> scoreScaleService.addScoreScale(dto));
        Mockito.verify(this.scoreRepository).save(any(ScoringScale.class));

    }
    @Test
    void testAddScoreScaleDuplicateScoreScaleException() {
        ScoreScaleDto dto = new ScoreScaleDto();
        when(validateScoreRange.duplicateScoreScale(dto)).thenReturn(true);

        assertThrows(CustomException.class, () -> scoreScaleService.addScoreScale(dto));
        verify(scoreRepository, never()).save(any(ScoringScale.class));
    }
    @Test
    void testAddScoreScaleInvalidScoreScaleException() {
        ScoreScaleDto dto = new ScoreScaleDto();
        when(validateScoreRange.duplicateScoreScale(dto)).thenReturn(false);
        when(validateScoreRange.validateScoreScale(dto)).thenReturn(true);

        assertThrows(CustomException.class, () -> scoreScaleService.addScoreScale(dto));
        verify(scoreRepository, never()).save(any(ScoringScale.class));
    }
    @Test
    void testAddScoreScaleInBetweenScoreScaleException() {
        ScoreScaleDto dto = new ScoreScaleDto();
        when(validateScoreRange.duplicateScoreScale(dto)).thenReturn(false);
        when(validateScoreRange.validateScoreScale(dto)).thenReturn(false);
        when(validateScoreRange.inBetweenScoreScale(dto)).thenReturn(true);

        assertThrows(CustomException.class, () -> scoreScaleService.addScoreScale(dto));
        verify(scoreRepository, never()).save(any(ScoringScale.class));
    }
    @Test
    void testAddScoreScaleInvalidJsonException() {
        ScoreScaleDto dto = new ScoreScaleDto();
        when(validateScoreRange.duplicateScoreScale(dto)).thenReturn(false);
        when(validateScoreRange.validateScoreScale(dto)).thenReturn(false);
        when(validateScoreRange.inBetweenScoreScale(dto)).thenReturn(false);
        when(validateScoreRange.invalidJson(dto)).thenReturn(true);

        assertThrows(CustomException.class, () -> scoreScaleService.addScoreScale(dto));
        verify(scoreRepository, never()).save(any(ScoringScale.class));
    }
}
