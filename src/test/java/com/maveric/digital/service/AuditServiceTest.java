package com.maveric.digital.service;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.model.Audit;
import com.maveric.digital.repository.AuditRepository;
import com.maveric.digital.responsedto.AuditDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class AuditServiceTest {
    @Autowired
    AuditService auditService;

    @MockBean
    ConversationService conversationService;

    @MockBean
    AuditRepository auditRepository;
    AuditDto auditDto;
    Audit audit;

    @BeforeEach
    private void insertData() {
        auditDto = new AuditDto(111L, "Ram", "assessment", 111L, "Assessment saved", 111L);
        audit = new Audit();
        BeanUtils.copyProperties(auditDto, audit);
    }


    @Test
    void getTemplateByIdTest() {
        List<AuditDto> auditDtoList = List.of(auditDto);
        List<Audit> auditList = List.of(audit);
        when(this.auditRepository.findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(anyLong(), anyLong(), anyString())).thenReturn(auditList);
        when(this.conversationService.toAuditDtoList(auditList)).thenReturn(auditDtoList);
        List<AuditDto> auditDtos = this.auditService.getAuditsForOperation(111L, 111L, "Assessment_Saved");
        Assertions.assertEquals(auditDtoList, auditDtos);
        Mockito.verify(this.auditRepository).findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(anyLong(), anyLong(), anyString());
        Mockito.verify(this.conversationService).toAuditDtoList(auditList);
    }

    @Test
    void ThrowingCustomExceptionForGetAuditsByOperationByMakingAuditListNull() {
        List<Audit> auditList = List.of();
        when(this.auditRepository.findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(anyLong(), anyLong(), anyString())).thenReturn(auditList);
        assertThrows(CustomException.class, () -> {
            auditService.getAuditsForOperation(111L, 111L, "Assessment_Saved");
        });
        Mockito.verify(this.auditRepository).findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(anyLong(), anyLong(), anyString());

    }

    @Test
    void ThrowingCustomExceptionForGetAuditsByOperation() {
        when(this.auditRepository.findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(anyLong(), anyLong(), anyString())).thenThrow(CustomException.class);
        assertThrows(CustomException.class, () -> {
            auditService.getAuditsForOperation(111L, 111L, "Assessment_Saved");
        });
        Mockito.verify(this.auditRepository).findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(anyLong(), anyLong(), anyString());

    }
    @Test
    void testLogActivity_Success() {
        Audit audit = new Audit();
        when(auditRepository.save(any(Audit.class))).thenReturn(audit);
        Audit result = auditService.logActivity(audit);
        assertNotNull(result.getUpdatedAt());
    }
    @Test
    void testLogActivity_Exception() {
        Audit audit = new Audit();
        when(auditRepository.save(any(Audit.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(ResourceCreationException.class, () -> {
            auditService.logActivity(audit);
        });

    }

}
