package org.orienteer.core.component.property.date;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.text.SimpleDateFormat;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

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
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, getDateAsString());
    }

    @SuppressWarnings("unchecked")
    private String getDateAsString() {
        Date date = (Date) getDefaultModelObject();
        if (date == null) {
            return "";
        }
        Locale locale = getLocale();
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.MEDIUM, time ? FormatStyle.MEDIUM : null,
                Chronology.ofLocale(locale), locale);
        return new SimpleDateFormat(pattern).format(date);
    }
}
