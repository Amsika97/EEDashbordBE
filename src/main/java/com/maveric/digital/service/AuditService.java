package com.maveric.digital.service;


import com.maveric.digital.model.Audit;
import com.maveric.digital.responsedto.AuditDto;

import java.util.List;


public interface AuditService {


    List<AuditDto> getAuditsForOperation( Long fromDate, Long toDate,String operationContext);
    List<AuditDto> getAuditsByUpdatedById(String updatedById);
     Audit logActivity(Audit audit);


}
