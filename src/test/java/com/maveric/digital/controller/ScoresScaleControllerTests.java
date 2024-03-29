package com.maveric.digital.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.model.ScoringScale;
import com.maveric.digital.model.embedded.ScoreRange;
import com.maveric.digital.responsedto.ScoreScaleDto;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.ScoreScaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(ScoresScaleController.class)
@ExtendWith(SpringExtension.class)
class ScoresScaleControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ScoresScaleController scoresScaleController;
    @MockBean
    private ScoreScaleService scoreScaleService;
    @MockBean
    private ConversationService conversationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void getAllScoresScale() throws Exception {
        List<ScoringScale> domain = new ArrayList<>();
        domain.add(new ScoringScale());
        domain.add(new ScoringScale());
        when(conversationService.toScoreDtos(Mockito.anyList())).thenReturn(new ArrayList<>());
        when(scoreScaleService.getScoreScale()).thenReturn(domain);
        ResponseEntity<List<ScoreScaleDto>> response = scoresScaleController.getAllScoresScale();
        mockMvc.perform(get("/v1/scoreScales"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void testAddScoreScaleSuccess() throws Exception {
        ScoringScale sampleScoringScale = new ScoringScale();
        sampleScoringScale.setName("Sample Score Scale");
        sampleScoringScale.setCreatedUserId(1L);
        sampleScoringScale.setScoreScaleType("multiValue");
        List<ScoreRange> range = new ArrayList<>();
        range.add(new ScoreRange());
        sampleScoringScale.setRange(range);
        ScoreScaleDto dto = new ScoreScaleDto();
        dto.setName("Sample Score Scale");
        dto.setCreatedUserId(1L);
        dto.setScoreScaleType("multiValue");
        List<ScoreRange> range1 = new ArrayList<>();
        range1.add(new ScoreRange());
        dto.setRange(range1);
        when(conversationService.toScoreDtos(Mockito.anyList())).thenReturn(new ArrayList<>());
        when(scoreScaleService.addScoreScale(any(ScoreScaleDto.class))).thenReturn(sampleScoringScale);
        ResponseEntity<ScoreScaleDto> response = scoresScaleController.addScoreScale(dto);
        mockMvc.perform(post("/v1/addScoreScale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    @Test
    void testAddScoreScaleEmptyRange() throws Exception {
        ScoreScaleDto dto = new ScoreScaleDto();
        dto.setName("Sample Score Scale");
        dto.setCreatedUserId(1L);
        dto.setScoreScaleType("multiValue");
        dto.setRange(Collections.emptyList());
        when(conversationService.toScoreDtos(Mockito.anyList())).thenReturn(new ArrayList<>());
        when(scoreScaleService.addScoreScale(any(ScoreScaleDto.class))).thenReturn(new ScoringScale());
        mockMvc.perform(post("/v1/addScoreScale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}