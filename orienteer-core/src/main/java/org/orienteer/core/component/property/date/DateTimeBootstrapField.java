package org.orienteer.core.component.property.date;

import org.apache.wicket.Component;
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

/**
 * Bootstrap enabled date time field
 */
public class DateTimeBootstrapField extends DateTimeField {

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
    }

    @Override
    protected DateTextField newDateTextField(String id, PropertyModel<Date> model) {
        DateTextField dateTextField = DateTextField.forDatePattern(id, model, "yyyy-MM-dd");
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

            @Override
            public void beforeRender(Component component) {
                Response response = component.getResponse();
                response.write("<div class='input-group date' data-provide='datepicker'>");
            }

            @Override
            public void afterRender(Component component) {
                Response response = component.getResponse();
                response.write("<a href='#' class='input-group-addon date-btn'>" +
                        "<span class='glyphicon glyphicon-th'></span></a>");
                response.write("</div>");
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                String jqueryInit = String.format("; initJQDatepicker('%s', '%s', '%s');", getDateTextField().getMarkupId(),
                        getLocale().getLanguage(), getDateFormat());
                response.render(OnDomReadyHeaderItem.forScript(jqueryInit));
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {

        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(DateTimeBootstrapField.class, "datetime.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(DateTimeBootstrapField.class, "datetime.css")));
        super.renderHead(response);

        response.render(OnDomReadyHeaderItem.forScript(String.format("; initDateMarkup('%s')", getMarkupId())));
    }

    /**
     * @return date format for Bootstrap datepicker
     */
    private String getDateFormat() {
        return getDateTextField().getTextFormat().toLowerCase();
    }
}
