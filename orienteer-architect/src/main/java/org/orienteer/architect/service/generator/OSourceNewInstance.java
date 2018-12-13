package org.orienteer.architect.service.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.orienteer.architect.util.OSourceUtil.appendArgs;

/**
 * Represents create new instance of object in Java sources
 */
public class OSourceNewInstance implements ISource {

    private final String className;
    private final List<String> args;

    public OSourceNewInstance(String className) {
        this(className, Collections.emptyList());
    }

    public OSourceNewInstance(String className, String... args) {
        this(className, Arrays.asList(args));
    }

    public OSourceNewInstance(String className, List<String> args) {
        if (className == null && args.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.className = className;
        this.args = args;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public void appendJavaSrc(StringBuilder sb) {
        if (className == null) {
            sb.append(args.get(0));
        } else {
            sb.append("new ")
                    .append(className)
                    .append("(");
            appendArgs(sb, args)
                    .append(")");
        }
    }
}
