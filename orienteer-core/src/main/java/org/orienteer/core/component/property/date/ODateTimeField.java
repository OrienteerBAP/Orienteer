package org.orienteer.core.component.property.date;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;

import java.time.*;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * Bootstrap enabled date time field
 */
public class ODateTimeField extends FormComponentPanel<Date> {

    public static final CssResourceReference ORIENTEER_DATE_FIELD_CSS = new CssResourceReference(ODateTimeField.class, "orienteer-date-field.css");
    public static final CssResourceReference DATETIME_CSS             = new CssResourceReference(ODateTimeField.class, "datetime.css");

    private static final String AM = "AM";
    private static final String PM = "PM";

    private TextField<Integer> hoursField;
    private TextField<Integer> minutesField;
    private DropDownChoice<String> amOrPmChoice;
    private ODateField picker;

    private final ZoneId clientZone;

    public ODateTimeField(String id, IModel<Date> model) {
        super(id, model);
        clientZone = ((WebClientInfo)Session.get().getClientInfo()).getProperties().getTimeZone().toZoneId();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Date date = getModelObject();
        Instant instant = date != null ? date.toInstant() : null;
        Date pickerDate = null;
        ZonedDateTime zonedDateTime = null;
        if (instant != null) {
            zonedDateTime = instant.atZone(clientZone);
            pickerDate = Date.from(zonedDateTime.toInstant());
        }
        add(picker = new ODateField("datePanel", Model.of(pickerDate), clientZone));
        addTimeComponents(zonedDateTime);
        add(AttributeModifier.append("class", "bootstrap-data-picker"));
        setOutputMarkupId(true);
    }

    private void addTimeComponents(ZonedDateTime dateTime) {
        int hours = 0;
        int minutes = 0;
        int mode = -1;

        if (dateTime != null) {
            if (isSupportAmPm()) {
                mode = dateTime.get(ChronoField.AMPM_OF_DAY);
            }
            hours = mode == -1 ? dateTime.getHour() : dateTime.get(ChronoField.HOUR_OF_AMPM);
            minutes = dateTime.getMinute();
        }

        add(hoursField = createHoursField("hours", hours));
        add(minutesField = createMinutesField("minutes", minutes));
        add(amOrPmChoice = createChoice("amOrPmChoice", mode));
    }

    @Override
    public void convertInput() {
        boolean supportAmOrPm = isSupportAmPm();
        int hours = Optional.ofNullable(hoursField.getConvertedInput()).orElse(0);
        int minutes = Optional.ofNullable(minutesField.getConvertedInput()).orElse(0);
        Date date = picker.getConvertedInput();
        if (date != null) {
            LocalDate localDate = date.toInstant().atZone(clientZone).toLocalDate();
            LocalTime localTime = LocalTime.of(hours, minutes);
            ZonedDateTime dateTime = ZonedDateTime.of(localDate, localTime, clientZone);
            if (supportAmOrPm) {
                int amPm = amOrPmChoice.getConvertedInput().equals(AM) ? 0 : 1;
                dateTime = dateTime.with(ChronoField.AMPM_OF_DAY, amPm);
            }
            setConvertedInput(Date.from(dateTime.toInstant()));
        } else setConvertedInput(null);
    }

    private TextField<Integer> createHoursField(String id, int hours) {
        TextField<Integer> field = new TextField<>(id, Model.of(hours), Integer.class);
        field.setOutputMarkupId(true);
        field.add(RangeValidator.range(0, isSupportAmPm() ? 12 : 23));
        return field;
    }

    private TextField<Integer> createMinutesField(String id, int minutes) {
        TextField<Integer> field = new TextField<>(id, Model.of(minutes), Integer.class);
        field.setOutputMarkupId(true);
        field.add(RangeValidator.range(0, 59));
        return field;
    }

    private DropDownChoice<String> createChoice(String id, int mode) {
        DropDownChoice<String> choice = new DropDownChoice<>(id, new Model<>(), Arrays.asList(AM, PM));
        boolean support = isSupportAmPm();
        choice.setOutputMarkupId(true);
        choice.setNullValid(false);
        if (mode != -1) choice.setModelObject(mode == Calendar.AM ? choice.getChoices().get(0) : choice.getChoices().get(1));
        else choice.setModelObject(choice.getChoices().get(0));
        choice.setVisible(support);
        choice.setEnabled(support);
        return choice;
    }

    private boolean isSupportAmPm() {
        Locale locale = getLocale();
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.MEDIUM,
                Chronology.ofLocale(locale), locale).toLowerCase();
        return pattern.contains("a");
    }

    public String getHoursMarkupId() {
        return hoursField.getMarkupId();
    }

    public String getMinutesMarkupId() {
        return minutesField.getMarkupId();
    }


    public String getDateMarkupId() {
        return picker.getDateMarkupId();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(ORIENTEER_DATE_FIELD_CSS));
        response.render(CssHeaderItem.forReference(DATETIME_CSS));
    }
}