package com.maveric.digital.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.maveric.digital.responsedto.UserProfileDto;
import com.maveric.digital.service.UserProfileService;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTests {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserProfileService userProfileService;

    @Test
    void testGetAssessmentsAndMatricBySubmitedBy() throws Exception {

        String userId = "e268befe-648f-49c9-9a36-c0ba87698051";
        String type="self";
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userProfileService.getAssessmentsAndMatricBySubmitedBy(userId,type)).thenReturn(userProfileDto);

        mockMvc.perform(get("/v1/user/profile/details/{userid}/{type}", userId,type)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

}
