package ru.fatrat.jsonmarshal.impl;

import org.junit.jupiter.api.Test;
import ru.fatrat.jsonmarshal.JsonCustomMarshallable;
import ru.fatrat.jsonmarshal.JsonCustomUnmarshallable;
import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshal;
import ru.fatrat.jsonmarshal.JsonUnmarshal;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomMarshalTest {

    private static class A implements JsonCustomMarshallable, JsonCustomUnmarshallable {

        private final JsonValue value;

        @Override public void jsonMarshal(JsonGeneratorHelper dest) {
            dest.writeBoolean(false);
        }
        public A(JsonValue value) {
            this.value = value;
        }
    }

    @Test
    public void unmarshalTest() {
        JsonUnmarshal unmarshaller = StandardJsonUnmarshal.standardUnmarshal();
        A a = unmarshaller.unmarshal(JsonValue.TRUE, A.class);
        assertEquals(a.value, JsonValue.TRUE);
    }

    @Test
    public void marshalTest() {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        JsonGeneratorHelper helper = new JsonGeneratorHelper(generator);
        JsonMarshal marshaller = StandardJsonMarshal.standardMarshal();
        generator.writeStartArray();
        marshaller.marshal(new A(JsonValue.NULL), helper);
        generator.writeEnd();
        generator.close();
        assertEquals("[false]", writer.toString());
    }

}
