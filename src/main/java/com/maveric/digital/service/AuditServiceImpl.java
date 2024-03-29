package com.maveric.digital.service;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceCreationException;
import com.maveric.digital.model.Audit;
import com.maveric.digital.repository.AuditRepository;
import com.maveric.digital.responsedto.AuditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final ConversationService conversationService;

    @Override
    public  List<AuditDto> getAuditsForOperation(Long fromDate,Long toDate,String operationContext) {
        log.debug("AuditServiceImpl: getAuditsForOperation() : stared");
       List<Audit> audit = auditRepository.findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(fromDate,toDate,operationContext);
        if (CollectionUtils.isEmpty(audit)) {
            log.error("No Audits found for given range of fromDate:{} and toDate:{} and operationContext :{}", fromDate, toDate, operationContext);
            throw new CustomException(String.format("No Audits Found for given Date range of from : %s, to : %s and operationContext : %s ", fromDate, toDate, operationContext), HttpStatus.OK);
        }
        log.debug("Audit data from DB : {}", audit);
        List<AuditDto> auditDtoList = conversationService.toAuditDtoList(audit);
        log.debug("AuditServiceImpl: getAuditsForOperation() : ended");
        return auditDtoList;
    }

  @Override
  public List<AuditDto> getAuditsByUpdatedById(String updatedById) {
    log.debug("AuditServiceImpl: getAuditsByUpdatedById() : stared");
    if (updatedById == null) {
      log.error("AssessmentServiceImpl::getAuditsByUpdatedBy() {}", updatedById);
      throw new IllegalArgumentException("Invalid SubmittedBy Id : " + updatedById);
    }
    List<Audit> audit = auditRepository.findByUpdatedByIdOrderByUpdatedAtDesc(updatedById);
    if (CollectionUtils.isEmpty(audit)) {
      log.error("No Audits found for given updatedById:{}",updatedById);
      throw new CustomException(String.format("No Audits found for given updatedById: %s",updatedById), HttpStatus.OK);
    }
    log.debug("Audit data from DB : {}", audit);
    List<AuditDto> auditDtoList = conversationService.toAuditDtoList(audit);
    log.debug("AuditServiceImpl: getAuditsByUpdatedById() : ended");
    return auditDtoList;
  }

  @Override
  public Audit logActivity(Audit audit) {
    try {
      log.debug("AuditServiceImpl: logActivity() : started");
      audit.setUpdatedAt(System.currentTimeMillis());
      log.debug("AuditServiceImpl: logActivity() : ended");
      audit=auditRepository.save(audit);
      log.info("Audit data saved in DB{}", audit);
      return audit;
    } catch (RuntimeException e) {
      log.error("An error occurred while saving the Audit: {}", e.getMessage());
      throw new ResourceCreationException("Failed to save the Audit");
    }

  }

}
