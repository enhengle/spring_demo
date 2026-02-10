package com.practise.demo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.config.CozeConfig;
import com.practise.demo.exception.ServerException;
import com.practise.demo.model.dto.CozeWorkflowRequestDTO;
import com.practise.demo.model.dto.CozeWorkflowResponseDTO;
import com.practise.demo.service.CozeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Coze服务实现类
 * 
 * @author system
 */
@Service
public class CozeServiceImpl implements CozeService {
    
    private static final Logger logger = LoggerFactory.getLogger(CozeServiceImpl.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CozeConfig cozeConfig;
    
    @Override
    public CozeWorkflowResponseDTO executeWorkflow(CozeWorkflowRequestDTO request) {
        logger.info("调用Coze SQL识别工作流：input={}, responseMode={}", 
                request.getInput(), request.getResponseMode());
        
        // 保存原始输入，用于后续提取SQL
        String originalInput = request.getInput();
        
        // 检查配置
        if (cozeConfig.getToken() == null || cozeConfig.getToken().isEmpty()) {
            throw new ServerException(ErrorCode.SYSTEM_ERROR, "Coze Token未配置");
        }
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("input", request.getInput());
            requestBody.put("sql_text", request.getInput());

            // 如果有额外参数，添加到请求中
            if (request.getParameters() != null && !request.getParameters().isEmpty()) {
                requestBody.putAll(request.getParameters());
            }
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + cozeConfig.getToken());
            
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
            
            logger.info("调用Coze工作流API：url={}", cozeConfig.getWorkflowUrl());
            
            // 发送请求
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(
                        cozeConfig.getWorkflowUrl(),
                        HttpMethod.POST,
                        httpEntity,
                        String.class
                );
                logger.debug("Coze API原始响应：{}", response.getBody());
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                String errorBody = e.getResponseBodyAsString();
                logger.error("Coze API调用失败：状态码={}, 响应体={}", e.getStatusCode(), errorBody);
                
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                            "Coze API认证失败（401），请检查Token是否正确");
                } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                            "Coze API端点不存在（404），请检查API URL是否正确：" + cozeConfig.getWorkflowUrl());
                } else {
                    throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                            "Coze API调用失败：" + e.getStatusCode() + " - " + 
                            (errorBody != null && !errorBody.isEmpty() ? errorBody : e.getMessage()));
                }
            } catch (org.springframework.web.client.ResourceAccessException e) {
                logger.error("Coze API连接失败", e);
                throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                        "无法连接到Coze服务，请检查网络连接和API URL：" + cozeConfig.getWorkflowUrl());
            } catch (Exception e) {
                logger.error("Coze API调用异常", e);
                throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                        "Coze API调用失败：" + e.getMessage());
            }
            
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                        "Coze API调用失败，状态码：" + response.getStatusCode());
            }
            
            // 解析响应
            String responseBody = response.getBody();
            logger.debug("Coze API响应：{}", responseBody);
            
            CozeWorkflowResponseDTO responseDTO = parseCozeResponse(responseBody, originalInput);
            
            logger.info("Coze工作流执行成功：taskId={}", responseDTO.getTaskId());
            
            return responseDTO;
            
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Coze API调用失败", e);
            throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                    "Coze API调用失败：" + e.getMessage());
        }
    }
    
    /**
     * 解析Coze工作流响应
     */
    private CozeWorkflowResponseDTO parseCozeResponse(String responseBody, String originalInput) {
        try {
            logger.debug("开始解析Coze响应：{}", responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);
            CozeWorkflowResponseDTO responseDTO = new CozeWorkflowResponseDTO();
            
            // 保存完整数据
            Map<String, Object> dataMap = new HashMap<>();
            rootNode.fields().forEachRemaining(entry -> {
                dataMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), Object.class));
            });
            responseDTO.setData(dataMap);
            
            // 优先解析 analysis_result 格式（新格式）
            if (rootNode.has("analysis_result")) {
                JsonNode analysisResult = rootNode.get("analysis_result");
                
                // 提取SQL类型
                if (analysisResult.has("syntax_type")) {
                    String syntaxType = analysisResult.get("syntax_type").asText();
                    responseDTO.setSqlType(syntaxType);
                    // 如果syntax_type是"通用SQL"，尝试从关键字推断具体类型
                    if ("通用SQL".equals(syntaxType) && analysisResult.has("keywords")) {
                        JsonNode keywordsNode = analysisResult.get("keywords");
                        if (keywordsNode.isArray() && keywordsNode.size() > 0) {
                            String firstKeyword = keywordsNode.get(0).asText().toUpperCase();
                            if (firstKeyword.equals("SELECT") || firstKeyword.equals("INSERT") || 
                                firstKeyword.equals("UPDATE") || firstKeyword.equals("DELETE")) {
                                responseDTO.setSqlType(firstKeyword);
                            }
                        }
                    }
                }
                
                // 提取关键字
                if (analysisResult.has("keywords")) {
                    JsonNode keywordsNode = analysisResult.get("keywords");
                    if (keywordsNode.isArray()) {
                        List<String> keywords = new ArrayList<>();
                        for (JsonNode keywordNode : keywordsNode) {
                            keywords.add(keywordNode.asText().toUpperCase());
                        }
                        responseDTO.setKeywords(keywords);
                    }
                }
                
                // 提取错误信息
                if (analysisResult.has("error_message") && !analysisResult.get("error_message").isNull()) {
                    responseDTO.setError(analysisResult.get("error_message").asText());
                }
                
                // 判断是否正常（has_error: false 表示正常）
                if (analysisResult.has("has_error")) {
                    boolean hasError = analysisResult.get("has_error").asBoolean();
                    responseDTO.setIsValid(!hasError);
                } else {
                    // 如果没有has_error字段，根据error_message判断
                    responseDTO.setIsValid(responseDTO.getError() == null || responseDTO.getError().isEmpty());
                }
            }
            
            // 提取任务ID（支持 run_id）
            if (rootNode.has("run_id")) {
                responseDTO.setTaskId(rootNode.get("run_id").asText());
            } else if (rootNode.has("task_id")) {
                responseDTO.setTaskId(rootNode.get("task_id").asText());
            } else if (rootNode.has("taskId")) {
                responseDTO.setTaskId(rootNode.get("taskId").asText());
            } else if (rootNode.has("id")) {
                responseDTO.setTaskId(rootNode.get("id").asText());
            }
            
            // 提取结果 - 支持多种可能的字段名
            String resultText = null;
            if (rootNode.has("result")) {
                resultText = rootNode.get("result").asText();
            } else if (rootNode.has("output")) {
                resultText = rootNode.get("output").asText();
            } else if (rootNode.has("text")) {
                resultText = rootNode.get("text").asText();
            } else if (rootNode.has("content")) {
                resultText = rootNode.get("content").asText();
            } else if (rootNode.has("data")) {
                JsonNode dataNode = rootNode.get("data");
                if (dataNode.isTextual()) {
                    resultText = dataNode.asText();
                } else if (dataNode.isObject()) {
                    if (dataNode.has("result")) {
                        resultText = dataNode.get("result").asText();
                    } else if (dataNode.has("output")) {
                        resultText = dataNode.get("output").asText();
                    } else if (dataNode.has("text")) {
                        resultText = dataNode.get("text").asText();
                    } else if (dataNode.has("content")) {
                        resultText = dataNode.get("content").asText();
                    }
                }
            }
            
            if (resultText != null && !resultText.trim().isEmpty()) {
                responseDTO.setResult(resultText.trim());
            }
            
            // 提取SQL - 支持多种可能的字段名
            String sqlText = null;
            if (rootNode.has("sql")) {
                sqlText = rootNode.get("sql").asText();
            } else if (rootNode.has("sql_text")) {
                sqlText = rootNode.get("sql_text").asText();
            } else if (rootNode.has("sqlText")) {
                sqlText = rootNode.get("sqlText").asText();
            } else if (rootNode.has("data")) {
                JsonNode dataNode = rootNode.get("data");
                if (dataNode.isObject()) {
                    if (dataNode.has("sql")) {
                        sqlText = dataNode.get("sql").asText();
                    } else if (dataNode.has("sql_text")) {
                        sqlText = dataNode.get("sql_text").asText();
                    }
                }
            }
            
            if (sqlText != null && !sqlText.trim().isEmpty()) {
                responseDTO.setSql(sqlText.trim());
            }
            
            // 如果result包含SQL关键字，也设置为sql
            if (responseDTO.getResult() != null && responseDTO.getSql() == null) {
                String result = responseDTO.getResult();
                String upperResult = result.toUpperCase();
                // 检查是否包含SQL关键字
                if (upperResult.contains("SELECT") || upperResult.contains("INSERT") || 
                    upperResult.contains("UPDATE") || upperResult.contains("DELETE") ||
                    upperResult.contains("CREATE") || upperResult.contains("ALTER") ||
                    upperResult.contains("DROP") || upperResult.contains("TRUNCATE") ||
                    upperResult.contains("FROM") || upperResult.contains("WHERE")) {
                    responseDTO.setSql(result);
                }
            }
            
            // 如果没有SQL文本但有analysis_result，使用原始输入作为SQL
            if (responseDTO.getSql() == null && originalInput != null && !originalInput.trim().isEmpty()) {
                // 检查原始输入是否包含SQL关键字
                String upperInput = originalInput.toUpperCase();
                if (upperInput.contains("SELECT") || upperInput.contains("INSERT") || 
                    upperInput.contains("UPDATE") || upperInput.contains("DELETE") ||
                    upperInput.contains("CREATE") || upperInput.contains("ALTER") ||
                    upperInput.contains("DROP") || upperInput.contains("TRUNCATE") ||
                    upperInput.contains("FROM") || upperInput.contains("WHERE")) {
                    responseDTO.setSql(originalInput);
                    if (responseDTO.getResult() == null) {
                        responseDTO.setResult(originalInput);
                    }
                }
            }
            
            // 如果仍然没有SQL，使用result
            if (responseDTO.getSql() == null && responseDTO.getResult() != null) {
                responseDTO.setSql(responseDTO.getResult());
            }
            
            // 如果没有从analysis_result中提取到SQL类型和关键字，则分析SQL
            if (responseDTO.getSqlType() == null || responseDTO.getKeywords() == null) {
                String sqlTextForAnalysis = responseDTO.getSql();
                if (sqlTextForAnalysis == null || sqlTextForAnalysis.trim().isEmpty()) {
                    sqlTextForAnalysis = responseDTO.getResult();
                }
                
                if (sqlTextForAnalysis != null && !sqlTextForAnalysis.trim().isEmpty()) {
                    analyzeSql(responseDTO, sqlTextForAnalysis);
                }
            }
            
            // 如果没有设置isValid，则根据错误信息判断
            if (responseDTO.getIsValid() == null) {
                if (responseDTO.getError() != null && !responseDTO.getError().isEmpty()) {
                    responseDTO.setIsValid(false);
                } else if (responseDTO.getStatus() != null) {
                    responseDTO.setIsValid("success".equalsIgnoreCase(responseDTO.getStatus()) || 
                                           "completed".equalsIgnoreCase(responseDTO.getStatus()));
                } else {
                    // 默认如果有SQL且没有错误，则认为正常
                    responseDTO.setIsValid(responseDTO.getSql() != null && 
                                           !responseDTO.getSql().trim().isEmpty() && 
                                           responseDTO.getError() == null);
                }
            }
            
            // 提取状态
            if (rootNode.has("status")) {
                responseDTO.setStatus(rootNode.get("status").asText());
            } else if (rootNode.has("state")) {
                responseDTO.setStatus(rootNode.get("state").asText());
            }
            
            // 提取错误信息（如果还没有设置）
            if (responseDTO.getError() == null) {
                if (rootNode.has("error")) {
                    responseDTO.setError(rootNode.get("error").asText());
                } else if (rootNode.has("message") && rootNode.get("message").asText().contains("错误")) {
                    responseDTO.setError(rootNode.get("message").asText());
                } else if (rootNode.has("err_msg")) {
                    responseDTO.setError(rootNode.get("err_msg").asText());
                }
            }
            
            // 如果仍然没有结果，使用默认值
            if (responseDTO.getResult() == null || responseDTO.getResult().isEmpty()) {
                if (responseDTO.getError() != null && !responseDTO.getError().isEmpty()) {
                    responseDTO.setResult("错误: " + responseDTO.getError());
                } else {
                    logger.warn("Coze响应格式异常，无法提取结果。响应内容：{}", responseBody);
                    // 尝试直接使用响应体作为结果
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        responseDTO.setResult(responseBody);
                    } else {
                        responseDTO.setResult("抱歉，无法识别SQL语句。");
                    }
                }
            }
            
            logger.debug("解析完成，result={}, sql={}, sqlType={}, isValid={}, keywords={}, taskId={}", 
                    responseDTO.getResult(), responseDTO.getSql(), 
                    responseDTO.getSqlType(), responseDTO.getIsValid(), 
                    responseDTO.getKeywords(), responseDTO.getTaskId());
            
            return responseDTO;
            
        } catch (Exception e) {
            logger.error("解析Coze响应失败，响应内容：{}", responseBody, e);
            throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                    "解析Coze响应失败：" + e.getMessage());
        }
    }
    
    /**
     * 分析SQL语句，提取类型和关键字
     */
    private void analyzeSql(CozeWorkflowResponseDTO responseDTO, String sqlText) {
        if (sqlText == null || sqlText.trim().isEmpty()) {
            return;
        }
        
        String upperSql = sqlText.toUpperCase().trim();
        
        // 提取SQL类型（第一个关键字）
        String[] sqlTypes = {"SELECT", "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", 
                            "DROP", "TRUNCATE", "REPLACE", "MERGE", "CALL"};
        String sqlType = null;
        for (String type : sqlTypes) {
            if (upperSql.startsWith(type + " ") || upperSql.startsWith(type + "\n") || 
                upperSql.startsWith(type + "\t") || upperSql.startsWith(type + "(")) {
                sqlType = type;
                break;
            }
        }
        responseDTO.setSqlType(sqlType != null ? sqlType : "UNKNOWN");
        
        // 提取SQL关键字
        Set<String> keywordSet = new LinkedHashSet<>();
        String[] commonKeywords = {
            "SELECT", "FROM", "WHERE", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER",
            "ON", "GROUP", "BY", "HAVING", "ORDER", "ASC", "DESC", "LIMIT", "OFFSET",
            "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE", "CREATE", "TABLE",
            "ALTER", "DROP", "INDEX", "PRIMARY", "KEY", "FOREIGN", "UNIQUE", "NOT", "NULL",
            "DEFAULT", "AUTO_INCREMENT", "AS", "DISTINCT", "COUNT", "SUM", "AVG", "MAX", "MIN",
            "AND", "OR", "IN", "LIKE", "BETWEEN", "IS", "EXISTS", "UNION", "ALL",
            "CASE", "WHEN", "THEN", "ELSE", "END", "IF", "ELSEIF", "WHILE", "FOR"
        };
        
        // 使用正则表达式提取关键字
        Pattern keywordPattern = Pattern.compile("\\b(" + String.join("|", commonKeywords) + ")\\b", 
                                                Pattern.CASE_INSENSITIVE);
        Matcher matcher = keywordPattern.matcher(sqlText);
        while (matcher.find()) {
            keywordSet.add(matcher.group(1).toUpperCase());
        }
        
        // 转换为列表并排序
        List<String> keywords = new ArrayList<>(keywordSet);
        keywords.sort(String::compareTo);
        responseDTO.setKeywords(keywords);
    }
    
    /**
     * 处理流式事件数据
     * @return 更新后的taskId
     */
    private String processStreamEvent(JsonNode eventNode, SseEmitter emitter, 
                                   StringBuilder fullResult, String taskId) throws Exception {
        // 处理 analysis_result 格式（新格式）
        if (eventNode.has("analysis_result")) {
            JsonNode analysisResult = eventNode.get("analysis_result");
            // 将analysis_result转换为字符串追加到fullResult
            String analysisStr = objectMapper.writeValueAsString(analysisResult);
            fullResult.append(analysisStr);
            
            // 发送完整的事件数据
            Map<String, Object> chunkData = new HashMap<>();
            chunkData.put("analysis_result", analysisResult);
            if (eventNode.has("run_id")) {
                chunkData.put("taskId", eventNode.get("run_id").asText());
            }
            
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(objectMapper.writeValueAsString(chunkData)));
        }
        // 提取结果片段
        else if (eventNode.has("result")) {
            String resultChunk = eventNode.get("result").asText();
            fullResult.append(resultChunk);
            
            // 发送结果片段到前端
            Map<String, Object> chunkData = new HashMap<>();
            chunkData.put("chunk", resultChunk);
            chunkData.put("taskId", eventNode.has("task_id") ? 
                    eventNode.get("task_id").asText() : 
                    (eventNode.has("run_id") ? eventNode.get("run_id").asText() : taskId));
            
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(objectMapper.writeValueAsString(chunkData)));
        } else if (eventNode.has("data")) {
            JsonNode dataNode = eventNode.get("data");
            if (dataNode.has("result")) {
                String resultChunk = dataNode.get("result").asText();
                fullResult.append(resultChunk);
                
                Map<String, Object> chunkData = new HashMap<>();
                chunkData.put("chunk", resultChunk);
                if (dataNode.has("task_id")) {
                    chunkData.put("taskId", dataNode.get("task_id").asText());
                } else if (dataNode.has("run_id")) {
                    chunkData.put("taskId", dataNode.get("run_id").asText());
                }
                
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(objectMapper.writeValueAsString(chunkData)));
            }
        }
        
        // 处理错误
        if (eventNode.has("error")) {
            String errorMsg = eventNode.get("error").asText();
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(errorMsg));
            throw new RuntimeException("流式响应包含错误：" + errorMsg);
        }
        
        // 返回更新后的taskId
        if (eventNode.has("run_id")) {
            return eventNode.get("run_id").asText();
        } else if (eventNode.has("task_id")) {
            return eventNode.get("task_id").asText();
        }
        return taskId;
    }
    
    @Override
    public void executeWorkflowStream(CozeWorkflowRequestDTO request, SseEmitter emitter) {
        logger.info("调用Coze流式SQL识别工作流：input={}", request.getInput());
        
        // 检查配置
        if (cozeConfig.getToken() == null || cozeConfig.getToken().isEmpty()) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("Coze Token未配置"));
                emitter.complete();
            } catch (Exception e) {
                logger.error("发送错误消息失败", e);
            }
            return;
        }
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("input", request.getInput());
            requestBody.put("sql_text", request.getInput());

            // 如果有额外参数，添加到请求中
            if (request.getParameters() != null && !request.getParameters().isEmpty()) {
                requestBody.putAll(request.getParameters());
            }
            
            // 处理Token
            String authHeader = "Bearer " + cozeConfig.getToken();
            
            logger.info("调用Coze流式工作流API：url={}", cozeConfig.getWorkflowUrl());
            
            // 使用HttpURLConnection进行流式请求
            URL url = new URL(cozeConfig.getWorkflowUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setDoOutput(true);
            connection.setConnectTimeout(cozeConfig.getConnectTimeout());
            connection.setReadTimeout(cozeConfig.getReadTimeout());
            
            // 发送请求体
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            connection.getOutputStream().write(requestBodyJson.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder fullResult = new StringBuilder();
            String taskId = null;
            StringBuilder responseBuilder = new StringBuilder();
            
            // 读取流式响应
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                boolean isSseFormat = false;
                
                while ((line = reader.readLine()) != null) {
                    logger.debug("读取到流式数据行：{}", line);
                    
                    // 检查是否是SSE格式
                    if (line.startsWith("data: ")) {
                        isSseFormat = true;
                        String data = line.substring(6);
                        
                        if ("[DONE]".equals(data)) {
                            emitter.send(SseEmitter.event()
                                    .name("done")
                                    .data(""));
                            break;
                        }
                        
                        try {
                            JsonNode eventNode = objectMapper.readTree(data);
                            // 处理事件并更新taskId
                            taskId = processStreamEvent(eventNode, emitter, fullResult, taskId);
                        } catch (Exception e) {
                            logger.warn("解析SSE数据失败：{}", data, e);
                        }
                    } else if (line.trim().isEmpty()) {
                        // SSE格式的空行，跳过
                        continue;
                    } else {
                        // 可能是直接JSON格式，累积所有行
                        responseBuilder.append(line);
                    }
                }
                
                // 如果不是SSE格式，尝试解析为完整JSON响应
                if (!isSseFormat && responseBuilder.length() > 0) {
                    String jsonResponse = responseBuilder.toString();
                    logger.debug("收到非SSE格式响应，尝试解析为JSON：{}", jsonResponse);
                    
                    try {
                        JsonNode rootNode = objectMapper.readTree(jsonResponse);
                        
                        // 处理 analysis_result 格式
                        if (rootNode.has("analysis_result")) {
                            JsonNode analysisResult = rootNode.get("analysis_result");
                            
                            Map<String, Object> chunkData = new HashMap<>();
                            chunkData.put("analysis_result", analysisResult);
                            if (rootNode.has("run_id")) {
                                taskId = rootNode.get("run_id").asText();
                                chunkData.put("taskId", taskId);
                            }
                            
                            emitter.send(SseEmitter.event()
                                    .name("message")
                                    .data(objectMapper.writeValueAsString(chunkData)));
                            
                            fullResult.append(jsonResponse);
                        } else {
                            // 其他格式，直接发送
                            emitter.send(SseEmitter.event()
                                    .name("message")
                                    .data(jsonResponse));
                            fullResult.append(jsonResponse);
                        }
                    } catch (Exception e) {
                        logger.error("解析JSON响应失败：{}", jsonResponse, e);
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("解析响应失败：" + e.getMessage()));
                    }
                }
                
                // 发送完整响应
                String fullResultStr = fullResult.toString();
                CozeWorkflowResponseDTO finalResponse = parseCozeResponse(fullResultStr, request.getInput());
                
                // 如果没有解析到SQL，使用原始输入
                if (finalResponse.getSql() == null && request.getInput() != null) {
                    String input = request.getInput();
                    String upperInput = input.toUpperCase();
                    if (upperInput.contains("SELECT") || upperInput.contains("INSERT") || 
                        upperInput.contains("UPDATE") || upperInput.contains("DELETE") ||
                        upperInput.contains("CREATE") || upperInput.contains("ALTER") ||
                        upperInput.contains("DROP") || upperInput.contains("TRUNCATE") ||
                        upperInput.contains("FROM") || upperInput.contains("WHERE")) {
                        finalResponse.setSql(input);
                        if (finalResponse.getResult() == null) {
                            finalResponse.setResult(input);
                        }
                    }
                }
                
                // 设置任务ID
                if (taskId != null) {
                    finalResponse.setTaskId(taskId);
                }
                
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data(objectMapper.writeValueAsString(finalResponse)));
                
            } finally {
                connection.disconnect();
            }
            
            emitter.complete();
            logger.info("Coze流式工作流执行成功：taskId={}", taskId);
            
        } catch (Exception e) {
            logger.error("Coze流式API调用失败", e);
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("Coze API调用失败：" + e.getMessage()));
                emitter.completeWithError(e);
            } catch (Exception ex) {
                logger.error("发送错误消息失败", ex);
            }
        }
    }
}
