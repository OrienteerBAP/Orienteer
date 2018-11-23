package org.orienteer.architect.util;

/**
 * Utils for working with JavaScript in 'orienteer-architect'
 */
public final class OArchitectJsUtils {

    private OArchitectJsUtils() {}

    public static String switchPageScroll(boolean show) {
        return String.format("app.editor.fullScreenEnable = %s; app.switchPageScrolling();", !show);
    }

    public static String callback() {
        return "app.executeCallback('%s');";
    }
}
