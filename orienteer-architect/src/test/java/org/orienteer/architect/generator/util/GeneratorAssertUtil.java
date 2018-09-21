package org.orienteer.architect.generator.util;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public final class GeneratorAssertUtil {

    private GeneratorAssertUtil() {}

    public static void assertNextLine(String expected, BufferedReader reader) throws IOException {
        String actual = readLine(reader);
        assertEquals("Lines are not equals", expected, actual);
    }

    public static String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return line != null ? line.trim() : null;
    }
}
