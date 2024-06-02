package org.soulsoftware.spigot.core.Utilities.Reflections;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getFieldObj(Object object, int ind) {
        Field field = object.getClass().getDeclaredFields()[ind];
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldObj(Object object, Class<?> source, int ind) {
        Field field = source.getDeclaredFields()[ind];
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
