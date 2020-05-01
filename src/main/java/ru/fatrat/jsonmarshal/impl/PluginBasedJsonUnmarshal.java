package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Type;
import java.util.Iterator;

public abstract class PluginBasedJsonUnmarshal implements JsonUnmarshal {
    
    protected abstract @Nonnull Iterator<JsonUnmarshalPlugin> getPlugins();

    @Override
    @Nullable public Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource
    ) {
        
        class Context extends StackContext implements JsonUnmarshalContext {

            private @Nullable Object unmarshal(
                    @Nonnull JsonValue source,
                    @Nonnull Type destType,
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                Iterator<JsonUnmarshalPlugin> plugins = getPlugins();
                while(plugins.hasNext()) {
                    JsonUnmarshalPlugin plugin = plugins.next();
                    if (!plugin.canHandle(destType)) continue;
                    return plugin.unmarshal(source, destType, annotationSource, this);
                }
                throw new JsonMarshalException("No plugin can unmarshal");
            }
            
            @Override
            @Nullable
            public Object callback(
                    @Nonnull JsonValue source,
                    @Nonnull Type destType,
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                return unmarshal(source, destType, annotationSource);
            }

        }
        Context context = new Context();
        try {
            return context.unmarshal(source, destType, annotationSource);
        } catch (Exception e) {
            throw new JsonMarshalException(String.format("Unmarshal error, path %s", context.toString()), e);
        }
    }
}
