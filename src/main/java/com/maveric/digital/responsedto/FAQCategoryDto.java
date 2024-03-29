package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FAQCategoryDto {
    private List<FAQDto> userFAQ;
    private List<FAQDto> reviewerFAQ;
    private List<FAQDto> adminFAQ;
}
