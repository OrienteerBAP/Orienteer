package org.orienteer.core.component.property.date;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

/**
 * {@link Label} for displaying dates 
 */
public class ODateLabel extends Label {

    private final boolean showTime;
    private final boolean convertTimeZone;

    public ODateLabel(String id, IModel<Date> model) {
        this(id, model, false);
    }

    public ODateLabel(String id, IModel<Date> model, boolean showTime) {
        this(id, model, showTime, true);
    }
    
    public ODateLabel(String id, IModel<Date> model, boolean showTime, boolean convertTimeZone) {
    	super(id, model);
        this.showTime = showTime;
        this.convertTimeZone = convertTimeZone;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        Date date = (Date) getDefaultModelObject();
        String formattedDate = "";
        if (date != null) {
        	if(convertTimeZone) {
	            ZoneId zoneId = OrienteerWebSession.get().getClientZoneId();
	            ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
	            formattedDate = zonedDateTime.format(getFormatter(zoneId));
        	} else {
        		formattedDate = getFormatter(null).format(date.toInstant());
        	}
        }
        replaceComponentTagBody(markupStream, openTag, formattedDate);
    }

    private DateTimeFormatter getFormatter(ZoneId zoneId) {
        DateTimeFormatter formatter;
        if (showTime) {
            formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        } else formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        return formatter.withLocale(getLocale())
        				.withZone(convertTimeZone&&zoneId!=null
        									?zoneId
        									:OrienteerWebSession.get().getDatabase().getStorage()
        												.getConfiguration().getTimeZone().toZoneId());
    }
}
