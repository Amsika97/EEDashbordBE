package com.maveric.digital.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.model.Audit;
import com.maveric.digital.responsedto.AuditDto;
import com.maveric.digital.service.AuditService;
import com.maveric.digital.service.ConversationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuditController.class})
@ExtendWith({SpringExtension.class})
class AuditControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuditService auditService;
    @MockBean
    ConversationService conversationService;
    @Autowired
    AuditController auditController;
    AuditDto auditDto;
    Audit audit;

    @BeforeEach
    private void insertData() {
        auditDto = new AuditDto(111L, "Ram", "assessment", 111L, "Assessment saved", 111L);
        audit = new Audit();
        BeanUtils.copyProperties(auditDto, audit);
    }
    @Test
    void getAuditsByOperation() throws Exception {
        List<AuditDto> auditDtoList = List.of(auditDto);
        when(this.auditService.getAuditsForOperation(anyLong(), anyLong(), anyString())).thenReturn(auditDtoList);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/audit/{fromDate}/{toDate}/{operationContext}", 111L, 111L, 111L).contentType(MediaType.APPLICATION_JSON).content(
                this.asJsonString(auditDtoList))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(auditDtoList.size())));
    }


    @Test
    void ThrowingCustomExceptionForGetAuditsByOperation() throws Exception {
        when(this.auditService.getAuditsForOperation(anyLong(), anyLong(), anyString())).thenThrow(CustomException.class);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/audit/{fromDate}/{toDate}/{operationContext}", 111L, 111L, 111L).contentType(MediaType.APPLICATION_JSON).content(this.asJsonString(this.auditDto))).andExpect(status().isOk()).andExpect((result) -> {
            Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
        });
    }


    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
    @Test
    void testLogActivitySuccess() throws Exception {
        Audit audit = new Audit();
        when(auditService.logActivity(any(Audit.class))).thenReturn(audit);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/audit/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(audit)))
            .andExpect(status().isOk());
    }


}
