package org.soulsoftware.spigot.core.Utilities;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MapUtility {
    public static <K, V> Map<K, V> cloneMap(Map<K, V> map) {
        Map<K, V> clone = new HashMap<>();
        for (K key : map.keySet()) {
            V value = map.get(key);
            if (value instanceof Cloneable) {
                try {
                    V cloned = (V) value.getClass().getMethod("clone").invoke(value);
                    clone.put(key, cloned);
                    continue;
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            clone.put(key, map.get(key));
        }
        return clone;
    }

    public static byte[] mapToBytes(Map<?, ?> map) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(map);
            byte[] arr = byteOut.toByteArray();
            out.close();
            byteOut.close();
            return arr;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<?, ?> bytesToMap(byte[] bytes) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(byteIn);
            return (Map<?, ?>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
