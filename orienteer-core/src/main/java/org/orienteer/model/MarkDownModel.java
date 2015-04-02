package org.orienteer.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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
        if (markDownValue != null)
            return new PegDownProcessor().markdownToHtml(markDawnModel.getObject());
        else
            return markDawnModel.getObject();
    }
}
