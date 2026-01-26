package com.practise.demo.controller;

import com.practise.demo.service.SparkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Spark Controller 层
 * 提供 Spark 功能的 REST API 接口
 */
@RestController
@RequestMapping("/api/spark")
public class SparkController {

    @Autowired
    private SparkService sparkService;

    /**
     * 读取 CSV 文件
     * GET /api/spark/read/csv?filePath=data.csv&hasHeader=true
     */
    @GetMapping("/read/csv")
    public Map<String, Object> readCsv(
            @RequestParam String filePath,
            @RequestParam(defaultValue = "true") boolean hasHeader) {
        return sparkService.readCsv(filePath, hasHeader);
    }

    /**
     * 读取 JSON 文件
     * GET /api/spark/read/json?filePath=data.json
     */
    @GetMapping("/read/json")
    public Map<String, Object> readJson(@RequestParam String filePath) {
        return sparkService.readJson(filePath);
    }

    /**
     * 创建临时视图（从文件，已废弃）
     * POST /api/spark/view/create
     * Body: {"filePath": "data.csv", "viewName": "users", "fileType": "csv", "hasHeader": true}
     * @deprecated 建议使用 /api/spark/view/create-by-sql 接口，通过 SQL 直接从文件创建视图
     */
    @Deprecated
    @PostMapping("/view/create")
    public Map<String, Object> createTempView(@RequestBody Map<String, Object> request) {
        String filePath = (String) request.get("filePath");
        String viewName = (String) request.get("viewName");
        String fileType = (String) request.getOrDefault("fileType", "csv");
        Boolean hasHeader = request.get("hasHeader") != null ? 
            Boolean.parseBoolean(request.get("hasHeader").toString()) : true;
        
        return sparkService.createTempView(filePath, viewName, fileType, hasHeader);
    }

    /**
     * 通过 SQL 语句创建临时视图
     * POST /api/spark/view/create-by-sql
     * Body: {"sql": "CREATE TEMPORARY VIEW viewName AS SELECT * FROM table WHERE condition"}
     */
    @PostMapping("/view/create-by-sql")
    public Map<String, Object> createTempViewBySql(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        return sparkService.createTempViewBySql(sql);
    }

    /**
     * 删除临时视图
     * DELETE /api/spark/view/{viewName}
     */
    @DeleteMapping("/view/{viewName}")
    public Map<String, Object> dropTempView(@PathVariable String viewName) {
        return sparkService.dropTempView(viewName);
    }

    /**
     * 检查临时视图是否存在
     * GET /api/spark/view/{viewName}/exists
     */
    @GetMapping("/view/{viewName}/exists")
    public Map<String, Object> checkTempView(@PathVariable String viewName) {
        return sparkService.checkTempView(viewName);
    }

    /**
     * 列出所有临时视图
     * GET /api/spark/view/list
     */
    @GetMapping("/view/list")
    public Map<String, Object> listTempViews() {
        return sparkService.listTempViews();
    }

    /**
     * 获取视图 Schema
     * GET /api/spark/view/{viewName}/schema
     */
    @GetMapping("/view/{viewName}/schema")
    public Map<String, Object> getViewSchema(@PathVariable String viewName) {
        return sparkService.getViewSchema(viewName);
    }

    /**
     * 预览视图数据
     * GET /api/spark/view/{viewName}/preview?limit=10
     */
    @GetMapping("/view/{viewName}/preview")
    public Map<String, Object> previewView(
            @PathVariable String viewName,
            @RequestParam(defaultValue = "20") Integer limit) {
        return sparkService.previewView(viewName, limit);
    }

    /**
     * 校验 SQL 语法
     * POST /api/spark/sql/validate
     * Body: {"sql": "SELECT * FROM users"}
     */
    @PostMapping("/sql/validate")
    public Map<String, Object> validateSql(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        return sparkService.validateSql(sql);
    }

    /**
     * 解析 SELECT SQL
     * POST /api/spark/sql/parse
     * Body: {"sql": "SELECT name, age FROM users WHERE age > 25"}
     */
    @PostMapping("/sql/parse")
    public Map<String, Object> parseSql(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        return sparkService.parseSql(sql);
    }

    /**
     * 执行 SQL 查询
     * POST /api/spark/sql/execute
     * Body: {"sql": "SELECT * FROM users", "limit": 100}
     */
    @PostMapping("/sql/execute")
    public Map<String, Object> executeSql(@RequestBody Map<String, Object> request) {
        String sql = (String) request.get("sql");
        Integer limit = request.get("limit") != null ? 
            Integer.parseInt(request.get("limit").toString()) : null;
        return sparkService.executeSql(sql, limit);
    }

    /**
     * 快速执行 SQL（GET 方式，用于简单查询）
     * GET /api/spark/sql/execute?sql=SELECT * FROM users&limit=10
     */
    @GetMapping("/sql/execute")
    public Map<String, Object> executeSqlGet(
            @RequestParam String sql,
            @RequestParam(required = false) Integer limit) {
        return sparkService.executeSql(sql, limit);
    }

    /**
     * 将 CSV 文件转换为 INSERT 语句
     * POST /api/spark/csv/to-insert
     * Body: {"csvFilePath": "data.csv", "tableName": "users", "hasHeader": true, "batchSize": 100}
     */
    @PostMapping("/csv/to-insert")
    public Map<String, Object> csvToInsert(@RequestBody Map<String, Object> request) {
        String csvFilePath = (String) request.get("csvFilePath");
        String tableName = (String) request.get("tableName");
        Boolean hasHeader = request.get("hasHeader") != null ? 
            Boolean.parseBoolean(request.get("hasHeader").toString()) : true;
        Integer batchSize = request.get("batchSize") != null ? 
            Integer.parseInt(request.get("batchSize").toString()) : null;
        
        return sparkService.csvToInsertStatements(csvFilePath, tableName, hasHeader, batchSize);
    }

    /**
     * 将 CSV 文件转换为 REPLACE 语句
     * POST /api/spark/csv/to-replace
     * Body: {"csvFilePath": "data.csv", "tableName": "users", "hasHeader": true, "batchSize": 100}
     */
    @PostMapping("/csv/to-replace")
    public Map<String, Object> csvToReplace(@RequestBody Map<String, Object> request) {
        String csvFilePath = (String) request.get("csvFilePath");
        String tableName = (String) request.get("tableName");
        Boolean hasHeader = request.get("hasHeader") != null ? 
            Boolean.parseBoolean(request.get("hasHeader").toString()) : true;
        Integer batchSize = request.get("batchSize") != null ? 
            Integer.parseInt(request.get("batchSize").toString()) : null;
        
        return sparkService.csvToReplaceStatements(csvFilePath, tableName, hasHeader, batchSize);
    }

    /**
     * 将 CSV 文件转换为 SQL 语句并保存到文件
     * POST /api/spark/csv/to-sql-file
     * Body: {"csvFilePath": "data.csv", "tableName": "users", "hasHeader": true, 
     *        "outputFilePath": "output.sql", "batchSize": 100, "sqlType": "INSERT"}
     */
    @PostMapping("/csv/to-sql-file")
    public Map<String, Object> csvToSqlFile(@RequestBody Map<String, Object> request) {
        String csvFilePath = (String) request.get("csvFilePath");
        String tableName = (String) request.get("tableName");
        Boolean hasHeader = request.get("hasHeader") != null ? 
            Boolean.parseBoolean(request.get("hasHeader").toString()) : true;
        String outputFilePath = (String) request.get("outputFilePath");
        Integer batchSize = request.get("batchSize") != null ? 
            Integer.parseInt(request.get("batchSize").toString()) : null;
        String sqlType = (String) request.getOrDefault("sqlType", "INSERT");
        
        return sparkService.csvToSqlFile(csvFilePath, tableName, hasHeader, outputFilePath, batchSize, sqlType);
    }

    /**
     * 将视图数据转换为 INSERT 语句
     * POST /api/spark/view/{viewName}/to-insert
     * Body: {"tableName": "users", "batchSize": 100}
     */
    @PostMapping("/view/{viewName}/to-insert")
    public Map<String, Object> viewToInsert(@PathVariable String viewName, 
                                           @RequestBody Map<String, Object> request) {
        String tableName = (String) request.get("tableName");
        Integer batchSize = request.get("batchSize") != null ? 
            Integer.parseInt(request.get("batchSize").toString()) : null;
        
        return sparkService.viewToInsertStatements(viewName, tableName, batchSize);
    }

    /**
     * 将视图数据转换为 REPLACE 语句
     * POST /api/spark/view/{viewName}/to-replace
     * Body: {"tableName": "users", "batchSize": 100}
     */
    @PostMapping("/view/{viewName}/to-replace")
    public Map<String, Object> viewToReplace(@PathVariable String viewName, 
                                             @RequestBody Map<String, Object> request) {
        String tableName = (String) request.get("tableName");
        Integer batchSize = request.get("batchSize") != null ? 
            Integer.parseInt(request.get("batchSize").toString()) : null;
        
        return sparkService.viewToReplaceStatements(viewName, tableName, batchSize);
    }

    /**
     * 健康检查
     * GET /api/spark/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("status", "UP");
        result.put("message", "Spark Service is running");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
