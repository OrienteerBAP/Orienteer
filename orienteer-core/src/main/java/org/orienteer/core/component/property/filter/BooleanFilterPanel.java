package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Filter panel for boolean properties
 */
public class BooleanFilterPanel extends FormComponentPanel<Boolean> {

    private final FormComponent<Boolean> choiceComponent;

    public BooleanFilterPanel(String id, final IModel<Boolean> valueModel) {
        super(id, valueModel);
        List<Boolean> list = Lists.newArrayList();
        list.add(Boolean.TRUE);
        list.add(Boolean.FALSE);
        final DropDownChoice<Boolean> choice = new DropDownChoice<>("booleanChoice", valueModel, list);
        choice.add(new AjaxFormSubmitBehavior("change") {});
        choice.setNullValid(true);
        add(choice);
        this.choiceComponent = choice;
    }

    @Override
    public void convertInput() {
        setConvertedInput(choiceComponent.getConvertedInput());
    }

}
