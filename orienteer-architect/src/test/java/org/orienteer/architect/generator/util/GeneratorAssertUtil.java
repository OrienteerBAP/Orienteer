package org.orienteer.architect.generator.util;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public final class GeneratorAssertUtil {

    private GeneratorAssertUtil() {}

    public static void assertNextLine(String expected, BufferedReader reader) throws IOException {
        String actual = readLine(reader, true);
        assertEquals("Lines are not equals", expected, actual);
    }

    public static void assertNextPrettyLine(String expected, BufferedReader reader) throws IOException {
        String actual = readLine(reader, false);
        assertEquals("Lines are not equals", expected, actual);
    }

    public static String readLine(BufferedReader reader, boolean trim) throws IOException {
        String line = reader.readLine();
        if (line != null) {
            return trim ? line.trim() : line;
        }
        return null;
    }
}
