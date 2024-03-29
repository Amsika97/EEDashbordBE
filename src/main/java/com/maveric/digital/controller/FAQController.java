package com.maveric.digital.controller;

import com.maveric.digital.responsedto.FAQCategoryDto;
import com.maveric.digital.service.FAQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class FAQController {

    private final FAQService faqService;
    @GetMapping("/faq/data")
    public ResponseEntity<List<FAQCategoryDto>> getFAQList() {
        log.debug("FaqController::getFAQList() call started");
        List<FAQCategoryDto> faqCategoryDtos= faqService.getFAQList();
        log.debug("FaqController::getFAQList() call ended");
        return ResponseEntity.ok(faqCategoryDtos);

    }
}