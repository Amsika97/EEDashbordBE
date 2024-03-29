package com.maveric.digital.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileConvertorService {
    Object uploadCSVFile(MultipartFile file, String fileContext) throws IOException;
}
