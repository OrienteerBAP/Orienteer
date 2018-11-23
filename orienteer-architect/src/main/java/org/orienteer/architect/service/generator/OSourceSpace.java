package org.orienteer.architect.service.generator;

/**
 * Represents space in Java sources
 */
public class OSourceSpace implements ISource {

    private final int num;

    public OSourceSpace(int num) {
        this.num = num;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        for (int i = 0; i < num; i++) {
            sb.append(' ');
        }
    }
}
