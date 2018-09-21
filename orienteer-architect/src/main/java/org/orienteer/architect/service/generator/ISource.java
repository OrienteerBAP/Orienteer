package org.orienteer.architect.service.generator;

public interface ISource {

    void appendJavaSrc(StringBuilder sb);

    default String toJavaSrc() {
        StringBuilder sb = new StringBuilder();
        appendJavaSrc(sb);
        return sb.toString();
    }
}
