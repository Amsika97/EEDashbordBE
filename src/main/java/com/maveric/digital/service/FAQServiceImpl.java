package com.maveric.digital.service;

import com.maveric.digital.model.FAQCategory;
import com.maveric.digital.repository.FAQRepository;
import com.maveric.digital.responsedto.FAQCategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class FAQServiceImpl implements FAQService {
    private final FAQRepository faqRepository;
    private final ConversationService conversationService;

    @Override
    public List<FAQCategoryDto> getFAQList() {
        log.debug("FaqServiceImpl - getFAQList() call started");
        List<FAQCategory> faqCategories = faqRepository.findAll();
        List<FAQCategoryDto> faqCategoryDtos = conversationService.toFAQCategoryDtoList(faqCategories);
        log.debug("FaqServiceImpl - getFAQList() call ended");
        return faqCategoryDtos;
    }
}
