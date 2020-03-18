package ru.fatrat.jsonmarshal.impl;

import java.util.*;

public class SimpleTypes {
    
    public final static Map<Class<?>, Class<?>> primitiveTypeMap;
    public final static Set<Class<?>> types;

    static {
        types = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                String.class, Integer.class, int.class, Float.class, float.class,
                Double.class, double.class, Long.class, long.class, Short.class, short.class,
                Byte.class, byte.class, Boolean.class, boolean.class
        )));
        {
            Map<Class<?>, Class<?>> map = new HashMap<>();
            map.put(int.class, Integer.class);
            map.put(float.class, Float.class);
            map.put(double.class, Double.class);
            map.put(long.class, Long.class);
            map.put(short.class, Short.class);
            map.put(byte.class, Byte.class);
            map.put(boolean.class, Boolean.class);
            primitiveTypeMap = Collections.unmodifiableMap(map);
        }
    }
    
    
}
