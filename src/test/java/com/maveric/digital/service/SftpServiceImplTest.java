    package com.maveric.digital.service;

    import com.jcraft.jsch.ChannelSftp;
    import com.jcraft.jsch.JSchException;
    import com.jcraft.jsch.Session;
    import com.jcraft.jsch.SftpException;
    import com.maveric.digital.exceptions.CustomException;
    import com.maveric.digital.exceptions.ResourceNotFoundException;
    import com.maveric.digital.responsedto.SFTPResponseDto;
    import com.maveric.digital.utils.SftpUtility;
    import org.junit.jupiter.api.Assertions;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.Mockito;
    import org.mockito.MockitoAnnotations;
    import org.springframework.http.HttpStatus;
    import org.springframework.mock.web.MockMultipartFile;
    import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

    import java.io.ByteArrayInputStream;
    import java.io.ByteArrayOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.util.Map;

    import static org.junit.Assert.*;
    import static org.mockito.BDDMockito.given;
    import static org.mockito.Mockito.*;


    class SftpServiceImplTest {

        @Mock
        private SftpUtility sftpUtility;

        @Mock
        private ChannelSftp channelSftp;

        @InjectMocks
        private SftpServiceImpl sftpService;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void testUploadFileSuccess() throws JSchException, SftpException, IOException {
            ChannelSftp channelSftp = mock(ChannelSftp.class);
            doNothing().when(channelSftp).mkdir(Mockito.<String>any());
            doNothing().when(channelSftp).put(Mockito.<InputStream>any(), Mockito.<String>any());
            when(sftpUtility.path(Mockito.<String>any(), Mockito.<String>any())).thenReturn("Path");
            doNothing().when(sftpUtility).disconnectChannel();
            when(sftpUtility.getSftpChannel()).thenReturn(channelSftp);
            when(sftpUtility.createFolderPath(Mockito.<String>any())).thenReturn("Create Folder Path");
            Map<String, String> actualUploadFileResult = sftpService
                    .uploadFile(new MockMultipartFile("SftpServiceImpl::uploadFile()::File upload started", "test.txt",
                            "text/plain", new ByteArrayInputStream(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1})), "Folder Name");
            Assertions.assertEquals(3, actualUploadFileResult.size());
            Assertions.assertEquals("Folder Name", actualUploadFileResult.get("folderName"));
            Assertions.assertEquals("200", actualUploadFileResult.get("status"));
            verify(sftpUtility).getSftpChannel();
            verify(sftpUtility).createFolderPath(Mockito.<String>any());
            verify(sftpUtility).path(Mockito.<String>any(), Mockito.<String>any());
            verify(sftpUtility).disconnectChannel();
            verify(channelSftp).mkdir(Mockito.<String>any());
            verify(channelSftp).put(Mockito.<InputStream>any(), Mockito.<String>any());
        }

        @Test
        void testUploadFileWithSftpChannelException() throws Exception {
            MockMultipartFile file = new MockMultipartFile("testFile.txt", "Hello World".getBytes());
            String folderName = "testFolder";
            when(sftpUtility.getSftpChannel()).thenThrow(new CustomException("SFTP Channel creation failed", HttpStatus.NOT_FOUND));
            assertThrows(CustomException.class, () -> sftpService.uploadFile(file, folderName));

        }

        @Test
        void testUploadFileNullFilename() {
            MockMultipartFile file = new MockMultipartFile("emptyfile", "Hello World".getBytes());
            String folderName = "testFolder";
            assertThrows(CustomException.class, () -> sftpService.uploadFile(file, folderName));
        }

        @Test
        void testUploadFileSftpException() throws Exception {
            MockMultipartFile file = new MockMultipartFile("testFile.txt", "Hello World".getBytes());
            String folderName = "testFolder";
            when(sftpUtility.getSftpChannel()).thenReturn(channelSftp);
            when(sftpUtility.createFolderPath(folderName)).thenReturn("/path/to/testFolder");
            when(sftpUtility.path(anyString(), anyString())).thenReturn("/path/to/testFolder/20231018_testFile.txt");
            doNothing().when(channelSftp).mkdir(anyString());
            doThrow(new SftpException(0, "Simulated SftpException")).when(channelSftp).put(any(InputStream.class), anyString());

            CustomException exception = assertThrows(CustomException.class, () -> sftpService.uploadFile(file, folderName));
            Assertions.assertEquals("Error uploading file {CustomException(httpStatus=400 BAD_REQUEST, message= Original filename is null)}", exception.getMessage());
        }

        @Test
        void testUploadFileIOException() throws Exception {
            MockMultipartFile file = new MockMultipartFile("testFile.txt", "Hello World".getBytes());
            String folderName = "testFolder";
            given(sftpUtility.getSftpChannel()).willReturn(channelSftp);
            given(sftpUtility.createFolderPath(folderName)).willReturn("/path/to/testFolder");
            given(sftpUtility.path(anyString(), anyString())).willReturn("/path/to/testFolder/20231018_testFile.txt");

            doThrow(new CustomException("Error uploading file {CustomException(httpStatus=400 BAD_REQUEST, message= Original filename is null)}", HttpStatus.INTERNAL_SERVER_ERROR)).when(channelSftp).put(any(InputStream.class), anyString());

            CustomException customException = assertThrows(CustomException.class, () -> sftpService.uploadFile(file, folderName));

            Assertions.assertEquals("Error uploading file {CustomException(httpStatus=400 BAD_REQUEST, message= Original filename is null)}", customException.getMessage());
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        }

        @Test
        void testDeleteFile() throws JSchException {
            when(sftpUtility.getSftpChannel()).thenReturn(null);
            when(sftpUtility.path(Mockito.<String>any(), Mockito.<String>any())).thenReturn("Path");
            assertThrows(CustomException.class, () -> sftpService.deleteFile("foo.txt", "Folder Name"));
            verify(sftpUtility).getSftpChannel();
            verify(sftpUtility).path(Mockito.<String>any(), Mockito.<String>any());
        }

        @Test
        void testDeleteFileSuccess() throws JSchException, SftpException {
            ChannelSftp channelSftp = mock(ChannelSftp.class);
            doNothing().when(channelSftp).rm(Mockito.<String>any());
            doNothing().when(sftpUtility).disconnectChannel();
            when(sftpUtility.getSftpChannel()).thenReturn(channelSftp);
            when(sftpUtility.path(Mockito.<String>any(), Mockito.<String>any())).thenReturn("Path");
            Assertions.assertEquals(new SFTPResponseDto("foo.txt", "File deleted successfully", 200), sftpService.deleteFile("foo.txt", "Folder Name"));
            verify(sftpUtility).getSftpChannel();
            verify(sftpUtility).path(Mockito.<String>any(), Mockito.<String>any());
            verify(sftpUtility).disconnectChannel();
            verify(channelSftp).rm(Mockito.<String>any());
        }

        @Test
        void testDeleteFileException() throws JSchException, SftpException {
            ChannelSftp channelSftp = mock(ChannelSftp.class);
            when(sftpUtility.getSftpChannel()).thenReturn(channelSftp);
            when(sftpUtility.path(Mockito.<String>any(), Mockito.<String>any())).thenReturn("Path");

            doAnswer(invocation -> {
                throw new JSchException("Simulated JSchException");
            }).when(channelSftp).rm(Mockito.<String>any());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> sftpService.deleteFile("foo.txt", "Folder Name"));

            assertTrue(exception.getMessage().contains("SFTP Failure : File not existed"));
            verify(sftpUtility).getSftpChannel();
            verify(sftpUtility).path(Mockito.<String>any(), Mockito.<String>any());
            verify(channelSftp).rm(Mockito.<String>any());
        }

        @Test
        void testDownloadFile() throws JSchException, SftpException {
            when(sftpUtility.getSftpChannel()).thenReturn(new ChannelSftp());
            when(sftpUtility.path(Mockito.<String>any(), Mockito.<String>any())).thenReturn("Path");
            sftpService.downloadFile("Folder Name", "foo.txt");
            verify(sftpUtility).getSftpChannel();
            verify(sftpUtility).path(Mockito.<String>any(), Mockito.<String>any());
        }

        @Test
        void testDownloadFileException() throws JSchException {
            when(sftpUtility.path(any(), any())).thenThrow(new ResourceNotFoundException("An error occurred"));
            CustomException exception = assertThrows(CustomException.class,
                    () -> sftpService.downloadFile("Folder Name", "foo.txt"));
            Assertions.assertTrue(exception.getMessage().contains("An error occurred"));
            verify(sftpUtility).path(any(), any());
        }

        @Test
        void testDownloadFileJSchException() throws JSchException {
            when(sftpUtility.getSftpChannel()).thenThrow(new JSchException("Connection failed"));
            JSchException exception = assertThrows(JSchException.class,
                    () -> sftpService.downloadFile("folderName", "fileName"));
            Assertions.assertEquals("Connection failure ", exception.getMessage());
        }

        @Test
        void testDownloadFileGeneralException() throws JSchException {
            when(sftpUtility.getSftpChannel()).thenThrow(new RuntimeException("Unexpected error"));
            CustomException exception = assertThrows(CustomException.class,
                    () -> sftpService.downloadFile("folderName", "fileName"));
            Assertions.assertTrue(exception.getMessage().startsWith("error downloading file"));
            verify(sftpUtility).getSftpChannel();
        }

        @Test
        void testDisconnectChannel() throws JSchException {
            ChannelSftp channel = mock(ChannelSftp.class);
            when(channel.isConnected()).thenReturn(true);
            SftpUtility sftpUtility = new SftpUtility();
            sftpUtility.setChannelSftp(channel);
            sftpUtility.disconnectChannel();
            verify(channel, times(1)).disconnect();
        }

        @Test
        void testDisconnectSession() throws JSchException {
            Session session = mock(Session.class);
            when(session.isConnected()).thenReturn(true);
            SftpUtility sftpUtility = new SftpUtility();
            sftpUtility.setSessions(session);
            sftpUtility.disconnectSession();
            verify(session, times(1)).disconnect();
        }

        @Test
        void testUploadFileOriginalFilenameBlank() {
            MockMultipartFile file = new MockMultipartFile("emptyfile", new byte[0]);
            String folderName = "testFolder";
            assertThrows(CustomException.class, () -> sftpService.uploadFile(file, folderName));
        }
        @Test
        void testDownloadFileSuccess() throws JSchException, SftpException, IOException {
            when(sftpUtility.getSftpChannel()).thenReturn(channelSftp);
            when(sftpUtility.path(Mockito.anyString(), Mockito.anyString())).thenReturn("testPath");
            InputStream mockInputStream = new ByteArrayInputStream("test content".getBytes());
            when(channelSftp.get("testPath")).thenReturn(mockInputStream);

            StreamingResponseBody responseBody = sftpService.downloadFile("testFolder", "testFile.txt");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            responseBody.writeTo(outputStream);

            verify(channelSftp).get("testPath");
            assertEquals("test content", outputStream.toString());
            verify(sftpUtility).disconnectChannel();
        }
        @Test
        void testDisconnectChannelWhenNotConnected() throws JSchException {
            ChannelSftp channel = mock(ChannelSftp.class);
            when(channel.isConnected()).thenReturn(false);
            sftpUtility.setChannelSftp(channel);

            sftpUtility.disconnectChannel();

            verify(channel, never()).disconnect();
        }

        @Test
        void testDisconnectSessionWhenNotConnected() throws JSchException {
            Session session = mock(Session.class);
            when(session.isConnected()).thenReturn(false);
            sftpUtility.setSessions(session);

            sftpUtility.disconnectSession();

            verify(session, never()).disconnect();
        }

        @Test
        void testDownloadFileFailure() throws JSchException, SftpException, IOException {
            when(sftpUtility.getSftpChannel()).thenReturn(channelSftp);
            when(sftpUtility.path(Mockito.anyString(), Mockito.anyString())).thenReturn("testPath");
            when(channelSftp.get("testPath")).thenThrow(new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "File not found"));

            CustomException exception = assertThrows(CustomException.class, () -> {
                StreamingResponseBody responseBody = sftpService.downloadFile("testFolder", "testFile.txt");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                responseBody.writeTo(outputStream);
            });

            verify(sftpUtility).disconnectChannel();
            assertTrue(exception.getMessage().contains("File not found"));
        }


    }
