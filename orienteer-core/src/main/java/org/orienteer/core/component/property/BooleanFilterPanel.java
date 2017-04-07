package org.orienteer.core.component.property;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * @author Vitaliy Gonchar
 */
public class BooleanFilterPanel extends GenericPanel<Boolean> {


    public BooleanFilterPanel(String id, IModel<Boolean> model) {
        super(id, model);
        List<Boolean> list = Lists.newArrayList();
        list.add(Boolean.TRUE);
        list.add(Boolean.FALSE);
        DropDownChoice<Boolean> choice = new DropDownChoice<Boolean>("booleanChoice", model, list) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        };
        choice.setNullValid(true);
        add(choice);
    }
}
