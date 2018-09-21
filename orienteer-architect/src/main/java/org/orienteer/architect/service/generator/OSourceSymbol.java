package org.orienteer.architect.service.generator;

public class OSourceSymbol implements ISource {

    private final String operator;

    public OSourceSymbol(String operator) {
        this.operator = operator;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        sb.append(operator);
    }
}
