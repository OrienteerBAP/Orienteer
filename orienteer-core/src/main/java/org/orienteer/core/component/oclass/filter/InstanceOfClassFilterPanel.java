package org.orienteer.core.component.oclass.filter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.filter.AbstractFilterPanel;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.model.OClassTextChoiceProvider;
import org.orienteer.core.model.SubClassesModel;
import org.wicketstuff.select2.Select2Choice;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.List;

/**
 *
 */
public class InstanceOfClassFilterPanel extends AbstractFilterPanel<OClass, OClass> {

    private FormComponent<OClass> formComponent;

    public InstanceOfClassFilterPanel(String id, IModel<OClass> model, String filterId,
                                      IModel<OClass> entityModel, IVisualizer visualizer,
                                      IFilterCriteriaManager manager) {
        super(id, model, filterId, entityModel, visualizer, manager, Model.of(true));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(formComponent = createFilterComponent(getModel()));
    }

    @Override
    public OClass getFilterInput() {
        return formComponent.getConvertedInput();
    }

    @Override
    public void focus(AjaxRequestTarget target) {
        target.focusComponent(formComponent);
    }

    @Override
    public FormComponent<OClass> createFilterComponent(IModel<?> model) {
        IModel<OClass> entityModel = getEntityModel();
        IModel<List<OClass>> loadModel = new SubClassesModel(entityModel, true, false);

        return new Select2Choice<OClass>(getFilterId(), getModel(), new OClassTextChoiceProvider(loadModel)) {

            @Override
            protected void onInitialize() {
                super.onInitialize();
                getSettings().setWidth("100%")
                        .setCloseOnSelect(true)
                        .setTheme(OClassMetaPanel.BOOTSTRAP_SELECT2_THEME);
                add(new AjaxFormSubmitBehavior("change") {});
                setOutputMarkupPlaceholderTag(true);
            }
        };
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CLASS_INSTANCE_OF;
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<OClass> model) {
        IModel<String> nameModel = model.map(OClass::getName);
        IFilterCriteria criteria = manager.createClassInstanceOfCriteria(nameModel, getJoinModel());
        manager.addFilterCriteria(criteria);
    }

    @Override
    protected void clearInputs() {
        formComponent.setConvertedInput(null);
        formComponent.setModelObject(null);
    }
}
