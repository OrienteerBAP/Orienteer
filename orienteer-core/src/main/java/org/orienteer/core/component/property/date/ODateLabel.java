package org.orienteer.core.component.property.date;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.util.Date;
import java.util.Locale;

import static org.orienteer.core.OrienteerWebApplication.DATE_CONVERTER;
import static org.orienteer.core.OrienteerWebApplication.DATE_TIME_CONVERTER;

/**
 * {@link Label} for displaying dates 
 */
public class ODateLabel extends Label {

    private final boolean time;

    public ODateLabel(String id, IModel<Date> model) {
        this(id, model, false);
    }

    public ODateLabel(String id, IModel<Date> model, boolean time) {
        super(id, model);
        this.time = time;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        Date date = (Date) getDefaultModelObject();
        Locale locale = getLocale();
        replaceComponentTagBody(markupStream, openTag, time ? DATE_TIME_CONVERTER.convertToString(date, locale) :
                DATE_CONVERTER.convertToString(date, locale));
    }
}
