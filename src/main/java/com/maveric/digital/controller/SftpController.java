package com.maveric.digital.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.maveric.digital.responsedto.SFTPResponseDto;
import com.maveric.digital.service.AssessmentService;
import com.maveric.digital.service.SftpService;

import jakarta.validation.constraints.NotBlank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class SftpController {

    private final SftpService sftpService;

    private final AssessmentService assessmentService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") @NotBlank MultipartFile file, @RequestParam("folderName") @NotBlank String folderName) throws IOException, SftpException {
        log.debug("SftpController::uploadFile()::File upload request received call started");
        Map<String, String> response = sftpService.uploadFile(file, folderName);
        log.debug("SftpController::uploadFile()::File upload request processed successfully, call ended");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<SFTPResponseDto> deleteFile(@RequestParam("fileName") @NotBlank String fileName, @RequestParam("folderName") @NotBlank String folderName) {
        log.debug("SftpController::deleteFile()::File deletion request received,call started");
        SFTPResponseDto sftpResponseDto = sftpService.deleteFile(fileName, folderName);
        assessmentService.removeFileUri(fileName,folderName);
        log.debug("sftpResponseDto {} ", sftpResponseDto);
        log.debug("SftpController::deleteFile()::File deletion request processed successfully,call ended  ");
        return ResponseEntity.ok(sftpResponseDto);
    }

    @GetMapping("/download/{folderName}/{fileName}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable("folderName") @NotBlank String folderName,
                                                              @NotBlank @PathVariable("fileName") String fileName) throws JSchException, SftpException, IOException {
        log.debug("SftpController::downloadFile()::File download request received,call started");

        ResponseEntity<StreamingResponseBody> response =ResponseEntity.ok().body(sftpService.downloadFile(folderName, fileName));
        log.debug("SftpController::downloadFile()::File download request processed successfully,call ended");
        return response;
    }
}
