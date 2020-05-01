package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Iterator;

public abstract class PluginBasedJsonMarshal implements JsonMarshal {
    
    protected abstract @Nonnull Iterator<JsonMarshalPlugin> getPlugins();

    @Override
    public void marshal(
            @Nonnull Object source,
            @Nonnull Type sourceType,
            @Nonnull JsonGeneratorHelper destination,
            @Nullable JsonMarshalAnnotationSource annotationSource
    ) {
        
        class Context extends StackContext implements JsonMarshalContext {

            private boolean marshal(
                    @Nonnull Object source,
                    @Nonnull Type sourceType,
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                Iterator<JsonMarshalPlugin> plugins = getPlugins();
                while(plugins.hasNext()) {
                    JsonMarshalPlugin plugin = plugins.next();
                    if (!plugin.canHandle(sourceType)) continue;
                    plugin.marshal(source, sourceType, annotationSource, this);
                    return true;
                }
                return false;
            }
            
            
            @Override
            public void callback(
                    @Nonnull Object source, 
                    @Nonnull Type sourceType,
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                if (!marshal(source, sourceType, annotationSource)) {
                    throw new JsonMarshalException(String.format("Cannot marshal class %s", sourceType.getTypeName()));
                }
            }

            @Override
            @Nonnull
            public JsonGeneratorHelper getGeneratorHelper() {
                return destination;
            }
        }
        Context context = new Context();
        try {
            context.marshal(source, sourceType, annotationSource);
        } catch (Exception e) {
            throw new JsonMarshalException(String.format("Marshal error, path %s", context.toString()), e);
        }
    }
}
