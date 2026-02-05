package com.practise.demo.util.filetransfer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件传输工厂测试用例
 * 
 * @author system
 */
@DisplayName("文件传输工厂测试")
class FileTransferFactoryTest {
    
    @Test
    @DisplayName("测试创建FTP服务")
    void testCreateFtpService() {
        FileTransferService service = FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);
        assertNotNull(service);
        assertTrue(service instanceof FtpTransferService);
    }
    
    @Test
    @DisplayName("测试创建SFTP服务")
    void testCreateSftpService() {
        FileTransferService service = FileTransferFactory.createService(FileTransferFactory.TransferType.SFTP);
        assertNotNull(service);
        assertTrue(service instanceof SftpTransferService);
    }
    
    @Test
    @DisplayName("测试创建本地文件服务")
    void testCreateLocalService() {
        FileTransferService service = FileTransferFactory.createService(FileTransferFactory.TransferType.LOCAL);
        assertNotNull(service);
        assertTrue(service instanceof LocalFileTransferService);
    }
    
    @Test
    @DisplayName("测试根据字符串创建服务")
    void testCreateServiceByString() {
        FileTransferService ftpService = FileTransferFactory.createService("FTP");
        assertNotNull(ftpService);
        assertTrue(ftpService instanceof FtpTransferService);
        
        FileTransferService sftpService = FileTransferFactory.createService("SFTP");
        assertNotNull(sftpService);
        assertTrue(sftpService instanceof SftpTransferService);
        
        FileTransferService localService = FileTransferFactory.createService("LOCAL");
        assertNotNull(localService);
        assertTrue(localService instanceof LocalFileTransferService);
    }
    
    @Test
    @DisplayName("测试不支持的传输类型")
    void testUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> {
            FileTransferFactory.createService("INVALID_TYPE");
        });
    }
}
