package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class BooleanFilterPanel extends GenericPanel<Boolean> {

    public BooleanFilterPanel(String id, Form form, final IModel<Boolean> valueModel) {
        super(id, valueModel);
        List<Boolean> list = Lists.newArrayList();
        list.add(Boolean.TRUE);
        list.add(Boolean.FALSE);
        final DropDownChoice<Boolean> choice = new DropDownChoice<>("booleanChoice", valueModel, list);
        choice.add(new AjaxFormSubmitBehavior(form, "change") {});
        choice.setNullValid(true);
        add(choice);
    }

}
