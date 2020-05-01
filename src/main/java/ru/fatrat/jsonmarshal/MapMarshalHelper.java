package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class MapMarshalHelper {

     @Nullable
     public static <K, V> Map<K,V> unmarshalNullable(
             @Nonnull JsonValue source, @Nonnull JsonUnmarshal unmarshaller,
             @Nonnull Class<K> keyClass, @Nonnull Class<V> valueClass
     ) {
          @SuppressWarnings("unchecked")
          Map<K, V> result = (Map<K, V>) unmarshaller.unmarshal(source, mapType(keyClass, valueClass), null);
          return result;
     }

     @Nonnull
     public static <K, V> Map<K,V> unmarshal(
             @Nonnull JsonValue source, @Nonnull JsonUnmarshal unmarshaller,
             @Nonnull Class<K> keyClass, @Nonnull Class<V> valueClass
     ) {
          return Optional.ofNullable(unmarshalNullable(source, unmarshaller, keyClass, valueClass))
                  .orElseThrow(() -> new JsonMarshalException("Unmarshalling null map"));
     }

     public static <K,V> void marshal(
             @Nonnull Map<K, V> source, @Nonnull Class<K> keyClass, @Nonnull Class<V> valueClass,
             @Nonnull JsonMarshal marshaller, @Nonnull JsonGeneratorHelper dest) {
          marshaller.marshal(source, mapType(keyClass, valueClass), dest);
     }

     private static ParameterizedType mapType(Class<?> keyClass, Class<?> valueClass) {
          return new ParameterizedType() {
               @Override
               public Type[] getActualTypeArguments() { return new Type[]{keyClass, valueClass}; }

               @Override
               public Type getRawType() { return Map.class; }

               @Override
               public Type getOwnerType() { return null; }
          };
     }
}
