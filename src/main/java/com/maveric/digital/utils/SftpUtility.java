package com.maveric.digital.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.maveric.digital.utils.ServiceConstants.SLASH;

@Component
@Data
public class SftpUtility {
    @Value("${sftp.remote.directory}")
    private String sftpRemoteDirectory;

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.port}")
    private int port;

    private Session sessions;

    public void setSessions(Session sessions) {
        this.sessions = sessions;
    }

    private ChannelSftp channelSftp;
    public void setChannelSftp(ChannelSftp channelSftp) {
        this.channelSftp = channelSftp;
    }

    public ChannelSftp getSftpChannel() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);

        // Disable strict host key checking
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        return channel;
    }

    public void disconnectChannel() {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
    }

    public void disconnectSession() {
        if (sessions != null && sessions.isConnected()) {
            sessions.disconnect();
        }
    }
    public String createFolderPath(String folderName)
    {
        return sftpRemoteDirectory.concat(folderName);
    }
    public String path( String fileName, String folderName)
    {
        return sftpRemoteDirectory.concat(folderName).concat( SLASH).concat(fileName);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
