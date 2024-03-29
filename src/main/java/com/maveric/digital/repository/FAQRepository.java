package com.maveric.digital.repository;

import com.maveric.digital.model.FAQCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends MongoRepository<FAQCategory, Long> {

}