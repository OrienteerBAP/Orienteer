package org.orienteer.core.model;

import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.Strings;

/**
 * {@link IModel} for transformation of markdown to HTML
 */
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
        if(Strings.isEmpty(markDownValue)) return "";
        Class<?> processor = WicketObjects.resolveClass("org.pegdown.PegDownProcessor");
        if(processor!=null) {
        	try {
				Method toHtml = processor.getMethod("markdownToHtml", String.class);
				return Objects.toString(toHtml.invoke(processor.newInstance(), markDownValue));
			} catch (Exception e) {
				throw new WicketRuntimeException("Can't use pegdown for markups", e);
			}
        }
        return markDownValue;

    }
}
