package com.practise.demo.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RDF 工具类测试用例
 * 
 * @author system
 */
@DisplayName("RDF工具类测试")
class RdfUtilTest {
    
    private Model model;
    private static final String NS = "http://example.org/";
    private static final String PERSON_URI = NS + "Person";
    private static final String ALICE_URI = NS + "alice";
    private static final String BOB_URI = NS + "bob";
    private static final String NAME_PROPERTY = NS + "name";
    private static final String AGE_PROPERTY = NS + "age";
    
    @BeforeEach
    void setUp() {
        model = RdfUtil.createModel(NS, "ex");
    }
    
    @Test
    @DisplayName("创建空的RDF模型")
    void testCreateModel() {
        Model emptyModel = RdfUtil.createModel();
        assertNotNull(emptyModel);
        assertTrue(RdfUtil.isEmpty(emptyModel));
    }
    
    @Test
    @DisplayName("创建带命名空间的RDF模型")
    void testCreateModelWithNamespace() {
        Model modelWithNs = RdfUtil.createModel(NS, "ex");
        assertNotNull(modelWithNs);
        Map<String, String> namespaces = RdfUtil.getNamespaces(modelWithNs);
        assertTrue(namespaces.containsKey("ex"));
        assertEquals(NS, namespaces.get("ex"));
    }
    
    @Test
    @DisplayName("添加三元组到模型")
    void testAddStatement() {
        Statement stmt = RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        assertNotNull(stmt);
        assertEquals(ALICE_URI, stmt.getSubject().getURI());
        assertEquals(NAME_PROPERTY, stmt.getPredicate().getURI());
        assertEquals("Alice", stmt.getObject().asLiteral().getString());
        
        assertEquals(1, RdfUtil.getSize(model));
    }
    
    @Test
    @DisplayName("批量添加三元组")
    void testAddStatements() {
        List<String[]> triples = Arrays.asList(
            new String[]{ALICE_URI, NAME_PROPERTY, "Alice"},
            new String[]{ALICE_URI, AGE_PROPERTY, "30"},
            new String[]{BOB_URI, NAME_PROPERTY, "Bob"},
            new String[]{BOB_URI, AGE_PROPERTY, "25"}
        );
        
        RdfUtil.addStatements(model, triples);
        assertEquals(4, RdfUtil.getSize(model));
    }
    
    @Test
    @DisplayName("查询所有三元组")
    void testListStatements() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        
        List<Map<String, String>> statements = RdfUtil.listStatements(model);
        assertEquals(2, statements.size());
        
        Map<String, String> firstStmt = statements.get(0);
        assertTrue(firstStmt.containsKey("subject"));
        assertTrue(firstStmt.containsKey("predicate"));
        assertTrue(firstStmt.containsKey("object"));
    }
    
    @Test
    @DisplayName("按主体查询三元组")
    void testListStatementsBySubject() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        RdfUtil.addStatement(model, BOB_URI, NAME_PROPERTY, "Bob");
        
        List<Map<String, String>> statements = RdfUtil.listStatements(model, ALICE_URI, null, null);
        assertEquals(2, statements.size());
        assertTrue(statements.stream().allMatch(s -> s.get("subject").equals(ALICE_URI)));
    }
    
    @Test
    @DisplayName("按谓词查询三元组")
    void testListStatementsByPredicate() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model, BOB_URI, NAME_PROPERTY, "Bob");
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        
        List<Map<String, String>> statements = RdfUtil.listStatements(model, null, NAME_PROPERTY, null);
        assertEquals(2, statements.size());
        assertTrue(statements.stream().allMatch(s -> s.get("predicate").equals(NAME_PROPERTY)));
    }
    
    @Test
    @DisplayName("删除三元组")
    void testRemoveStatement() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        
        assertEquals(2, RdfUtil.getSize(model));
        
        RdfUtil.removeStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        assertEquals(1, RdfUtil.getSize(model));
        
        List<Map<String, String>> statements = RdfUtil.listStatements(model);
        assertFalse(statements.stream().anyMatch(s -> s.get("predicate").equals(NAME_PROPERTY)));
    }
    
    @Test
    @DisplayName("执行SPARQL SELECT查询")
    void testExecuteQuery() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        RdfUtil.addStatement(model, BOB_URI, NAME_PROPERTY, "Bob");
        RdfUtil.addStatement(model, BOB_URI, AGE_PROPERTY, "25");
        
        String query = "PREFIX ex: <" + NS + "> " +
                      "SELECT ?name ?age WHERE { " +
                      "  ?person ex:name ?name . " +
                      "  ?person ex:age ?age . " +
                      "}";
        
        List<Map<String, String>> results = RdfUtil.executeQuery(model, query);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // 验证结果包含查询变量
        Map<String, String> firstResult = results.get(0);
        assertTrue(firstResult.containsKey("name") || firstResult.containsKey("age"));
    }
    
    @Test
    @DisplayName("执行SPARQL ASK查询")
    void testExecuteAskQuery() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        
        String query = "PREFIX ex: <" + NS + "> " +
                      "ASK { " +
                      "  ?person ex:name \"Alice\" . " +
                      "}";
        
        boolean result = RdfUtil.executeAskQuery(model, query);
        assertTrue(result);
        
        String falseQuery = "PREFIX ex: <" + NS + "> " +
                           "ASK { " +
                           "  ?person ex:name \"Charlie\" . " +
                           "}";
        
        boolean falseResult = RdfUtil.executeAskQuery(model, falseQuery);
        assertFalse(falseResult);
    }
    
    @Test
    @DisplayName("执行SPARQL CONSTRUCT查询")
    void testExecuteConstructQuery() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        
        String query = "PREFIX ex: <" + NS + "> " +
                      "CONSTRUCT { ?person ex:hasName ?name } " +
                      "WHERE { ?person ex:name ?name }";
        
        Model constructed = RdfUtil.executeConstructQuery(model, query);
        assertNotNull(constructed);
        assertFalse(RdfUtil.isEmpty(constructed));
    }
    
    @Test
    @DisplayName("合并两个RDF模型")
    void testUnion() {
        Model model1 = RdfUtil.createModel();
        Model model2 = RdfUtil.createModel();
        
        RdfUtil.addStatement(model1, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model2, BOB_URI, NAME_PROPERTY, "Bob");
        
        Model merged = RdfUtil.union(model1, model2);
        assertEquals(2, RdfUtil.getSize(merged));
        
        List<Map<String, String>> statements = RdfUtil.listStatements(merged);
        assertTrue(statements.stream().anyMatch(s -> s.get("subject").equals(ALICE_URI)));
        assertTrue(statements.stream().anyMatch(s -> s.get("subject").equals(BOB_URI)));
    }
    
    @Test
    @DisplayName("获取模型交集")
    void testIntersection() {
        Model model1 = RdfUtil.createModel();
        Model model2 = RdfUtil.createModel();
        
        RdfUtil.addStatement(model1, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model1, ALICE_URI, AGE_PROPERTY, "30");
        RdfUtil.addStatement(model2, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model2, BOB_URI, NAME_PROPERTY, "Bob");
        
        Model intersection = RdfUtil.intersection(model1, model2);
        assertEquals(1, RdfUtil.getSize(intersection));
        
        List<Map<String, String>> statements = RdfUtil.listStatements(intersection);
        assertEquals(ALICE_URI, statements.get(0).get("subject"));
        assertEquals(NAME_PROPERTY, statements.get(0).get("predicate"));
    }
    
    @Test
    @DisplayName("获取模型差集")
    void testDifference() {
        Model model1 = RdfUtil.createModel();
        Model model2 = RdfUtil.createModel();
        
        RdfUtil.addStatement(model1, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model1, ALICE_URI, AGE_PROPERTY, "30");
        RdfUtil.addStatement(model2, ALICE_URI, NAME_PROPERTY, "Alice");
        
        Model difference = RdfUtil.difference(model1, model2);
        assertEquals(1, RdfUtil.getSize(difference));
        
        List<Map<String, String>> statements = RdfUtil.listStatements(difference);
        assertEquals(AGE_PROPERTY, statements.get(0).get("predicate"));
    }
    
    @Test
    @DisplayName("获取资源属性")
    void testGetResourceProperties() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice Smith");
        
        Map<String, List<String>> properties = RdfUtil.getResourceProperties(model, ALICE_URI);
        assertNotNull(properties);
        assertTrue(properties.containsKey(NAME_PROPERTY));
        assertTrue(properties.containsKey(AGE_PROPERTY));
        assertEquals(2, properties.get(NAME_PROPERTY).size()); // 两个name值
    }
    
    @Test
    @DisplayName("按类型查询资源")
    void testGetResourcesByType() {
        RdfUtil.addResourceType(model, ALICE_URI, PERSON_URI);
        RdfUtil.addResourceType(model, BOB_URI, PERSON_URI);
        
        List<String> resources = RdfUtil.getResourcesByType(model, PERSON_URI);
        assertEquals(2, resources.size());
        assertTrue(resources.contains(ALICE_URI));
        assertTrue(resources.contains(BOB_URI));
    }
    
    @Test
    @DisplayName("添加资源类型")
    void testAddResourceType() {
        RdfUtil.addResourceType(model, ALICE_URI, PERSON_URI);
        
        List<String> resources = RdfUtil.getResourcesByType(model, PERSON_URI);
        assertEquals(1, resources.size());
        assertEquals(ALICE_URI, resources.get(0));
    }
    
    @Test
    @DisplayName("添加资源标签")
    void testAddResourceLabel() {
        RdfUtil.addResourceLabel(model, ALICE_URI, "Alice", "en");
        RdfUtil.addResourceLabel(model, ALICE_URI, "爱丽丝", "zh");
        
        Map<String, List<String>> properties = RdfUtil.getResourceProperties(model, ALICE_URI);
        assertTrue(properties.containsKey(RDFS.label.getURI()));
        assertEquals(2, properties.get(RDFS.label.getURI()).size());
    }
    
    @Test
    @DisplayName("添加资源注释")
    void testAddResourceComment() {
        RdfUtil.addResourceComment(model, ALICE_URI, "This is Alice", "en");
        
        Map<String, List<String>> properties = RdfUtil.getResourceProperties(model, ALICE_URI);
        assertTrue(properties.containsKey(RDFS.comment.getURI()));
    }
    
    @Test
    @DisplayName("设置命名空间")
    void testSetNamespace() {
        String customNs = "http://custom.org/";
        RdfUtil.setNamespace(model, "custom", customNs);
        
        Map<String, String> namespaces = RdfUtil.getNamespaces(model);
        assertTrue(namespaces.containsKey("custom"));
        assertEquals(customNs, namespaces.get("custom"));
    }
    
    @Test
    @DisplayName("清空模型")
    void testClear() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        assertFalse(RdfUtil.isEmpty(model));
        
        RdfUtil.clear(model);
        assertTrue(RdfUtil.isEmpty(model));
    }
    
    @Test
    @DisplayName("转换为Turtle格式")
    void testToTurtle() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        
        String turtle = RdfUtil.toTurtle(model);
        assertNotNull(turtle);
        assertFalse(turtle.isEmpty());
        assertTrue(turtle.contains("Alice") || turtle.contains(ALICE_URI));
    }
    
    @Test
    @DisplayName("转换为RDF/XML格式")
    void testToRdfXml() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        
        String rdfXml = RdfUtil.toRdfXml(model);
        assertNotNull(rdfXml);
        assertFalse(rdfXml.isEmpty());
        assertTrue(rdfXml.contains("<?xml") || rdfXml.contains("<rdf:"));
    }
    
    @Test
    @DisplayName("转换为N-Triples格式")
    void testToNTriples() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        
        String nTriples = RdfUtil.toNTriples(model);
        assertNotNull(nTriples);
        assertFalse(nTriples.isEmpty());
        assertTrue(nTriples.contains(ALICE_URI));
    }
    
    @Test
    @DisplayName("转换为JSON-LD格式")
    void testToJsonLd() {
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        
        String jsonLd = RdfUtil.toJsonLd(model);
        assertNotNull(jsonLd);
        assertFalse(jsonLd.isEmpty());
        assertTrue(jsonLd.contains("Alice") || jsonLd.contains("\"@value\""));
    }
    
    @Test
    @DisplayName("从JSON-LD创建模型")
    void testFromJsonLd() {
        String jsonLd = "{\n" +
                        "  \"@context\": {\"ex\": \"" + NS + "\"},\n" +
                        "  \"@id\": \"" + ALICE_URI + "\",\n" +
                        "  \"ex:name\": \"Alice\"\n" +
                        "}";
        
        Model modelFromJson = RdfUtil.fromJsonLd(jsonLd);
        assertNotNull(modelFromJson);
        assertFalse(RdfUtil.isEmpty(modelFromJson));
        
        List<Map<String, String>> statements = RdfUtil.listStatements(modelFromJson);
        assertFalse(statements.isEmpty());
    }
    
    @Test
    @DisplayName("获取模型大小")
    void testGetSize() {
        assertEquals(0, RdfUtil.getSize(model));
        
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        assertEquals(1, RdfUtil.getSize(model));
        
        RdfUtil.addStatement(model, ALICE_URI, AGE_PROPERTY, "30");
        assertEquals(2, RdfUtil.getSize(model));
    }
    
    @Test
    @DisplayName("检查模型是否为空")
    void testIsEmpty() {
        assertTrue(RdfUtil.isEmpty(model));
        
        RdfUtil.addStatement(model, ALICE_URI, NAME_PROPERTY, "Alice");
        assertFalse(RdfUtil.isEmpty(model));
    }
}
