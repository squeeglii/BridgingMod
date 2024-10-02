package me.cg360.mod.bridging.util;

public class ReflectSupport {

    public static Class<?> boxPrimitive(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) return Boolean.class;
            if (clazz == double.class) return Double.class;
            if (clazz == float.class) return Float.class;
            if (clazz == int.class) return Integer.class;
            if (clazz == long.class) return Long.class;
            if (clazz == short.class) return Short.class;
            if (clazz == char.class) return Character.class;
            if (clazz == byte.class) return Byte.class;
            if (clazz == void.class) return Void.class;
        }

        return clazz;
    }

}
