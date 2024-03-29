package com.maveric.digital.service;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.TemplateNotFoundException;
import com.maveric.digital.responsedto.ScoreCategoryDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maveric.digital.exceptions.ScoreCategoryNotFoundException;
import com.maveric.digital.model.ScoreCategory;
import com.maveric.digital.model.embedded.Options;
import com.maveric.digital.repository.ScoreCategoryRepository;

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = { ConversationService.class, ModelMapper.class,ScoreCategoryServiceImpl.class })
class ScoreCategoryServiceTests {

	@Autowired
	private ScoreCategoryServiceImpl scoreCategoryService;
	@MockBean
	private ScoreCategoryRepository scoreCategoryRepository;
	@Autowired
	private ObjectMapper objectMapper;

	private ScoreCategory populateScoreCategory() {
		ScoreCategory scoreCategory = new ScoreCategory();
		scoreCategory.setCategoryName("importance");
		scoreCategory.setCategoryOptions(singletonList(this.populateOptions()));
		scoreCategory.setCreatedAt(System.currentTimeMillis());
		scoreCategory.setCreatedBy("Ravi");
		scoreCategory.setId(1l);
		scoreCategory.setUpdatedAt(System.currentTimeMillis());

		return scoreCategory;
	}

	private Options populateOptions() {
		Options options = new Options();
		options.setLabel("Very well-defined");
		options.setOptionId(1l);
		options.setOptionIndex(1);
		return options;
	}

	@Test
	void getAllScoreCategories() throws JsonProcessingException {
		ScoreCategory scoreCategory = this.populateScoreCategory();
		when(scoreCategoryRepository.findAll()).thenReturn(List.of(scoreCategory));
		when(scoreCategoryService.getAllScoreCategories()).thenReturn(List.of(scoreCategory));
		List<ScoreCategory> scoreCategories = scoreCategoryService.getAllScoreCategories();
		assertEquals(scoreCategories, List.of(scoreCategory));

	}
	@Test
	void getScoreCategoryById() throws JsonProcessingException {
		ScoreCategory scoreCategory = this.populateScoreCategory();
		when(scoreCategoryRepository.findById(anyLong())).thenReturn(Optional.of(scoreCategory));
		ScoreCategory scoreCategoryObj=scoreCategoryService.getScoreCategoryById(anyLong());
		assertEquals(scoreCategoryObj, scoreCategory);
	}

	@Test
	void testScoreCategoryNotFoundException() {
		when(scoreCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());
		ScoreCategoryNotFoundException exception = assertThrows(ScoreCategoryNotFoundException.class, () -> {
			scoreCategoryService.getScoreCategoryById(anyLong());
		});
		assertEquals(ScoreCategoryServiceImpl.SCORE_CATEGORY_NOT_FOUND, exception.getMessage());
	}
	@Test
	void testAddNewScoreCategory() throws IOException {
		File jsonFile = new ClassPathResource("sample/ScoreCategoryDto.json").getFile();
		ScoreCategoryDto scoreCategoryDto = objectMapper.readValue(Files.readString(jsonFile.toPath()), ScoreCategoryDto.class);
		ScoreCategory scoreCategory=new ScoreCategory();
		BeanUtils.copyProperties(scoreCategoryDto,scoreCategory);
		when(scoreCategoryRepository.save(scoreCategory)).thenReturn(scoreCategory);
		ScoreCategory result=scoreCategoryService.addScoreCategory(scoreCategoryDto);
		assertEquals(result,scoreCategory);
	}
	@Test
	void testAddNewScoreCategoryInvalidJson() throws IOException {
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> scoreCategoryService.addScoreCategory(null));
		assertEquals("Not Valid request", illegalArgumentException.getMessage());

	}

}
