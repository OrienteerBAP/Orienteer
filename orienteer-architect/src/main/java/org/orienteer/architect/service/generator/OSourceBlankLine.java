package org.orienteer.architect.service.generator;

public class OSourceBlankLine implements ISource {

    private final int num;

    public OSourceBlankLine() {
        this(1);
    }

    public OSourceBlankLine(int num) {
        this.num = num;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        for (int i = 0; i < num; i++) {
            sb.append('\n');
        }
    }
}
