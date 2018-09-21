package org.orienteer.architect.service.generator;

public class OSourceVariableDeclaration implements ISource {

    private final String modifier;
    private final String type;
    private final String name;

    public OSourceVariableDeclaration(String type, String name) {
        this(null, type, name);
    }

    public OSourceVariableDeclaration(String modifier, String type, String name) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
    }

    public String getModifier() {
        return modifier;
    }


    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }



    @Override
    public void appendJavaSrc(StringBuilder sb) {
        if (modifier != null) {
            sb.append(modifier)
                    .append(" ");
        }
        sb.append(type)
                .append(" ")
                .append(name);
    }
}
