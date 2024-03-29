package com.maveric.digital.controller;

import com.maveric.digital.service.AdminService;
import com.maveric.digital.service.ConversationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private ConversationService conversationService;

    @Mock
    private AdminService adminService;

    private MultipartFile multipartFile;

    @BeforeEach
    public void setUp() {
        String fileName = "metadata.csv";
        String content = "header1,header2,header3\n" +
                "data1,data2,data3\n" +
                "data4,data5,data6";
        multipartFile = new MockMultipartFile(fileName, content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testImportMetaData() throws IOException {
        adminController.importMetaData(multipartFile);

        verify(conversationService, times(1)).parseToCsvMetaDataDto(any(InputStreamReader.class));
        verify(adminService, times(1)).importCsvMetaData(any());
    }
}
