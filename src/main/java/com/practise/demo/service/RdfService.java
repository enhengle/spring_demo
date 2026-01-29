package com.practise.demo.service;

import com.practise.demo.util.RdfUtil;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * RDF 服务类
 * 
 * @author system
 */
@Service
public class RdfService {
    
    private static final Logger logger = LoggerFactory.getLogger(RdfService.class);
    
    /**
     * 读取上传的 RDF 文件
     * 
     * @param file 上传的文件
     * @param lang 语言格式（可选）
     * @return RDF 模型
     */
    public Model readRdf(MultipartFile file, String lang) {
        try {
            InputStream inputStream = file.getInputStream();
            return RdfUtil.readModel(inputStream, lang);
        } catch (IOException e) {
            logger.error("读取 RDF 文件失败", e);
            throw new RuntimeException("读取 RDF 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 执行 SPARQL 查询
     * 
     * @param file 上传的 RDF 文件
     * @param queryString SPARQL 查询字符串
     * @param lang RDF 文件格式（可选）
     * @return 查询结果
     */
    public List<Map<String, String>> executeQuery(MultipartFile file, String queryString, String lang) {
        Model model = readRdf(file, lang);
        return RdfUtil.executeQuery(model, queryString);
    }
    
    /**
     * 执行 SPARQL ASK 查询
     * 
     * @param file 上传的 RDF 文件
     * @param queryString SPARQL ASK 查询字符串
     * @param lang RDF 文件格式（可选）
     * @return 查询结果（true/false）
     */
    public boolean executeAskQuery(MultipartFile file, String queryString, String lang) {
        Model model = readRdf(file, lang);
        return RdfUtil.executeAskQuery(model, queryString);
    }
    
    /**
     * 添加三元组
     * 
     * @param file 上传的 RDF 文件
     * @param subject 主体
     * @param predicate 谓词
     * @param object 客体
     * @param lang RDF 文件格式（可选）
     * @return 更新后的模型
     */
    public Model addStatement(MultipartFile file, String subject, String predicate, String object, String lang) {
        Model model = readRdf(file, lang);
        RdfUtil.addStatement(model, subject, predicate, object);
        return model;
    }
    
    /**
     * 查询三元组
     * 
     * @param file 上传的 RDF 文件
     * @param subject 主体（可选）
     * @param predicate 谓词（可选）
     * @param object 客体（可选）
     * @param lang RDF 文件格式（可选）
     * @return 三元组列表
     */
    public List<Map<String, String>> listStatements(MultipartFile file, String subject, String predicate, 
                                                    String object, String lang) {
        Model model = readRdf(file, lang);
        return RdfUtil.listStatements(model, subject, predicate, object);
    }
    
    /**
     * 获取资源属性
     * 
     * @param file 上传的 RDF 文件
     * @param resourceUri 资源 URI
     * @param lang RDF 文件格式（可选）
     * @return 属性映射
     */
    public Map<String, List<String>> getResourceProperties(MultipartFile file, String resourceUri, String lang) {
        Model model = readRdf(file, lang);
        return RdfUtil.getResourceProperties(model, resourceUri);
    }
    
    /**
     * 获取指定类型的所有资源
     * 
     * @param file 上传的 RDF 文件
     * @param typeUri 类型 URI
     * @param lang RDF 文件格式（可选）
     * @return 资源 URI 列表
     */
    public List<String> getResourcesByType(MultipartFile file, String typeUri, String lang) {
        Model model = readRdf(file, lang);
        return RdfUtil.getResourcesByType(model, typeUri);
    }
    
    /**
     * 转换 RDF 格式
     * 
     * @param file 上传的 RDF 文件
     * @param outputLang 输出格式
     * @param inputLang 输入格式（可选）
     * @return 转换后的 RDF 字符串
     */
    public String convertFormat(MultipartFile file, String outputLang, String inputLang) {
        Model model = readRdf(file, inputLang);
        
        switch (outputLang.toUpperCase()) {
            case "JSON-LD":
            case "JSONLD":
                return RdfUtil.toJsonLd(model);
            case "TURTLE":
            case "TTL":
                return RdfUtil.toTurtle(model);
            case "RDF/XML":
            case "RDFXML":
            case "XML":
                return RdfUtil.toRdfXml(model);
            case "N-TRIPLES":
            case "NTRIPLES":
            case "NT":
                return RdfUtil.toNTriples(model);
            default:
                return RdfUtil.toTurtle(model);
        }
    }
    
    /**
     * 验证 RDF 文件
     * 
     * @param file 上传的文件
     * @param lang RDF 文件格式（可选）
     * @return 验证结果
     */
    public boolean validateRdf(MultipartFile file, String lang) {
        try {
            Model model = readRdf(file, lang);
            return model != null && !model.isEmpty();
        } catch (Exception e) {
            logger.error("RDF 验证失败", e);
            return false;
        }
    }
    
    /**
     * 合并两个 RDF 文件
     * 
     * @param file1 第一个文件
     * @param file2 第二个文件
     * @param lang1 第一个文件格式（可选）
     * @param lang2 第二个文件格式（可选）
     * @param outputLang 输出格式
     * @return 合并后的 RDF 字符串
     */
    public String mergeRdf(MultipartFile file1, MultipartFile file2, String lang1, String lang2, String outputLang) {
        Model model1 = readRdf(file1, lang1);
        Model model2 = readRdf(file2, lang2);
        Model merged = RdfUtil.union(model1, model2);
        
        switch (outputLang.toUpperCase()) {
            case "JSON-LD":
            case "JSONLD":
                return RdfUtil.toJsonLd(merged);
            case "TURTLE":
            case "TTL":
                return RdfUtil.toTurtle(merged);
            case "RDF/XML":
            case "RDFXML":
            case "XML":
                return RdfUtil.toRdfXml(merged);
            case "N-TRIPLES":
            case "NTRIPLES":
            case "NT":
                return RdfUtil.toNTriples(merged);
            default:
                return RdfUtil.toTurtle(merged);
        }
    }
}
