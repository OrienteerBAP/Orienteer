package org.orienteer.core.util.converter;

import org.apache.wicket.util.convert.converter.DateConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

/**
 * Orienteer date converter
 */
public class ODateConverter extends DateConverter {

    private final boolean time;

    public ODateConverter(boolean time) {
        this.time = time;
    }

    @Override
    public Date convertToObject(String value, Locale locale) {
        DateFormat format = getODateFormat(locale);
        try {
            return format.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String convertToString(Date value, Locale locale) {
        return value != null ? getODateFormat(locale).format(value) : "";
    }

    private DateFormat getODateFormat(Locale locale) {
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.MEDIUM, time ? FormatStyle.MEDIUM : null,
                Chronology.ofLocale(locale), locale);
        return new SimpleDateFormat(pattern);
    }
}
