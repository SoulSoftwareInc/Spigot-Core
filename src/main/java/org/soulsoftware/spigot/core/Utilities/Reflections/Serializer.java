package org.soulsoftware.spigot.core.Utilities.Reflections;

import java.io.File;
import java.io.InputStream;

public interface Serializer {
    static File prepareFile(String filename) {
        File file = new File(filename);
        File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }
        return file;
    }

    /**
     * reads the input stream into a new Reflections instance, populating it's store
     */
    Reflections read(InputStream inputStream);

    /**
     * saves a Reflections instance into the given filename
     */
    File save(Reflections reflections, String filename);
}

