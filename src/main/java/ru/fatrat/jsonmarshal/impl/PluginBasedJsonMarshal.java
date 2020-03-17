package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public abstract class PluginBasedJsonMarshal implements JsonMarshal {
    
    protected abstract @Nonnull Iterator<JsonMarshalPlugin> getPlugins();

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nonnull JsonGeneratorHelper destination) {
        
        class Context extends StackContext implements JsonMarshalContext {

            private boolean marshal(
                    @Nonnull Object source,
                    @Nonnull Class<?> sourceClass,
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                Iterator<JsonMarshalPlugin> plugins = getPlugins();
                while(plugins.hasNext()) {
                    JsonMarshalPlugin plugin = plugins.next();
                    if (plugin.marshal(source, sourceClass, annotationSource, this)) return true;
                }
                return false;
            }
            
            
            @Override
            public void callback(
                    @Nonnull Object source, 
                    @Nonnull Class<?> sourceClass, 
                    @Nullable JsonMarshalAnnotationSource annotationSource
            ) {
                if (!marshal(source, sourceClass, annotationSource)) {
                    throw new JsonMarshalException(String.format("Cannot marshal class %s", sourceClass.getName()));
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
            context.marshal(source, sourceClass, null);
        } catch (Exception e) {
            throw new JsonMarshalException(String.format("Marshal error, path %s", context.toString()), e);
        }
    }
}
