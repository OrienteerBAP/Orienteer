package org.orienteer.architect.util;

public final class OArchitectJsUtils {

    public static String switchPageScroll(boolean show) {
        return String.format("app.editor.fullScreenEnable = %s; app.switchPageScrolling();", !show);
    }

    public static String callback() {
        return "app.executeCallback('%s');";
    }
}
