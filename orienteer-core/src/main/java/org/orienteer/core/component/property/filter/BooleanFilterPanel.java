package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class BooleanFilterPanel extends AbstractFilterUpdaterPanel<Boolean> {

    public BooleanFilterPanel(String id, IModel<Boolean> model) {
        super(id, model);
    }

    @Override
    protected Component getFilterComponent(IModel<Boolean> valueModel) {
        List<Boolean> list = Lists.newArrayList();
        list.add(Boolean.TRUE);
        list.add(Boolean.FALSE);
        DropDownChoice<Boolean> choice = new DropDownChoice<Boolean>("booleanChoice", valueModel, list);
        choice.setNullValid(true);
        return add(choice);
    }
}
