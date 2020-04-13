package org.orienteer.core.model;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.Strings;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link IModel} for transformation of markdown to HTML
 */
public class MarkDownModel extends LoadableDetachableModel<String> {

    private IModel<String> markDawnModel;

    public MarkDownModel(IModel<String> markDawnModel) {
        this.markDawnModel = markDawnModel;
    }

    @Override
    protected String load() {
        String markDownValue = markDawnModel.getObject();
        if (Strings.isEmpty(markDownValue)) {
            return "";
        }
        try {
            MutableDataSet options = new MutableDataSet();
            options.set(Parser.EXTENSIONS, createExtensions());
            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            Node node = parser.parse(markDawnModel.getObject());
            markDownValue = renderer.render(node);
        } catch (Exception e) {
            throw new WicketRuntimeException("Can't use flexmark-java for markups", e);
        }
        return markDownValue;
    }

    private List<Extension> createExtensions() {
        List<Extension> extensions = new LinkedList<>();
        extensions.add(TablesExtension.create());
        extensions.add(StrikethroughExtension.create());
        extensions.add(EmojiExtension.create());
        return extensions;
    }
}
