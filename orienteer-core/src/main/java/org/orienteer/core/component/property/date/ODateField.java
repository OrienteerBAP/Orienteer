package org.orienteer.core.component.property.date;

import com.github.openjson.JSONObject;
import com.google.common.base.Strings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

/**
 * {@link FormComponent} to enter date
 */
public class ODateField extends FormComponentPanel<Date> {

    public static final JavaScriptResourceReference PICKER_JS = new JavaScriptResourceReference(ODateField.class, "picker.js");

    private TextField<String> dateField;

    private final ZoneId clientZone;

    public ODateField(String id, IModel<Date> model) {
        this(id, model, ZoneId.systemDefault());
    }

    public ODateField(String id, IModel<Date> model, ZoneId clientZone) {
        super(id, model);
        this.clientZone = clientZone;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        dateField = new TextField<>("date", getDateAsString(getModel()));
        dateField.setOutputMarkupId(true);
        add(dateField);
        setOutputMarkupId(true);
        add(AttributeModifier.append("class", "bootstrap-data-picker"));
    }

    @Override
    public void convertInput() {
        String date = dateField.getConvertedInput();
        if (!Strings.isNullOrEmpty(date)) {
            LocalDate localDate = LocalDate.parse(date, getDateFormatter());
            Date from = Date.from(localDate.atStartOfDay(clientZone).toInstant());
            setConvertedInput(from);
        } else setConvertedInput(null);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(ODateTimeField.ORIENTEER_DATE_FIELD_CSS));
        response.render(JavaScriptHeaderItem.forReference(PICKER_JS));
        response.render(OnDomReadyHeaderItem.forScript(String.format("initJQDatepicker('%s', %s)", dateField.getMarkupId(),
                getDatePickerParams().toString())));
    }

    private JSONObject getDatePickerParams() {
        JSONObject params = new JSONObject();
        params.put("autoclose", Boolean.toString(true));
        params.put("language", getLocale().getLanguage());
        params.put("orientation", "bottom");
        params.put("weekStart", Integer.toString(1));
        params.put("format", getJavaScriptDatePattern());
        return params;
    }

    public String getDateMarkupId() {
        return dateField.getMarkupId();
    }

    /**
     * @return Java date format as String
     */
    private String getJavaDatePattern() {
        Locale locale = getLocale();
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, Chronology.ofLocale(locale), locale);
        //To convert yy->yyyy but keep yyyy as is
        pattern = pattern.replace("yyyy", "yy").replace("yy", "yyyy");
        return pattern;
    }
    
    private String getJavaScriptDatePattern() {
    	return getJavaDatePattern().toLowerCase();
    }

    private IModel<String> getDateAsString(IModel<Date> model) {
        Date date = model.getObject();
        if (date == null) {
            return Model.of();
        }
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(clientZone).toLocalDate();
        return Model.of(localDate.format(getDateFormatter()));
    }

    private DateTimeFormatter getDateFormatter() {
    	return DateTimeFormatter.ofPattern(getJavaDatePattern(), getLocale());
    }

}
