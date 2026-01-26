package com.practise.demo.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spark 本地模式工具类
 * 不连接 Hive，用于本地 Spark 数据处理
 */
public class SparkUtils {

    private static SparkSession sparkSession;
    private static JavaSparkContext javaSparkContext;

    /**
     * 获取或创建 SparkSession（单例模式）
     * 使用本地模式，不连接 Hive
     *
     * @param appName 应用名称
     * @return SparkSession 实例
     */
    public static SparkSession getSparkSession(String appName) {
        if (sparkSession == null) {
            SparkConf conf = new SparkConf()
                    .setAppName(appName)
                    .setMaster("local[*]")  // 本地模式，使用所有可用核心
                    .set("spark.sql.warehouse.dir", "file:///tmp/spark-warehouse")  // 本地仓库目录
                    .set("spark.driver.host", "localhost")
                    .set("spark.sql.shuffle.partitions", "2");  // 减少分区数，适合本地测试

            sparkSession = SparkSession.builder()
                    .config(conf)
                    // 不启用 Hive 支持，使用本地模式
                    .getOrCreate();

            // 设置日志级别，减少输出
            sparkSession.sparkContext().setLogLevel("WARN");
        }
        return sparkSession;
    }

    /**
     * 获取默认的 SparkSession
     *
     * @return SparkSession 实例
     */
    public static SparkSession getSparkSession() {
        return getSparkSession("SparkLocalApp");
    }

    /**
     * 获取或创建 JavaSparkContext（单例模式）
     *
     * @param appName 应用名称
     * @return JavaSparkContext 实例
     */
    public static JavaSparkContext getJavaSparkContext(String appName) {
        if (javaSparkContext == null) {
            SparkConf conf = new SparkConf()
                    .setAppName(appName)
                    .setMaster("local[*]")
                    .set("spark.driver.host", "localhost");

            javaSparkContext = new JavaSparkContext(conf);
            javaSparkContext.setLogLevel("WARN");
        }
        return javaSparkContext;
    }

    /**
     * 获取默认的 JavaSparkContext
     *
     * @return JavaSparkContext 实例
     */
    public static JavaSparkContext getJavaSparkContext() {
        return getJavaSparkContext("SparkLocalApp");
    }

    /**
     * 从 CSV 文件读取数据
     *
     * @param filePath CSV 文件路径
     * @param header   是否包含表头
     * @return Dataset<Row>
     */
    public static Dataset<Row> readCsv(String filePath, boolean header) {
        SparkSession spark = getSparkSession();
        return spark.read()
                .option("header", header)
                .option("inferSchema", "true")
                .csv(filePath);
    }

    /**
     * 从 JSON 文件读取数据
     *
     * @param filePath JSON 文件路径
     * @return Dataset<Row>
     */
    public static Dataset<Row> readJson(String filePath) {
        SparkSession spark = getSparkSession();
        return spark.read().json(filePath);
    }

    /**
     * 从文本文件读取数据
     *
     * @param filePath 文本文件路径
     * @return JavaRDD<String>
     */
    public static JavaRDD<String> readTextFile(String filePath) {
        JavaSparkContext sc = getJavaSparkContext();
        return sc.textFile(filePath);
    }

    /**
     * 执行 SQL 查询
     *
     * @param sql SQL 查询语句
     * @return Dataset<Row>
     */
    public static Dataset<Row> executeSql(String sql) {
        SparkSession spark = getSparkSession();
        return spark.sql(sql);
    }

    /**
     * 将 Dataset 注册为临时视图
     *
     * @param dataset Dataset
     * @param viewName 视图名称
     */
    public static void createTempView(Dataset<Row> dataset, String viewName) {
        dataset.createOrReplaceTempView(viewName);
    }

    /**
     * 删除临时视图
     * 注意：虽然不连接 Hive，临时视图在 SparkSession 关闭时会自动清理，
     * 但显式删除是个好习惯，特别是在创建大量临时视图时，可以及时释放资源
     *
     * @param viewName 视图名称
     * @return 是否删除成功（视图存在则返回 true，不存在返回 false）
     */
    public static boolean dropTempView(String viewName) {
        try {
            SparkSession spark = getSparkSession();
            spark.catalog().dropTempView(viewName);
            return true;
        } catch (Exception e) {
            // 视图不存在或其他错误
            return false;
        }
    }

    /**
     * 检查临时视图是否存在
     *
     * @param viewName 视图名称
     * @return 视图是否存在
     */
    public static boolean tempViewExists(String viewName) {
        try {
            SparkSession spark = getSparkSession();
            return spark.catalog().tableExists(viewName);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取所有临时视图名称列表
     *
     * @return 临时视图名称列表
     */
    public static List<String> listTempViews() {
        SparkSession spark = getSparkSession();
        String[] tables = spark.catalog().listTables().select("name").collectAsList()
                .stream()
                .map(row -> row.getString(0))
                .toArray(String[]::new);
        return Arrays.asList(tables);
    }

    /**
     * 校验 SQL 是否合理（语法检查）
     * 通过尝试解析 SQL 来验证语法是否正确
     *
     * @param sql SQL 语句
     * @return 校验结果，包含是否有效和错误信息
     */
    public static SqlValidationResult validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new SqlValidationResult(false, "SQL 语句不能为空");
        }

        try {
            SparkSession spark = getSparkSession();
            // 尝试解析 SQL，如果语法错误会抛出异常
            spark.sessionState().sqlParser().parsePlan(sql);
            return new SqlValidationResult(true, "SQL 语法正确");
        } catch (Exception e) {
            return new SqlValidationResult(false, "SQL 语法错误: " + e.getMessage());
        }
    }

    /**
     * 解析 SELECT SQL 语句，提取表名和字段信息
     * 支持简单的 SELECT 语句解析，包括：
     * - 单表查询
     * - JOIN 查询
     * - 子查询（部分支持）
     *
     * @param sql SELECT SQL 语句
     * @return SQL 解析结果，包含表名、字段等信息
     */
    public static SqlParseResult parseSelectSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new SqlParseResult(false, "SQL 语句不能为空", null, null, null);
        }

        String normalizedSql = sql.trim().toUpperCase();
        
        // 检查是否是 SELECT 语句
        if (!normalizedSql.startsWith("SELECT")) {
            return new SqlParseResult(false, "只支持 SELECT 语句", null, null, null);
        }

        try {
            List<String> tableNames = new ArrayList<>();
            List<String> columns = new ArrayList<>();
            Map<String, String> columnTypes = new HashMap<>();

            // 提取表名（支持 FROM 和 JOIN）
            extractTableNames(sql, tableNames);
            
            // 提取字段名
            extractColumns(sql, columns);
            
            // 如果表存在，尝试获取字段类型
            SparkSession spark = getSparkSession();
            for (String tableName : tableNames) {
                if (tempViewExists(tableName)) {
                    Dataset<Row> df = spark.table(tableName);
                    StructType schema = df.schema();
                    for (StructField field : schema.fields()) {
                        String fieldName = field.name().toLowerCase();
                        if (columns.contains(fieldName) || columns.contains("*")) {
                            columnTypes.put(fieldName, field.dataType().toString());
                        }
                    }
                }
            }

            return new SqlParseResult(true, "解析成功", tableNames, columns, columnTypes);
        } catch (Exception e) {
            return new SqlParseResult(false, "解析失败: " + e.getMessage(), null, null, null);
        }
    }

    /**
     * 通过 SQL 语句创建临时视图
     * 支持 CREATE TEMPORARY VIEW 语法
     * 例如: CREATE TEMPORARY VIEW viewName AS SELECT * FROM table WHERE condition
     *
     * @param sql CREATE TEMPORARY VIEW 语句
     * @return 创建的视图名称，如果失败返回 null
     */
    public static String createTempViewBySql(String sql) {
        try {
            SparkSession spark = getSparkSession();
            
            // 解析 SQL 获取视图名称
            String viewName = extractViewNameFromCreateSql(sql);
            if (viewName == null) {
                throw new IllegalArgumentException("无法从 SQL 中提取视图名称");
            }
            
            // 执行 CREATE TEMPORARY VIEW 语句
            spark.sql(sql);
            
            return viewName;
        } catch (Exception e) {
            throw new RuntimeException("通过 SQL 创建临时视图失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 CREATE TEMPORARY VIEW 语句中提取视图名称
     *
     * @param sql CREATE TEMPORARY VIEW 语句
     * @return 视图名称
     */
    private static String extractViewNameFromCreateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return null;
        }
        
        // 匹配 CREATE TEMPORARY VIEW viewName AS ...
        Pattern pattern = Pattern.compile(
            "CREATE\\s+TEMPORARY\\s+VIEW\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(sql);
        
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        
        // 也支持 CREATE OR REPLACE TEMPORARY VIEW
        pattern = Pattern.compile(
            "CREATE\\s+(?:OR\\s+REPLACE\\s+)?TEMPORARY\\s+VIEW\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
            Pattern.CASE_INSENSITIVE
        );
        matcher = pattern.matcher(sql);
        
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        
        return null;
    }

    /**
     * 从 SQL 中提取表名
     */
    private static void extractTableNames(String sql, List<String> tableNames) {
        // 匹配 FROM 子句中的表名
        Pattern fromPattern = Pattern.compile(
            "FROM\\s+([a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)?)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher fromMatcher = fromPattern.matcher(sql);
        while (fromMatcher.find()) {
            String tableName = fromMatcher.group(1).toLowerCase();
            // 移除数据库前缀（如果有）
            if (tableName.contains(".")) {
                tableName = tableName.substring(tableName.lastIndexOf(".") + 1);
            }
            if (!tableNames.contains(tableName)) {
                tableNames.add(tableName);
            }
        }

        // 匹配 JOIN 子句中的表名
        Pattern joinPattern = Pattern.compile(
            "JOIN\\s+([a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)?)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher joinMatcher = joinPattern.matcher(sql);
        while (joinMatcher.find()) {
            String tableName = joinMatcher.group(1).toLowerCase();
            if (tableName.contains(".")) {
                tableName = tableName.substring(tableName.lastIndexOf(".") + 1);
            }
            if (!tableNames.contains(tableName)) {
                tableNames.add(tableName);
            }
        }
    }

    /**
     * 从 SQL 中提取字段名
     */
    private static void extractColumns(String sql, List<String> columns) {
        // 提取 SELECT 和 FROM 之间的内容
        Pattern selectPattern = Pattern.compile(
            "SELECT\\s+(.*?)\\s+FROM",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        Matcher selectMatcher = selectPattern.matcher(sql);
        
        if (selectMatcher.find()) {
            String selectClause = selectMatcher.group(1).trim();
            
            // 如果包含 *，添加 *
            if (selectClause.contains("*")) {
                columns.add("*");
            } else {
                // 分割字段（处理逗号分隔）
                String[] fields = selectClause.split(",");
                for (String field : fields) {
                    field = field.trim();
                    // 移除别名（AS 或空格后的别名）
                    if (field.contains(" AS ")) {
                        field = field.substring(0, field.indexOf(" AS ")).trim();
                    } else if (field.contains(" ")) {
                        // 可能是别名，取第一部分
                        String[] parts = field.split("\\s+");
                        field = parts[0].trim();
                    }
                    // 移除表前缀（如果有）
                    if (field.contains(".")) {
                        field = field.substring(field.lastIndexOf(".") + 1);
                    }
                    // 移除函数调用（简单处理）
                    if (field.contains("(")) {
                        // 提取函数内的字段或保留函数名
                        Pattern funcPattern = Pattern.compile("\\(([^)]+)\\)");
                        Matcher funcMatcher = funcPattern.matcher(field);
                        if (funcMatcher.find()) {
                            String funcParam = funcMatcher.group(1).trim();
                            if (funcParam.contains(".")) {
                                funcParam = funcParam.substring(funcParam.lastIndexOf(".") + 1);
                            }
                            columns.add(funcParam);
                        } else {
                            columns.add(field);
                        }
                    } else if (!field.isEmpty() && !field.equals("*")) {
                        columns.add(field.toLowerCase());
                    }
                }
            }
        }
    }

    /**
     * SQL 校验结果类
     */
    public static class SqlValidationResult {
        private final boolean valid;
        private final String message;

        public SqlValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "SqlValidationResult{" +
                    "valid=" + valid +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    /**
     * SQL 解析结果类
     */
    public static class SqlParseResult {
        private final boolean success;
        private final String message;
        private final List<String> tableNames;
        private final List<String> columns;
        private final Map<String, String> columnTypes;

        public SqlParseResult(boolean success, String message, 
                             List<String> tableNames, 
                             List<String> columns, 
                             Map<String, String> columnTypes) {
            this.success = success;
            this.message = message;
            this.tableNames = tableNames != null ? tableNames : new ArrayList<>();
            this.columns = columns != null ? columns : new ArrayList<>();
            this.columnTypes = columnTypes != null ? columnTypes : new HashMap<>();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getTableNames() {
            return tableNames;
        }

        public List<String> getColumns() {
            return columns;
        }

        public Map<String, String> getColumnTypes() {
            return columnTypes;
        }

        @Override
        public String toString() {
            return "SqlParseResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", tableNames=" + tableNames +
                    ", columns=" + columns +
                    ", columnTypes=" + columnTypes +
                    '}';
        }
    }

    /**
     * 将 Dataset 写入 CSV 文件
     *
     * @param dataset  Dataset
     * @param filePath 输出文件路径
     */
    public static void writeCsv(Dataset<Row> dataset, String filePath) {
        dataset.write()
                .mode("overwrite")
                .option("header", "true")
                .csv(filePath);
    }

    /**
     * 将 Dataset 写入 JSON 文件
     *
     * @param dataset  Dataset
     * @param filePath 输出文件路径
     */
    public static void writeJson(Dataset<Row> dataset, String filePath) {
        dataset.write()
                .mode("overwrite")
                .json(filePath);
    }

    /**
     * 将 Dataset 转换为 List<Map>
     *
     * @param dataset Dataset
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> datasetToMapList(Dataset<Row> dataset) {
        String[] columns = dataset.columns();
        Row[] rows = (Row[]) dataset.collect();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Row row : rows) {
            Map<String, Object> map = new java.util.HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                map.put(columns[i], row.get(i));
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 从 List 创建 Dataset
     *
     * @param data     数据列表
     * @param schema   数据结构
     * @return Dataset<Row>
     */
    public static Dataset<Row> createDatasetFromList(List<Row> data, StructType schema) {
        SparkSession spark = getSparkSession();
        return spark.createDataFrame(data, schema);
    }

    /**
     * 创建简单的 StructType Schema
     *
     * @param fields 字段定义，格式：("name", "String"), ("age", "Integer")
     * @return StructType
     */
    public static StructType createSchema(String[]... fields) {
        List<StructField> structFields = new ArrayList<>();
        for (String[] field : fields) {
            String name = field[0];
            String type = field[1];
            StructField structField = null;

            switch (type.toLowerCase()) {
                case "string":
                    structField = DataTypes.createStructField(name, DataTypes.StringType, true);
                    break;
                case "integer":
                case "int":
                    structField = DataTypes.createStructField(name, DataTypes.IntegerType, true);
                    break;
                case "long":
                    structField = DataTypes.createStructField(name, DataTypes.LongType, true);
                    break;
                case "double":
                    structField = DataTypes.createStructField(name, DataTypes.DoubleType, true);
                    break;
                case "float":
                    structField = DataTypes.createStructField(name, DataTypes.FloatType, true);
                    break;
                case "boolean":
                    structField = DataTypes.createStructField(name, DataTypes.BooleanType, true);
                    break;
                case "date":
                    structField = DataTypes.createStructField(name, DataTypes.DateType, true);
                    break;
                case "timestamp":
                    structField = DataTypes.createStructField(name, DataTypes.TimestampType, true);
                    break;
                default:
                    structField = DataTypes.createStructField(name, DataTypes.StringType, true);
            }
            structFields.add(structField);
        }
        return DataTypes.createStructType(structFields);
    }

    /**
     * 关闭 SparkSession
     */
    public static void closeSparkSession() {
        if (sparkSession != null) {
            sparkSession.close();
            sparkSession = null;
        }
    }

    /**
     * 关闭 JavaSparkContext
     */
    public static void closeJavaSparkContext() {
        if (javaSparkContext != null) {
            javaSparkContext.close();
            javaSparkContext = null;
        }
    }

    /**
     * 关闭所有 Spark 资源
     */
    public static void closeAll() {
        closeSparkSession();
        closeJavaSparkContext();
    }

    /**
     * 打印 Dataset 的前 N 行
     *
     * @param dataset Dataset
     * @param n       行数
     */
    public static void show(Dataset<Row> dataset, int n) {
        dataset.show(n);
    }

    /**
     * 打印 Dataset 的前 20 行
     *
     * @param dataset Dataset
     */
    public static void show(Dataset<Row> dataset) {
        dataset.show();
    }

    /**
     * 获取 Dataset 的行数
     *
     * @param dataset Dataset
     * @return 行数
     */
    public static long count(Dataset<Row> dataset) {
        return dataset.count();
    }

    /**
     * 打印 Dataset 的 Schema
     *
     * @param dataset Dataset
     */
    public static void printSchema(Dataset<Row> dataset) {
        dataset.printSchema();
    }

    // ==================== 新增实用功能 ====================

    /**
     * 获取数据统计信息（describe）
     * 返回数值列的统计信息：count, mean, stddev, min, max
     *
     * @param dataset Dataset
     * @return 统计信息 Dataset
     */
    public static Dataset<Row> describe(Dataset<Row> dataset) {
        return dataset.describe();
    }

    /**
     * 获取详细统计信息（summary）
     * 包含更多统计指标：count, mean, stddev, min, 25%, 50%, 75%, max
     *
     * @param dataset Dataset
     * @return 详细统计信息 Dataset
     */
    public static Dataset<Row> summary(Dataset<Row> dataset) {
        return dataset.summary();
    }

    /**
     * 数据去重
     *
     * @param dataset Dataset
     * @return 去重后的 Dataset
     */
    public static Dataset<Row> distinct(Dataset<Row> dataset) {
        return dataset.distinct();
    }

    /**
     * 按指定列去重
     *
     * @param dataset Dataset
     * @param columns 列名数组
     * @return 去重后的 Dataset
     */
    public static Dataset<Row> dropDuplicates(Dataset<Row> dataset, String... columns) {
        return dataset.dropDuplicates(columns);
    }

    /**
     * 数据采样（随机采样）
     *
     * @param dataset Dataset
     * @param fraction 采样比例 (0.0 - 1.0)
     * @param seed 随机种子（可选，用于可重复采样）
     * @return 采样后的 Dataset
     */
    public static Dataset<Row> sample(Dataset<Row> dataset, double fraction, Long seed) {
        if (seed != null) {
            return dataset.sample(false, fraction, seed);
        } else {
            return dataset.sample(false, fraction);
        }
    }

    /**
     * 数据合并（UNION）
     * 合并两个 Dataset，要求 Schema 相同
     *
     * @param dataset1 第一个 Dataset
     * @param dataset2 第二个 Dataset
     * @return 合并后的 Dataset
     */
    public static Dataset<Row> union(Dataset<Row> dataset1, Dataset<Row> dataset2) {
        return dataset1.union(dataset2);
    }

    /**
     * 获取数据分区数
     *
     * @param dataset Dataset
     * @return 分区数
     */
    public static int getPartitionCount(Dataset<Row> dataset) {
        return dataset.rdd().getNumPartitions();
    }

    /**
     * 重新分区
     *
     * @param dataset Dataset
     * @param numPartitions 目标分区数
     * @return 重新分区后的 Dataset
     */
    public static Dataset<Row> repartition(Dataset<Row> dataset, int numPartitions) {
        return dataset.repartition(numPartitions);
    }

    /**
     * 减少分区数（coalesce）
     * 比 repartition 更高效，只能减少分区数
     *
     * @param dataset Dataset
     * @param numPartitions 目标分区数
     * @return 合并分区后的 Dataset
     */
    public static Dataset<Row> coalesce(Dataset<Row> dataset, int numPartitions) {
        return dataset.coalesce(numPartitions);
    }

    /**
     * 缓存 Dataset 到内存
     *
     * @param dataset Dataset
     * @return 缓存后的 Dataset
     */
    public static Dataset<Row> cache(Dataset<Row> dataset) {
        return dataset.cache();
    }

    /**
     * 释放缓存
     *
     * @param dataset Dataset
     */
    public static void unpersist(Dataset<Row> dataset) {
        dataset.unpersist();
    }

    /**
     * 检查 Dataset 是否已缓存
     *
     * @param dataset Dataset
     * @return 是否已缓存
     */
    public static boolean isCached(Dataset<Row> dataset) {
        return dataset.isStreaming() ? false : dataset.storageLevel().useMemory();
    }

    /**
     * 处理空值 - 删除包含 null 的行
     *
     * @param dataset Dataset
     * @return 处理后的 Dataset
     */
    public static Dataset<Row> dropNulls(Dataset<Row> dataset) {
        return dataset.na().drop();
    }

    /**
     * 处理空值 - 删除指定列包含 null 的行
     *
     * @param dataset Dataset
     * @param columns 列名数组
     * @return 处理后的 Dataset
     */
    public static Dataset<Row> dropNulls(Dataset<Row> dataset, String... columns) {
        return dataset.na().drop(columns);
    }

    /**
     * 填充空值 - 用指定值填充所有 null
     *
     * @param dataset Dataset
     * @param value 填充值
     * @return 处理后的 Dataset
     */
    public static Dataset<Row> fillNulls(Dataset<Row> dataset, String value) {
        return dataset.na().fill(value);
    }

    /**
     * 填充空值 - 用指定值填充指定列的 null
     *
     * @param dataset Dataset
     * @param valueMap 列名和填充值的映射
     * @return 处理后的 Dataset
     */
    public static Dataset<Row> fillNulls(Dataset<Row> dataset, Map<String, Object> valueMap) {
        return dataset.na().fill(valueMap);
    }

    /**
     * 获取执行计划（逻辑计划）
     *
     * @param dataset Dataset
     * @return 执行计划字符串
     */
    public static String explain(Dataset<Row> dataset) {
        return dataset.queryExecution().logical().toString();
    }

    /**
     * 获取详细执行计划
     *
     * @param dataset Dataset
     * @param extended 是否显示扩展信息
     * @return 执行计划字符串
     */
    public static String explain(Dataset<Row> dataset, boolean extended) {
        if (extended) {
            return dataset.queryExecution().toString();
        } else {
            return dataset.queryExecution().logical().toString();
        }
    }

    /**
     * 打印执行计划
     *
     * @param dataset Dataset
     */
    public static void explainPlan(Dataset<Row> dataset) {
        dataset.explain();
    }

    /**
     * 打印详细执行计划
     *
     * @param dataset Dataset
     */
    public static void explainPlan(Dataset<Row> dataset, boolean extended) {
        dataset.explain(extended);
    }

    /**
     * 获取 SQL 执行计划
     *
     * @param sql SQL 语句
     * @return 执行计划字符串
     */
    public static String explainSql(String sql) {
        SparkSession spark = getSparkSession();
        Dataset<Row> df = spark.sql(sql);
        return df.queryExecution().logical().toString();
    }

    /**
     * 数据采样 - 获取前 N 行
     *
     * @param dataset Dataset
     * @param n 行数
     * @return 采样后的 Dataset
     */
    public static Dataset<Row> takeSample(Dataset<Row> dataset, int n) {
        return dataset.limit(n);
    }

    /**
     * 数据排序
     *
     * @param dataset Dataset
     * @param sortCol 排序列
     * @param ascending 是否升序
     * @return 排序后的 Dataset
     */
    public static Dataset<Row> sort(Dataset<Row> dataset, String sortCol, boolean ascending) {
        if (ascending) {
            return dataset.sort(org.apache.spark.sql.functions.col(sortCol).asc());
        } else {
            return dataset.sort(org.apache.spark.sql.functions.col(sortCol).desc());
        }
    }

    /**
     * 数据排序（多列）
     *
     * @param dataset Dataset
     * @param sortCols 排序列数组
     * @return 排序后的 Dataset
     */
    public static Dataset<Row> sort(Dataset<Row> dataset, String... sortCols) {
        org.apache.spark.sql.Column[] columns = new org.apache.spark.sql.Column[sortCols.length];
        for (int i = 0; i < sortCols.length; i++) {
            columns[i] = org.apache.spark.sql.functions.col(sortCols[i]);
        }
        return dataset.sort(columns);
    }

    /**
     * 获取列的数据类型
     *
     * @param dataset Dataset
     * @param columnName 列名
     * @return 数据类型字符串
     */
    public static String getColumnType(Dataset<Row> dataset, String columnName) {
        return dataset.schema().apply(columnName).dataType().toString();
    }

    /**
     * 获取所有列的数据类型
     *
     * @param dataset Dataset
     * @return 列名和类型的映射
     */
    public static Map<String, String> getColumnTypes(Dataset<Row> dataset) {
        Map<String, String> types = new java.util.HashMap<>();
        for (StructField field : dataset.schema().fields()) {
            types.put(field.name(), field.dataType().toString());
        }
        return types;
    }

    /**
     * 数据转换 - 重命名列
     *
     * @param dataset Dataset
     * @param oldName 旧列名
     * @param newName 新列名
     * @return 转换后的 Dataset
     */
    public static Dataset<Row> renameColumn(Dataset<Row> dataset, String oldName, String newName) {
        return dataset.withColumnRenamed(oldName, newName);
    }

    /**
     * 数据转换 - 添加新列（常量值）
     *
     * @param dataset Dataset
     * @param columnName 新列名
     * @param value 常量值
     * @return 转换后的 Dataset
     */
    public static Dataset<Row> addColumn(Dataset<Row> dataset, String columnName, Object value) {
        return dataset.withColumn(columnName, org.apache.spark.sql.functions.lit(value));
    }

    /**
     * 数据转换 - 删除列
     *
     * @param dataset Dataset
     * @param columns 要删除的列名
     * @return 转换后的 Dataset
     */
    public static Dataset<Row> dropColumns(Dataset<Row> dataset, String... columns) {
        Dataset<Row> result = dataset;
        for (String col : columns) {
            result = result.drop(col);
        }
        return result;
    }

    /**
     * 数据转换 - 选择列
     *
     * @param dataset Dataset
     * @param columns 要选择的列名
     * @return 选择后的 Dataset
     */
    public static Dataset<Row> selectColumns(Dataset<Row> dataset, String... columns) {
        org.apache.spark.sql.Column[] cols = new org.apache.spark.sql.Column[columns.length];
        for (int i = 0; i < columns.length; i++) {
            cols[i] = org.apache.spark.sql.functions.col(columns[i]);
        }
        return dataset.select(cols);
    }

    /**
     * 数据分组统计
     *
     * @param dataset Dataset
     * @param groupByCols 分组列
     * @return 分组后的 Dataset（需要配合聚合函数使用）
     */
    public static org.apache.spark.sql.RelationalGroupedDataset groupBy(Dataset<Row> dataset, String... groupByCols) {
        org.apache.spark.sql.Column[] cols = new org.apache.spark.sql.Column[groupByCols.length];
        for (int i = 0; i < groupByCols.length; i++) {
            cols[i] = org.apache.spark.sql.functions.col(groupByCols[i]);
        }
        return dataset.groupBy(cols);
    }

    /**
     * 数据连接（JOIN）
     *
     * @param left 左表
     * @param right 右表
     * @param joinExprs JOIN 条件表达式
     * @param joinType JOIN 类型 (inner, left, right, outer, left_outer, right_outer, left_semi, left_anti)
     * @return JOIN 后的 Dataset
     */
    public static Dataset<Row> join(Dataset<Row> left, Dataset<Row> right, 
                                    org.apache.spark.sql.Column joinExprs, String joinType) {
        return left.join(right, joinExprs, joinType);
    }

    /**
     * 内连接（INNER JOIN）
     *
     * @param left 左表
     * @param right 右表
     * @param joinExprs JOIN 条件表达式
     * @return JOIN 后的 Dataset
     */
    public static Dataset<Row> innerJoin(Dataset<Row> left, Dataset<Row> right, 
                                        org.apache.spark.sql.Column joinExprs) {
        return left.join(right, joinExprs, "inner");
    }

    /**
     * 将 Dataset 写入 Parquet 格式
     *
     * @param dataset Dataset
     * @param filePath 输出文件路径
     */
    public static void writeParquet(Dataset<Row> dataset, String filePath) {
        dataset.write()
                .mode("overwrite")
                .parquet(filePath);
    }

    /**
     * 从 Parquet 文件读取数据
     *
     * @param filePath Parquet 文件路径
     * @return Dataset
     */
    public static Dataset<Row> readParquet(String filePath) {
        SparkSession spark = getSparkSession();
        return spark.read().parquet(filePath);
    }

    // ==================== CSV 转 SQL 语句功能 ====================

    /**
     * 将 CSV 文件转换为 INSERT 语句
     *
     * @param csvFilePath CSV 文件路径
     * @param tableName 目标表名
     * @param hasHeader 是否包含表头
     * @param batchSize 批量生成大小（每批生成多少条INSERT语句）
     * @return INSERT 语句列表
     */
    public static List<String> csvToInsertStatements(String csvFilePath, String tableName, 
                                                     boolean hasHeader, Integer batchSize) {
        return csvToSqlStatements(csvFilePath, tableName, hasHeader, batchSize, "INSERT");
    }

    /**
     * 将 CSV 文件转换为 REPLACE 语句
     *
     * @param csvFilePath CSV 文件路径
     * @param tableName 目标表名
     * @param hasHeader 是否包含表头
     * @param batchSize 批量生成大小（每批生成多少条REPLACE语句）
     * @return REPLACE 语句列表
     */
    public static List<String> csvToReplaceStatements(String csvFilePath, String tableName, 
                                                       boolean hasHeader, Integer batchSize) {
        return csvToSqlStatements(csvFilePath, tableName, hasHeader, batchSize, "REPLACE");
    }

    /**
     * 将 CSV 文件转换为 SQL 语句（INSERT 或 REPLACE）
     *
     * @param csvFilePath CSV 文件路径
     * @param tableName 目标表名
     * @param hasHeader 是否包含表头
     * @param batchSize 批量生成大小（null 或 0 表示不批量，每条数据一个语句）
     * @param sqlType SQL 类型：INSERT 或 REPLACE
     * @return SQL 语句列表
     */
    public static List<String> csvToSqlStatements(String csvFilePath, String tableName, 
                                                  boolean hasHeader, Integer batchSize, String sqlType) {
        List<String> sqlStatements = new ArrayList<>();
        
        try {
            // 读取 CSV 文件
            Dataset<Row> df = readCsv(csvFilePath, hasHeader);
            String[] columns = df.columns();
            
            // 收集所有数据
            Row[] rows = (Row[]) df.collect();
            
            if (rows.length == 0) {
                return sqlStatements;
            }
            
            // 构建列名部分
            String columnsPart = String.join(", ", columns);
            
            // 判断是否批量生成
            boolean useBatch = batchSize != null && batchSize > 0;
            int actualBatchSize = useBatch ? batchSize : 1;
            
            StringBuilder currentBatch = new StringBuilder();
            int currentBatchCount = 0;
            
            for (Row row : rows) {
                // 构建 VALUES 部分
                StringBuilder values = new StringBuilder("(");
                for (int i = 0; i < columns.length; i++) {
                    if (i > 0) {
                        values.append(", ");
                    }
                    Object value = row.get(i);
                    values.append(formatSqlValue(value));
                }
                values.append(")");
                
                if (useBatch) {
                    // 批量模式
                    if (currentBatchCount == 0) {
                        // 开始新的批量语句
                        currentBatch.append(sqlType).append(" INTO ").append(tableName)
                                .append(" (").append(columnsPart).append(") VALUES ");
                    } else {
                        currentBatch.append(", ");
                    }
                    currentBatch.append(values);
                    currentBatchCount++;
                    
                    // 达到批量大小时，完成当前批量语句
                    if (currentBatchCount >= actualBatchSize) {
                        sqlStatements.add(currentBatch.toString());
                        currentBatch = new StringBuilder();
                        currentBatchCount = 0;
                    }
                } else {
                    // 单条模式
                    String sql = sqlType + " INTO " + tableName + " (" + columnsPart + ") VALUES " + values;
                    sqlStatements.add(sql);
                }
            }
            
            // 处理剩余的批量数据
            if (useBatch && currentBatchCount > 0) {
                sqlStatements.add(currentBatch.toString());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("转换 CSV 到 SQL 语句失败: " + e.getMessage(), e);
        }
        
        return sqlStatements;
    }

    /**
     * 将 CSV 文件转换为 SQL 语句并保存到文件
     *
     * @param csvFilePath CSV 文件路径
     * @param tableName 目标表名
     * @param hasHeader 是否包含表头
     * @param outputFilePath 输出 SQL 文件路径
     * @param batchSize 批量生成大小
     * @param sqlType SQL 类型：INSERT 或 REPLACE
     * @return 生成的 SQL 语句数量
     */
    public static int csvToSqlFile(String csvFilePath, String tableName, boolean hasHeader,
                                   String outputFilePath, Integer batchSize, String sqlType) {
        try {
            List<String> sqlStatements = csvToSqlStatements(csvFilePath, tableName, hasHeader, batchSize, sqlType);
            
            // 写入文件
            java.io.FileWriter writer = new java.io.FileWriter(outputFilePath);
            for (String sql : sqlStatements) {
                writer.write(sql);
                writer.write(";\n");
            }
            writer.close();
            
            return sqlStatements.size();
        } catch (Exception e) {
            throw new RuntimeException("保存 SQL 文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 格式化 SQL 值（处理转义和类型）
     *
     * @param value 值
     * @return 格式化后的 SQL 值字符串
     */
    private static String formatSqlValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        
        // 字符串类型需要加引号和转义
        if (value instanceof String) {
            String str = (String) value;
            // 转义单引号
            str = str.replace("'", "''");
            // 转义反斜杠
            str = str.replace("\\", "\\\\");
            return "'" + str + "'";
        }
        
        // 数字类型直接返回
        if (value instanceof Number) {
            return value.toString();
        }
        
        // 布尔类型
        if (value instanceof Boolean) {
            return value.toString();
        }
        
        // 日期时间类型
        if (value instanceof java.sql.Date || value instanceof java.sql.Timestamp) {
            return "'" + value.toString() + "'";
        }
        
        // 其他类型转为字符串
        String str = value.toString();
        str = str.replace("'", "''");
        str = str.replace("\\", "\\\\");
        return "'" + str + "'";
    }

    /**
     * 将 Dataset 转换为 INSERT 语句
     *
     * @param dataset Dataset
     * @param tableName 目标表名
     * @param batchSize 批量生成大小
     * @return INSERT 语句列表
     */
    public static List<String> datasetToInsertStatements(Dataset<Row> dataset, String tableName, Integer batchSize) {
        return datasetToSqlStatements(dataset, tableName, batchSize, "INSERT");
    }

    /**
     * 将 Dataset 转换为 REPLACE 语句
     *
     * @param dataset Dataset
     * @param tableName 目标表名
     * @param batchSize 批量生成大小
     * @return REPLACE 语句列表
     */
    public static List<String> datasetToReplaceStatements(Dataset<Row> dataset, String tableName, Integer batchSize) {
        return datasetToSqlStatements(dataset, tableName, batchSize, "REPLACE");
    }

    /**
     * 将 Dataset 转换为 SQL 语句
     *
     * @param dataset Dataset
     * @param tableName 目标表名
     * @param batchSize 批量生成大小
     * @param sqlType SQL 类型：INSERT 或 REPLACE
     * @return SQL 语句列表
     */
    public static List<String> datasetToSqlStatements(Dataset<Row> dataset, String tableName, 
                                                      Integer batchSize, String sqlType) {
        List<String> sqlStatements = new ArrayList<>();
        
        try {
            String[] columns = dataset.columns();
            Row[] rows = (Row[]) dataset.collect();
            
            if (rows.length == 0) {
                return sqlStatements;
            }
            
            String columnsPart = String.join(", ", columns);
            boolean useBatch = batchSize != null && batchSize > 0;
            int actualBatchSize = useBatch ? batchSize : 1;
            
            StringBuilder currentBatch = new StringBuilder();
            int currentBatchCount = 0;
            
            for (Row row : rows) {
                StringBuilder values = new StringBuilder("(");
                for (int i = 0; i < columns.length; i++) {
                    if (i > 0) {
                        values.append(", ");
                    }
                    Object value = row.get(i);
                    values.append(formatSqlValue(value));
                }
                values.append(")");
                
                if (useBatch) {
                    if (currentBatchCount == 0) {
                        currentBatch.append(sqlType).append(" INTO ").append(tableName)
                                .append(" (").append(columnsPart).append(") VALUES ");
                    } else {
                        currentBatch.append(", ");
                    }
                    currentBatch.append(values);
                    currentBatchCount++;
                    
                    if (currentBatchCount >= actualBatchSize) {
                        sqlStatements.add(currentBatch.toString());
                        currentBatch = new StringBuilder();
                        currentBatchCount = 0;
                    }
                } else {
                    String sql = sqlType + " INTO " + tableName + " (" + columnsPart + ") VALUES " + values;
                    sqlStatements.add(sql);
                }
            }
            
            if (useBatch && currentBatchCount > 0) {
                sqlStatements.add(currentBatch.toString());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("转换 Dataset 到 SQL 语句失败: " + e.getMessage(), e);
        }
        
        return sqlStatements;
    }
}
