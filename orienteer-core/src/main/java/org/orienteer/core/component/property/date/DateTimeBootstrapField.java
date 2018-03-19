package org.orienteer.core.component.property.date;

import com.google.common.base.Strings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Bootstrap enabled date time field
 */
public class DateTimeBootstrapField extends FormComponentPanel<Date> {

    private static final Logger LOG = LoggerFactory.getLogger(DateTimeBootstrapField.class);

    private static final String DATE            = "date";
    private static final String HOURS           = "hours";
    private static final String MINUTES         = "minutes";
    private static final String AM_OR_PM_CHOICE = "amOrPmChoice";

    public DateTimeBootstrapField(String id) {
        super(id);
    }

    public DateTimeBootstrapField(String id, IModel<Date> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(createDateField(DATE));
        add(createHoursField(HOURS));
        add(createMinutesField(MINUTES));
        add(createChoice(AM_OR_PM_CHOICE));
        add(AttributeModifier.append("class", "bootstrap-data-picker"));
        setOutputMarkupId(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void convertInput() {
        super.convertInput();
        TextField<Date> dateTextField = (TextField<Date>) get(DATE);
        Date date = dateTextField.getConvertedInput();
        long millis = isSupportAmPm() ? getMillisOffset() : 0;
        setConvertedInput(new Date(date.getTime() + millis));
        LOG.info("converted input: {}", getConvertedInput());
        LOG.info("current model:   {}", getModelObject());
    }

    @SuppressWarnings("unchecked")
    private long getMillisOffset() {
        long millis = 0;
        int hours = ((TextField<Integer>) get(HOURS)).getConvertedInput();
        int minutes = ((TextField<Integer>) get(MINUTES)).getConvertedInput();
        DropDownChoice<String> amOrPm = (DropDownChoice<String>) get(AM_OR_PM_CHOICE);
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("hh:mm aa");
        try {
            calendar.setTime(format.parse(hours + ":" + minutes + " " + amOrPm.getConvertedInput()));
            millis = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 60 + calendar.get(Calendar.MINUTE) * 60 * 60;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    private TextField createDateField(String id) {
        TextField<Date> field = createField(id);
        field.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.render(OnDomReadyHeaderItem.forScript(String.format("initJQDatepicker('%s', %s)", component.getMarkupId(), getDatePickerParams().toString())));
            }
        });
        field.setType(Date.class);
        return field;
    }

    private TextField<Integer> createHoursField(String id) {
        TextField<Integer> field = createField(id);
        field.setType(Integer.class);
        field.add(RangeValidator.range(0, isSupportAmPm() ? 11 : 23));
        return field;
    }

    private TextField<Integer> createMinutesField(String id) {
        TextField<Integer> field = createField(id);
        field.setType(Integer.class);
        field.add(RangeValidator.range(0, 59));
        return field;
    }

    private <V extends Serializable> TextField<V> createField(String id) {
        TextField<V> field = new TextField<>(id, new Model<>());
        field.setOutputMarkupId(true);
        return field;
    }

    private DropDownChoice<String> createChoice(String id) {
        DropDownChoice<String> choice = new DropDownChoice<>(id, new Model<>(), Arrays.asList("AM", "PM"));
        choice.setOutputMarkupId(true);
        choice.setNullValid(false);
        choice.setModelObject(choice.getChoices().get(0));
        choice.setVisible(isSupportAmPm());
        return choice;
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(DateTimeBootstrapField.class, "datetime.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(DateTimeBootstrapField.class, "datetime.css")));
    }

    private Map<String, String> getDatePickerParams() {
        Map<String, String> params = createParamsMap();
        params.put("autoclose", Boolean.toString(true));
        params.put("language", "'" + getLocale().getLanguage() + "'");
        params.put("orientation", "'bottom'");
        params.put("weekStart", Integer.toString(1));
        params.put("format", "'" + getJavaDatePattern() + "'");
        return params;
    }

    /**
     * @return Java date format as String
     */
    private String getJavaDatePattern() {
        Locale locale = getLocale();
        return DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, Chronology.ofLocale(locale), locale).toLowerCase();
    }

    private boolean isSupportAmPm() {
        Locale locale = getLocale();
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.MEDIUM,
                Chronology.ofLocale(locale), locale);
        return pattern.contains("a");
    }

    /**
     * @return {@link Map} with override toString() method which returns JavaScript object
     */
    protected final Map<String, String> createParamsMap() {
        return new HashMap<String, String>() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                int counter = 0;
                sb.append("{");
                for (String key : keySet()) {
                    String value = get(key);
                    if (!Strings.isNullOrEmpty(value)) {
                        if (counter > 0) sb.append(",");
                        sb.append(key).append(":")
                                .append(value);
                        counter++;
                    }
                }
                sb.append("}");
                return sb.toString();
            }
        };
    }

    public String getDateMarkupId() {
        Component date = get(DATE);
        return date.getMarkupId();
    }

    public String getHoursMarkupId() {
        Component hours = get(HOURS);
        return hours.getMarkupId();
    }

    public String getMinutesMarkupId() {
        Component minutes = get(MINUTES);
        return minutes.getMarkupId();
    }
}