package org.orienteer.core.component.property.filter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.model.OClassTextChoiceProvider;
import org.orienteer.core.util.ODocumentChoiceProvider;
import org.wicketstuff.select2.Select2Choice;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import static org.orienteer.core.component.meta.OClassMetaPanel.BOOTSTRAP_SELECT2_THEME;

/**
 * Panel for search documents which contains equals links with value
 * SELECT FROM Class WHERE link = '#21:00'
 */
public class LinkEqualsFilterPanel extends AbstractFilterPanel<ODocument> {

    private FormComponent<OClass> classFormComponent;
    private FormComponent<ODocument> docFormComponent;

    public LinkEqualsFilterPanel(String id, IModel<ODocument> model, String filterId, IModel<OProperty> propertyModel,
                                 IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager, Model.of(true));
        WebMarkupContainer container = new WebMarkupContainer(getFilterId()) {
            @Override
            public IMarkupFragment getMarkup(Component child) {
                if (child != null && (child.getId().equals("class") || child.getId().equals("document")))
                    return markupProvider.provideMarkup(child);
                return super.getMarkup(child);
            }
        };
        IModel<OClass> classModel = new OClassModel(Model.<String>of());
        container.add(classFormComponent = createClassChoiceComponent("class", classModel));
        container.add(docFormComponent = createDocumentChoiceComponent("document", classModel));
        container.add(new Label("classLabel", new ResourceModel("widget.document.filter.link.class")));
        container.add(new Label("documentLabel", new ResourceModel("widget.document.filter.link.document")));
        add(container);
    }

    private Select2Choice<OClass> createClassChoiceComponent(String id, IModel<OClass> classModel) {
        Select2Choice<OClass> choice = new Select2Choice<OClass>(id, classModel, new OClassTextChoiceProvider()) {

            @Override
            protected void onInitialize() {
                super.onInitialize();
                OProperty property = LinkEqualsFilterPanel.this.getPropertyModel().getObject();
                if (property != null && property.getLinkedClass() != null) {
                    setModelObject(property.getLinkedClass());
                    setEnabled(false);
                }
            }
        };
        choice.getSettings()
                .setWidth("100%")
                .setCloseOnSelect(true)
                .setTheme(BOOTSTRAP_SELECT2_THEME)
                .setContainerCssClass("link-filter-class-choice");
        choice.add(new AjaxFormSubmitBehavior("change") {});
        choice.setOutputMarkupId(true);
        return choice;
    }

    private Select2Choice<ODocument> createDocumentChoiceComponent(String id, IModel<OClass> classModel) {
        Select2Choice<ODocument> choice = new Select2Choice<>(id, getModel(), new ODocumentChoiceProvider(classModel));
        choice.getSettings()
                .setWidth("100%")
                .setCloseOnSelect(true)
                .setTheme(BOOTSTRAP_SELECT2_THEME)
                .setContainerCssClass("link-filter-document-choice");
        choice.add(new AjaxFormSubmitBehavior("change") {});
        choice.setOutputMarkupId(true);
        return choice;
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<ODocument> filterModel) {
        manager.addFilterCriteria(manager.createEqualsFilterCriteria(filterModel, getJoinModel()));
    }

    @Override
    protected ODocument getFilterInput() {
        return docFormComponent.getConvertedInput();
    }

    @Override
    protected void focus(AjaxRequestTarget target) {
        if (classFormComponent.isEnabled()) {
            target.focusComponent(classFormComponent);
        } else target.focusComponent(docFormComponent);
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.LINK;
    }

    @Override
    protected void clearInputs() {
        if (classFormComponent.isEnabled()) {
            classFormComponent.setConvertedInput(null);
            classFormComponent.setModelObject(null);
        }
        docFormComponent.setConvertedInput(null);
        docFormComponent.setModelObject(null);
    }
}
