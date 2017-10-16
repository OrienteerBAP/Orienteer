package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.model.util.CollectionModel;
import org.orienteer.core.component.visualizer.IVisualizer;
import org.orienteer.core.util.OClassCollectionTextChoiceProvider;
import org.orienteer.core.util.ODocumentTextChoiceProvider;
import org.wicketstuff.select2.Select2MultiChoice;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.Arrays;
import java.util.Collection;

import static org.orienteer.core.component.meta.OClassMetaPanel.BOOTSTRAP_SELECT2_THEME;

/**
 * SELECT FROM Class WHERE link IN [#21:0, #22:0]
 */
public class CollectionLinkFilterPanel extends AbstractFilterPanel<Collection<ODocument>> {


    private FormComponent<Collection<String>> classesFormComponent;
    private FormComponent<Collection<ODocument>> docsFormComponent;

    public CollectionLinkFilterPanel(String id, IModel<Collection<ODocument>> model, String filterId, IModel<OProperty> propertyModel,
                                     IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, model, filterId, propertyModel, visualizer, manager, Model.of(true));
        WebMarkupContainer container = new WebMarkupContainer(getFilterId()) {
            @Override
            public IMarkupFragment getMarkup(Component child) {
                if (child != null && (child.getId().equals("classes") || child.getId().equals("documents")))
                    return markupProvider.provideMarkup(child);
                return super.getMarkup(child);
            }
        };
        IModel<Collection<String>> classNamesModel = new CollectionModel<>();
        Label classLabel = new Label("classLabel", new ResourceModel("widget.document.filter.linkCollection.class"));
        classLabel.add(AttributeModifier.append("style", "display: block"));
        Label documentLabel = new Label("documentLabel", new ResourceModel("widget.document.filter.linkCollection.document"));
        documentLabel.add(AttributeModifier.append("style", "display: block"));

        container.add(classesFormComponent = createClassChooseComponent("classes", classNamesModel));
        container.add(docsFormComponent = createODocumentChooseComponent("documents", classNamesModel, getModel()));
        container.add(classLabel);
        container.add(documentLabel);
        add(container);
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<Collection<ODocument>> filterModel) {
        manager.addFilterCriteria(manager.createLinkCollectionFilterCriteria(
                filterModel, type.equals(FilterCriteriaType.LINKLIST), getJoinModel()));
    }

    @Override
    protected Collection<ODocument> getFilterInput() {
        return docsFormComponent.getConvertedInput();
    }

    @Override
    protected void focus(AjaxRequestTarget target) {
        if (classesFormComponent.isEnabled()) {
            target.focusComponent(classesFormComponent);
        } else target.focusComponent(docsFormComponent);
    }

    private Select2MultiChoice<String> createClassChooseComponent(String id, IModel<Collection<String>> classNamesModel) {
        Select2MultiChoice<String> choice = new Select2MultiChoice<String>(id, classNamesModel,
                OClassCollectionTextChoiceProvider.INSTANCE) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                OProperty property = CollectionLinkFilterPanel.this.getPropertyModel().getObject();
                if (property != null && property.getLinkedClass() != null) {
                    setModelObject(Arrays.asList(property.getLinkedClass().getName()));
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
        return choice;
    }

    private Select2MultiChoice<ODocument> createODocumentChooseComponent(String id, IModel<Collection<String>> classNamesModel,
                                                                         IModel<Collection<ODocument>> documentsModel) {
        Select2MultiChoice<ODocument> choice = new Select2MultiChoice<>(id, documentsModel,
                new ODocumentTextChoiceProvider(classNamesModel));
        choice.getSettings()
                .setWidth("100%")
                .setCloseOnSelect(true)
                .setTheme(BOOTSTRAP_SELECT2_THEME)
                .setContainerCssClass("link-filter-class-choice");
        choice.add(new AjaxFormSubmitBehavior("change") {});
        return choice;
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        OProperty property = getPropertyModel().getObject();
        if (property != null) {
            OType type = property.getType();
            return type.equals(OType.LINKSET) ? FilterCriteriaType.LINKSET : FilterCriteriaType.LINKLIST;
        }
        return FilterCriteriaType.LINKLIST;
    }

    @Override
    protected void clearInputs() {
        if (classesFormComponent.isEnabled()) {
            classesFormComponent.setConvertedInput(Lists.<String>newArrayList());
            classesFormComponent.setModelObject(Lists.<String>newArrayList());
        }
        docsFormComponent.setConvertedInput(Lists.<ODocument>newArrayList());
        docsFormComponent.setModelObject(Lists.<ODocument>newArrayList());
    }
}
