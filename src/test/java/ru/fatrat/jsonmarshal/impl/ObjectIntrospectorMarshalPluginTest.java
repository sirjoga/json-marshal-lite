package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObjectIntrospectorMarshalPluginTest {

    private StringWriter writer;
    private JsonGeneratorHelper helper;
    private JsonMarshal subj;

    @BeforeEach
    public void beforeEach() {
        writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        helper = new JsonGeneratorHelper(generator);
        subj = StandardJsonMarshal.standardObjectIntrospectorMarshal();
    }

    private void closeAndAssertResult(String expected) {
        helper.getGenerator().close();
        Assertions.assertEquals(expected, writer.toString());
    }

    enum E {EA, EB, EC}

    private static class A {
        private int b;
        public int getB() { return b;}
        private boolean bool;
        public boolean getBool() { return bool;}
        private Double c;
        public Double getC() { return c;}
        private String d;
        public String getD() { return d;}
        private boolean[] e;
        public boolean[] getE() { return e; }
        private E f;
        public E getF() { return f; }
        private JsonOptional<String> opt1;
        public JsonOptional<String> getOpt1() {return opt1;}

    }

    @Test public void test() {
        A a = new A();
        a.b = 1;
        a.c = 2.2;
        a.d = "abc";
        a.e = new boolean[] { true, true, false};
        a.f = E.EB;
        a.opt1 = new JsonOptional<>(null);
        subj.marshal(a, helper);
        closeAndAssertResult("{'b':1,'bool':false,'c':2.2,'d':'abc','e':[true,true,false],'f':'EB','opt1':null}"
                .replace("'", "\""));
    }

}
