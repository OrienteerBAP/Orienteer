package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.model.OClassTextChoiceProvider;
import org.orienteer.core.util.ODocumentChoiceProvider;
import org.wicketstuff.select2.Select2Choice;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import static org.orienteer.core.component.meta.OClassMetaPanel.BOOTSTRAP_SELECT2_THEME;

/**
 * Panel for search documents which contains equals links with value
 * SELECT FROM Class WHERE link = '#21:00'
 */
public class LinkEqualsFilterPanel extends AbstractFilterPanel<IModel<ODocument>> {

    private final IModel<OClass> classModel;

    public LinkEqualsFilterPanel(String id, Form form, String filterId, IModel<OProperty> propertyModel,
                                 IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, filterId, form, propertyModel, visualizer, manager, Model.of(true));
        this.classModel = new OClassModel(Model.<String>of());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer container = new WebMarkupContainer(getFilterId()) {
            @Override
            public IMarkupFragment getMarkup(Component child) {
                if (child != null && (child.getId().equals("class") || child.getId().equals("document")))
                    return markupProvider.provideMarkup(child);
                return super.getMarkup(child);
            }
        };
        container.add(new Label("classLabel", new ResourceModel("widget.document.filter.link.class")));
        container.add(createClassChoiceComponent("class"));
        container.add(new Label("documentLabel", new ResourceModel("widget.document.filter.link.document")));
        container.add(createDocumentChoiceComponent("document"));
        add(container);
    }

    private Component createClassChoiceComponent(String id) {
        Select2Choice<OClass> choice = new Select2Choice<>(id, classModel, new OClassTextChoiceProvider());
        choice.getSettings()
                .setWidth("100%")
                .setCloseOnSelect(true)
                .setTheme(BOOTSTRAP_SELECT2_THEME)
                .setContainerCssClass("link-filter-class-choice");
        choice.add(new AjaxFormSubmitBehavior(getForm(), "change") {});
        return choice;
    }

    private Component createDocumentChoiceComponent(String id) {
        Select2Choice<ODocument> choice = new Select2Choice<>(id, getFilterModel(), new ODocumentChoiceProvider(classModel));
        choice.getSettings()
                .setWidth("100%")
                .setCloseOnSelect(true)
                .setTheme(BOOTSTRAP_SELECT2_THEME)
                .setContainerCssClass("link-filter-document-choice");
        choice.add(new AjaxFormSubmitBehavior(getForm(), "change") {});
        return choice;
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<ODocument> filterModel) {
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(filterModel, getJoinModel()));
    }

    @Override
    protected IModel<ODocument> createFilterModel() {
        return new ODocumentModel();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.LINK;
    }

    @Override
    protected void clearInputs() {
        getFilterModel().setObject(null);
        classModel.setObject(null);
    }
}
