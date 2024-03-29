package com.maveric.digital.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.maveric.digital.responsedto.SFTPResponseDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;

public interface SftpService {
    Map<String, String> uploadFile(MultipartFile file, String folderName) throws SftpException, IOException;

    SFTPResponseDto deleteFile(String fileName, String folderName);

    StreamingResponseBody downloadFile(String userId, String fileName) throws SftpException, IOException, JSchException;
}
