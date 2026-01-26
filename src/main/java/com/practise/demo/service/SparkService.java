package com.practise.demo.service;

import com.practise.demo.utils.SparkUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Spark 服务层
 * 封装 SparkUtils 的功能，提供业务逻辑处理
 */
@Service
public class SparkService {

    /**
     * 读取 CSV 文件
     *
     * @param filePath 文件路径
     * @param hasHeader 是否包含表头
     * @return 数据行数和列信息
     */
    public Map<String, Object> readCsv(String filePath, boolean hasHeader) {
        Dataset<Row> df = SparkUtils.readCsv(filePath, hasHeader);
        long count = df.count();
        String[] columns = df.columns();
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("message", "CSV 文件读取成功");
        result.put("rowCount", count);
        result.put("columns", columns);
        result.put("columnCount", columns.length);
        return result;
    }

    /**
     * 读取 JSON 文件
     *
     * @param filePath 文件路径
     * @return 数据行数和列信息
     */
    public Map<String, Object> readJson(String filePath) {
        Dataset<Row> df = SparkUtils.readJson(filePath);
        long count = df.count();
        String[] columns = df.columns();
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("message", "JSON 文件读取成功");
        result.put("rowCount", count);
        result.put("columns", columns);
        result.put("columnCount", columns.length);
        return result;
    }

    /**
     * 创建临时视图（从文件，已废弃，建议使用 createTempViewBySql）
     *
     * @param filePath 文件路径
     * @param viewName 视图名称
     * @param fileType 文件类型 (csv/json)
     * @param hasHeader CSV 文件是否包含表头
     * @return 操作结果
     * @deprecated 建议使用 createTempViewBySql 方法，通过 SQL 创建视图
     */
    @Deprecated
    public Map<String, Object> createTempView(String filePath, String viewName, String fileType, Boolean hasHeader) {
        try {
            Dataset<Row> df;
            if ("json".equalsIgnoreCase(fileType)) {
                df = SparkUtils.readJson(filePath);
            } else {
                df = SparkUtils.readCsv(filePath, hasHeader != null && hasHeader);
            }
            
            SparkUtils.createTempView(df, viewName);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "临时视图创建成功");
            result.put("viewName", viewName);
            result.put("rowCount", df.count());
            result.put("columns", df.columns());
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "创建临时视图失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 通过 SQL 语句创建临时视图
     * 支持 CREATE TEMPORARY VIEW 语法
     * 支持从 CSV/JSON 文件直接读取并创建视图
     *
     * @param sql CREATE TEMPORARY VIEW 语句
     * @return 操作结果
     */
    public Map<String, Object> createTempViewBySql(String sql) {
        try {
            // 先校验 SQL 语法
            SparkUtils.SqlValidationResult validation = SparkUtils.validateSql(sql);
            if (!validation.isValid()) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "SQL 语法错误: " + validation.getMessage());
                return result;
            }

            // 检查是否是 CREATE TEMPORARY VIEW 语句
            String upperSql = sql.trim().toUpperCase();
            if (!upperSql.startsWith("CREATE") || !upperSql.contains("TEMPORARY") || !upperSql.contains("VIEW")) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "只支持 CREATE TEMPORARY VIEW 语句");
                return result;
            }

            // 执行创建视图的 SQL
            String viewName = SparkUtils.createTempViewBySql(sql);
            
            // 获取视图信息
            if (SparkUtils.tempViewExists(viewName)) {
                Dataset<Row> df = SparkUtils.getSparkSession().table(viewName);
                long rowCount = df.count();
                String[] columns = df.columns();

                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", true);
                result.put("message", "通过 SQL 创建临时视图成功");
                result.put("viewName", viewName);
                result.put("rowCount", rowCount);
                result.put("columns", columns);
                result.put("columnCount", columns.length);
                return result;
            } else {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "视图创建后无法找到: " + viewName);
                return result;
            }
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "通过 SQL 创建临时视图失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除临时视图
     *
     * @param viewName 视图名称
     * @return 操作结果
     */
    public Map<String, Object> dropTempView(String viewName) {
        boolean deleted = SparkUtils.dropTempView(viewName);
        Map<String, Object> result = new java.util.HashMap<>();
        if (deleted) {
            result.put("success", true);
            result.put("message", "临时视图删除成功");
        } else {
            result.put("success", false);
            result.put("message", "临时视图不存在或删除失败");
        }
        result.put("viewName", viewName);
        return result;
    }

    /**
     * 检查临时视图是否存在
     *
     * @param viewName 视图名称
     * @return 检查结果
     */
    public Map<String, Object> checkTempView(String viewName) {
        boolean exists = SparkUtils.tempViewExists(viewName);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("exists", exists);
        result.put("viewName", viewName);
        result.put("message", exists ? "视图存在" : "视图不存在");
        return result;
    }

    /**
     * 列出所有临时视图
     *
     * @return 视图列表
     */
    public Map<String, Object> listTempViews() {
        List<String> views = SparkUtils.listTempViews();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("views", views);
        result.put("count", views.size());
        result.put("message", "获取临时视图列表成功");
        return result;
    }

    /**
     * 校验 SQL 语法
     *
     * @param sql SQL 语句
     * @return 校验结果
     */
    public Map<String, Object> validateSql(String sql) {
        SparkUtils.SqlValidationResult validation = SparkUtils.validateSql(sql);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("valid", validation.isValid());
        result.put("message", validation.getMessage());
        return result;
    }

    /**
     * 解析 SELECT SQL
     *
     * @param sql SQL 语句
     * @return 解析结果
     */
    public Map<String, Object> parseSql(String sql) {
        SparkUtils.SqlParseResult parseResult = SparkUtils.parseSelectSql(sql);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", parseResult.isSuccess());
        result.put("message", parseResult.getMessage());
        if (parseResult.isSuccess()) {
            result.put("tableNames", parseResult.getTableNames());
            result.put("columns", parseResult.getColumns());
            result.put("columnTypes", parseResult.getColumnTypes());
        }
        return result;
    }

    /**
     * 执行 SQL 查询
     *
     * @param sql SQL 语句
     * @param limit 限制返回行数（可选）
     * @return 查询结果
     */
    public Map<String, Object> executeSql(String sql, Integer limit) {
        try {
            // 先校验 SQL
            SparkUtils.SqlValidationResult validation = SparkUtils.validateSql(sql);
            if (!validation.isValid()) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "SQL 语法错误: " + validation.getMessage());
                return result;
            }

            // 执行 SQL
            Dataset<Row> df = SparkUtils.executeSql(sql);
            long totalCount = df.count();
            
            // 限制返回行数
            Dataset<Row> resultDf = limit != null && limit > 0 ? df.limit(limit) : df;
            List<Map<String, Object>> data = SparkUtils.datasetToMapList(resultDf);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "SQL 执行成功");
            result.put("totalCount", totalCount);
            result.put("returnCount", data.size());
            result.put("data", data);
            result.put("columns", df.columns());
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "SQL 执行失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 获取视图的 Schema
     *
     * @param viewName 视图名称
     * @return Schema 信息
     */
    public Map<String, Object> getViewSchema(String viewName) {
        try {
            if (!SparkUtils.tempViewExists(viewName)) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "视图不存在: " + viewName);
                return result;
            }

            Dataset<Row> df = SparkUtils.getSparkSession().table(viewName);
            String[] columns = df.columns();
            List<Map<String, Object>> schema = new java.util.ArrayList<>();
            
            for (String column : columns) {
                Map<String, Object> field = new java.util.HashMap<>();
                field.put("name", column);
                // 获取字段类型
                try {
                    String type = df.schema().apply(column).dataType().toString();
                    field.put("type", type);
                } catch (Exception e) {
                    field.put("type", "Unknown");
                }
                schema.add(field);
            }

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "获取 Schema 成功");
            result.put("viewName", viewName);
            result.put("schema", schema);
            result.put("columnCount", columns.length);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "获取 Schema 失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 获取视图数据预览
     *
     * @param viewName 视图名称
     * @param limit 预览行数
     * @return 预览数据
     */
    public Map<String, Object> previewView(String viewName, Integer limit) {
        try {
            if (!SparkUtils.tempViewExists(viewName)) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "视图不存在: " + viewName);
                return result;
            }

            Dataset<Row> df = SparkUtils.getSparkSession().table(viewName);
            long totalCount = df.count();
            int previewLimit = limit != null && limit > 0 ? limit : 20;
            
            Dataset<Row> previewDf = df.limit(previewLimit);
            List<Map<String, Object>> data = SparkUtils.datasetToMapList(previewDf);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "获取预览数据成功");
            result.put("viewName", viewName);
            result.put("totalCount", totalCount);
            result.put("previewCount", data.size());
            result.put("data", data);
            result.put("columns", df.columns());
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "获取预览数据失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 将 CSV 文件转换为 INSERT 语句
     *
     * @param csvFilePath CSV 文件路径
     * @param tableName 目标表名
     * @param hasHeader 是否包含表头
     * @param batchSize 批量生成大小（可选，null 表示每条数据一个语句）
     * @return 转换结果
     */
    public Map<String, Object> csvToInsertStatements(String csvFilePath, String tableName, 
                                                      Boolean hasHeader, Integer batchSize) {
        try {
            List<String> statements = SparkUtils.csvToInsertStatements(
                csvFilePath, tableName, hasHeader != null && hasHeader, batchSize);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "转换成功");
            result.put("tableName", tableName);
            result.put("statementCount", statements.size());
            result.put("statements", statements);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "转换失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 将 CSV 文件转换为 REPLACE 语句
     *
     * @param csvFilePath CSV 文件路径
     * @param tableName 目标表名
     * @param hasHeader 是否包含表头
     * @param batchSize 批量生成大小（可选，null 表示每条数据一个语句）
     * @return 转换结果
     */
    public Map<String, Object> csvToReplaceStatements(String csvFilePath, String tableName, 
                                                       Boolean hasHeader, Integer batchSize) {
        try {
            List<String> statements = SparkUtils.csvToReplaceStatements(
                csvFilePath, tableName, hasHeader != null && hasHeader, batchSize);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "转换成功");
            result.put("tableName", tableName);
            result.put("statementCount", statements.size());
            result.put("statements", statements);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "转换失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 将 CSV 文件转换为 SQL 语句并保存到文件
     *
     * @param csvFilePath CSV 文件路径
     * @param tableName 目标表名
     * @param hasHeader 是否包含表头
     * @param outputFilePath 输出 SQL 文件路径
     * @param batchSize 批量生成大小
     * @param sqlType SQL 类型（INSERT 或 REPLACE）
     * @return 转换结果
     */
    public Map<String, Object> csvToSqlFile(String csvFilePath, String tableName, Boolean hasHeader,
                                            String outputFilePath, Integer batchSize, String sqlType) {
        try {
            int count = SparkUtils.csvToSqlFile(
                csvFilePath, tableName, hasHeader != null && hasHeader, 
                outputFilePath, batchSize, sqlType);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "转换并保存成功");
            result.put("tableName", tableName);
            result.put("statementCount", count);
            result.put("outputFile", outputFilePath);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "转换并保存失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 将视图数据转换为 INSERT 语句
     *
     * @param viewName 视图名称
     * @param tableName 目标表名
     * @param batchSize 批量生成大小
     * @return 转换结果
     */
    public Map<String, Object> viewToInsertStatements(String viewName, String tableName, Integer batchSize) {
        try {
            if (!SparkUtils.tempViewExists(viewName)) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "视图不存在: " + viewName);
                return result;
            }

            Dataset<Row> df = SparkUtils.getSparkSession().table(viewName);
            List<String> statements = SparkUtils.datasetToInsertStatements(df, tableName, batchSize);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "转换成功");
            result.put("tableName", tableName);
            result.put("statementCount", statements.size());
            result.put("statements", statements);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "转换失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 将视图数据转换为 REPLACE 语句
     *
     * @param viewName 视图名称
     * @param tableName 目标表名
     * @param batchSize 批量生成大小
     * @return 转换结果
     */
    public Map<String, Object> viewToReplaceStatements(String viewName, String tableName, Integer batchSize) {
        try {
            if (!SparkUtils.tempViewExists(viewName)) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("success", false);
                result.put("message", "视图不存在: " + viewName);
                return result;
            }

            Dataset<Row> df = SparkUtils.getSparkSession().table(viewName);
            List<String> statements = SparkUtils.datasetToReplaceStatements(df, tableName, batchSize);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("message", "转换成功");
            result.put("tableName", tableName);
            result.put("statementCount", statements.size());
            result.put("statements", statements);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("message", "转换失败: " + e.getMessage());
            return result;
        }
    }
}
