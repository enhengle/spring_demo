package com.practise.demo.util.filetransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 本地文件传输服务测试用例
 * 用于测试文件传输的基础功能
 * 
 * @author system
 */
@DisplayName("本地文件传输服务测试")
class LocalFileTransferServiceTest {
    
    @TempDir
    Path tempDir;
    
    private LocalFileTransferService service;
    private FileTransferConfig config;
    
    @BeforeEach
    void setUp() {
        service = new LocalFileTransferService();
        config = new FileTransferConfig();
        config.setRemotePath(tempDir.resolve("remote").toString());
    }
    
    @Test
    @DisplayName("测试连接")
    void testConnect() {
        assertTrue(service.connect(config));
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试上传文件")
    void testUploadFile() throws IOException {
        service.connect(config);
        
        // 创建测试文件
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write("test content".getBytes());
        }
        
        String remotePath = config.getRemotePath() + "/uploaded.txt";
        boolean success = service.uploadFile(testFile.getAbsolutePath(), remotePath);
        
        assertTrue(success);
        assertTrue(new File(remotePath).exists());
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试从输入流上传文件")
    void testUploadFileFromInputStream() throws IOException {
        service.connect(config);
        
        String content = "test content from stream";
        java.io.ByteArrayInputStream inputStream = 
            new java.io.ByteArrayInputStream(content.getBytes());
        
        String remotePath = config.getRemotePath() + "/stream.txt";
        boolean success = service.uploadFile(inputStream, remotePath);
        
        assertTrue(success);
        File uploadedFile = new File(remotePath);
        assertTrue(uploadedFile.exists());
        
        // 验证内容
        try (FileInputStream fis = new FileInputStream(uploadedFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);
            String readContent = new String(buffer, 0, bytesRead);
            assertEquals(content, readContent);
        }
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试下载文件")
    void testDownloadFile() throws IOException {
        service.connect(config);
        
        // 创建远程文件
        File remoteFile = new File(config.getRemotePath() + "/remote.txt");
        remoteFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(remoteFile)) {
            fos.write("remote content".getBytes());
        }
        
        String localPath = tempDir.resolve("downloaded.txt").toString();
        boolean success = service.downloadFile(remoteFile.getAbsolutePath(), localPath);
        
        assertTrue(success);
        assertTrue(new File(localPath).exists());
        
        // 验证内容
        try (FileInputStream fis = new FileInputStream(localPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);
            String content = new String(buffer, 0, bytesRead);
            assertEquals("remote content", content);
        }
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试下载文件为输入流")
    void testDownloadFileAsInputStream() throws IOException {
        service.connect(config);
        
        // 创建远程文件
        File remoteFile = new File(config.getRemotePath() + "/remote.txt");
        remoteFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(remoteFile)) {
            fos.write("remote content".getBytes());
        }
        
        java.io.InputStream inputStream = service.downloadFile(remoteFile.getAbsolutePath());
        assertNotNull(inputStream);
        
        // 读取内容
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        String content = new String(buffer, 0, bytesRead);
        assertEquals("remote content", content);
        
        inputStream.close();
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试删除文件")
    void testDeleteFile() throws IOException {
        service.connect(config);
        
        // 创建文件
        File testFile = new File(config.getRemotePath() + "/to_delete.txt");
        testFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write("content".getBytes());
        }
        
        assertTrue(testFile.exists());
        
        boolean success = service.deleteFile(testFile.getAbsolutePath());
        assertTrue(success);
        assertFalse(testFile.exists());
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试列出文件")
    void testListFiles() throws IOException {
        service.connect(config);
        
        // 创建多个文件
        File dir = new File(config.getRemotePath());
        dir.mkdirs();
        
        new File(dir, "file1.txt").createNewFile();
        new File(dir, "file2.txt").createNewFile();
        new File(dir, "file3.txt").createNewFile();
        
        List<String> files = service.listFiles(dir.getAbsolutePath());
        
        assertNotNull(files);
        assertTrue(files.size() >= 3);
        assertTrue(files.contains("file1.txt"));
        assertTrue(files.contains("file2.txt"));
        assertTrue(files.contains("file3.txt"));
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试创建目录")
    void testCreateDirectory() {
        service.connect(config);
        
        String newDir = config.getRemotePath() + "/new/directory";
        boolean success = service.createDirectory(newDir);
        
        assertTrue(success);
        assertTrue(new File(newDir).exists());
        assertTrue(new File(newDir).isDirectory());
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试删除目录")
    void testDeleteDirectory() throws IOException {
        service.connect(config);
        
        // 创建目录和文件
        File dir = new File(config.getRemotePath() + "/to_delete");
        dir.mkdirs();
        new File(dir, "file.txt").createNewFile();
        
        assertTrue(dir.exists());
        
        boolean success = service.deleteDirectory(dir.getAbsolutePath());
        assertTrue(success);
        assertFalse(dir.exists());
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试文件是否存在")
    void testExists() throws IOException {
        service.connect(config);
        
        // 创建文件
        File testFile = new File(config.getRemotePath() + "/exists.txt");
        testFile.getParentFile().mkdirs();
        testFile.createNewFile();
        
        assertTrue(service.exists(testFile.getAbsolutePath()));
        assertFalse(service.exists(config.getRemotePath() + "/not_exists.txt"));
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试获取文件大小")
    void testGetFileSize() throws IOException {
        service.connect(config);
        
        // 创建文件
        File testFile = new File(config.getRemotePath() + "/size.txt");
        testFile.getParentFile().mkdirs();
        String content = "test content";
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write(content.getBytes());
        }
        
        long size = service.getFileSize(testFile.getAbsolutePath());
        assertEquals(content.getBytes().length, size);
        
        service.disconnect();
    }
    
    @Test
    @DisplayName("测试重命名文件")
    void testRename() throws IOException {
        service.connect(config);
        
        // 创建文件
        File oldFile = new File(config.getRemotePath() + "/old.txt");
        oldFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(oldFile)) {
            fos.write("content".getBytes());
        }
        
        String newPath = config.getRemotePath() + "/new.txt";
        boolean success = service.rename(oldFile.getAbsolutePath(), newPath);
        
        assertTrue(success);
        assertFalse(oldFile.exists());
        assertTrue(new File(newPath).exists());
        
        service.disconnect();
    }
}
