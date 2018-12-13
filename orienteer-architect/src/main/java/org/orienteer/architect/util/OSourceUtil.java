package org.orienteer.architect.util;

import java.util.List;

/**
 * Utils for {@link org.orienteer.architect.service.ISourceGenerator}
 */
public final class OSourceUtil {

    private OSourceUtil() {}

    public static StringBuilder appendArgs(StringBuilder sb, List<String> args) {
        if (!args.isEmpty()) {
            int i = 0;
            for (String arg : args) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(arg);
                i++;
            }
        }
        return sb;
    }

    public static String wrapString(String str) {
        return "\"" + str + "\"";
    }
}
