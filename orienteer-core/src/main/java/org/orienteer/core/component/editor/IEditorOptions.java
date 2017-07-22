package org.orienteer.core.component.editor;

import org.apache.wicket.util.io.IClusterable;

/**
 * Interface which contains base options for configure <a href="http://codemirror.net">CodeMirror</a> code editor
 */
public interface IEditorOptions extends IClusterable {

    /**
     * readOnly - if true editor only for read and can't update code
     * if false - editor can update code
     */
    public static final String READ_ONLY = "readOnly";

    /**
     * Contains number ms. Default 503.
     * If negative number cursor is hide.
     */
    public static final String CURSOR_BLINK_RATE  = "cursorBlinkRate";

    /**
     * If true shows line numbers
     */
    public static final String LINE_NUMBERS = "lineNumbers";

    /**
     * If true shows active line.
     * Need dependency: {@link CodeMirrorJs#ACTIVELINE_ADDON}
     */
    public static final String STYLE_ACTIVE_LINE = "styleActiveLine";

    /**
     * Contains scrollbar style.
     * Can be 'simple' and 'overlay'.
     * Need dependencies: {@link CodeMirrorJs#SCROLLBAR_ADDON} and {@link CodeMirrorCss#SCROLLBAR_ADDON}
     */
    public static final String SCROLLBAR_STYLE = "scrollbarStyle";

    /**
     * Contains extra keys for editor as JavaScript object
     */
    public static final String EXTRAKEYS = "extraKeys";

    /**
     * Contains name of editor
     */
    public static final String NAME = "name";

    /**
     * Contains editor language mode.
     * Example: mode : 'javascript'. For current example need dependency: {@link CodeMirrorJs#JS_MODE}
     */
    public static final String MODE = "mode";

    /**
     * If true - search match tags
     * Need dependencies: {@link CodeMirrorJs#MATCH_TAG_ADDON} and {@link CodeMirrorJs#FOLD_ADDON}
     */
    public static final String MATCH_TAGS = "matchTags";

    /**
     * If true - search match brackets
     * Need dependency: {@link CodeMirrorJs#MATCH_BRACKETS_ADDON}
     */
    public static final String MATCH_BRACKETS = "matchBrackets";

    /**
     * If true - auto close brackets.
     * Need dependency: {@link CodeMirrorJs#AUTOCLOSE_BRACKETS_ADDON}
     */
    public static final String AUTOCLOSE_BRACKETS = "autoCloseBrackets";

    /**
     * If true - auto close tags.
     * Need dependencies: {@link CodeMirrorJs#AUTOCLOSE_TAGS_ADDON}
     * and {@link CodeMirrorJs#FOLD_ADDON}
     */
    public static final String AUTOCLOSE_TAGS = "autoCloseTags";

    /**
     * Contains theme for editor. Need theme css file.
     * Example: theme: 'eclipse'. Need eclipse.css {@link CodeMirrorCss#ECLIPSE_THEME}
     */
    public static final String THEME = "theme";
}
