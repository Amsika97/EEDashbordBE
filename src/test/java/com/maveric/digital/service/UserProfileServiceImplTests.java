package com.maveric.digital.service;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.maveric.digital.model.Assessment;
import com.maveric.digital.model.MetricSubmitted;
import com.maveric.digital.model.User;
import com.maveric.digital.model.embedded.AssessmentStatus;
import com.maveric.digital.repository.AssessmentRepository;
import com.maveric.digital.repository.MetricSubmittedRepository;
import com.maveric.digital.repository.UserRepository;
import com.maveric.digital.responsedto.UserDto;
import com.maveric.digital.responsedto.UserProfileDto;

@SpringBootTest
public class UserProfileServiceImplTests {

    @MockBean
    private AssessmentRepository assessmentRepository;

    @MockBean
    private MetricSubmittedRepository metricSubmittedRepository;

    @MockBean
    private MetricConversationService conversationService;

    @MockBean
    private ConversationService conversationServices;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetAssessmentsAndMatricBySubmitedBy() {
        String submittedBy = "e268befe-648f-49c9-9a36-c0ba87698051";
        String type = "self";
        
        List<Assessment> mockAssessmentList = new ArrayList<>();
        mockAssessmentList.add(new Assessment());
        List<MetricSubmitted> mockMetricSubmittedList = new ArrayList<>();
        mockMetricSubmittedList.add(new MetricSubmitted());
        
        User mockUser = new User();
        mockUser.setId(1L); 
        mockUser.setOid(UUID.fromString("e268befe-648f-49c9-9a36-c0ba87698051"));
        
        UserDto userDto = new UserDto();
        userDto.setId(1L); 
        userDto.setOid(UUID.fromString("e268befe-648f-49c9-9a36-c0ba87698051"));
        
        UserProfileDto expectedUserProfileDto = new UserProfileDto();
        expectedUserProfileDto.setUserDto(userDto);
        
        List<AssessmentStatus> statuses = List.of(AssessmentStatus.SUBMITTED, AssessmentStatus.REVIEWED, AssessmentStatus.SAVE);

        when(assessmentRepository.findBySubmitStatusInAndSubmitedByOrReviewerId(submittedBy, statuses)).thenReturn(mockAssessmentList);
        when(metricSubmittedRepository.findBySubmitStatusInAndSubmitedByOrReviewerId(submittedBy, statuses)).thenReturn(mockMetricSubmittedList);
        when(userRepository.findByOid(UUID.fromString(submittedBy))).thenReturn(Optional.of(mockUser));

        when(conversationServices.toMetricAndAssessmentDetailsDto(mockAssessmentList)).thenReturn(new ArrayList<>());
        when(conversationService.toMetricSubmitted(mockMetricSubmittedList)).thenReturn(new ArrayList<>());
        when(conversationServices.convertToUserDto(mockUser)).thenReturn(userDto);

        UserProfileServiceImpl userProfileService = new UserProfileServiceImpl(assessmentRepository, metricSubmittedRepository, userRepository, conversationService, conversationServices);
        UserProfileDto actualUserProfileDto = userProfileService.getAssessmentsAndMatricBySubmitedBy(submittedBy, type);

        verify(conversationService).toMetricSubmitted(mockMetricSubmittedList);
        verify(conversationServices).toMetricAndAssessmentDetailsDto(mockAssessmentList);

        assertEquals(expectedUserProfileDto.getUserDto().getId(), actualUserProfileDto.getUserDto().getId());
    }

}

