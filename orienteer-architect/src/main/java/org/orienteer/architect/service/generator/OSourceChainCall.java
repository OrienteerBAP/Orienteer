package org.orienteer.architect.service.generator;

import java.util.Arrays;
import java.util.List;

import static org.orienteer.architect.util.OSourceUtil.appendArgs;

/**
 * Represents chain call in Java sources
 */
public class OSourceChainCall extends OSourceCall {
    public OSourceChainCall(String methodName) {
        this(null, methodName);
    }

    public OSourceChainCall(String methodName, String... args) {
        this(methodName, Arrays.asList(args));
    }

    public OSourceChainCall(String methodName, List<String> args) {
        super(null, methodName, args);
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        sb.append(".")
                .append(getMethodName())
                .append("(");
        appendArgs(sb, getArgs())
                .append(")");
    }
}
