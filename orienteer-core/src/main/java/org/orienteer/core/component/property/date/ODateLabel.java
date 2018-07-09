package org.orienteer.core.component.property.date;

import org.apache.wicket.Session;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.request.WebClientInfo;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

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
        String formattedDate = "";
        if (date != null) {
            ZoneId zoneId = ((WebClientInfo) Session.get().getClientInfo()).getProperties().getTimeZone().toZoneId();
            ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
            formattedDate = zonedDateTime.format(getFormatter(zoneId));
        }
        replaceComponentTagBody(markupStream, openTag, formattedDate);
    }

    private DateTimeFormatter getFormatter(ZoneId zoneId) {
        DateTimeFormatter formatter;
        if (time) {
            formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        } else formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        return formatter.withLocale(getLocale()).withZone(zoneId);
    }
}
