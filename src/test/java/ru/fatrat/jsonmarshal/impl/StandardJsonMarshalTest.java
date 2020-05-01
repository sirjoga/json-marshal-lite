package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.*;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StandardJsonMarshalTest {

    private StringWriter writer;
    private JsonGeneratorHelper helper;
    private JsonMarshal subj;

    @BeforeEach
    public void beforeEach() {
        writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        helper = new JsonGeneratorHelper(generator);
        subj = StandardJsonMarshal.standardObjectFieldMarshal();
    }

    private void closeAndAssertResult(String expected) {
        helper.getGenerator().close();
        Assertions.assertEquals(expected, writer.toString());
    }

    enum E {EA, EB, @JsonName("E_C") EC}

    private static class A {
        int b;
        Double c;
        String d;
        boolean[] e;
        E f;
        E ff;

        JsonOptional<String> opt1;
        JsonOptional<String> opt2;
        JsonOptional<String> opt3;
        Map<String, Integer> map;
        Map<Integer, Integer> map1;

        JsonValue rawValue;
    }

    @Test public void test() {
        A a = new A();
        a.b = 1;
        a.c = 2.2;
        a.d = "abc";
        a.e = new boolean[] { true, true, false};
        a.f = E.EB;
        a.ff = E.EC;
        a.opt1 = new JsonOptional<>(null);
        a.opt3 = new JsonOptional<>("A");
        a.map = new HashMap<>();
        a.map.put("a", 1);

        a.map1 = new LinkedHashMap<>();
        a.map1.put(2,3);
        a.map1.put(4,5);

        a.rawValue = Json.createObjectBuilder().add("a", 10).build().get("a");


        subj.marshal(a, helper);
        closeAndAssertResult(("{'b':1,'c':2.2,'d':'abc','e':[true,true,false],'f':'EB','ff':'E_C','opt1':null," +
                "'opt3':'A','map':{'a':1},'map1':[[2,3],[4,5]],'rawValue':10}")
                .replace("'", "\""));
    }

}
