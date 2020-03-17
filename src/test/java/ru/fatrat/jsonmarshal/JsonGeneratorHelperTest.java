package ru.fatrat.jsonmarshal;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;

public class JsonGeneratorHelperTest {

    private StringWriter writer;
    private JsonGeneratorHelper subj;
    
    @BeforeEach
    public void beforeEach() {
        writer = new StringWriter();
        JsonGenerator jsonGenerator = Json.createGenerator(writer);
        subj = new JsonGeneratorHelper(jsonGenerator);
    }
    
    private String closeAndGetResult() {
        subj.getGenerator().close();
        return writer.toString();
    }
    
    private void assertResult(String expected) {
        Assertions.assertEquals(expected, closeAndGetResult());
    }

    @Test
    public void testWriteArray() {
        subj.writeStartArray();
        subj.writeEnd();
        assertResult("[]");
    }

    @Test
    public void testWriteInt() {
        subj.writeStartArray();
        subj.writeInt(1);
        subj.writeEnd();
        assertResult("[1]");
    }

    @Test
    public void testWriteString() {
        subj.writeStartArray();
        subj.writeString("a");
        subj.writeEnd();
        assertResult("[\"a\"]");
    }

    @Test
    public void testWriteFloat() {
        subj.writeStartArray();
        subj.writeFloat(1.0);
        subj.writeEnd();
        assertResult("[1.0]");
    }

    @Test
    public void testWriteNull() {
        subj.writeStartArray();
        subj.writeNull();
        subj.writeEnd();
        assertResult("[null]");
    }

    @Test
    public void testWriteBoolean() {
        subj.writeStartArray();
        subj.writeBoolean(true);
        subj.writeBoolean(false);
        subj.writeEnd();
        assertResult("[true,false]");
    }

    @Test
    public void testWriteObject() {
        subj.writeStartObject();
        subj.setName("a");
        subj.writeNull();
        subj.writeEnd();
        assertResult("{\"a\":null}");
    }
    
    @Test
    public void testBufferCommit() {
        subj.startBuffer();
        subj.writeStartObject();
        subj.writeEnd();
        subj.endBuffer(true);
        assertResult("{}");
    }

    @Test
    public void testBufferRollback() {
        subj.startBuffer();
        subj.writeStartArray();
        subj.endBuffer(false);
        subj.writeStartObject();
        subj.writeEnd();
        assertResult("{}");
    }

    @Test
    public void testNestedCommit() {
        subj.startBuffer();
        subj.writeStartArray();
        subj.startBuffer();
        subj.writeStartArray();
        subj.writeEnd();
        subj.endBuffer(true);
        subj.writeEnd();
        subj.endBuffer(true);
        assertResult("[[]]");
    }

    @Test
    public void testNestedRollback() {
        subj.startBuffer();
        subj.writeStartArray();
        subj.startBuffer();
        subj.writeStartArray();
        subj.writeEnd();
        subj.endBuffer(false);
        subj.writeEnd();
        subj.endBuffer(true);
        assertResult("[]");
    }

    @Test
    public void testNestedIncompleteRollback() {
        subj.startBuffer();
        subj.writeStartArray();
        subj.startBuffer();
        subj.writeStartArray();
        subj.endBuffer(false);
        subj.writeEnd();
        subj.endBuffer(true);
        assertResult("[]");
    }
    
    @Test
    public void testComplexStructure() {
        subj.writeStartObject();
        {
            subj.setName("a");
            subj.writeStartArray();
            {
                subj.writeInt(1);
                subj.writeNull();
            }
            subj.writeEnd();
        }
        subj.writeEnd();
        assertResult("{\"a\":[1,null]}");                
    }

}
