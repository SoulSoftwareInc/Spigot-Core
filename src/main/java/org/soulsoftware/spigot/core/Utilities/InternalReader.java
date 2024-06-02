package org.soulsoftware.spigot.core.Utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class InternalReader {
    private InternalReader() {

    }

    public String read(String file) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(file);
        return new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
