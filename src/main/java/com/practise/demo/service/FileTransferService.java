package com.practise.demo.service;

import com.practise.demo.util.filetransfer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 文件传输服务类
 * 封装文件上传下载功能
 * 
 * @author system
 */
@Service
public class FileTransferService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileTransferService.class);
    
    /**
     * 上传文件到FTP服务器
     * 
     * @param file 上传的文件
     * @param config 传输配置
     * @param remotePath 远程路径
     * @return 是否成功
     */
    public boolean uploadToFtp(MultipartFile file, FileTransferConfig config, String remotePath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);
        try {
            if (!transferService.connect(config)) {
                return false;
            }
            
            File tempFile = saveTempFile(file);
            boolean success = transferService.uploadFile(tempFile.getAbsolutePath(), remotePath);
            deleteTempFile(tempFile.getAbsolutePath());
            
            return success;
        } catch (IOException e) {
            logger.error("上传文件到FTP失败", e);
            return false;
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 从FTP服务器下载文件
     * 
     * @param config 传输配置
     * @param remotePath 远程路径
     * @param localPath 本地路径
     * @return 是否成功
     */
    public boolean downloadFromFtp(FileTransferConfig config, String remotePath, String localPath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);
        try {
            if (!transferService.connect(config)) {
                return false;
            }
            return transferService.downloadFile(remotePath, localPath);
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 上传文件到SFTP服务器
     * 
     * @param file 上传的文件
     * @param config 传输配置
     * @param remotePath 远程路径
     * @return 是否成功
     */
    public boolean uploadToSftp(MultipartFile file, FileTransferConfig config, String remotePath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.SFTP);
        try {
            if (!transferService.connect(config)) {
                return false;
            }
            
            File tempFile = saveTempFile(file);
            boolean success = transferService.uploadFile(tempFile.getAbsolutePath(), remotePath);
            deleteTempFile(tempFile.getAbsolutePath());
            
            return success;
        } catch (IOException e) {
            logger.error("上传文件到SFTP失败", e);
            return false;
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 从SFTP服务器下载文件
     * 
     * @param config 传输配置
     * @param remotePath 远程路径
     * @param localPath 本地路径
     * @return 是否成功
     */
    public boolean downloadFromSftp(FileTransferConfig config, String remotePath, String localPath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.SFTP);
        try {
            if (!transferService.connect(config)) {
                return false;
            }
            return transferService.downloadFile(remotePath, localPath);
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 列出FTP服务器文件
     * 
     * @param config 传输配置
     * @param remotePath 远程路径
     * @return 文件列表
     */
    public List<String> listFtpFiles(FileTransferConfig config, String remotePath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);
        try {
            if (!transferService.connect(config)) {
                return java.util.Collections.emptyList();
            }
            return transferService.listFiles(remotePath);
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 列出SFTP服务器文件
     * 
     * @param config 传输配置
     * @param remotePath 远程路径
     * @return 文件列表
     */
    public List<String> listSftpFiles(FileTransferConfig config, String remotePath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.SFTP);
        try {
            if (!transferService.connect(config)) {
                return java.util.Collections.emptyList();
            }
            return transferService.listFiles(remotePath);
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 删除FTP服务器文件
     * 
     * @param config 传输配置
     * @param remotePath 远程路径
     * @return 是否成功
     */
    public boolean deleteFtpFile(FileTransferConfig config, String remotePath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);
        try {
            if (!transferService.connect(config)) {
                return false;
            }
            return transferService.deleteFile(remotePath);
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 删除SFTP服务器文件
     * 
     * @param config 传输配置
     * @param remotePath 远程路径
     * @return 是否成功
     */
    public boolean deleteSftpFile(FileTransferConfig config, String remotePath) {
        com.practise.demo.util.filetransfer.FileTransferService transferService = 
            FileTransferFactory.createService(FileTransferFactory.TransferType.SFTP);
        try {
            if (!transferService.connect(config)) {
                return false;
            }
            return transferService.deleteFile(remotePath);
        } finally {
            transferService.disconnect();
        }
    }
    
    /**
     * 保存临时文件
     */
    private File saveTempFile(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }
    
    /**
     * 删除临时文件
     */
    private void deleteTempFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.warn("删除临时文件失败: {}", filePath, e);
        }
    }
}
