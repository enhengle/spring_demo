package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.response.Response;
import com.practise.demo.service.RdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * RDF 操作控制器
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/rdf")
@Tag(name = "RDF操作", description = "RDF文件的读取、查询、转换等功能")
public class RdfController {
    
    @Autowired
    private RdfService rdfService;
    
    /**
     * 验证 RDF 文件
     * 
     * @param file RDF 文件
     * @param lang 文件格式（可选，如 RDF/XML, TURTLE, N-TRIPLES, JSON-LD）
     * @return 验证结果
     */
    @PostMapping("/validate")
    @Operation(summary = "验证RDF文件", description = "验证上传的RDF文件格式是否正确")
    public Response<Boolean> validateRdf(
            @Parameter(description = "RDF文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件格式") @RequestParam(value = "lang", required = false) String lang) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        try {
            boolean isValid = rdfService.validateRdf(file, lang);
            return Response.ok(isValid);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "验证RDF文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行 SPARQL 查询
     * 
     * @param file RDF 文件
     * @param query SPARQL 查询字符串
     * @param lang 文件格式（可选）
     * @return 查询结果
     */
    @PostMapping("/query")
    @Operation(summary = "执行SPARQL查询", description = "对上传的RDF文件执行SPARQL查询")
    public Response<List<Map<String, String>>> executeQuery(
            @Parameter(description = "RDF文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "SPARQL查询字符串") @RequestParam("query") String query,
            @Parameter(description = "文件格式") @RequestParam(value = "lang", required = false) String lang) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "查询字符串不能为空");
        }
        
        try {
            List<Map<String, String>> results = rdfService.executeQuery(file, query, lang);
            return Response.ok(results);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "执行查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行 SPARQL ASK 查询
     * 
     * @param file RDF 文件
     * @param query SPARQL ASK 查询字符串
     * @param lang 文件格式（可选）
     * @return 查询结果（true/false）
     */
    @PostMapping("/ask")
    @Operation(summary = "执行SPARQL ASK查询", description = "执行SPARQL ASK查询，返回布尔值")
    public Response<Boolean> executeAskQuery(
            @Parameter(description = "RDF文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "SPARQL ASK查询字符串") @RequestParam("query") String query,
            @Parameter(description = "文件格式") @RequestParam(value = "lang", required = false) String lang) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (query == null || query.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "查询字符串不能为空");
        }
        
        try {
            boolean result = rdfService.executeAskQuery(file, query, lang);
            return Response.ok(result);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "执行ASK查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询三元组
     * 
     * @param file RDF 文件
     * @param subject 主体（可选）
     * @param predicate 谓词（可选）
     * @param object 客体（可选）
     * @param lang 文件格式（可选）
     * @return 三元组列表
     */
    @PostMapping("/statements")
    @Operation(summary = "查询三元组", description = "查询RDF文件中的三元组，支持按主体、谓词、客体过滤")
    public Response<List<Map<String, String>>> listStatements(
            @Parameter(description = "RDF文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "主体URI") @RequestParam(value = "subject", required = false) String subject,
            @Parameter(description = "谓词URI") @RequestParam(value = "predicate", required = false) String predicate,
            @Parameter(description = "客体URI或字面量") @RequestParam(value = "object", required = false) String object,
            @Parameter(description = "文件格式") @RequestParam(value = "lang", required = false) String lang) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        try {
            List<Map<String, String>> statements = rdfService.listStatements(file, subject, predicate, object, lang);
            return Response.ok(statements);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "查询三元组失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取资源属性
     * 
     * @param file RDF 文件
     * @param resourceUri 资源 URI
     * @param lang 文件格式（可选）
     * @return 属性映射
     */
    @PostMapping("/resource/properties")
    @Operation(summary = "获取资源属性", description = "获取指定资源的所有属性")
    public Response<Map<String, List<String>>> getResourceProperties(
            @Parameter(description = "RDF文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "资源URI") @RequestParam("resourceUri") String resourceUri,
            @Parameter(description = "文件格式") @RequestParam(value = "lang", required = false) String lang) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (resourceUri == null || resourceUri.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "资源URI不能为空");
        }
        
        try {
            Map<String, List<String>> properties = rdfService.getResourceProperties(file, resourceUri, lang);
            return Response.ok(properties);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "获取资源属性失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定类型的所有资源
     * 
     * @param file RDF 文件
     * @param typeUri 类型 URI
     * @param lang 文件格式（可选）
     * @return 资源 URI 列表
     */
    @PostMapping("/resources/by-type")
    @Operation(summary = "按类型查询资源", description = "查询具有指定类型的所有资源")
    public Response<List<String>> getResourcesByType(
            @Parameter(description = "RDF文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "类型URI") @RequestParam("typeUri") String typeUri,
            @Parameter(description = "文件格式") @RequestParam(value = "lang", required = false) String lang) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (typeUri == null || typeUri.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "类型URI不能为空");
        }
        
        try {
            List<String> resources = rdfService.getResourcesByType(file, typeUri, lang);
            return Response.ok(resources);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "查询资源失败: " + e.getMessage());
        }
    }
    
    /**
     * 转换 RDF 格式
     * 
     * @param file RDF 文件
     * @param outputLang 输出格式（TURTLE, RDF/XML, N-TRIPLES, JSON-LD）
     * @param inputLang 输入格式（可选）
     * @return 转换后的 RDF 字符串
     */
    @PostMapping("/convert")
    @Operation(summary = "转换RDF格式", description = "将RDF文件转换为指定格式")
    public Response<String> convertFormat(
            @Parameter(description = "RDF文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "输出格式") @RequestParam("outputLang") String outputLang,
            @Parameter(description = "输入格式") @RequestParam(value = "inputLang", required = false) String inputLang) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (outputLang == null || outputLang.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "输出格式不能为空");
        }
        
        try {
            String result = rdfService.convertFormat(file, outputLang, inputLang);
            return Response.ok(result);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "转换格式失败: " + e.getMessage());
        }
    }
    
    /**
     * 合并两个 RDF 文件
     * 
     * @param file1 第一个 RDF 文件
     * @param file2 第二个 RDF 文件
     * @param outputLang 输出格式
     * @param lang1 第一个文件格式（可选）
     * @param lang2 第二个文件格式（可选）
     * @return 合并后的 RDF 字符串
     */
    @PostMapping("/merge")
    @Operation(summary = "合并RDF文件", description = "合并两个RDF文件")
    public Response<String> mergeRdf(
            @Parameter(description = "第一个RDF文件") @RequestParam("file1") MultipartFile file1,
            @Parameter(description = "第二个RDF文件") @RequestParam("file2") MultipartFile file2,
            @Parameter(description = "输出格式") @RequestParam("outputLang") String outputLang,
            @Parameter(description = "第一个文件格式") @RequestParam(value = "lang1", required = false) String lang1,
            @Parameter(description = "第二个文件格式") @RequestParam(value = "lang2", required = false) String lang2) {
        
        if (file1.isEmpty() || file2.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (outputLang == null || outputLang.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "输出格式不能为空");
        }
        
        try {
            String result = rdfService.mergeRdf(file1, file2, lang1, lang2, outputLang);
            return Response.ok(result);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "合并RDF文件失败: " + e.getMessage());
        }
    }
}
