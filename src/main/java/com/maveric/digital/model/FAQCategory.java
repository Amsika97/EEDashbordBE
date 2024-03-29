package com.maveric.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "faqs")
public class FAQCategory extends IdentifiedEntity {
    private List<FAQDetails> userFAQ;
    private List<FAQDetails> reviewerFAQ;
    private List<FAQDetails> adminFAQ;

}
