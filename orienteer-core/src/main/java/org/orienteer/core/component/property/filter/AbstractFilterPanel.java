package org.orienteer.core.component.property.filter;

import com.google.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.service.IMarkupProvider;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;

/**
 * Abstract panel for filters
 */
public abstract class AbstractFilterPanel extends Panel {

    @Inject
    protected IMarkupProvider markupProvider;

    private final IModel<Boolean> join;

    public AbstractFilterPanel(String id, IModel<Boolean> join) {
        super(id);
        this.join = join;
        add(new CheckBox("join", join).setOutputMarkupPlaceholderTag(true));
        add(new Label("joinTitle", new ResourceModel("widget.document.filter.join"))
                .setOutputMarkupPlaceholderTag(true));
    }

    protected IModel<Boolean> getJoinModel() {
        return join;
    }

    public abstract FilterCriteriaType getFilterCriteriaType();

    public abstract void clearInputs(AjaxRequestTarget target);

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && child.getId().equals("join"))
            return markupProvider.provideMarkup(child);
        return super.getMarkup(child);
    }
}
