package org.orienteer.architect.service.generator;

/**
 * Represents blank line in Java soources
 */
public class OSourceBlankLine implements ISource {

    private final int num;

    public OSourceBlankLine() {
        this(1);
    }

    /**
     * 1 - one blank lines
     * 2 - two blank lines
     * @param num number of blank lines
     */
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
