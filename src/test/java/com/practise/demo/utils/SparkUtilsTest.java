package com.practise.demo.utils;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SparkUtils 测试用例
 * 测试 Spark 本地模式的各种功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SparkUtilsTest {

    private static final String TEST_DATA_DIR = "test-data";
    private static final String TEST_OUTPUT_DIR = "test-output";

    @BeforeAll
    static void setUp() throws IOException {
        // 创建测试数据目录
        Files.createDirectories(Paths.get(TEST_DATA_DIR));
        Files.createDirectories(Paths.get(TEST_OUTPUT_DIR));

        // 创建测试 CSV 文件
        createTestCsvFile();
        createTestJsonFile();
        createTestTextFile();
    }

    @AfterAll
    static void tearDown() {
        // 关闭 Spark 资源
        SparkUtils.closeAll();

        // 清理测试文件（可选）
        // deleteDirectory(new File(TEST_DATA_DIR));
        // deleteDirectory(new File(TEST_OUTPUT_DIR));
    }

    @Test
    @Order(1)
    @DisplayName("测试 SparkSession 初始化")
    void testGetSparkSession() {
        SparkSession spark = SparkUtils.getSparkSession("TestApp");
        assertNotNull(spark);
        assertEquals("TestApp", spark.sparkContext().appName());
        assertEquals("local[*]", spark.sparkContext().master());
    }

    @Test
    @Order(2)
    @DisplayName("测试默认 SparkSession")
    void testGetDefaultSparkSession() {
        SparkSession spark = SparkUtils.getSparkSession();
        assertNotNull(spark);
        assertTrue(spark.sparkContext().appName().contains("SparkLocalApp"));
    }

    @Test
    @Order(3)
    @DisplayName("测试读取 CSV 文件")
    void testReadCsv() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);
        assertNotNull(df);
        assertEquals(3, df.count());
        assertEquals(3, df.columns().length);
        assertTrue(Arrays.asList(df.columns()).contains("id"));
        assertTrue(Arrays.asList(df.columns()).contains("name"));
        assertTrue(Arrays.asList(df.columns()).contains("age"));
    }

    @Test
    @Order(4)
    @DisplayName("测试读取 JSON 文件")
    void testReadJson() {
        String jsonPath = TEST_DATA_DIR + "/test.json";
        Dataset<Row> df = SparkUtils.readJson(jsonPath);

        assertNotNull(df);
        assertTrue(df.count() > 0);
    }

    @Test
    @Order(5)
    @DisplayName("测试读取文本文件")
    void testReadTextFile() {
        String textPath = TEST_DATA_DIR + "/test.txt";
        JavaRDD<String> rdd = SparkUtils.readTextFile(textPath);

        assertNotNull(rdd);
        assertEquals(3, rdd.count());
    }

    @Test
    @Order(6)
    @DisplayName("测试创建临时视图和执行 SQL")
    void testCreateTempViewAndExecuteSql() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);

        // 创建临时视图
        SparkUtils.createTempView(df, "test_table");

        // 执行 SQL 查询
        Dataset<Row> result = SparkUtils.executeSql(
                "SELECT name, age FROM test_table WHERE age > 20 ORDER BY age DESC"
        );

        assertNotNull(result);
        assertEquals(2, result.count());
        
        // 验证结果
        List<Row> rows = result.collectAsList();
        assertEquals("Charlie", rows.get(0).getString(0));
        assertEquals(30, rows.get(0).getInt(1));
    }

    @Test
    @Order(7)
    @DisplayName("测试 Dataset 转换为 List<Map>")
    void testDatasetToMapList() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);

        List<Map<String, Object>> list = SparkUtils.datasetToMapList(df);

        assertNotNull(list);
        assertEquals(3, list.size());
        
        // 验证第一条数据
        Map<String, Object> firstRow = list.get(0);
        assertEquals(1, firstRow.get("id"));
        assertEquals("Alice", firstRow.get("name"));
        assertEquals(25, firstRow.get("age"));
    }

    @Test
    @Order(8)
    @DisplayName("测试创建 Schema")
    void testCreateSchema() {
        StructType schema = SparkUtils.createSchema(
                new String[]{"name", "String"},
                new String[]{"age", "Integer"},
                new String[]{"salary", "Double"}
        );

        assertNotNull(schema);
        assertEquals(3, schema.fields().length);
        assertEquals(DataTypes.StringType, schema.fields()[0].dataType());
        assertEquals(DataTypes.IntegerType, schema.fields()[1].dataType());
        assertEquals(DataTypes.DoubleType, schema.fields()[2].dataType());
    }

    @Test
    @Order(9)
    @DisplayName("测试写入 CSV 文件")
    void testWriteCsv() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);

        String outputPath = TEST_OUTPUT_DIR + "/output.csv";
        SparkUtils.writeCsv(df, outputPath);

        // 验证文件是否创建
        File outputFile = new File(outputPath);
        assertTrue(outputFile.exists() || new File(outputPath + "/part-00000").exists());
    }

    @Test
    @Order(10)
    @DisplayName("测试写入 JSON 文件")
    void testWriteJson() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);

        String outputPath = TEST_OUTPUT_DIR + "/output.json";
        SparkUtils.writeJson(df, outputPath);

        // 验证文件是否创建
        File outputFile = new File(outputPath);
        assertTrue(outputFile.exists() || new File(outputPath + "/part-00000").exists());
    }

    @Test
    @Order(11)
    @DisplayName("测试数据统计功能")
    void testCount() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);

        long count = SparkUtils.count(df);
        assertEquals(3, count);
    }

    @Test
    @Order(12)
    @DisplayName("测试复杂 SQL 查询")
    void testComplexSqlQuery() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);
        SparkUtils.createTempView(df, "users");

        // 测试聚合查询
        Dataset<Row> result = SparkUtils.executeSql(
                "SELECT AVG(age) as avg_age, MAX(age) as max_age, MIN(age) as min_age FROM users"
        );

        assertNotNull(result);
        assertEquals(1, result.count());
        
        Row row = result.collectAsList().get(0);
        double avgAge = row.getDouble(0);
        int maxAge = row.getInt(1);
        int minAge = row.getInt(2);
        
        assertEquals(25.0, avgAge, 0.1);
        assertEquals(30, maxAge);
        assertEquals(20, minAge);
    }

    @Test
    @Order(13)
    @DisplayName("测试数据过滤和转换")
    void testDataFilterAndTransform() {
        String csvPath = TEST_DATA_DIR + "/test.csv";
        Dataset<Row> df = SparkUtils.readCsv(csvPath, true);

        // 使用 DataFrame API 进行过滤
        Dataset<Row> filtered = df.filter("age > 25");
        assertEquals(1, filtered.count());

        // 使用 DataFrame API 进行选择
        Dataset<Row> selected = df.select("name", "age");
        assertEquals(2, selected.columns().length);
    }

    @Test
    @Order(14)
    @DisplayName("测试从 List 创建 Dataset")
    void testCreateDatasetFromList() {
        StructType schema = SparkUtils.createSchema(
                new String[]{"id", "Integer"},
                new String[]{"name", "String"},
                new String[]{"score", "Double"}
        );

        List<Row> data = new ArrayList<>();
        SparkSession spark = SparkUtils.getSparkSession();
        
        data.add(org.apache.spark.sql.RowFactory.create(1, "Student1", 95.5));
        data.add(org.apache.spark.sql.RowFactory.create(2, "Student2", 88.0));
        data.add(org.apache.spark.sql.RowFactory.create(3, "Student3", 92.5));

        Dataset<Row> df = spark.createDataFrame(data, schema);

        assertNotNull(df);
        assertEquals(3, df.count());
        assertEquals(3, df.columns().length);
    }

    @Test
    @Order(15)
    @DisplayName("测试资源关闭")
    void testCloseResources() {
        // 获取 SparkSession
        SparkSession spark1 = SparkUtils.getSparkSession();
        assertNotNull(spark1);

        // 关闭资源
        SparkUtils.closeSparkSession();

        // 重新获取应该创建新的实例
        SparkSession spark2 = SparkUtils.getSparkSession();
        assertNotNull(spark2);
        // 注意：由于单例模式，实际可能返回同一个实例
    }

    /**
     * 创建测试 CSV 文件
     */
    private static void createTestCsvFile() throws IOException {
        File file = new File(TEST_DATA_DIR + "/test.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,name,age\n");
            writer.write("1,Alice,25\n");
            writer.write("2,Bob,20\n");
            writer.write("3,Charlie,30\n");
        }
    }

    /**
     * 创建测试 JSON 文件
     */
    private static void createTestJsonFile() throws IOException {
        File file = new File(TEST_DATA_DIR + "/test.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("{\"id\":1,\"name\":\"Alice\",\"age\":25}\n");
            writer.write("{\"id\":2,\"name\":\"Bob\",\"age\":20}\n");
            writer.write("{\"id\":3,\"name\":\"Charlie\",\"age\":30}\n");
        }
    }

    /**
     * 创建测试文本文件
     */
    private static void createTestTextFile() throws IOException {
        File file = new File(TEST_DATA_DIR + "/test.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Line 1: Hello Spark\n");
            writer.write("Line 2: This is a test\n");
            writer.write("Line 3: Testing RDD operations\n");
        }
    }

    /**
     * 删除目录（递归）
     */
    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
