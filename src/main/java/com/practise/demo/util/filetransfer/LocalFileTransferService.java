package com.practise.demo.util.filetransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地文件系统传输服务实现
 * 用于本地文件操作和测试
 * 
 * @author system
 */
public class LocalFileTransferService implements FileTransferService {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalFileTransferService.class);
    
    private FileTransferConfig config;
    
    @Override
    public boolean connect(FileTransferConfig config) {
        this.config = config;
        // 本地文件系统不需要连接
        if (config.getRemotePath() != null) {
            File remoteDir = new File(config.getRemotePath());
            if (!remoteDir.exists()) {
                remoteDir.mkdirs();
            }
        }
        logger.info("本地文件系统已就绪");
        return true;
    }
    
    @Override
    public void disconnect() {
        // 本地文件系统不需要断开连接
        logger.info("本地文件系统操作完成");
    }
    
    @Override
    public boolean uploadFile(String localPath, String remotePath) {
        try {
            File sourceFile = new File(localPath);
            if (!sourceFile.exists()) {
                logger.error("源文件不存在: {}", localPath);
                return false;
            }
            
            File targetFile = new File(remotePath);
            File parentDir = targetFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("文件上传成功: {} -> {}", localPath, remotePath);
            return true;
            
        } catch (IOException e) {
            logger.error("上传文件失败: {} -> {}", localPath, remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean uploadFile(InputStream inputStream, String remotePath) {
        try {
            File targetFile = new File(remotePath);
            File parentDir = targetFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            logger.info("文件上传成功: {}", remotePath);
            return true;
            
        } catch (IOException e) {
            logger.error("上传文件失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean downloadFile(String remotePath, String localPath) {
        try {
            File sourceFile = new File(remotePath);
            if (!sourceFile.exists()) {
                logger.error("源文件不存在: {}", remotePath);
                return false;
            }
            
            File targetFile = new File(localPath);
            File parentDir = targetFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("文件下载成功: {} -> {}", remotePath, localPath);
            return true;
            
        } catch (IOException e) {
            logger.error("下载文件失败: {} -> {}", remotePath, localPath, e);
            return false;
        }
    }
    
    @Override
    public InputStream downloadFile(String remotePath) {
        try {
            File file = new File(remotePath);
            if (!file.exists()) {
                logger.error("文件不存在: {}", remotePath);
                return null;
            }
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("下载文件失败: {}", remotePath, e);
            return null;
        }
    }
    
    @Override
    public boolean deleteFile(String remotePath) {
        try {
            File file = new File(remotePath);
            boolean deleted = file.delete();
            if (deleted) {
                logger.info("文件删除成功: {}", remotePath);
            } else {
                logger.error("文件删除失败: {}", remotePath);
            }
            return deleted;
        } catch (Exception e) {
            logger.error("删除文件失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public List<String> listFiles(String remotePath) {
        List<String> files = new ArrayList<>();
        try {
            File dir = new File(remotePath);
            if (!dir.exists() || !dir.isDirectory()) {
                logger.error("目录不存在或不是目录: {}", remotePath);
                return files;
            }
            
            File[] fileArray = dir.listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    files.add(file.getName());
                }
            }
        } catch (Exception e) {
            logger.error("列出文件失败: {}", remotePath, e);
        }
        return files;
    }
    
    @Override
    public boolean createDirectory(String remotePath) {
        try {
            File dir = new File(remotePath);
            boolean created = dir.mkdirs();
            if (created) {
                logger.info("目录创建成功: {}", remotePath);
            } else {
                logger.warn("目录创建失败或已存在: {}", remotePath);
            }
            return created || dir.exists();
        } catch (Exception e) {
            logger.error("创建目录失败: {}", remotePath, e);
            return false;
        }
    }
    
    @Override
    public boolean deleteDirectory(String remotePath) {
        try {
            File dir = new File(remotePath);
            if (!dir.exists()) {
                return true;
            }
            
            deleteDirectoryRecursive(dir);
            logger.info("目录删除成功: {}", remotePath);
            return true;
            
        } catch (Exception e) {
            logger.error("删除目录失败: {}", remotePath, e);
            return false;
        }
    }
    
    private void deleteDirectoryRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursive(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
    
    @Override
    public boolean exists(String remotePath) {
        File file = new File(remotePath);
        return file.exists();
    }
    
    @Override
    public long getFileSize(String remotePath) {
        try {
            File file = new File(remotePath);
            if (file.exists()) {
                return file.length();
            }
            return -1;
        } catch (Exception e) {
            logger.error("获取文件大小失败: {}", remotePath, e);
            return -1;
        }
    }
    
    @Override
    public boolean rename(String oldPath, String newPath) {
        try {
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);
            
            // 确保新文件的父目录存在
            File parentDir = newFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            boolean renamed = oldFile.renameTo(newFile);
            if (renamed) {
                logger.info("文件重命名成功: {} -> {}", oldPath, newPath);
            } else {
                logger.error("文件重命名失败: {} -> {}", oldPath, newPath);
            }
            return renamed;
            
        } catch (Exception e) {
            logger.error("文件重命名失败: {} -> {}", oldPath, newPath, e);
            return false;
        }
    }
}
