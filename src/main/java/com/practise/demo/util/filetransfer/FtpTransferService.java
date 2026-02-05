package com.practise.demo.util.filetransfer;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP 文件传输服务实现
 * 使用 Apache Commons Net 库
 * 
 * @author system
 */
public class FtpTransferService implements FileTransferService {
    
    private static final Logger logger = LoggerFactory.getLogger(FtpTransferService.class);
    
    private FTPClient ftpClient;
    private FileTransferConfig config;
    
    public FtpTransferService() {
        this.ftpClient = new FTPClient();
    }
    
    @Override
    public boolean connect(FileTransferConfig config) {
        this.config = config;
        try {
            // 设置超时
            ftpClient.setConnectTimeout(config.getConnectTimeout());
            ftpClient.setDataTimeout(config.getTimeout());
            ftpClient.setControlKeepAliveTimeout(300);
            
            // 连接服务器
            ftpClient.connect(config.getHost(), config.getPort());
            
            // 检查连接状态
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.error("FTP服务器连接失败，返回码: {}", replyCode);
                disconnect();
                return false;
            }
            
            // 登录
            boolean loginSuccess = ftpClient.login(config.getUsername(), config.getPassword());
            if (!loginSuccess) {
                logger.error("FTP登录失败");
                disconnect();
                return false;
            }
            
            // 设置传输模式
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            // 设置被动模式
            if (config.isPassiveMode()) {
                ftpClient.enterLocalPassiveMode();
            } else {
                ftpClient.enterLocalActiveMode();
            }
            
            // 设置编码
            ftpClient.setControlEncoding(config.getEncoding());
            
            logger.info("FTP连接成功: {}:{}", config.getHost(), config.getPort());
            return true;
            
        } catch (Exception e) {
            logger.error("FTP连接失败", e);
            disconnect();
            return false;
        }
    }
    
    @Override
    public void disconnect() {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                logger.info("FTP连接已断开");
            }
        } catch (IOException e) {
            logger.error("断开FTP连接失败", e);
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
            
            // 切换到目录
            String parentDir = new File(remotePath).getParent();
            if (parentDir != null) {
                ftpClient.changeWorkingDirectory(parentDir);
            }
            
            // 上传文件
            String fileName = new File(remotePath).getName();
            boolean success = ftpClient.storeFile(fileName, inputStream);
            
            if (success) {
                logger.info("文件上传成功: {}", remotePath);
            } else {
                logger.error("文件上传失败: {}, 返回码: {}", remotePath, ftpClient.getReplyCode());
            }
            
            return success;
            
        } catch (IOException e) {
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
            try (FileOutputStream fos = new FileOutputStream(localFile)) {
                String remoteDir = new File(remotePath).getParent();
                if (remoteDir != null) {
                    ftpClient.changeWorkingDirectory(remoteDir);
                }
                
                String fileName = new File(remotePath).getName();
                boolean success = ftpClient.retrieveFile(fileName, fos);
                
                if (success) {
                    logger.info("文件下载成功: {} -> {}", remotePath, localPath);
                } else {
                    logger.error("文件下载失败: {}, 返回码: {}", remotePath, ftpClient.getReplyCode());
                }
                
                return success;
            }
            
        } catch (IOException e) {
            logger.error("下载文件失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public InputStream downloadFile(String remotePath) {
        try {
            String remoteDir = new File(remotePath).getParent();
            if (remoteDir != null) {
                ftpClient.changeWorkingDirectory(remoteDir);
            }
            
            String fileName = new File(remotePath).getName();
            return ftpClient.retrieveFileStream(fileName);
            
        } catch (IOException e) {
            logger.error("下载文件失败: {}", remotePath, e);
            return null;
        }
    }
    
    @Override
    public boolean deleteFile(String remotePath) {
        try {
            String remoteDir = new File(remotePath).getParent();
            if (remoteDir != null) {
                ftpClient.changeWorkingDirectory(remoteDir);
            }
            
            String fileName = new File(remotePath).getName();
            boolean success = ftpClient.deleteFile(fileName);
            
            if (success) {
                logger.info("文件删除成功: {}", remotePath);
            } else {
                logger.error("文件删除失败: {}, 返回码: {}", remotePath, ftpClient.getReplyCode());
            }
            
            return success;
            
        } catch (IOException e) {
            logger.error("删除文件失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public List<String> listFiles(String remotePath) {
        List<String> files = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(remotePath);
            for (FTPFile file : ftpFiles) {
                files.add(file.getName());
            }
        } catch (IOException e) {
            logger.error("列出文件失败: {}", remotePath, e);
        }
        return files;
    }
    
    @Override
    public boolean createDirectory(String remotePath) {
        try {
            String[] pathElements = remotePath.split("/");
            String currentPath = "";
            
            for (String pathElement : pathElements) {
                if (pathElement.isEmpty()) {
                    continue;
                }
                currentPath += "/" + pathElement;
                
                if (!ftpClient.changeWorkingDirectory(currentPath)) {
                    if (ftpClient.makeDirectory(currentPath)) {
                        logger.info("目录创建成功: {}", currentPath);
                    } else {
                        logger.error("目录创建失败: {}, 返回码: {}", currentPath, ftpClient.getReplyCode());
                        return false;
                    }
                }
            }
            
            return true;
            
        } catch (IOException e) {
            logger.error("创建目录失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean deleteDirectory(String remotePath) {
        try {
            boolean success = ftpClient.removeDirectory(remotePath);
            if (success) {
                logger.info("目录删除成功: {}", remotePath);
            } else {
                logger.error("目录删除失败: {}, 返回码: {}", remotePath, ftpClient.getReplyCode());
            }
            return success;
        } catch (IOException e) {
            logger.error("删除目录失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean exists(String remotePath) {
        try {
            String remoteDir = new File(remotePath).getParent();
            String fileName = new File(remotePath).getName();
            
            if (remoteDir != null) {
                ftpClient.changeWorkingDirectory(remoteDir);
            }
            
            FTPFile[] files = ftpClient.listFiles();
            if (files != null) {
                for (FTPFile file : files) {
                    if (file.getName().equals(fileName)) {
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (IOException e) {
            logger.error("检查文件是否存在失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public long getFileSize(String remotePath) {
        try {
            String remoteDir = new File(remotePath).getParent();
            String fileName = new File(remotePath).getName();
            
            if (remoteDir != null) {
                ftpClient.changeWorkingDirectory(remoteDir);
            }
            
            FTPFile[] files = ftpClient.listFiles();
            if (files != null) {
                for (FTPFile file : files) {
                    if (file.getName().equals(fileName)) {
                        return file.getSize();
                    }
                }
            }
            
            return -1;
            
        } catch (IOException e) {
            logger.error("获取文件大小失败: {}", remotePath, e);
            return -1;
        }
    }
    
    @Override
    public boolean rename(String oldPath, String newPath) {
        try {
            boolean success = ftpClient.rename(oldPath, newPath);
            if (success) {
                logger.info("文件重命名成功: {} -> {}", oldPath, newPath);
            } else {
                logger.error("文件重命名失败: {} -> {}, 返回码: {}", oldPath, newPath, ftpClient.getReplyCode());
            }
            return success;
        } catch (IOException e) {
            logger.error("文件重命名失败: {} -> {}", oldPath, newPath, e);
            return false;
        }
    }
}
