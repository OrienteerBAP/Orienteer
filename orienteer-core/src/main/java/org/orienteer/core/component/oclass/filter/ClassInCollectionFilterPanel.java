package org.orienteer.core.component.oclass.filter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.filter.AbstractFilterPanel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.model.SubClassesModel;
import org.orienteer.core.util.OClassCollectionTextChoiceProvider;
import org.wicketstuff.select2.Select2MultiChoice;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.orienteer.core.component.meta.OClassMetaPanel.BOOTSTRAP_SELECT2_THEME;

/**
 * Panel which implements class in collection filtering
 */
public class ClassInCollectionFilterPanel extends AbstractFilterPanel<Collection<String>, OClass> {

    private FormComponent<Collection<String>> formComponent;

    public ClassInCollectionFilterPanel(String id, IModel<Collection<String>> model,
                                        String filterId, IModel<OClass> entityModel, IVisualizer visualizer,
                                        IFilterCriteriaManager manager) {
        super(id, model, filterId, entityModel, visualizer, manager, Model.of(true));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(formComponent = createFilterComponent(getModel()));
    }

    @Override
    public Collection<String> getFilterInput() {
        return formComponent.getConvertedInput();
    }

    @Override
    public void focus(AjaxRequestTarget target) {
        target.focusComponent(formComponent);
    }

    @Override
    public FormComponent<Collection<String>> createFilterComponent(IModel<?> model) {
        IModel<OClass> entityModel = getEntityModel();
        IModel<List<OClass>> classesModel = new SubClassesModel(entityModel, true, false);

        return new Select2MultiChoice<String>(getFilterId(), getModel(), new OClassCollectionTextChoiceProvider(classesModel)) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                getSettings()
                        .setWidth("100%")
                        .setCloseOnSelect(true)
                        .setTheme(BOOTSTRAP_SELECT2_THEME);
                add(new AjaxFormSubmitBehavior("change") {});
            }
        };
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CLASS_IN_COLLECTION;
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<Collection<String>> model) {
        IFilterCriteria criteria = manager.createClassInCollectionCriteria(model, getJoinModel());
        manager.addFilterCriteria(criteria);
    }

    @Override
    protected void clearInputs() {
        formComponent.setConvertedInput(new ArrayList<>());
        formComponent.setModelObject(new ArrayList<>());
    }
}
