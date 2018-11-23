package org.orienteer.architect.service.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.orienteer.architect.util.OSourceUtil.appendArgs;

/**
 * Represents create new instance of object by using static method
 */
public class OSourceStaticNewInstance extends OSourceNewInstance {

    private final String methodName;

    public OSourceStaticNewInstance(String className, String methodName) {
        this(className, methodName, Collections.emptyList());
    }

    public OSourceStaticNewInstance(String className, String methodName, String... args) {
        this(className, methodName, Arrays.asList(args));
    }

    public OSourceStaticNewInstance(String className, String methodName, List<String> args) {
        super(className, args);
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        sb.append(getClassName())
                .append(".")
                .append(methodName)
                .append("(");
        appendArgs(sb, getArgs())
                .append(")");
    }
}
