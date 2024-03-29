package com.maveric.digital.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.responsedto.AssessmentDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.maveric.digital.responsedto.ScoreCategoryDto;
import com.maveric.digital.service.ConversationService;
import com.maveric.digital.service.ScoreCategoryService;

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = { ScoreCategoryController.class, ScoreCategoryService.class,
		ConversationService.class })
class ScoreCategoryControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ScoreCategoryService scoreCategoryService;
	@MockBean
	private ConversationService conversationService;

	@Autowired
	private ScoreCategoryController scoreCategoryController;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testGetAllScoreCategories() throws Exception {
		ResponseEntity<List<ScoreCategoryDto>> response = scoreCategoryController.getAllScoreCategories();
		mockMvc.perform(get("/v1/score/category").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}

	@Test
	void getScoreCategoryById() throws Exception {
		ResponseEntity<ScoreCategoryDto> response = scoreCategoryController.getScoreCategoryById(anyLong());
		mockMvc.perform(get("/v1/score/category/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}
	@Test
	void addNewScoreCateegory() throws Exception {
		File jsonFile = new ClassPathResource("sample/ScoreCategoryDto.json").getFile();
		ScoreCategoryDto scoreCategoryDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), ScoreCategoryDto.class);
		when(conversationService.toScoreCategoryDto(scoreCategoryService.addScoreCategory(scoreCategoryDto))).thenReturn(scoreCategoryDto);
		ResponseEntity<ScoreCategoryDto> response = scoreCategoryController.addNewScoreCategory(scoreCategoryDto);

		mockMvc.perform(post("/v1/score/category/save")
						.content(Files.readString(jsonFile.toPath()))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(Files.readString(jsonFile.toPath()))).andReturn();
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}
}
