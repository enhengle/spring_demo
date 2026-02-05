package com.practise.demo.util.filetransfer;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * SFTP 文件传输服务实现
 * 使用 JSch 库
 * 
 * @author system
 */
public class SftpTransferService implements FileTransferService {
    
    private static final Logger logger = LoggerFactory.getLogger(SftpTransferService.class);
    
    private Session session;
    private ChannelSftp channelSftp;
    private FileTransferConfig config;
    
    @Override
    public boolean connect(FileTransferConfig config) {
        this.config = config;
        try {
            JSch jsch = new JSch();
            
            // 如果使用密钥认证
            if (config.getPrivateKeyPath() != null && !config.getPrivateKeyPath().isEmpty()) {
                if (config.getPrivateKeyPassphrase() != null) {
                    jsch.addIdentity(config.getPrivateKeyPath(), config.getPrivateKeyPassphrase());
                } else {
                    jsch.addIdentity(config.getPrivateKeyPath());
                }
            }
            
            // 创建会话
            session = jsch.getSession(config.getUsername(), config.getHost(), config.getPort());
            session.setPassword(config.getPassword());
            
            // 设置SSH配置
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no"); // 跳过主机密钥检查（生产环境应设置为yes）
            session.setConfig(properties);
            
            // 设置超时
            session.setTimeout(config.getConnectTimeout());
            
            // 连接
            session.connect();
            
            // 打开SFTP通道
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            
            logger.info("SFTP连接成功: {}:{}", config.getHost(), config.getPort());
            return true;
            
        } catch (JSchException e) {
            logger.error("SFTP连接失败", e);
            disconnect();
            return false;
        }
    }
    
    @Override
    public void disconnect() {
        try {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            logger.info("SFTP连接已断开");
        } catch (Exception e) {
            logger.error("断开SFTP连接失败", e);
        }
    }
    
    @Override
    public boolean uploadFile(String localPath, String remotePath) {
        try (FileInputStream fis = new FileInputStream(localPath)) {
            return uploadFile(fis, remotePath);
        } catch (IOException e) {
            logger.error("上传文件失败: {}", localPath, e);
            return false;
        }
    }
    
    @Override
    public boolean uploadFile(InputStream inputStream, String remotePath) {
        try {
            // 确保目录存在
            String remoteDir = new File(remotePath).getParent();
            if (remoteDir != null) {
                createDirectory(remoteDir);
            }
            
            // 上传文件
            channelSftp.put(inputStream, remotePath);
            logger.info("文件上传成功: {}", remotePath);
            return true;
            
        } catch (SftpException e) {
            logger.error("上传文件失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean downloadFile(String remotePath, String localPath) {
        try {
            // 确保本地目录存在
            File localFile = new File(localPath);
            File parentDir = localFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // 下载文件
            channelSftp.get(remotePath, localPath);
            logger.info("文件下载成功: {} -> {}", remotePath, localPath);
            return true;
            
        } catch (SftpException e) {
            logger.error("下载文件失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public InputStream downloadFile(String remotePath) {
        try {
            return channelSftp.get(remotePath);
        } catch (SftpException e) {
            logger.error("下载文件失败: {}", remotePath, e);
            return null;
        }
    }
    
    @Override
    public boolean deleteFile(String remotePath) {
        try {
            channelSftp.rm(remotePath);
            logger.info("文件删除成功: {}", remotePath);
            return true;
        } catch (SftpException e) {
            logger.error("删除文件失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public List<String> listFiles(String remotePath) {
        List<String> files = new ArrayList<>();
        try {
            Vector<?> fileList = channelSftp.ls(remotePath);
            for (Object obj : fileList) {
                if (obj instanceof ChannelSftp.LsEntry) {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) obj;
                    if (!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                        files.add(entry.getFilename());
                    }
                }
            }
        } catch (SftpException e) {
            logger.error("列出文件失败: {}", remotePath, e);
        }
        return files;
    }
    
    @Override
    public boolean createDirectory(String remotePath) {
        try {
            // 检查目录是否存在
            try {
                channelSftp.stat(remotePath);
                return true; // 目录已存在
            } catch (SftpException e) {
                // 目录不存在，创建它
            }
            
            // 递归创建目录
            String[] pathElements = remotePath.split("/");
            String currentPath = "";
            
            for (String pathElement : pathElements) {
                if (pathElement.isEmpty()) {
                    continue;
                }
                currentPath += "/" + pathElement;
                
                try {
                    channelSftp.stat(currentPath);
                } catch (SftpException e) {
                    channelSftp.mkdir(currentPath);
                    logger.info("目录创建成功: {}", currentPath);
                }
            }
            
            return true;
            
        } catch (SftpException e) {
            logger.error("创建目录失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean deleteDirectory(String remotePath) {
        try {
            // 先删除目录下的所有文件
            Vector<?> fileList = channelSftp.ls(remotePath);
            for (Object obj : fileList) {
                if (obj instanceof ChannelSftp.LsEntry) {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) obj;
                    String filename = entry.getFilename();
                    if (filename.equals(".") || filename.equals("..")) {
                        continue;
                    }
                    
                    String fullPath = remotePath + "/" + filename;
                    if (entry.getAttrs().isDir()) {
                        deleteDirectory(fullPath);
                    } else {
                        deleteFile(fullPath);
                    }
                }
            }
            
            // 删除目录本身
            channelSftp.rmdir(remotePath);
            logger.info("目录删除成功: {}", remotePath);
            return true;
            
        } catch (SftpException e) {
            logger.error("删除目录失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean exists(String remotePath) {
        try {
            channelSftp.stat(remotePath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }
    
    @Override
    public long getFileSize(String remotePath) {
        try {
            SftpATTRS attrs = channelSftp.stat(remotePath);
            return attrs.getSize();
        } catch (SftpException e) {
            logger.error("获取文件大小失败: {}", remotePath, e);
            return -1;
        }
    }
    
    @Override
    public boolean rename(String oldPath, String newPath) {
        try {
            channelSftp.rename(oldPath, newPath);
            logger.info("文件重命名成功: {} -> {}", oldPath, newPath);
            return true;
        } catch (SftpException e) {
            logger.error("文件重命名失败: {} -> {}", oldPath, newPath, e);
            return false;
        }
    }
}
