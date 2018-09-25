package org.orienteer.architect.service.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.orienteer.architect.util.OSourceUtil.appendArgs;

/**
 * Represents call method in Java sources
 */
public class OSourceCall implements ISource {

    private final String instanceName;
    private final String methodName;
    private final List<String> args;

    public OSourceCall(String instanceName, String methodName) {
        this(instanceName, methodName, Collections.emptyList());
    }

    public OSourceCall(String instanceName, String methodName, String...args) {
        this(instanceName, methodName, Arrays.asList(args));
    }

    public OSourceCall(String instanceName, String methodName, List<String> args) {
        this.instanceName = instanceName;
        this.methodName = methodName;
        this.args = args;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        sb.append(instanceName)
                .append(".")
                .append(methodName)
                .append("(");
        appendArgs(sb, args)
                .append(")");
    }

}
