package org.orienteer.core.component.property.date;

import com.github.openjson.JSONObject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

/**
 * {@link FormComponent} to enter date
 */
public class ODateField extends FormComponentPanel<Date> {

    public static final JavaScriptResourceReference PICKER_JS = new JavaScriptResourceReference(ODateField.class, "picker.js");

    private TextField<Date> dateField;

    public ODateField(String id, IModel<Date> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        dateField = new TextField<>("date", getModel());
        dateField.setOutputMarkupId(true);
        dateField.setType(Date.class);
        add(dateField);
        setOutputMarkupId(true);
        add(AttributeModifier.append("class", "bootstrap-data-picker"));
    }

    @Override
    public void convertInput() {
        setConvertedInput(dateField.getConvertedInput());
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
        params.put("format", getJavaDatePattern());
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
        return DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, Chronology.ofLocale(locale), locale).toLowerCase();
    }
}
