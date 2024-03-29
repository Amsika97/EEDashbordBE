package com.maveric.digital.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceNotFoundException;
import com.maveric.digital.responsedto.SFTPResponseDto;
import com.maveric.digital.service.AssessmentService;
import com.maveric.digital.service.SftpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({SftpController.class})
@ExtendWith({SpringExtension.class})
class SftpControllerTest {

    @MockBean
    private SftpService sftpService;

    @MockBean
    private AssessmentService assessmentService;

    @InjectMocks
    private SftpController sftpController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSuccessfulFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String folderName = "uploads";
        Map<String, String> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        when(sftpService.uploadFile(any(), any())).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/upload")
                        .file(file)
                        .param("folderName", folderName))
                .andExpect(status().isOk());
    }

    @Test
    void testFileUploadWithMissingFile() throws Exception {
        String folderName = "uploads";
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/upload")
                        .param("folderName", folderName))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void testUploadFileCustomException() throws Exception {
        String fileName = "test.txt";
        String folderName = "downloads";
        when(sftpService.uploadFile(any(), any())).thenThrow(new CustomException("error", HttpStatus.INTERNAL_SERVER_ERROR));
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/upload")
                .file(file)
                .param("folderName", ""))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
                });
    }
    @Test
    void testUploadFileSftpException() throws Exception {
        String fileName = "test.txt";
        String folderName = "downloads";
        when(sftpService.uploadFile(any(), any())).thenThrow(SftpException.class);
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/upload")
                        .file(file)
                        .param("folderName", folderName))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof SftpException);
                });
    }

    @Test
    void testDeleteFileSuccess() throws Exception {
        String fileName = "existing_file.txt";
        String folderName = "existing_folder";
        when(sftpService.deleteFile(anyString(), anyString())).thenReturn(new SFTPResponseDto(fileName,"File deleted successfully",200));
        mockMvc.perform(delete("/v1/delete")
                        .param("fileName", fileName)
                        .param("folderName", folderName))
                .andExpect(status().isOk());
    }


    @Test
    void testDeleteFileSFTPException() throws Exception {
        String fileName = "existing_file.txt";
        String folderName = "existing_folder";
        when(sftpService.deleteFile(anyString(), anyString())).thenThrow(new ResourceNotFoundException("SFTP Failure : File not existed "));
        mockMvc.perform(delete("/v1/delete")
                        .param("fileName", fileName)
                        .param("folderName", folderName))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFileCustomException() throws Exception {
        String fileName = "FileName";
        String folderName = "FolderName";
        when(sftpService.deleteFile(anyString(), anyString()))
                .thenThrow(new CustomException("Folder already existed ",HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(delete("/v1/delete")
                        .param("fileName", fileName)
                        .param("folderName", folderName))
                .andExpect(status().isInternalServerError());
    }



    @Test
    void testDownloadFile() throws Exception {
        StreamingResponseBody streamingResponseBody = mock(StreamingResponseBody.class);
        String folderName = "FolderName";
        String fileName = "FileName";
        when(sftpService.downloadFile(folderName, fileName)).thenReturn(streamingResponseBody);
        mockMvc.perform(get("/v1/download/{folderName}/{fileName}", folderName, fileName))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void testDownloadFileJschException() throws Exception {
        String fileName = "test.txt";
        String folderName = "downloads";
        when(sftpService.downloadFile(anyString(), anyString())).thenThrow(new JSchException("error"));
        mockMvc.perform(get("/v1/download/{folderName}/{fileName}", folderName, fileName))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof JSchException);
                });
    }

    @Test
    void testDownloadFileInternalServerError() throws Exception {
        String fileName = "test.txt";
        String folderName = "downloads";
        when(sftpService.downloadFile(anyString(), anyString())).thenThrow(new CustomException("Internal server error",HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(get("/v1/download/{folderName}/{fileName}", folderName, fileName))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void testDownloadFileCustomException() throws Exception {
        String fileName = "test.txt";
        String folderName = "downloads";
        when(sftpService.downloadFile(anyString(), anyString())).thenThrow(new CustomException("error", HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(get("/v1/download/{folderName}/{fileName}", folderName, fileName))
                .andExpect(status().isInternalServerError()).andExpect((result) -> {
                    Assertions.assertTrue(result.getResolvedException() instanceof CustomException);
                });
    }
}




