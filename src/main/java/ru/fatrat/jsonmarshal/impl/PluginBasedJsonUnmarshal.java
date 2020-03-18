package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.util.Iterator;

public abstract class PluginBasedJsonUnmarshal implements JsonUnmarshal {
    
    protected abstract @Nonnull Iterator<JsonUnmarshalPlugin> getPlugins();

    @Override
    @Nullable public <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<?> destClass,
            @Nullable JsonMarshalAnnotationSource annotationSource
    ) {
        
        class Context extends StackContext implements JsonUnmarshalContext {

            private @Nullable Object unmarshal(
                    @Nonnull JsonValue source,
                    @Nonnull Class<?> destClass,
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                Iterator<JsonUnmarshalPlugin> plugins = getPlugins();
                while(plugins.hasNext()) {
                    JsonUnmarshalPlugin plugin = plugins.next();
                    if (!plugin.canHandle(destClass)) continue;
                    return plugin.unmarshal(source, destClass, annotationSource, this);
                }
                throw new JsonMarshalException("No plugin can unmarshal");
            }
            
            @Override
            @Nullable
            public Object callback(
                    @Nonnull JsonValue source,
                    @Nonnull Class<?> destClass,
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                return unmarshal(source, destClass, annotationSource);
            }

        }
        Context context = new Context();
        try {
            @SuppressWarnings("unchecked")
            T result = (T) context.unmarshal(source, destClass, annotationSource);
            return result;
        } catch (Exception e) {
            throw new JsonMarshalException(String.format("Unmarshal error, path %s", context.toString()), e);
        }
    }
}
