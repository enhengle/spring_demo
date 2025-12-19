package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP工具类
 */
@Slf4j
public class HttpUtils {

    private static final int DEFAULT_TIMEOUT = 30000; // 30秒
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * 发送GET请求
     */
    public static String sendGet(String url) {
        return sendGet(url, null, DEFAULT_TIMEOUT);
    }

    /**
     * 发送GET请求（带请求头）
     */
    public static String sendGet(String url, Map<String, String> headers) {
        return sendGet(url, headers, DEFAULT_TIMEOUT);
    }

    /**
     * 发送GET请求（带请求头和超时时间）
     */
    public static String sendGet(String url, Map<String, String> headers, int timeout) {
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
            
            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection.getInputStream());
            } else {
                log.warn("GET请求失败，响应码: {}, URL: {}", responseCode, url);
                return readResponse(connection.getErrorStream());
            }
            
        } catch (Exception e) {
            log.error("发送GET请求失败: {}", url, e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 发送POST请求
     */
    public static String sendPost(String url, String data) {
        return sendPost(url, data, null, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（带请求头）
     */
    public static String sendPost(String url, String data, Map<String, String> headers) {
        return sendPost(url, data, headers, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（带请求头和超时时间）
     */
    public static String sendPost(String url, String data, Map<String, String> headers, int timeout) {
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
            connection.setRequestProperty("Content-Type", DEFAULT_CONTENT_TYPE);
            
            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 发送数据
            if (data != null && !data.isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = data.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection.getInputStream());
            } else {
                log.warn("POST请求失败，响应码: {}, URL: {}", responseCode, url);
                return readResponse(connection.getErrorStream());
            }
            
        } catch (Exception e) {
            log.error("发送POST请求失败: {}", url, e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 发送POST请求（Map参数）
     */
    public static String sendPost(String url, Map<String, String> params) {
        return sendPost(url, params, null, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（Map参数，带请求头）
     */
    public static String sendPost(String url, Map<String, String> params, Map<String, String> headers) {
        return sendPost(url, params, headers, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（Map参数，带请求头和超时时间）
     */
    public static String sendPost(String url, Map<String, String> params, Map<String, String> headers, int timeout) {
        String data = buildQueryString(params);
        return sendPost(url, data, headers, timeout);
    }

    /**
     * 发送POST请求（JSON数据）
     */
    public static String sendPostJson(String url, String jsonData) {
        return sendPostJson(url, jsonData, null, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（JSON数据，带请求头）
     */
    public static String sendPostJson(String url, String jsonData, Map<String, String> headers) {
        return sendPostJson(url, jsonData, headers, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（JSON数据，带请求头和超时时间）
     */
    public static String sendPostJson(String url, String jsonData, Map<String, String> headers, int timeout) {
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            
            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 发送JSON数据
            if (jsonData != null && !jsonData.isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection.getInputStream());
            } else {
                log.warn("POST JSON请求失败，响应码: {}, URL: {}", responseCode, url);
                return readResponse(connection.getErrorStream());
            }
            
        } catch (Exception e) {
            log.error("发送POST JSON请求失败: {}", url, e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 发送POST请求（文件上传）
     */
    public static String sendPostFile(String url, Map<String, String> textParams, Map<String, File> fileParams) {
        return sendPostFile(url, textParams, fileParams, null, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（文件上传，带请求头）
     */
    public static String sendPostFile(String url, Map<String, String> textParams, Map<String, File> fileParams, 
                                    Map<String, String> headers, int timeout) {
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
            
            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 生成分隔符
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            // 发送数据
            try (OutputStream os = connection.getOutputStream()) {
                // 发送文本参数
                if (textParams != null) {
                    for (Map.Entry<String, String> entry : textParams.entrySet()) {
                        os.write(("--" + boundary + "\r\n").getBytes());
                        os.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());
                        os.write((entry.getValue() + "\r\n").getBytes());
                    }
                }
                
                // 发送文件参数
                if (fileParams != null) {
                    for (Map.Entry<String, File> entry : fileParams.entrySet()) {
                        File file = entry.getValue();
                        if (file.exists()) {
                            os.write(("--" + boundary + "\r\n").getBytes());
                            os.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + 
                                    "\"; filename=\"" + file.getName() + "\"\r\n").getBytes());
                            os.write(("Content-Type: " + getFileMimeType(file) + "\r\n\r\n").getBytes());
                            
                            try (FileInputStream fis = new FileInputStream(file)) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = fis.read(buffer)) != -1) {
                                    os.write(buffer, 0, bytesRead);
                                }
                            }
                            os.write("\r\n".getBytes());
                        }
                    }
                }
                
                os.write(("--" + boundary + "--\r\n").getBytes());
            }
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection.getInputStream());
            } else {
                log.warn("POST文件上传请求失败，响应码: {}, URL: {}", responseCode, url);
                return readResponse(connection.getErrorStream());
            }
            
        } catch (Exception e) {
            log.error("发送POST文件上传请求失败: {}", url, e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 构建查询字符串
     */
    public static String buildQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            try {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                  .append("=")
                  .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("编码参数失败: {}", entry.getKey(), e);
            }
        }
        return sb.toString();
    }

    /**
     * 读取响应内容
     */
    public static String readResponse(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            log.error("读取响应内容失败", e);
            return null;
        }
    }

    /**
     * 获取文件的MIME类型
     */
    public static String getFileMimeType(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return "application/msword";
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 检查URL是否有效
     */
    public static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取URL的域名
     */
    public static String getDomain(String url) {
        try {
            URL urlObj = new URL(url);
            return urlObj.getHost();
        } catch (Exception e) {
            log.error("获取域名失败: {}", url, e);
            return null;
        }
    }

    /**
     * 获取URL的协议
     */
    public static String getProtocol(String url) {
        try {
            URL urlObj = new URL(url);
            return urlObj.getProtocol();
        } catch (Exception e) {
            log.error("获取协议失败: {}", url, e);
            return null;
        }
    }

    /**
     * 获取URL的端口
     */
    public static int getPort(String url) {
        try {
            URL urlObj = new URL(url);
            int port = urlObj.getPort();
            if (port == -1) {
                // 使用默认端口
                if ("https".equals(urlObj.getProtocol())) {
                    return 443;
                } else if ("http".equals(urlObj.getProtocol())) {
                    return 80;
                }
            }
            return port;
        } catch (Exception e) {
            log.error("获取端口失败: {}", url, e);
            return -1;
        }
    }
}