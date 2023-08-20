package org.soulsoftware.spigot.core.Utils;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Collectors;

@Getter
public class InternalReader {
    private final String value;

    public InternalReader(String file) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(file);
        value = new BufferedReader(
          new InputStreamReader(is, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }
}
