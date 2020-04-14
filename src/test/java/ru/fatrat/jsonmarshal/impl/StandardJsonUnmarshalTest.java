package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class StandardJsonUnmarshalTest {

    private JsonUnmarshal subj;
    
    @BeforeEach
    public void beforeEach() {
        subj = StandardJsonUnmarshal.standardObjectFieldUnmarshal();
    }
    
    private JsonValue parse(String source) {
        JsonReader reader = Json.createReader(new StringReader("[" + source + "]"));
        return ((JsonArray) reader.read()).get(0);
    }

    enum E {EA, EB, @JsonName("E_C") EC}

    static class T {
        int a;
        String [] b;
        E c;
        E cc;
        @JsonOptionalClass(String.class) JsonOptional<String> opt1;
        @JsonOptionalClass(String.class) JsonOptional<String> opt2;
        @JsonMap(Integer.class) Map<String, Integer> map;
        @JsonMap(asArray = Integer.class, value = Integer.class) Map<Integer, Integer> map1;
    }
    
    @Test public void test() {
        Assertions.assertEquals((Integer) 1, subj.unmarshal(parse("1"), Integer.class));
        Assertions.assertEquals("a", subj.unmarshal(parse("\"a\""), String.class));
        T t = subj.unmarshal(parse(("{'a':1, 'b':['c','d'],'c':'EB','cc':'E_C','opt1':null,'opt2':'a'," +
                "'map': {'a':1}, 'map1':[[1,2],[3,4]]}")
                .replace("'", "\"")), T.class);
        Assertions.assertEquals(t.a, 1);
        Assertions.assertEquals(t.b.length, 2);
        Assertions.assertEquals(t.b[0], "c");
        Assertions.assertEquals(t.b[1], "d");
        Assertions.assertNull(t.opt1.value);
        Assertions.assertEquals(t.c, E.EB);
        Assertions.assertEquals(t.cc, E.EC);
        Assertions.assertEquals(t.map.get("a"), 1);
        Assertions.assertEquals(t.map1.get(1), 2);
        Assertions.assertEquals(t.map1.get(3), 4);
        Assertions.assertEquals(t.opt2.value, "a");
    }

}
