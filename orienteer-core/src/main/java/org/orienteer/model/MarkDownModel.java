package org.orienteer.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.Strings;
import org.pegdown.PegDownProcessor;

public class MarkDownModel extends LoadableDetachableModel<String>
{
    private IModel<String> markDawnModel;

    public MarkDownModel(IModel<String> markDawnModel)
    {
        this.markDawnModel = markDawnModel;
    }

    @Override
    protected String load()
    {
        String markDownValue = markDawnModel.getObject();
        return !Strings.isEmpty(markDownValue) ? new PegDownProcessor().markdownToHtml(markDownValue) : "";

    }
}
