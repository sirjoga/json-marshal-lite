package ru.fatrat.jsonmarshal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.impl.StandardJsonMarshal;
import ru.fatrat.jsonmarshal.impl.StandardJsonUnmarshal;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapMarshalHelperTest {


    private JsonValue parse(String source) {
        JsonReader reader = Json.createReader(new StringReader("[" + source.replace("'", "\"") + "]"));
        return ((JsonArray) reader.read()).get(0);
    }

    @BeforeEach
    public void beforeEach() {
    }

    @Test
    public void testUnmarshal() {
        StringWriter writer;
        JsonUnmarshal unmarshal;
        writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        unmarshal = StandardJsonUnmarshal.standardObjectFieldUnmarshal();
        Map<String, Integer> res = MapMarshalHelper.unmarshal(parse("{'a':1, 'b':2}"), unmarshal,
                String.class, Integer.class);
        assertEquals(res.get("a"), 1);
        assertEquals(res.get("b"), 2);
    }

    @Test
    public void testMarshal() {
        StringWriter writer;
        JsonGeneratorHelper helper;
        JsonMarshal marshal;
        JsonUnmarshal unmarshal;
        writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        helper = new JsonGeneratorHelper(generator);
        marshal = StandardJsonMarshal.standardObjectFieldMarshal();
        Map<String, Integer> src = new LinkedHashMap<>();
        src.put("a", 1);
        src.put("b", 2);
        MapMarshalHelper.marshal(src, String.class, Integer.class, marshal, helper);
        generator.close();
        assertEquals(writer.toString(), "{'a':1,'b':2}".replace("'", "\""));
    }


}