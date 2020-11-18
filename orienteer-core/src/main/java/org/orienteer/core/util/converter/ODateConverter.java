package org.orienteer.core.util.converter;

import org.apache.wicket.util.convert.converter.DateConverter;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Orienteer date converter
 */
public class ODateConverter extends DateConverter {

    private final boolean time;
    private final boolean applyTZ;
    
    public ODateConverter(boolean time) {
    	this(time, false);
    }

    public ODateConverter(boolean time, boolean applyTZ) {
        this.time = time;
        this.applyTZ = time && applyTZ; //if time is not needed - no need to apply TZ
    }

    @Override
    public Date convertToObject(String value, Locale locale) {
    	if(Strings.isEmpty(value)) return null;
    	DateFormat format = getODateFormat(locale);
    	if(applyTZ) format.setTimeZone(TimeZone.getTimeZone(getUserZoneId()));
    	try {
			return format.parse(value);
		} catch (ParseException e) {
			return null;
		}
    }

    @Override
    public String convertToString(Date value, Locale locale) {
    	if(value==null) return null;
    	DateFormat format = getODateFormat(locale);
    	if(applyTZ) format.setTimeZone(TimeZone.getTimeZone(getUserZoneId()));
    	return format.format(value);
    }

    private DateFormat getODateFormat(Locale locale) {
    	String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.MEDIUM, time ? FormatStyle.MEDIUM : null,
    			Chronology.ofLocale(locale), locale);
    	return new SimpleDateFormat(pattern, locale);
    }
    
    private ZoneId getUserZoneId() {
    	ZoneId zoneId = OrienteerWebSession.exists()
    						?OrienteerWebSession.get().getClientZoneId()
    						:null;
		if(zoneId==null) {
			zoneId = getServerZoneId();
		}
		return zoneId;
    }
    
    private ZoneId getServerZoneId() {
    	ZoneId zoneId = OrienteerWebApplication.lookupApplication().getDatabaseDocumentInternal()
													.getStorage().getConfiguration().getTimeZone().toZoneId();
		if(zoneId==null) {
			zoneId = ZoneId.systemDefault();
		}
		return zoneId;
    }
}
