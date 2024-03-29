package com.maveric.digital.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.service.file.FileConvertorService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileConvertorControllerTest {

    @Test
    void shouldThrowExceptionWhenUploadCSVFileWithNullFileTest() throws IOException {
        assertThrows(CustomException.class,
                () -> (new FileConvertorController(mock(FileConvertorService.class))).uploadCSVFile(null, "foo"));
    }

    @Test
    void shouldUploadCSVFileSuccessfullyTest() throws IOException {
        FileConvertorService fileConvertorService = mock(FileConvertorService.class);
        when(fileConvertorService.uploadCSVFile(Mockito.<MultipartFile>any(), Mockito.<String>any()))
                .thenReturn("Upload CSVFile");
        FileConvertorController fileConvertorController = new FileConvertorController(fileConvertorService);
        ResponseEntity<Object> actualUploadCSVFileResult = fileConvertorController.uploadCSVFile(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "File Context");
        verify(fileConvertorService).uploadCSVFile(Mockito.<MultipartFile>any(), Mockito.<String>any());
        assertEquals("Upload CSVFile", actualUploadCSVFileResult.getBody());
        assertEquals(200, actualUploadCSVFileResult.getStatusCodeValue());
        assertTrue(actualUploadCSVFileResult.getHeaders().isEmpty());
    }

    @Test
    void shouldThrowCustomExceptionWhenUploadCSVFileFailsTest() throws IOException {

        FileConvertorService fileConvertorService = mock(FileConvertorService.class);
        when(fileConvertorService.uploadCSVFile(Mockito.<MultipartFile>any(), Mockito.<String>any()))
                .thenThrow(new CustomException("An error occurred", HttpStatus.CONTINUE));
        FileConvertorController fileConvertorController = new FileConvertorController(fileConvertorService);
        assertThrows(CustomException.class, () -> fileConvertorController.uploadCSVFile(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "File Context"));
        verify(fileConvertorService).uploadCSVFile(Mockito.<MultipartFile>any(), Mockito.<String>any());
    }
}
