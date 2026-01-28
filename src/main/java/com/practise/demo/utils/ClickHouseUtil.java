package com.practise.demo.utils;

import com.practise.demo.config.ClickHouseConfig;
import com.practise.demo.model.RealtimeMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

/**
 * ClickHouse 工具类
 */
@Component
public class ClickHouseUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseUtil.class);

    @Autowired
    private ClickHouseConfig clickHouseConfig;

    private Connection connection;
    private volatile boolean initialized = false;

    /**
     * 获取连接（延迟初始化）
     */
    private Connection getConnection() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    try {
                        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
                        connection = DriverManager.getConnection(
                                clickHouseConfig.getUrl(),
                                clickHouseConfig.getUsername(),
                                clickHouseConfig.getPassword()
                        );
                        createTableIfNotExists();
                        initialized = true;
                        logger.info("ClickHouse 连接初始化成功");
                    } catch (Exception e) {
                        logger.error("ClickHouse 连接初始化失败", e);
                        throw new RuntimeException("ClickHouse 连接初始化失败", e);
                    }
                }
            }
        }
        return connection;
    }

    /**
     * 创建表（如果不存在）
     */
    private void createTableIfNotExists() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + clickHouseConfig.getTable() + " (" +
                "metric_key String," +
                "metric_type String," +
                "metric_value Float64," +
                "window_start DateTime64(3)," +
                "window_end DateTime64(3)," +
                "dimension String," +
                "dimension_value String," +
                "create_time DateTime DEFAULT now()" +
                ") ENGINE = MergeTree() " +
                "ORDER BY (metric_key, window_start) " +
                "TTL create_time + INTERVAL 30 DAY";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
            logger.info("ClickHouse 表创建成功或已存在: {}", clickHouseConfig.getTable());
        } catch (SQLException e) {
            logger.error("创建 ClickHouse 表失败", e);
            throw new RuntimeException("创建 ClickHouse 表失败", e);
        }
    }

    /**
     * 批量插入指标数据
     *
     * @param metrics 指标列表
     */
    public void batchInsert(List<RealtimeMetric> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO " + clickHouseConfig.getTable() +
                " (metric_key, metric_type, metric_value, window_start, window_end, dimension, dimension_value) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (RealtimeMetric metric : metrics) {
                pstmt.setString(1, metric.getMetricKey());
                pstmt.setString(2, metric.getMetricType());
                pstmt.setDouble(3, metric.getMetricValue());
                pstmt.setTimestamp(4, new Timestamp(metric.getWindowStart()));
                pstmt.setTimestamp(5, new Timestamp(metric.getWindowEnd()));
                pstmt.setString(6, metric.getDimension());
                pstmt.setString(7, metric.getDimensionValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            logger.debug("批量写入 ClickHouse 成功: count={}", metrics.size());
        } catch (SQLException e) {
            logger.error("批量写入 ClickHouse 失败", e);
            throw new RuntimeException("批量写入 ClickHouse 失败", e);
        }
    }

    /**
     * 查询指标数据
     *
     * @param sql SQL 语句
     * @return 结果集
     */
    public ResultSet query(String sql) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            logger.error("ClickHouse 查询失败: sql={}", sql, e);
            throw new RuntimeException("ClickHouse 查询失败", e);
        }
    }
}
