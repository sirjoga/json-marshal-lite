package ru.fatrat.jsonmarshal;

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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapMarshalHelperTest {

    private JsonValue parse(String source) {
        JsonReader reader = Json.createReader(new StringReader("[" + source.replace("'", "\"") + "]"));
        return ((JsonArray) reader.read()).get(0);
    }

    @Test
    public void testUnmarshal() {
        JsonUnmarshal unmarshal = StandardJsonUnmarshal.standardUnmarshal();
        Map<String, ? extends Integer> res = unmarshal.unmarshal(parse("{'a':1, 'b':2}"),
                new JsonGenericType<Map<String, ? extends Integer>>() {});
        assertEquals(res.get("a"), 1);
        assertEquals(res.get("b"), 2);
    }

    @Test
    public void testMarshal() {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        JsonGeneratorHelper helper = new JsonGeneratorHelper(generator);
        JsonMarshal marshal = StandardJsonMarshal.standardMarshal();
        Map<String, Integer> src = new LinkedHashMap<>();
        src.put("a", 1);
        src.put("b", 2);
        marshal.marshal(src, new JsonGenericType<Map<String, Integer>>(){}, helper);
        generator.close();
        assertEquals(writer.toString(), "{'a':1,'b':2}".replace("'", "\""));
    }


}