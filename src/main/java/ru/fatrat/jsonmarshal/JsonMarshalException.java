package ru.fatrat.jsonmarshal;

public class JsonMarshalException extends RuntimeException {

    public JsonMarshalException(String msg) {
        super(msg);
    }

    public JsonMarshalException(String msg, Exception cause) {
        super(msg, cause);
    }
}
