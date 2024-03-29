package com.maveric.digital.controller;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.service.file.FileConvertorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping({"/v1"})
@RequiredArgsConstructor
@Slf4j
public class FileConvertorController {

    private final FileConvertorService fileConvertorService;

    @PostMapping(value = "/csv/file/{fileContext}")
    public ResponseEntity<Object> uploadCSVFile(@RequestParam("file") MultipartFile file,
                                                @PathVariable("fileContext") String fileContext) throws IOException {
        log.debug("FileConvertorController :: uploadCSVFile() : call started ");
        if (ObjectUtils.isEmpty(file)) {
            throw new CustomException("File Should not be Empty", HttpStatus.BAD_REQUEST);
        }
        Object object = fileConvertorService.uploadCSVFile(file, fileContext);
        log.debug("FileConvertorController :: uploadCSVFile() : call ended ");
        return new ResponseEntity<>(object, HttpStatus.OK);
    }


}
