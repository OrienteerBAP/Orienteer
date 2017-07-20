package org.orienteer.core.component.property;


import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.convert.IConverter;
import org.orienteer.core.OrienteerWebApplication;

import java.util.Date;

/**
 * Panel for edit OType.DATE
 */
public class DatePanel extends FormComponentPanel<Date> {

    private final String dateFormat;
    private final FormComponent<Date> formComponent;

    public DatePanel(String id, IModel<Date> model) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);
        ODatabaseDocument db = OrienteerWebApplication.lookupApplication().getDatabase();
        dateFormat = (String) db.get(ODatabase.ATTRIBUTES.DATEFORMAT);
        formComponent = newDateField("date", model);
        add(formComponent);
    }

    protected DateTextField newDateField(String id, IModel<Date> model) {
        return new DateTextField(id, model) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setOutputMarkupPlaceholderTag(true);
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                return OrienteerWebApplication.DATE_CONVERTER;
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                String jqueryInit = String.format("; initJQDatepicker('%s', '%s', '%s');", getMarkupId(),
                        getLocale().getLanguage(), dateFormat);
                response.render(OnLoadHeaderItem.forScript(jqueryInit));
            }
        };
    }

    @Override
    public void convertInput() {
        setConvertedInput(formComponent.getConvertedInput());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(getClass(), "datepicker.js")));
    }

}
