package ru.fatrat.jsonmarshal;

import org.junit.jupiter.api.Test;

import javax.json.JsonObject;
import javax.json.JsonStructure;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class JSConfigReaderTest {

    @Test
    public void test() {
        InputStream cfg = Objects.requireNonNull(JSConfigReaderTest.class.getResourceAsStream(
                JSConfigReaderTest.class.getSimpleName() + ".cfg"));
        JsonStructure struct = JSConfigReader.read(new InputStreamReader(cfg));
        assertTrue(struct instanceof JsonObject);
        JsonObject obj = (JsonObject) struct;
        assertEquals(1, obj.getInt("a"));
        assertEquals("2", obj.getJsonArray("b").getString(0));
        assertEquals(JsonObject.NULL, obj.getJsonArray("b").get(1));
        assertEquals("1\n2", obj.getString("c"));
        assertEquals(4.5, obj.getJsonNumber("d e").doubleValue(), 0.01);
    }

}