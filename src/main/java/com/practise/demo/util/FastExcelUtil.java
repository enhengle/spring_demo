package com.practise.demo.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 快速 Excel 处理工具类
 * 使用 EasyExcel 进行高性能 Excel 读写操作
 * EasyExcel 相比 Apache POI 具有以下优势：
 * 1. 内存占用更小（流式处理）
 * 2. 处理速度更快
 * 3. 支持大文件处理（不会内存溢出）
 * 
 * @author system
 */
public class FastExcelUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(FastExcelUtil.class);
    
    /**
     * 快速读取 Excel 文件（流式读取，内存占用小）
     * 
     * @param filePath Excel 文件路径
     * @param headRowNumber 表头行数（从1开始）
     * @param listener 数据监听器（用于处理每行数据）
     */
    public static void fastReadExcel(String filePath, int headRowNumber, 
                                    com.alibaba.excel.read.listener.ReadListener<?> listener) {
        try {
            EasyExcel.read(filePath, listener)
                    .headRowNumber(headRowNumber)
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            logger.error("快速读取 Excel 文件失败: {}", filePath, e);
            throw new RuntimeException("快速读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速读取 Excel 文件（从输入流）
     * 
     * @param inputStream 输入流
     * @param headRowNumber 表头行数
     * @param listener 数据监听器
     */
    public static void fastReadExcel(InputStream inputStream, int headRowNumber,
                                    com.alibaba.excel.read.listener.ReadListener<?> listener) {
        try {
            EasyExcel.read(inputStream, listener)
                    .headRowNumber(headRowNumber)
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            logger.error("快速读取 Excel 文件失败", e);
            throw new RuntimeException("快速读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速写入 Excel 文件（流式写入，支持大数据量）
     * 
     * @param filePath 输出文件路径
     * @param headClass 表头类（使用 @ExcelProperty 注解）
     * @param dataList 数据列表
     */
    public static <T> void fastWriteExcel(String filePath, Class<T> headClass, List<T> dataList) {
        try {
            EasyExcel.write(filePath, headClass)
                    .sheet("Sheet1")
                    .doWrite(dataList);
        } catch (Exception e) {
            logger.error("快速写入 Excel 文件失败: {}", filePath, e);
            throw new RuntimeException("快速写入 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速写入 Excel 文件（从输出流）
     * 
     * @param outputStream 输出流
     * @param headClass 表头类
     * @param dataList 数据列表
     */
    public static <T> void fastWriteExcel(OutputStream outputStream, Class<T> headClass, List<T> dataList) {
        try {
            EasyExcel.write(outputStream, headClass)
                    .sheet("Sheet1")
                    .doWrite(dataList);
        } catch (Exception e) {
            logger.error("快速写入 Excel 文件失败", e);
            throw new RuntimeException("快速写入 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速写入 Excel 文件（使用 Map，无需定义类）
     * 
     * @param filePath 输出文件路径
     * @param head 表头列表
     * @param dataList 数据列表（List<Map<String, Object>>）
     */
    public static void fastWriteExcelWithMap(String filePath, List<List<String>> head, 
                                             List<List<Object>> dataList) {
        try {
            EasyExcel.write(filePath)
                    .head(head)
                    .sheet("Sheet1")
                    .doWrite(dataList);
        } catch (Exception e) {
            logger.error("快速写入 Excel 文件失败: {}", filePath, e);
            throw new RuntimeException("快速写入 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速写入 Excel 文件（分批次写入，适合超大数据量）
     * 
     * @param filePath 输出文件路径
     * @param headClass 表头类
     * @param dataSupplier 数据提供者（每次调用返回一批数据，返回 null 表示结束）
     */
    public static <T> void fastWriteExcelBatch(String filePath, Class<T> headClass,
                                               java.util.function.Supplier<List<T>> dataSupplier) {
        try (ExcelWriter excelWriter = EasyExcel.write(filePath, headClass).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
            
            List<T> batch;
            while ((batch = dataSupplier.get()) != null && !batch.isEmpty()) {
                excelWriter.write(batch, writeSheet);
            }
        } catch (Exception e) {
            logger.error("快速批量写入 Excel 文件失败: {}", filePath, e);
            throw new RuntimeException("快速批量写入 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速读取 Excel 文件并转换为 Map 列表
     * 
     * @param filePath Excel 文件路径
     * @param hasHeader 是否包含表头
     * @return Map 列表
     */
    public static List<Map<Integer, String>> fastReadExcelToMap(String filePath, boolean hasHeader) {
        java.util.List<Map<Integer, String>> result = new java.util.ArrayList<>();
        
        com.alibaba.excel.read.listener.ReadListener<Map<Integer, String>> listener = 
            new com.alibaba.excel.read.listener.ReadListener<Map<Integer, String>>() {
                @Override
                public void invoke(Map<Integer, String> data, com.alibaba.excel.context.AnalysisContext context) {
                    result.add(data);
                }
                
                @Override
                public void doAfterAllAnalysed(com.alibaba.excel.context.AnalysisContext context) {
                    // 读取完成
                }
            };
        
        fastReadExcel(filePath, hasHeader ? 1 : 0, listener);
        return result;
    }
}
