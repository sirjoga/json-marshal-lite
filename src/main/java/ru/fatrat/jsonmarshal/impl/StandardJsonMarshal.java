package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshal;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Pre-composed plugin-based marshaller.
 */
public class StandardJsonMarshal extends PluginBasedJsonMarshal {

    public static final List<JsonMarshalPlugin> COMMON = Arrays.asList(
            new CustomMarshalPlugin(),
            new SimpleTypeMarshalPlugin(), new ArrayMarshalPlugin(), new JsonOptionalMarshalPlugin(),
            new EnumMarshalPlugin(new StandardEnumStringer(StandardEnumStringer.STANDARD_NAME_FUNCTION)),
            new MapMarshalPlugin(),
            new WildcardMarshalPlugin(),
            new ListMarshalPlugin(),
            new RawValueMarshalPlugin()
    );
    public static final List<JsonMarshalPlugin> FIELD = Collections.singletonList(
            new ObjectFieldMarshalPlugin(new ClassFieldIteratorImpl()));

    public static final List<JsonMarshalPlugin> INTROSPECT = Collections.singletonList(
            new ObjectIntrospectorMarshalPlugin());

    private final List<JsonMarshalPlugin> plugins;

    public StandardJsonMarshal(@Nonnull List<JsonMarshalPlugin> plugins) {
        this.plugins = plugins;
    }

    @Nonnull public static JsonMarshal standardMarshal() {
        return new StandardJsonMarshal(Stream.concat(Stream.concat(COMMON.stream(), FIELD.stream()), INTROSPECT.stream()).collect(Collectors.toList()));
    }

    @Override
    @Nonnull
    final protected Iterator<JsonMarshalPlugin> getPlugins() {
        return plugins.iterator();
    }
}
