package com.practise.demo.util;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.query.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * RDF 操作工具类
 * 提供 RDF 文件的读写、查询、转换等常见功能
 * 
 * @author system
 */
public class RdfUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(RdfUtil.class);
    
    /**
     * 创建空的 RDF 模型
     * 
     * @return RDF 模型
     */
    public static Model createModel() {
        return ModelFactory.createDefaultModel();
    }
    
    /**
     * 创建带命名空间的 RDF 模型
     * 
     * @param namespace 命名空间 URI
     * @param prefix 命名空间前缀
     * @return RDF 模型
     */
    public static Model createModel(String namespace, String prefix) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix(prefix, namespace);
        return model;
    }
    
    /**
     * 从文件读取 RDF 模型
     * 
     * @param filePath 文件路径
     * @return RDF 模型
     */
    public static Model readModel(String filePath) {
        return readModel(filePath, null);
    }
    
    /**
     * 从文件读取 RDF 模型（指定语言格式）
     * 
     * @param filePath 文件路径
     * @param lang 语言格式（RDF/XML, TURTLE, N-TRIPLES, JSON-LD等）
     * @return RDF 模型
     */
    public static Model readModel(String filePath, String lang) {
        try {
            Model model = ModelFactory.createDefaultModel();
            if (lang != null) {
                Lang rdfLang = RDFLanguages.nameToLang(lang);
                RDFDataMgr.read(model, filePath, rdfLang);
            } else {
                RDFDataMgr.read(model, filePath);
            }
            return model;
        } catch (Exception e) {
            logger.error("读取 RDF 文件失败: {}", filePath, e);
            throw new RuntimeException("读取 RDF 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从输入流读取 RDF 模型
     * 
     * @param inputStream 输入流
     * @param lang 语言格式
     * @return RDF 模型
     */
    public static Model readModel(InputStream inputStream, String lang) {
        try {
            Model model = ModelFactory.createDefaultModel();
            if (lang != null) {
                Lang rdfLang = RDFLanguages.nameToLang(lang);
                RDFDataMgr.read(model, inputStream, null, rdfLang);
            } else {
                // 默认使用 TURTLE 格式
                RDFDataMgr.read(model, inputStream, null, Lang.TURTLE);
            }
            return model;
        } catch (Exception e) {
            logger.error("从输入流读取 RDF 失败", e);
            throw new RuntimeException("从输入流读取 RDF 失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将 RDF 模型写入文件
     * 
     * @param model RDF 模型
     * @param filePath 文件路径
     */
    public static void writeModel(Model model, String filePath) {
        writeModel(model, filePath, null);
    }
    
    /**
     * 将 RDF 模型写入文件（指定格式）
     * 
     * @param model RDF 模型
     * @param filePath 文件路径
     * @param lang 语言格式（RDF/XML, TURTLE, N-TRIPLES, JSON-LD等）
     */
    public static void writeModel(Model model, String filePath, String lang) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            if (lang != null) {
                Lang rdfLang = RDFLanguages.nameToLang(lang);
                RDFDataMgr.write(fos, model, rdfLang);
            } else {
                RDFDataMgr.write(fos, model, Lang.TURTLE);
            }
        } catch (IOException e) {
            logger.error("写入 RDF 文件失败: {}", filePath, e);
            throw new RuntimeException("写入 RDF 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将 RDF 模型写入输出流
     * 
     * @param model RDF 模型
     * @param outputStream 输出流
     * @param lang 语言格式
     */
    public static void writeModel(Model model, OutputStream outputStream, String lang) {
        try {
            if (lang != null) {
                Lang rdfLang = RDFLanguages.nameToLang(lang);
                RDFDataMgr.write(outputStream, model, rdfLang);
            } else {
                RDFDataMgr.write(outputStream, model, Lang.TURTLE);
            }
        } catch (Exception e) {
            logger.error("写入 RDF 到输出流失败", e);
            throw new RuntimeException("写入 RDF 到输出流失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 添加三元组到模型
     * 
     * @param model RDF 模型
     * @param subject 主体（资源URI或资源对象）
     * @param predicate 谓词（属性URI）
     * @param object 客体（资源URI、字面量或资源对象）
     * @return 创建的 Statement
     */
    public static Statement addStatement(Model model, String subject, String predicate, String object) {
        Resource s = model.createResource(subject);
        Property p = model.createProperty(predicate);
        RDFNode o = createRDFNode(model, object);
        model.add(s, p, o);
        return model.createStatement(s, p, o);
    }
    
    /**
     * 添加三元组到模型（使用 Resource 对象）
     * 
     * @param model RDF 模型
     * @param subject 主体资源
     * @param predicate 谓词属性
     * @param object 客体节点
     * @return 创建的 Statement
     */
    public static Statement addStatement(Model model, Resource subject, Property predicate, RDFNode object) {
        model.add(subject, predicate, object);
        return model.createStatement(subject, predicate, object);
    }
    
    /**
     * 批量添加三元组
     * 
     * @param model RDF 模型
     * @param triples 三元组列表，每个三元组是长度为3的数组：[subject, predicate, object]
     */
    public static void addStatements(Model model, List<String[]> triples) {
        for (String[] triple : triples) {
            if (triple.length == 3) {
                addStatement(model, triple[0], triple[1], triple[2]);
            }
        }
    }
    
    /**
     * 删除三元组
     * 
     * @param model RDF 模型
     * @param subject 主体
     * @param predicate 谓词（可为null，表示删除所有匹配主体的三元组）
     * @param object 客体（可为null）
     */
    public static void removeStatement(Model model, String subject, String predicate, String object) {
        Resource s = model.createResource(subject);
        Property p = predicate != null ? model.createProperty(predicate) : null;
        RDFNode o = object != null ? createRDFNode(model, object) : null;
        model.remove(s, p, o);
    }
    
    /**
     * 查询所有三元组
     * 
     * @param model RDF 模型
     * @return 三元组列表
     */
    public static List<Map<String, String>> listStatements(Model model) {
        return listStatements(model, null, null, null);
    }
    
    /**
     * 查询匹配的三元组
     * 
     * @param model RDF 模型
     * @param subject 主体（可为null）
     * @param predicate 谓词（可为null）
     * @param object 客体（可为null）
     * @return 三元组列表，每个Map包含：subject, predicate, object
     */
    public static List<Map<String, String>> listStatements(Model model, String subject, String predicate, String object) {
        List<Map<String, String>> result = new ArrayList<>();
        
        Resource s = subject != null ? model.createResource(subject) : null;
        Property p = predicate != null ? model.createProperty(predicate) : null;
        RDFNode o = object != null ? createRDFNode(model, object) : null;
        
        StmtIterator iter = model.listStatements(s, p, o);
        try {
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                Map<String, String> triple = new LinkedHashMap<>();
                triple.put("subject", stmt.getSubject().getURI());
                triple.put("predicate", stmt.getPredicate().getURI());
                triple.put("object", stmt.getObject().toString());
                result.add(triple);
            }
        } finally {
            iter.close();
        }
        
        return result;
    }
    
    /**
     * 执行 SPARQL 查询
     * 
     * @param model RDF 模型
     * @param queryString SPARQL 查询字符串
     * @return 查询结果列表
     */
    public static List<Map<String, String>> executeQuery(Model model, String queryString) {
        List<Map<String, String>> result = new ArrayList<>();
        
        try (QueryExecution qexec = QueryExecutionFactory.create(queryString, model)) {
            ResultSet results = qexec.execSelect();
            
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Map<String, String> row = new LinkedHashMap<>();
                
                Iterator<String> varNames = soln.varNames();
                while (varNames.hasNext()) {
                    String varName = varNames.next();
                    RDFNode node = soln.get(varName);
                    row.put(varName, node != null ? node.toString() : null);
                }
                
                result.add(row);
            }
        } catch (Exception e) {
            logger.error("执行 SPARQL 查询失败", e);
            throw new RuntimeException("执行 SPARQL 查询失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * 执行 SPARQL ASK 查询（返回布尔值）
     * 
     * @param model RDF 模型
     * @param queryString SPARQL ASK 查询字符串
     * @return 查询结果（true/false）
     */
    public static boolean executeAskQuery(Model model, String queryString) {
        try (QueryExecution qexec = QueryExecutionFactory.create(queryString, model)) {
            return qexec.execAsk();
        } catch (Exception e) {
            logger.error("执行 SPARQL ASK 查询失败", e);
            throw new RuntimeException("执行 SPARQL ASK 查询失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 执行 SPARQL CONSTRUCT 查询（返回 RDF 模型）
     * 
     * @param model RDF 模型
     * @param queryString SPARQL CONSTRUCT 查询字符串
     * @return 构造的 RDF 模型
     */
    public static Model executeConstructQuery(Model model, String queryString) {
        try (QueryExecution qexec = QueryExecutionFactory.create(queryString, model)) {
            return qexec.execConstruct();
        } catch (Exception e) {
            logger.error("执行 SPARQL CONSTRUCT 查询失败", e);
            throw new RuntimeException("执行 SPARQL CONSTRUCT 查询失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 合并两个 RDF 模型
     * 
     * @param model1 第一个模型
     * @param model2 第二个模型
     * @return 合并后的新模型
     */
    public static Model union(Model model1, Model model2) {
        Model result = ModelFactory.createDefaultModel();
        result.add(model1);
        result.add(model2);
        return result;
    }
    
    /**
     * 获取模型交集
     * 
     * @param model1 第一个模型
     * @param model2 第二个模型
     * @return 交集模型
     */
    public static Model intersection(Model model1, Model model2) {
        Model result = ModelFactory.createDefaultModel();
        result.add(model1.intersection(model2));
        return result;
    }
    
    /**
     * 获取模型差集
     * 
     * @param model1 第一个模型
     * @param model2 第二个模型
     * @return 差集模型（model1 - model2）
     */
    public static Model difference(Model model1, Model model2) {
        Model result = ModelFactory.createDefaultModel();
        result.add(model1.difference(model2));
        return result;
    }
    
    /**
     * 获取模型大小（三元组数量）
     * 
     * @param model RDF 模型
     * @return 三元组数量
     */
    public static long getSize(Model model) {
        return model.size();
    }
    
    /**
     * 检查模型是否为空
     * 
     * @param model RDF 模型
     * @return 是否为空
     */
    public static boolean isEmpty(Model model) {
        return model.isEmpty();
    }
    
    /**
     * 清空模型
     * 
     * @param model RDF 模型
     */
    public static void clear(Model model) {
        model.removeAll();
    }
    
    /**
     * 设置命名空间前缀
     * 
     * @param model RDF 模型
     * @param prefix 前缀
     * @param namespace URI
     */
    public static void setNamespace(Model model, String prefix, String namespace) {
        model.setNsPrefix(prefix, namespace);
    }
    
    /**
     * 获取命名空间前缀映射
     * 
     * @param model RDF 模型
     * @return 命名空间前缀映射
     */
    public static Map<String, String> getNamespaces(Model model) {
        Map<String, String> namespaces = new LinkedHashMap<>();
        model.getNsPrefixMap().forEach((prefix, uri) -> namespaces.put(prefix, uri));
        return namespaces;
    }
    
    /**
     * 验证 RDF 语法
     * 
     * @param filePath RDF 文件路径
     * @return 验证结果
     */
    public static boolean validateRdf(String filePath) {
        try {
            Model model = readModel(filePath);
            return model != null && !model.isEmpty();
        } catch (Exception e) {
            logger.error("RDF 验证失败: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * 转换 RDF 格式
     * 
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param outputLang 输出格式
     */
    public static void convertFormat(String inputPath, String outputPath, String outputLang) {
        Model model = readModel(inputPath);
        writeModel(model, outputPath, outputLang);
    }
    
    /**
     * 查找资源的所有属性
     * 
     * @param model RDF 模型
     * @param resourceUri 资源 URI
     * @return 属性映射，key 为属性 URI，value 为属性值列表
     */
    public static Map<String, List<String>> getResourceProperties(Model model, String resourceUri) {
        Map<String, List<String>> properties = new LinkedHashMap<>();
        Resource resource = model.createResource(resourceUri);
        
        StmtIterator iter = model.listStatements(resource, null, (RDFNode) null);
        try {
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                String predicate = stmt.getPredicate().getURI();
                String object = stmt.getObject().toString();
                
                properties.computeIfAbsent(predicate, k -> new ArrayList<>()).add(object);
            }
        } finally {
            iter.close();
        }
        
        return properties;
    }
    
    /**
     * 查找具有指定属性的所有资源
     * 
     * @param model RDF 模型
     * @param propertyUri 属性 URI
     * @return 资源 URI 列表
     */
    public static List<String> getResourcesByProperty(Model model, String propertyUri) {
        List<String> resources = new ArrayList<>();
        Property property = model.createProperty(propertyUri);
        
        StmtIterator iter = model.listStatements(null, property, (RDFNode) null);
        try {
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                String subject = stmt.getSubject().getURI();
                if (!resources.contains(subject)) {
                    resources.add(subject);
                }
            }
        } finally {
            iter.close();
        }
        
        return resources;
    }
    
    /**
     * 查找具有指定类型的所有资源
     * 
     * @param model RDF 模型
     * @param typeUri 类型 URI
     * @return 资源 URI 列表
     */
    public static List<String> getResourcesByType(Model model, String typeUri) {
        List<String> resources = new ArrayList<>();
        Resource type = model.createResource(typeUri);
        
        StmtIterator iter = model.listStatements(null, RDF.type, type);
        try {
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                resources.add(stmt.getSubject().getURI());
            }
        } finally {
            iter.close();
        }
        
        return resources;
    }
    
    /**
     * 添加资源类型
     * 
     * @param model RDF 模型
     * @param resourceUri 资源 URI
     * @param typeUri 类型 URI
     */
    public static void addResourceType(Model model, String resourceUri, String typeUri) {
        Resource resource = model.createResource(resourceUri);
        Resource type = model.createResource(typeUri);
        model.add(resource, RDF.type, type);
    }
    
    /**
     * 添加资源标签（rdfs:label）
     * 
     * @param model RDF 模型
     * @param resourceUri 资源 URI
     * @param label 标签文本
     * @param language 语言代码（可选，如 "zh", "en"）
     */
    public static void addResourceLabel(Model model, String resourceUri, String label, String language) {
        Resource resource = model.createResource(resourceUri);
        Literal literal = language != null ? model.createLiteral(label, language) : model.createLiteral(label);
        model.add(resource, RDFS.label, literal);
    }
    
    /**
     * 添加资源注释（rdfs:comment）
     * 
     * @param model RDF 模型
     * @param resourceUri 资源 URI
     * @param comment 注释文本
     * @param language 语言代码（可选）
     */
    public static void addResourceComment(Model model, String resourceUri, String comment, String language) {
        Resource resource = model.createResource(resourceUri);
        Literal literal = language != null ? model.createLiteral(comment, language) : model.createLiteral(comment);
        model.add(resource, RDFS.comment, literal);
    }
    
    /**
     * 创建 RDF 节点（辅助方法）
     * 
     * @param model RDF 模型
     * @param value 值（URI 或字面量）
     * @return RDF 节点
     */
    private static RDFNode createRDFNode(Model model, String value) {
        if (value == null) {
            return null;
        }
        
        // 判断是否为 URI（简单判断：以 http:// 或 https:// 开头，或包含 ://）
        if (value.startsWith("http://") || value.startsWith("https://") || value.contains("://")) {
            return model.createResource(value);
        } else {
            return model.createLiteral(value);
        }
    }
    
    /**
     * 导出模型为 JSON-LD 格式
     * 
     * @param model RDF 模型
     * @return JSON-LD 字符串
     */
    public static String toJsonLd(Model model) {
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, Lang.JSONLD);
        return writer.toString();
    }
    
    /**
     * 从 JSON-LD 创建模型
     * 
     * @param jsonLd JSON-LD 字符串
     * @return RDF 模型
     */
    public static Model fromJsonLd(String jsonLd) {
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, new StringReader(jsonLd), null, Lang.JSONLD);
        return model;
    }
    
    /**
     * 导出模型为 Turtle 格式字符串
     * 
     * @param model RDF 模型
     * @return Turtle 格式字符串
     */
    public static String toTurtle(Model model) {
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, Lang.TURTLE);
        return writer.toString();
    }
    
    /**
     * 导出模型为 RDF/XML 格式字符串
     * 
     * @param model RDF 模型
     * @return RDF/XML 格式字符串
     */
    public static String toRdfXml(Model model) {
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, Lang.RDFXML);
        return writer.toString();
    }
    
    /**
     * 导出模型为 N-Triples 格式字符串
     * 
     * @param model RDF 模型
     * @return N-Triples 格式字符串
     */
    public static String toNTriples(Model model) {
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, Lang.NTRIPLES);
        return writer.toString();
    }
}
