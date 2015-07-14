package org.orienteer.core.component.property;

import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.DragAndDropBehavior;
import com.vaynberg.wicket.select2.Select2MultiChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * {@link FormComponentPanel} to select multiple values from list
 *
 * @param <T> the type of {@link com.orientechnologies.orient.core.db.record.OIdentifiable} - commonly {@link com.orientechnologies.orient.core.record.impl.ODocument}
 */
public abstract class MultiSelectPanel<T> extends FormComponentPanel<Collection<T>> {

    protected IModel<Collection<T>> choicesModel;

    public MultiSelectPanel(String id, IModel<Collection<T>> model) {
        super(id, model);
        initialize();
    }

    protected void initialize()
    {
        choicesModel = getModel();
        add(new Select2MultiChoice<T>("choice", choicesModel, getProvider()).add(new DragAndDropBehavior()));
    }

    protected abstract ChoiceProvider<T> getProvider();
}
