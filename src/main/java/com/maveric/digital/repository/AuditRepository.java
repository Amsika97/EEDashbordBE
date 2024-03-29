package com.maveric.digital.repository;


import com.maveric.digital.model.Audit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditRepository extends MongoRepository<Audit,Long> {

    List<Audit> findByUpdatedByIdOrderByUpdatedAtDesc(String updatedById);
    List<Audit> findByUpdatedAtBetweenAndOperationOrderByUpdatedAtDesc(Long fromDate, Long toDate, String operation);
}
