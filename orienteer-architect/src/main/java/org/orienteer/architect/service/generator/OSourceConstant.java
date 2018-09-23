package org.orienteer.architect.service.generator;

public class OSourceConstant extends OSourceVariableDeclaration {

    private final OSourceNewInstance instance;

    public OSourceConstant(String modifier, String type, String name, OSourceNewInstance instance) {
        super(modifier, type, name);
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
