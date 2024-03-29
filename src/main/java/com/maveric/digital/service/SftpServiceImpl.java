package com.maveric.digital.service;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.maveric.digital.exceptions.CustomException;
import com.maveric.digital.exceptions.ResourceNotFoundException;
import com.maveric.digital.responsedto.SFTPResponseDto;
import com.maveric.digital.utils.SftpUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.maveric.digital.utils.ServiceConstants.DATE_PATTERN;
import static com.maveric.digital.utils.ServiceConstants.UNDERSCORE;

@Slf4j
@Service
@RequiredArgsConstructor
public class SftpServiceImpl implements SftpService {

    private final SftpUtility sftpUtility;

    String generateTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        return dateFormat.format(new Date());
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile file, String folderName) throws SftpException, IOException {
        log.debug("SftpServiceImpl::uploadFile()::File upload started");
        try {
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isBlank(originalFilename)) {
                log.error("Original file is null");
                throw new CustomException(" Original filename is null", HttpStatus.BAD_REQUEST);
            }
            String timestamp = generateTimestamp();
            String timestampedFilename = timestamp.concat(UNDERSCORE).concat(Objects.requireNonNull(originalFilename));
            ChannelSftp channel = sftpUtility.getSftpChannel();
            channel.mkdir(sftpUtility.createFolderPath(folderName));
            String destinationPath = sftpUtility.path(timestampedFilename, folderName);
            channel.put(file.getInputStream(), destinationPath);
            sftpUtility.disconnectChannel();
            Map<String, String> response = new HashMap<>();
            response.put("status", "200");
            response.put("filename", timestampedFilename);
            response.put("folderName", folderName);
            log.debug("SftpServiceImpl::uploadFile()::File upload finished");
            return response;
        } catch (SftpException  e) {
            throw new SftpException(e.id,"SFTP FAILURE : Folder already existed");
        } catch (IOException e) {
            throw new IOException("uploadFile() failed",e);
        }catch (Exception ex) {
            throw new CustomException(String.format("Error uploading file {%s}",ex), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public SFTPResponseDto deleteFile(String fileName, String folderName) {
        log.debug("SftpServiceImpl::deleteFile()::File deletion started");
        try {
            ChannelSftp channel = sftpUtility.getSftpChannel();
            channel.rm(sftpUtility.path(fileName,folderName));
            sftpUtility.disconnectChannel();
            log.debug("SftpServiceImpl::deleteFile()::File deletion finished");
            return new SFTPResponseDto(fileName,"File deleted successfully",HttpStatus.OK.value());

        } catch (JSchException | SftpException e) {
            throw new ResourceNotFoundException("SFTP Failure : File not existed", e);
        }catch (Exception ex) {
            throw new CustomException(String.format("error deleting file {%s}",ex), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public StreamingResponseBody downloadFile(String folderName, String fileName) throws JSchException,SftpException {
        try {
            String remotePath = sftpUtility.path(fileName, folderName);
            log.debug("SftpServiceImpl::downloadFile()::Downloading file from the remote path");
            ChannelSftp channel = sftpUtility.getSftpChannel();
            StreamingResponseBody responseBody = response -> {
                try (InputStream inputStream = channel.get(remotePath)) {
                    IOUtils.copy(inputStream, response);
                } catch (SftpException | IOException e) {
                    throw new CustomException(String.format("File is not found or Input stream error  {%s}",e),HttpStatus.NO_CONTENT);
                } finally {
                    sftpUtility.disconnectChannel();
                }
            };
            log.debug("SftpServiceImpl::downloadFile()::End");
            return responseBody;
        } catch (JSchException  e) {
            throw new JSchException("Connection failure ",e);
        } catch (Exception ex) {
            throw new CustomException(String.format("error downloading file {%s}",ex), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
