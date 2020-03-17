package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshal;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class SimpleTypeMarshalPluginTest {

    private StringWriter writer;
    private JsonGeneratorHelper helper;
    private JsonMarshal subj;
    
    @BeforeEach public void beforeEach() {
        writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        helper = new JsonGeneratorHelper(generator);
        List<JsonMarshalPlugin> plugins = Collections.singletonList(new SimpleTypeMarshalPlugin()); 
        subj = new PluginBasedJsonMarshal() {
            @Override
            @Nonnull
            protected Iterator<JsonMarshalPlugin> getPlugins() {
                return plugins.iterator();
            }
        };
        helper.writeStartArray();
    }
    
    private void closeAndAssertResult(String expected) {
        helper.writeEnd();
        helper.getGenerator().close();
        Assertions.assertEquals("[" + expected + "]", writer.toString());
    }

    private void marshal(@Nonnull Object source) {
        subj.marshal(source, helper);
    }
    
    @Test public void testLong() {
        marshal(1L);
        closeAndAssertResult("1");
    }

    @Test public void testInteger() {
        marshal( 1);
        closeAndAssertResult("1");
    }
    @Test public void testTrue() {
        marshal(true);
        closeAndAssertResult("true");
    }

    @Test public void testFalse() {
        marshal(false);
        closeAndAssertResult("false");
    }

    @Test public void testDouble() {
        marshal(1.23);
        closeAndAssertResult("1.23");
    }

    @Test public void testFloat() {
        marshal((float) 2);
        closeAndAssertResult("2.0");
    }
}