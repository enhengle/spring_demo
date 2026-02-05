package com.practise.demo.util.filetransfer;

import java.io.InputStream;
import java.util.List;

/**
 * 文件传输服务接口
 * 定义通用的文件上传下载方法
 * 
 * @author system
 */
public interface FileTransferService {
    
    /**
     * 连接服务器
     * 
     * @param config 传输配置
     * @return 是否连接成功
     */
    boolean connect(FileTransferConfig config);
    
    /**
     * 断开连接
     */
    void disconnect();
    
    /**
     * 上传文件
     * 
     * @param localPath 本地文件路径
     * @param remotePath 远程文件路径
     * @return 是否上传成功
     */
    boolean uploadFile(String localPath, String remotePath);
    
    /**
     * 上传文件（从输入流）
     * 
     * @param inputStream 输入流
     * @param remotePath 远程文件路径
     * @return 是否上传成功
     */
    boolean uploadFile(InputStream inputStream, String remotePath);
    
    /**
     * 下载文件
     * 
     * @param remotePath 远程文件路径
     * @param localPath 本地文件路径
     * @return 是否下载成功
     */
    boolean downloadFile(String remotePath, String localPath);
    
    /**
     * 下载文件（返回输入流）
     * 
     * @param remotePath 远程文件路径
     * @return 输入流
     */
    InputStream downloadFile(String remotePath);
    
    /**
     * 删除文件
     * 
     * @param remotePath 远程文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String remotePath);
    
    /**
     * 列出目录文件
     * 
     * @param remotePath 远程目录路径
     * @return 文件列表
     */
    List<String> listFiles(String remotePath);
    
    /**
     * 创建目录
     * 
     * @param remotePath 远程目录路径
     * @return 是否创建成功
     */
    boolean createDirectory(String remotePath);
    
    /**
     * 删除目录
     * 
     * @param remotePath 远程目录路径
     * @return 是否删除成功
     */
    boolean deleteDirectory(String remotePath);
    
    /**
     * 检查文件是否存在
     * 
     * @param remotePath 远程文件路径
     * @return 是否存在
     */
    boolean exists(String remotePath);
    
    /**
     * 获取文件大小
     * 
     * @param remotePath 远程文件路径
     * @return 文件大小（字节）
     */
    long getFileSize(String remotePath);
    
    /**
     * 重命名文件
     * 
     * @param oldPath 旧路径
     * @param newPath 新路径
     * @return 是否重命名成功
     */
    boolean rename(String oldPath, String newPath);
}
