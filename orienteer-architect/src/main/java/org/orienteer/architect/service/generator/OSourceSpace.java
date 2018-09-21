package org.orienteer.architect.service.generator;

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
