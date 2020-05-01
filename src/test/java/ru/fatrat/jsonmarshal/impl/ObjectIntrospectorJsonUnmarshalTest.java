package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObjectIntrospectorJsonUnmarshalTest {

    private JsonUnmarshal subj;
    
    @BeforeEach
    public void beforeEach() {
        subj = StandardJsonUnmarshal.standardObjectIntrospectorUnmarshal();
    }

    private JsonValue parse(String source) {
        JsonReader reader = Json.createReader(new StringReader("[" + source + "]"));
        return ((JsonArray) reader.read()).get(0);
    }

    enum E {EA, EB, EC}

    static class T {
        private int a;
        public int getA() { return a;}
        public void setA(int value) { a = value;}
        private String [] b;
        public String[] getB() { return b;}
        public void setB(String[] value) { b = value; }
        private E c;
        public E getC() { return c;}
        public void setC(E value) { c = value;}
        private JsonOptional<String> opt1;
        public JsonOptional<String> getOpt1() { return opt1;}
        public void setOpt1(JsonOptional<String> value) { opt1 = value; }
        private JsonOptional<String> opt2;
        public JsonOptional<String> getOpt2() { return opt2; }
        public void setOpt2(JsonOptional<String> value) { opt2 = value; }
        private boolean bool;
        public boolean isBool() { return bool;}
        public void setBool(boolean value) {  bool = value;}
    }
    
    @Test public void test() {
        Assertions.assertEquals((Integer) 1, subj.unmarshal(parse("1"), Integer.class));
        Assertions.assertEquals("a", subj.unmarshal(parse("\"a\""), String.class));
        T t = subj.unmarshal(parse("{'a':1, 'b':['c','d'],'c':'EB','opt1':null,'opt2':'a','bool':true}".replace("'", "\"")), T.class);
        Assertions.assertEquals(t.a, 1);
        Assertions.assertEquals(t.b.length, 2);
        Assertions.assertEquals(t.b[0], "c");
        Assertions.assertEquals(t.b[1], "d");
        Assertions.assertNull(t.opt1.value);
        Assertions.assertEquals(t.opt2.value, "a");
        Assertions.assertTrue(t.bool);
    }

}
