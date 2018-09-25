package org.orienteer.architect.service.generator;

/**
 * Represents symbol in Java sources
 */
public class OSourceSymbol implements ISource {

    private final String symbol;

    public OSourceSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        sb.append(symbol);
    }
}
