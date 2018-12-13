package org.orienteer.architect.service.generator;

/**
 * Basic interface for generate Java source code
 */
public interface ISource {

    /**
     * Append Java sources to given string builder
     * @param sb string builder
     */
    void appendJavaSrc(StringBuilder sb);

    /**
     * Convert current instance to Java source code
     * @return Java source code
     */
    default String toJavaSrc() {
        StringBuilder sb = new StringBuilder();
        appendJavaSrc(sb);
        return sb.toString();
    }
}
