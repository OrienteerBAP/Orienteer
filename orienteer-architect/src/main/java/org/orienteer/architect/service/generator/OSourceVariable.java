package org.orienteer.architect.service.generator;

/**
 * Represents variable in Java sources
 */
public class OSourceVariable extends OSourceVariableDeclaration {

    private final OSourceNewInstance instance;

    public OSourceVariable(String type, String name, OSourceNewInstance instance) {
        super(type, name);
        this.instance = instance;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        super.appendJavaSrc(sb);
        sb.append(" = ");
        instance.appendJavaSrc(sb);
        sb.append(";");
    }
}
