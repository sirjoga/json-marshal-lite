package ru.fatrat.jsonmarshal.methods;

import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.util.function.Supplier;

/**
 * Interface of method with auto-marshalled argument and result.
 */
public interface AutoMarshalInstance {

    /**
     * Flag, if instance consumes any argument.
     */
    boolean hasArgument();

    /**
     * Flag, if instance returns a result.
     */
    boolean hasResult();

    /**
     * Argument and result marshalling wrapper.
     */
    @Nullable ResultMarshaller process(@Nullable Supplier<JsonValue> arg);
}
