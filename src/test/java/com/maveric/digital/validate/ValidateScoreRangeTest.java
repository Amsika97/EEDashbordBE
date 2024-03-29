package com.maveric.digital.validate;

import com.maveric.digital.model.embedded.ScoreRange;
import com.maveric.digital.responsedto.ScoreScaleDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ValidateScoreRange.class})
@ExtendWith(SpringExtension.class)
class ValidateScoreRangeTest {
    @Autowired
    private ValidateScoreRange validateScoreRange;

    @Test
    void testInvalidScoreScale() {
        assertFalse(validateScoreRange.validateScoreScale(new ScoreScaleDto()));
    }

    @Test
    void testInvalidScoreScaleType() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getScoreScaleType()).thenReturn("Score Scale Type");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::validateScoreScale()::Start");
        boolean result = validateScoreRange.validateScoreScale(dto);
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testInvalidMultiValueScoreScale() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(new ArrayList<>());
        when(dto.getScoreScaleType()).thenReturn("multiValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::validateScoreScale()::Start");
        boolean result = validateScoreRange.validateScoreScale(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testInvalidScoreRange() {
        ScoreRange scoreRange = new ScoreRange(1, 1, 1);
        scoreRange.setFrom(0);
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(scoreRange);
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("multiValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::validateScoreScale()::Start");
        boolean result = validateScoreRange.validateScoreScale(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testValidScoreRange() {
        ScoreRange scoreRange = new ScoreRange(1, 0, 1);
        scoreRange.setFrom(0);
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(scoreRange);
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("multiValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::validateScoreScale()::Start");
        boolean result = validateScoreRange.validateScoreScale(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertTrue(result);
    }

    @Test
    void testInvalidDuplicateScoreScale() {
        assertFalse(validateScoreRange.duplicateScoreScale(new ScoreScaleDto()));
    }


    @Test
    void testInvalidDuplicateScoreScaleType() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getScoreScaleType()).thenReturn("Score Scale Type");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::duplicateScoreScale()::Start");
        boolean result = validateScoreRange.duplicateScoreScale(dto);
        verify(dto, atLeast(1)).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testDuplicateScoreScaleWithSingleValue() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getScoreScaleType()).thenReturn("singleValue");
        assertFalse(validateScoreRange.duplicateScoreScale(dto));
        verify(dto, atLeast(1)).getScoreScaleType();
    }

    @Test
    void testDuplicateScoreScaleWithMultiValue() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getScoreScaleType()).thenReturn("multiValue");
        assertFalse(validateScoreRange.duplicateScoreScale(dto));
        verify(dto, atLeast(1)).getScoreScaleType();
    }

    @Test
    void testInvalidDuplicateSingleValueScoreScale() {
        ScoreRange scoreRange = new ScoreRange(1, 1, 1);
        scoreRange.setFrom(0);
        ScoreRange duplicateRange = mock(ScoreRange.class);
        when(duplicateRange.getFrom()).thenReturn(0);
        when(duplicateRange.getTo()).thenReturn(1);
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(duplicateRange);
        scoreRangeList.add(scoreRange);
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("singleValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::duplicateScoreScale()::Start");
        boolean result = validateScoreRange.duplicateScoreScale(dto);
        verify(duplicateRange).getFrom();
        verify(duplicateRange).getTo();
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertTrue(result);
    }

    @Test
    void testInvalidDuplicateSingleValueScoreScaleWithSameRange() {
        ScoreRange scoreRange = new ScoreRange(1, 1, 1);
        scoreRange.setFrom(0);
        ScoreRange duplicateRange = mock(ScoreRange.class);
        when(duplicateRange.getFrom()).thenReturn(0);
        when(duplicateRange.getTo()).thenReturn(0);
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(duplicateRange);
        scoreRangeList.add(scoreRange);
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("singleValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::duplicateScoreScale()::Start");
        boolean result = validateScoreRange.duplicateScoreScale(dto);
        verify(duplicateRange).getFrom();
        verify(duplicateRange).getTo();
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testInvalidInBetweenScoreScale() {
        assertFalse(validateScoreRange.inBetweenScoreScale(new ScoreScaleDto()));
    }

    @Test
    void testInvalidInBetweenScoreScaleType() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getScoreScaleType()).thenReturn("Score Scale Type");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::inBetweenScoreScale()::Start");
        boolean result = validateScoreRange.inBetweenScoreScale(dto);
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testInvalidMultiValueInBetweenScoreScale() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(new ArrayList<>());
        when(dto.getScoreScaleType()).thenReturn("multiValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::inBetweenScoreScale()::Start");
        boolean result = validateScoreRange.inBetweenScoreScale(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testInvalidMinValueInBetweenScoreScale() {
        ScoreRange scoreRange = new ScoreRange(1, 1, 1);
        scoreRange.setFrom(Integer.MIN_VALUE);
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(scoreRange);
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("multiValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::inBetweenScoreScale()::Start");
        boolean result = validateScoreRange.inBetweenScoreScale(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testValidInBetweenScoreScale() {
        ScoreRange scoreRange = new ScoreRange(1, Integer.MIN_VALUE, 1);
        scoreRange.setFrom(Integer.MIN_VALUE);
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(scoreRange);
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("multiValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::inBetweenScoreScale()::Start");
        boolean result = validateScoreRange.inBetweenScoreScale(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertTrue(result);
    }

    @Test
    void testInvalidJson() {
        assertFalse(validateScoreRange.invalidJson(new ScoreScaleDto()));
    }

    @Test
    void testInvalidJsonType() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getScoreScaleType()).thenReturn("Score Scale Type");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::invalidJson()::Start");
        boolean result = validateScoreRange.invalidJson(dto);
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testInvalidJsonSingleValue() {
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(new ArrayList<>());
        when(dto.getScoreScaleType()).thenReturn("singleValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::invalidJson()::Start");
        boolean result = validateScoreRange.invalidJson(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testInvalidJsonSameRange() {
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(new ScoreRange(1, 1, 1));
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("singleValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::invalidJson()::Start");
        boolean result = validateScoreRange.invalidJson(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertFalse(result);
    }

    @Test
    void testValidJson() {
        ScoreRange scoreRange = new ScoreRange();
        scoreRange.setFrom(1);
        ArrayList<ScoreRange> scoreRangeList = new ArrayList<>();
        scoreRangeList.add(scoreRange);
        ScoreScaleDto dto = mock(ScoreScaleDto.class);
        when(dto.getRange()).thenReturn(scoreRangeList);
        when(dto.getScoreScaleType()).thenReturn("singleValue");
        doNothing().when(dto).setScoreScaleType(Mockito.<String>any());
        dto.setScoreScaleType("Validate::invalidJson()::Start");
        boolean result = validateScoreRange.invalidJson(dto);
        verify(dto).getRange();
        verify(dto).getScoreScaleType();
        verify(dto).setScoreScaleType(Mockito.<String>any());
        assertTrue(result);
    }
    @Test
    void testInBetweenScoreScaleConditionsTrue() {
        ScoreRange range1 = new ScoreRange(1, 0, 5);
        ScoreRange range2 = new ScoreRange(2, 6, 10);
        ScoreRange range3 = new ScoreRange(3, 4, 8);
        ScoreScaleDto dto = new ScoreScaleDto();
        dto.setScoreScaleType("multiValue");
        dto.setRange(Arrays.asList(range1, range2, range3));
        boolean result = validateScoreRange.inBetweenScoreScale(dto);
        assertTrue(result);
    }
    @Test
    void testInBetweenScoreScaleBothBranches() {
        ScoreRange range1 = new ScoreRange(1, 0, 5);
        ScoreRange range2 = new ScoreRange(2, 6, 10);
        ScoreScaleDto dto = new ScoreScaleDto();
        dto.setScoreScaleType("multiValue");
        dto.setRange(Arrays.asList(range1, range2));


        range1.setFrom(6);
        range1.setTo(5);
        range2.setFrom(4);
        range2.setTo(10);

        boolean result = validateScoreRange.inBetweenScoreScale(dto);
        assertTrue(result);
    }

}
