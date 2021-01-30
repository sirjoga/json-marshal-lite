package ru.fatrat.jsonmarshal.methods;

import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshal;
import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshal;

import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

public class MethodAutoMarshal {

    private final JsonUnmarshal unmarshaller;
    private final JsonMarshal marshaller;

    public MethodAutoMarshal(JsonMarshal marshaller, JsonUnmarshal unmarshaller) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    public AutoMarshalInstance create(Object object, Method method) {
        return new AutoMarshalInstance() {

            @Override public boolean hasArgument() {
                return method.getParameterCount() > 0;
            }

            @Override public boolean hasResult() {
                return ! method.getReturnType().equals(Void.TYPE);
            }

            @Nullable @Override public ResultMarshaller process(@Nullable Supplier<JsonValue> arg) {
                try {
                    Object objResult;
                    if (!hasArgument())
                        objResult = method.invoke(object);
                    else {
                        if (method.getParameterCount() > 1)
                            throw new JsonMarshalException("Excessive method arguments");
                        Type type = method.getGenericParameterTypes()[0];
                        if (type instanceof Class && JsonValue.class.isAssignableFrom((Class<?>)type)) {
                            JsonValue value = Optional.ofNullable(arg).map(Supplier::get).orElse(null);
                            if (value != null && !((Class<?>)type).isAssignableFrom(value.getClass()))
                                throw new JsonMarshalException("");
                            objResult = method.invoke(object, value);
                        } else {
                            JsonValue jsonArg = Optional.ofNullable(arg).map(Supplier::get).orElse(null);
                            Object objArg;
                            if (jsonArg == null)
                                objArg = null;
                            else {
                                Annotation[] annotations = method.getParameterAnnotations()[0];
                                objArg = unmarshaller.unmarshal(jsonArg, type, new JsonMarshalAnnotationSource() {
                                    @SuppressWarnings("unchecked")
                                    @Nullable @Override public <T extends Annotation> T getAnnotation(Class<T> cls) {
                                        for (Annotation annotation: annotations) {
                                            if (annotation.getClass() == cls) return (T) annotation;
                                        }
                                        return null;
                                    }
                                });
                            }
                            objResult = method.invoke(object, objArg);
                        }
                    }
                    if (!hasResult()) return null;
                    Type resultType = method.getGenericReturnType();
                    if (resultType instanceof Class) {
                        Class<?> resultClass = (Class<?>) resultType;
                        if (JsonValue.class.isAssignableFrom(resultClass)) {
                            return helper -> helper.writeValue((JsonValue) objResult);
                        }
                        if (resultClass == ResultMarshaller.class) {
                            return (ResultMarshaller) objResult;
                        }
                    }
                    return new ResultMarshaller() {
                        @Override public void process(JsonGeneratorHelper destination) {
                            marshaller.marshal(objResult, resultType, destination);
                        }
                        @Override public void close() {
                            if (objResult instanceof AutoCloseable)
                                try {
                                    ((AutoCloseable) objResult).close();
                                } catch (Exception e) {
                                    if (e instanceof RuntimeException) throw (RuntimeException) e;
                                    throw new JsonMarshalException("Closing error", e);
                                }
                        }
                    };
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new JsonMarshalException("Reflection call error", e);
                }
            }
        };
    }
}
