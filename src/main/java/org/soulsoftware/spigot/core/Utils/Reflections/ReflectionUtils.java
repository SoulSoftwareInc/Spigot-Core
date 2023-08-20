package org.soulsoftware.spigot.core.Utils.Reflections;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getField(Object object, int ind) {
        Field field = object.getClass().getDeclaredFields()[ind];
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Object object, Class<?> source, int ind) {
        Field field = source.getDeclaredFields()[ind];
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printFields(Object obj) {
        for(Field field : obj.getClass().getDeclaredFields()) {
            System.out.println(field.getName());
            field.setAccessible(true);
            try {
                System.out.println(field.get(obj));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
