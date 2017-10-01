package org.orienteer.core.component.property.date;

import com.google.common.base.Strings;
import org.apache.wicket.Component;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Bootstrap enabled date time field
 */
public class DateTimeBootstrapField extends DateTimeField {

    private static final Pattern YEAR_FORMAT = Pattern.compile("^[^y]*yyyy[^y]*$");

    public DateTimeBootstrapField(String id) {
        super(id);
    }

    public DateTimeBootstrapField(String id, IModel<Date> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        get(DATE).setOutputMarkupId(true);
        get(HOURS).setOutputMarkupId(true);
        get(MINUTES).setOutputMarkupId(true);
    }

    @Override
    protected DateTextField newDateTextField(String id, PropertyModel<Date> model) {
        DateTextField dateTextField = new DateTextField(id, model, new PatternDateConverter(createJavaDateFormat(), false));
        dateTextField.setOutputMarkupId(true);
        return dateTextField;
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null) {
            switch (child.getId()) {
                case DATE:
                    return Markup.of("<input type='text' wicket:id='date' class='form-control'>");
                case HOURS:
                    return Markup.of(getMarkupForTime(HOURS, "hours"));
                case MINUTES:
                    return Markup.of(getMarkupForTime(MINUTES, "minutes"));
                case AM_OR_PM_CHOICE:
                    return Markup.of("<select wicket:id='amOrPmChoice' class='form-control am-or-pm-choice'></select>");
                case "hoursSeparator":
                    return Markup.of("<span wicket:id='hoursSeparator' class='time-separator'>" +
                            "<i class='fa fa-square top-square' aria-hidden='true'></i>" +
                            "<i class='fa fa-square bottom-square' aria-hidden='true'></i></span>");
            }
        }
        return super.getMarkup(child);
    }

    private String getMarkupForTime(String id, String cssClass) {
        return String.format(
                "<input type='text' wicket:id='%s' class='time form-control %s' size='2' maxlength='2'>", id, cssClass);
    }

    @Override
    protected DatePicker newDatePicker() {
        return new DatePicker() {

            private final String datePickerId = getDateTextField().getMarkupId() + "-datepicker";

            @Override
            public void beforeRender(Component component) {
                Response response = component.getResponse();
                response.write(String.format(
                        "<div id='%s' class='input-group date' data-provide='datepicker' style='width:200px;'>", datePickerId));
            }

            @Override
            public void afterRender(Component component) {
                Response response = component.getResponse();
                response.write("<span class='input-group-addon date-btn'>" +
                        "<span class='glyphicon glyphicon-th'></span></span>");
                response.write("</div>");
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                String jqueryInit = String.format("initJQDatepicker('%s', %s);", datePickerId,
                        getDatePickerParams().toString());
                response.render(OnDomReadyHeaderItem.forScript(jqueryInit));
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {

        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(DateTimeBootstrapField.class, "datetime.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(DateTimeBootstrapField.class, "datetime.css")));
        super.renderHead(response);

        response.render(OnDomReadyHeaderItem.forScript(String.format("initDateMarkup('%s')", getMarkupId())));
    }

    private Map<String, String> getDatePickerParams() {
        Map<String, String> params = createParamsMap();
        params.put("autoclose", Boolean.toString(true));
        params.put("language", "'" + getLocale().getLanguage() + "'");
        params.put("orientation", "'bottom'");
        params.put("weekStart", Integer.toString(1));
        params.put("format", "'" + getDatepickerDateFormat() + "'");
        configureDatapickerParams(params);
        return params;
    }

    /**
     * Configure Bootstrap data picker
     * @param params {@link Map<String, String>} which contains data picker params.
     */
    protected void configureDatapickerParams(Map<String, String> params) {

    }

    /**
     * @return Java date format as String
     */
    private String createJavaDateFormat() {
        StyleDateConverter converter = new StyleDateConverter(false);
        String format = converter.getDatePattern(getLocale());
        if (!YEAR_FORMAT.matcher(format).matches()) {
            format = format.replaceAll("(y+)", "yyyy");
        }
        return format;
    }

    /**
     * @return date format for Bootstrap datepicker
     */
    private String getDatepickerDateFormat() {
        return getDateTextField().getTextFormat().toLowerCase();
    }

    /**
     * @return {@link Map<String, String>} with override toString() method which returns JavaScript object
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