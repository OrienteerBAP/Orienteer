package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.IVisualizer;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Panel for equals filter.
 * SELECT FROM aClass WHERE a = 'value'
 */
public class EqualsFilterPanel extends AbstractFilterPanel {

    private final String filterId;

    private final IModel<?> currentModel;

    @SuppressWarnings("unchecked")
    public EqualsFilterPanel(String id, Form form, final String filterId,
                             final IModel<OProperty> propertyModel,
                             final IVisualizer visualizer,
                             IFilterCriteriaManager manager) {
        super(id, Model.of(true));
        this.filterId = filterId;
        currentModel = Model.of();
        manager.setFilterCriteria(FilterCriteriaType.EQUALS,
                manager.createEqualsFilterCriteria(currentModel, getJoinModel()));
        OProperty property = propertyModel.getObject();
        Component component;
        if (property.getType() != OType.BOOLEAN){
            component = visualizer.createComponent(filterId, DisplayMode.EDIT,
                    null, propertyModel, currentModel);
        } else component = new BooleanFilterPanel(filterId, form, (IModel<Boolean>) currentModel);
        add(component);
        add(new Label("title", new ResourceModel(String.format(AbstractFilterOPropertyPanel.TAB_FILTER_TEMPLATE,
                getFilterCriteriaType().getName()))));
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public IMarkupFragment getMarkup(Component child) {
        if (child != null && (child.getId().equals(filterId)))
            return markupProvider.provideMarkup(child);
        return super.getMarkup(child);
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EQUALS;
    }

    @Override
    public void clearInputs(AjaxRequestTarget target) {
        currentModel.setObject(null);
        getJoinModel().setObject(true);
        target.add(this);
    }

}
