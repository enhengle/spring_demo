package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.response.Response;
import com.practise.demo.service.FileTransferService;
import com.practise.demo.util.filetransfer.FileTransferConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件传输控制器
 * 提供FTP、SFTP等文件上传下载接口
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/file-transfer")
@Tag(name = "文件传输", description = "支持FTP、SFTP等多种文件传输方式")
public class FileTransferController {
    
    @Autowired
    private FileTransferService fileTransferService;
    
    /**
     * 上传文件到FTP服务器
     */
    @PostMapping("/ftp/upload")
    @Operation(summary = "上传文件到FTP服务器", description = "上传文件到FTP服务器")
    public Response<Boolean> uploadToFtp(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "FTP服务器地址") @RequestParam("host") String host,
            @Parameter(description = "FTP服务器端口") @RequestParam("port") int port,
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "密码") @RequestParam("password") String password,
            @Parameter(description = "远程路径") @RequestParam("remotePath") String remotePath) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        try {
            FileTransferConfig config = new FileTransferConfig(host, port, username, password);
            boolean success = fileTransferService.uploadToFtp(file, config, remotePath);
            return Response.ok(success);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "上传文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 从FTP服务器下载文件
     */
    @PostMapping("/ftp/download")
    @Operation(summary = "从FTP服务器下载文件", description = "从FTP服务器下载文件到本地")
    public Response<Boolean> downloadFromFtp(
            @Parameter(description = "FTP服务器地址") @RequestParam("host") String host,
            @Parameter(description = "FTP服务器端口") @RequestParam("port") int port,
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "密码") @RequestParam("password") String password,
            @Parameter(description = "远程路径") @RequestParam("remotePath") String remotePath,
            @Parameter(description = "本地路径") @RequestParam("localPath") String localPath) {
        
        try {
            FileTransferConfig config = new FileTransferConfig(host, port, username, password);
            boolean success = fileTransferService.downloadFromFtp(config, remotePath, localPath);
            return Response.ok(success);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "下载文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 列出FTP服务器文件
     */
    @GetMapping("/ftp/list")
    @Operation(summary = "列出FTP服务器文件", description = "列出FTP服务器指定目录下的文件")
    public Response<List<String>> listFtpFiles(
            @Parameter(description = "FTP服务器地址") @RequestParam("host") String host,
            @Parameter(description = "FTP服务器端口") @RequestParam("port") int port,
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "密码") @RequestParam("password") String password,
            @Parameter(description = "远程路径") @RequestParam("remotePath") String remotePath) {
        
        try {
            FileTransferConfig config = new FileTransferConfig(host, port, username, password);
            List<String> files = fileTransferService.listFtpFiles(config, remotePath);
            return Response.ok(files);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "列出文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传文件到SFTP服务器
     */
    @PostMapping("/sftp/upload")
    @Operation(summary = "上传文件到SFTP服务器", description = "上传文件到SFTP服务器")
    public Response<Boolean> uploadToSftp(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "SFTP服务器地址") @RequestParam("host") String host,
            @Parameter(description = "SFTP服务器端口") @RequestParam("port") int port,
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "密码") @RequestParam("password") String password,
            @Parameter(description = "远程路径") @RequestParam("remotePath") String remotePath) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        try {
            FileTransferConfig config = new FileTransferConfig(host, port, username, password);
            boolean success = fileTransferService.uploadToSftp(file, config, remotePath);
            return Response.ok(success);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "上传文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 从SFTP服务器下载文件
     */
    @PostMapping("/sftp/download")
    @Operation(summary = "从SFTP服务器下载文件", description = "从SFTP服务器下载文件到本地")
    public Response<Boolean> downloadFromSftp(
            @Parameter(description = "SFTP服务器地址") @RequestParam("host") String host,
            @Parameter(description = "SFTP服务器端口") @RequestParam("port") int port,
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "密码") @RequestParam("password") String password,
            @Parameter(description = "远程路径") @RequestParam("remotePath") String remotePath,
            @Parameter(description = "本地路径") @RequestParam("localPath") String localPath) {
        
        try {
            FileTransferConfig config = new FileTransferConfig(host, port, username, password);
            boolean success = fileTransferService.downloadFromSftp(config, remotePath, localPath);
            return Response.ok(success);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "下载文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 列出SFTP服务器文件
     */
    @GetMapping("/sftp/list")
    @Operation(summary = "列出SFTP服务器文件", description = "列出SFTP服务器指定目录下的文件")
    public Response<List<String>> listSftpFiles(
            @Parameter(description = "SFTP服务器地址") @RequestParam("host") String host,
            @Parameter(description = "SFTP服务器端口") @RequestParam("port") int port,
            @Parameter(description = "用户名") @RequestParam("username") String username,
            @Parameter(description = "密码") @RequestParam("password") String password,
            @Parameter(description = "远程路径") @RequestParam("remotePath") String remotePath) {
        
        try {
            FileTransferConfig config = new FileTransferConfig(host, port, username, password);
            List<String> files = fileTransferService.listSftpFiles(config, remotePath);
            return Response.ok(files);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "列出文件失败: " + e.getMessage());
        }
    }
}
