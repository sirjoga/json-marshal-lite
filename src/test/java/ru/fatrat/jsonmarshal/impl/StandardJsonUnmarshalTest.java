package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.*;

import javax.json.*;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class StandardJsonUnmarshalTest {

    private JsonUnmarshal subj;
    
    @BeforeEach
    public void beforeEach() {
        subj = StandardJsonUnmarshal.standardUnmarshal();
    }
    
    private JsonValue parse(String source) {
        JsonReader reader = Json.createReader(new StringReader("[" + source + "]"));
        return ((JsonArray) reader.read()).get(0);
    }

    enum E {EA, EB, @JsonName("E_C") EC}

    @JsonByFields
    static class T {
        int a;
        String [] b;
        E c;
        E cc;
        JsonOptional<String> opt1;
        JsonOptional<String> opt2;
        Map<String, Integer> map;
        Map<Integer, Integer> map1;
        JsonValue rawValue;
        List<Integer> list;
    }
    
    @Test public void test() {
        Assertions.assertEquals((Integer) 1, subj.unmarshal(parse("1"), Integer.class));
        Assertions.assertEquals("a", subj.unmarshal(parse("\"a\""), String.class));
        T t = subj.unmarshal(parse(("{'a':1, 'b':['c','d'],'c':'EB','cc':'E_C','opt1':null,'opt2':'a'," +
                "'map': {'a':1}, 'map1':[[1,2],[3,4]], 'rawValue':10, 'list':[1,null,2]}")
                .replace("'", "\"")), T.class);
        Assertions.assertEquals(t.a, 1);
        Assertions.assertEquals(t.b.length, 2);
        Assertions.assertEquals(t.b[0], "c");
        Assertions.assertEquals(t.b[1], "d");
        Assertions.assertNull(t.opt1.value);
        Assertions.assertEquals(t.c, E.EB);
        Assertions.assertEquals(t.cc, E.EC);
        Assertions.assertEquals(((JsonNumber)t.rawValue).longValue(), 10L);
        Assertions.assertEquals(t.map.get("a"), 1);
        Assertions.assertEquals(t.map1.get(1), 2);
        Assertions.assertEquals(t.map1.get(3), 4);
        Assertions.assertEquals(t.opt2.value, "a");
        Assertions.assertEquals(t.list.size(), 3);
        Assertions.assertEquals(t.list.get(0), 1);
        Assertions.assertNull(t.list.get(1));
        Assertions.assertEquals(t.list.get(2), 2);
    }

}
