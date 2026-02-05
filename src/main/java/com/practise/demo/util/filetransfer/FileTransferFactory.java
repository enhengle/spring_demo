package com.practise.demo.util.filetransfer;

/**
 * 文件传输服务工厂类
 * 根据传输类型创建相应的服务实例
 * 
 * @author system
 */
public class FileTransferFactory {
    
    /**
     * 传输类型枚举
     */
    public enum TransferType {
        FTP,
        SFTP,
        FTPS,
        LOCAL
    }
    
    /**
     * 创建文件传输服务实例
     * 
     * @param type 传输类型
     * @return 文件传输服务实例
     */
    public static FileTransferService createService(TransferType type) {
        switch (type) {
            case FTP:
                return new FtpTransferService();
            case SFTP:
                return new SftpTransferService();
            case LOCAL:
                return new LocalFileTransferService();
            case FTPS:
                // FTPS 可以使用 FtpTransferService，通过配置启用SSL
                return new FtpTransferService();
            default:
                throw new IllegalArgumentException("不支持的传输类型: " + type);
        }
    }
    
    /**
     * 根据字符串创建服务实例
     * 
     * @param typeStr 传输类型字符串（不区分大小写）
     * @return 文件传输服务实例
     */
    public static FileTransferService createService(String typeStr) {
        try {
            TransferType type = TransferType.valueOf(typeStr.toUpperCase());
            return createService(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("不支持的传输类型: " + typeStr, e);
        }
    }
}
