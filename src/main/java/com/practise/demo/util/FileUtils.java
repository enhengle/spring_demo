package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.*;
import java.util.Set;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.FileTime;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtils {

    private static final int BUFFER_SIZE = 8192;
    private static final long MAX_UNCOMPRESSED_SIZE = 100 * 1024 * 1024; // 100MB
    private static final int MAX_ENTRIES = 10000;

    /**
     * 获取文件内容（UTF-8编码）
     */
    public static String readFileContent(String filePath) {
        return readFileContent(filePath, StandardCharsets.UTF_8);
    }

    /**
     * 获取文件内容（指定编码）
     */
    public static String readFileContent(String filePath, Charset charset) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                log.warn("文件不存在: {}", filePath);
                return null;
            }
            
            // 兼容 Java 8+ 的读取方式
            return new String(Files.readAllBytes(path), charset);
        } catch (Exception e) {
            log.error("读取文件内容失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 获取文件内容（字节数组）
     */
    public static byte[] readFileBytes(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                log.warn("文件不存在: {}", filePath);
                return null;
            }
            
            return Files.readAllBytes(path);
        } catch (Exception e) {
            log.error("读取文件字节失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 写文件内容（UTF-8编码）
     */
    public static boolean writeFileContent(String filePath, String content) {
        return writeFileContent(filePath, content, StandardCharsets.UTF_8);
    }

    /**
     * 写文件内容（指定编码）
     */
    public static boolean writeFileContent(String filePath, String content, Charset charset) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            // 兼容 Java 8+ 的写入方式
            Files.write(path, content.getBytes(charset));
            return true;
        } catch (Exception e) {
            log.error("写入文件内容失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 写文件内容（字节数组）
     */
    public static boolean writeFileBytes(String filePath, byte[] content) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, content);
            return true;
        } catch (Exception e) {
            log.error("写入文件字节失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 追加文件内容（UTF-8编码）
     */
    public static boolean appendFileContent(String filePath, String content) {
        return appendFileContent(filePath, content, StandardCharsets.UTF_8);
    }

    /**
     * 追加文件内容（指定编码）
     */
    public static boolean appendFileContent(String filePath, String content, Charset charset) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            // 兼容 Java 8+ 的追加方式
            Files.write(path, content.getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (Exception e) {
            log.error("追加文件内容失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 获取文件名（不含扩展名）
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        
        String fileName = Paths.get(filePath).getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    /**
     * 判断文件类型
     */
    public static String getFileType(String filePath) {
        String extension = getFileExtension(filePath);
        
        switch (extension) {
            case "txt":
            case "log":
            case "md":
                return "TEXT";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
            case "webp":
                return "IMAGE";
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
            case "flv":
            case "mkv":
                return "VIDEO";
            case "mp3":
            case "wav":
            case "flac":
            case "aac":
            case "ogg":
                return "AUDIO";
            case "pdf":
                return "PDF";
            case "doc":
            case "docx":
                return "WORD";
            case "xls":
            case "xlsx":
                return "EXCEL";
            case "ppt":
            case "pptx":
                return "POWERPOINT";
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
                return "ARCHIVE";
            case "java":
            case "py":
            case "js":
            case "html":
            case "css":
            case "xml":
            case "json":
                return "CODE";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * 判断是否为压缩文件
     */
    public static boolean isCompressedFile(String filePath) {
        String extension = getFileExtension(filePath);
        return "zip".equals(extension) || "rar".equals(extension) || 
               "7z".equals(extension) || "tar".equals(extension) || 
               "gz".equals(extension) || "bz2".equals(extension);
    }

    /**
     * 压缩文件
     */
    public static boolean compressFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourcePath);
                return false;
            }
            
            Path target = Paths.get(targetPath);
            Files.createDirectories(target.getParent());
            
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(target))) {
                if (Files.isDirectory(source)) {
                    compressDirectory(source, zos, source);
                } else {
                    compressFile(source, zos, source);
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("压缩文件失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 压缩目录
     */
    private static void compressDirectory(Path source, ZipOutputStream zos, Path basePath) throws IOException {
        Files.walk(source).filter(path -> !Files.isDirectory(path)).forEach(file -> {
            try {
                compressFile(file, zos, basePath);
            } catch (IOException e) {
                log.error("压缩文件失败: {}", file, e);
            }
        });
    }

    /**
     * 压缩单个文件
     */
    private static void compressFile(Path file, ZipOutputStream zos, Path basePath) throws IOException {
        String entryName = basePath.relativize(file).toString();
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        
        Files.copy(file, zos);
        zos.closeEntry();
    }

    /**
     * 解压文件
     */
    public static boolean decompressFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourcePath);
                return false;
            }
            
            Path target = Paths.get(targetPath);
            Files.createDirectories(target);
            
            // 检查是否为ZIP炸弹
            if (isZipBomb(sourcePath)) {
                log.error("检测到ZIP炸弹: {}", sourcePath);
                return false;
            }
            
            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(source))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path entryPath = target.resolve(entry.getName());
                    
                    // 安全检查：防止路径遍历攻击
                    if (!entryPath.normalize().startsWith(target)) {
                        log.warn("检测到路径遍历攻击: {}", entry.getName());
                        continue;
                    }
                    
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    zis.closeEntry();
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("解压文件失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 判断是否为ZIP炸弹
     */
    public static boolean isZipBomb(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return false;
            }
            
            long totalUncompressedSize = 0;
            int entryCount = 0;
            
            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(path))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    entryCount++;
                    
                    if (entryCount > MAX_ENTRIES) {
                        log.warn("ZIP文件条目过多: {}", entryCount);
                        return true;
                    }
                    
                    if (entry.getSize() > 0) {
                        totalUncompressedSize += entry.getSize();
                        
                        if (totalUncompressedSize > MAX_UNCOMPRESSED_SIZE) {
                            log.warn("ZIP文件解压后大小过大: {} bytes", totalUncompressedSize);
                            return true;
                        }
                    }
                    
                    zis.closeEntry();
                }
            }
            
            return false;
        } catch (Exception e) {
            log.error("检查ZIP炸弹失败: {}", filePath, e);
            return true; // 出错时保守处理
        }
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.size(path);
            }
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", filePath, e);
        }
        return -1;
    }

    /**
     * 获取文件大小（格式化）
     */
    public static String getFileSizeFormatted(String filePath) {
        long size = getFileSize(filePath);
        if (size < 0) {
            return "Unknown";
        }
        
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 删除文件或目录
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    Files.walk(path).sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            log.error("删除文件失败: {}", p, e);
                        }
                    });
                } else {
                    Files.delete(path);
                }
                return true;
            }
        } catch (Exception e) {
            log.error("删除文件失败: {}", filePath, e);
        }
        return false;
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourcePath);
                return false;
            }
            
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            log.error("复制文件失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 移动文件
     */
    public static boolean moveFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourcePath);
                return false;
            }
            
            Files.createDirectories(target.getParent());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            log.error("移动文件失败: {} -> {}", sourcePath, targetPath, e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 检查是否为目录
     */
    public static boolean isDirectory(String filePath) {
        return Files.isDirectory(Paths.get(filePath));
    }

    /**
     * 创建目录
     */
    public static boolean createDirectory(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            Files.createDirectories(path);
            return true;
        } catch (Exception e) {
            log.error("创建目录失败: {}", dirPath, e);
            return false;
        }
    }

    /**
     * 列出目录下的所有文件
     */
    public static String[] listFiles(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (Files.isDirectory(path)) {
                return Files.list(path)
                        .map(Path::toString)
                        .toArray(String[]::new);
            }
        } catch (Exception e) {
            log.error("列出目录文件失败: {}", dirPath, e);
        }
        return new String[0];
    }

    /**
     * 列出目录下的所有文件（递归）
     */
    public static String[] listFilesRecursively(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (Files.isDirectory(path)) {
                return Files.walk(path)
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .toArray(String[]::new);
            }
        } catch (Exception e) {
            log.error("递归列出目录文件失败: {}", dirPath, e);
        }
        return new String[0];
    }

    /**
     * 获取文件的MIME类型
     */
    public static String getMimeType(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                String mimeType = Files.probeContentType(path);
                // 如果 probeContentType 返回 null，则根据扩展名判断
                if (mimeType == null) {
                    return getMimeTypeByExtension(filePath);
                }
                return mimeType;
            }
        } catch (Exception e) {
            log.error("获取MIME类型失败: {}", filePath, e);
        }
        return getMimeTypeByExtension(filePath);
    }

    /**
     * 根据文件扩展名获取MIME类型
     */
    private static String getMimeTypeByExtension(String filePath) {
        String extension = getFileExtension(filePath);
        switch (extension) {
            case "txt": return "text/plain";
            case "html": case "htm": return "text/html";
            case "css": return "text/css";
            case "js": return "application/javascript";
            case "json": return "application/json";
            case "xml": return "application/xml";
            case "pdf": return "application/pdf";
            case "zip": return "application/zip";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "mp3": return "audio/mpeg";
            case "mp4": return "video/mp4";
            default: return "application/octet-stream";
        }
    }

    /**
     * 检查文件是否为空
     */
    public static boolean isEmptyFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return Files.size(path) == 0;
            }
        } catch (Exception e) {
            log.error("检查文件是否为空失败: {}", filePath, e);
        }
        return true;
    }

    /**
     * 获取文件的最后修改时间
     */
    public static long getLastModifiedTime(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.getLastModifiedTime(path).toMillis();
            }
        } catch (Exception e) {
            log.error("获取文件最后修改时间失败: {}", filePath, e);
        }
        return -1;
    }

    /**
     * 设置文件的最后修改时间
     */
    public static boolean setLastModifiedTime(String filePath, long time) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.setLastModifiedTime(path, FileTime.fromMillis(time));
                return true;
            }
        } catch (Exception e) {
            log.error("设置文件最后修改时间失败: {}", filePath, e);
        }
        return false;
    }

    /**
     * 检查文件是否可读
     */
    public static boolean isReadable(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) && Files.isReadable(path);
        } catch (Exception e) {
            log.error("检查文件是否可读失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查文件是否可写
     */
    public static boolean isWritable(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) && Files.isWritable(path);
        } catch (Exception e) {
            log.error("检查文件是否可写失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查文件是否可执行
     */
    public static boolean isExecutable(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) && Files.isExecutable(path);
        } catch (Exception e) {
            log.error("检查文件是否可执行失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件的权限
     */
    public static String getFilePermissions(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                // 检查是否支持POSIX文件系统
                try {
                    Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
                    StringBuilder sb = new StringBuilder();
                    
                    sb.append(permissions.contains(PosixFilePermission.OWNER_READ) ? "r" : "-");
                    sb.append(permissions.contains(PosixFilePermission.OWNER_WRITE) ? "w" : "-");
                    sb.append(permissions.contains(PosixFilePermission.OWNER_EXECUTE) ? "x" : "-");
                    sb.append(permissions.contains(PosixFilePermission.GROUP_READ) ? "r" : "-");
                    sb.append(permissions.contains(PosixFilePermission.GROUP_WRITE) ? "w" : "-");
                    sb.append(permissions.contains(PosixFilePermission.GROUP_EXECUTE) ? "x" : "-");
                    sb.append(permissions.contains(PosixFilePermission.OTHERS_READ) ? "r" : "-");
                    sb.append(permissions.contains(PosixFilePermission.OTHERS_WRITE) ? "w" : "-");
                    sb.append(permissions.contains(PosixFilePermission.OTHERS_EXECUTE) ? "x" : "-");
                    
                    return sb.toString();
                } catch (UnsupportedOperationException e) {
                    // 不支持POSIX文件系统，返回默认权限
                    log.debug("文件系统不支持POSIX权限，返回默认权限: {}", filePath);
                    return "rw-r--r--";
                }
            }
        } catch (Exception e) {
            log.error("获取文件权限失败: {}", filePath, e);
        }
        return "rw-r--r--";
    }
} 