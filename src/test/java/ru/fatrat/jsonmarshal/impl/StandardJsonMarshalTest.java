package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshal;
import ru.fatrat.jsonmarshal.JsonOptional;
import ru.fatrat.jsonmarshal.JsonOptionalClass;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;

public class StandardJsonMarshalTest {

    private StringWriter writer;
    private JsonGeneratorHelper helper;
    private JsonMarshal subj;

    @BeforeEach
    public void beforeEach() {
        writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        helper = new JsonGeneratorHelper(generator);
        subj = new StandardJsonMarshal();
    }

    private void closeAndAssertResult(String expected) {
        helper.getGenerator().close();
        Assertions.assertEquals(expected, writer.toString());
    }

    private static class A {
        int b;
        Double c;
        String d;
        boolean[] e;
        @JsonOptionalClass(String.class) JsonOptional<String> opt1;
        @JsonOptionalClass(String.class) JsonOptional<String> opt2;
        @JsonOptionalClass(String.class) JsonOptional<String> opt3;
    }

    @Test public void test() {
        A a = new A();
        a.b = 1;
        a.c = 2.2;
        a.d = "abc";
        a.e = new boolean[] { true, true, false};
        a.opt1 = new JsonOptional<>(null, true);
        a.opt2 = new JsonOptional<>(null, false);
        a.opt3 = new JsonOptional<>("A", true);
        subj.marshal(a, helper);
        closeAndAssertResult("{'b':1,'c':2.2,'d':'abc','e':[true,true,false],'opt1':null,'opt3':'A'}"
                .replace("'", "\""));
    }

}
