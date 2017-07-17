package org.orienteer.core.component.property.filter;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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

import java.util.Collection;

import static org.orienteer.core.component.meta.OClassMetaPanel.BOOTSTRAP_SELECT2_THEME;

/**
 * SELECT FROM Class WHERE link IN [#21:0, #22:0]
 */
public class CollectionLinkFilterPanel extends AbstractFilterPanel<IModel<Collection<ODocument>>> {

    private IModel<Collection<String>> classNamesModel;

    public CollectionLinkFilterPanel(String id, Form form, String filterId, IModel<OProperty> propertyModel,
                                     IVisualizer visualizer, IFilterCriteriaManager manager) {
        super(id, filterId, form, propertyModel, visualizer, manager, Model.of(true));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer container = new WebMarkupContainer(getFilterId()) {
            @Override
            public IMarkupFragment getMarkup(Component child) {
                if (child != null && (child.getId().equals("classes") || child.getId().equals("documents")))
                    return markupProvider.provideMarkup(child);
                return super.getMarkup(child);
            }
        };

        Label classLabel = new Label("classLabel", new ResourceModel("widget.document.filter.linkCollection.class"));
        classLabel.add(AttributeModifier.append("style", "display: block"));
        Label documentLabel = new Label("documentLabel", new ResourceModel("widget.document.filter.linkCollection.document"));
        documentLabel.add(AttributeModifier.append("style", "display: block"));
        container.add(classLabel);
        container.add(createClassChooseComponent("classes"));
        container.add(documentLabel);
        container.add(createODocumentChooseComponent("documents", getFilterModel()));
        add(container);
    }

    @Override
    protected void setFilterCriteria(IFilterCriteriaManager manager, FilterCriteriaType type, IModel<Collection<ODocument>> filterModel) {
        manager.addFilterCriteria(manager.createLinkCollectionFilterCriteria(
                filterModel, type.equals(FilterCriteriaType.LINKLIST), getJoinModel()));
    }

    @Override
    protected IModel<Collection<ODocument>> createFilterModel() {
        classNamesModel = new CollectionModel<>(Lists.<String>newArrayList());
        return new CollectionModel<>(Lists.<ODocument>newArrayList());
    }

    private Component createClassChooseComponent(String id) {
        Select2MultiChoice<String> choice = new Select2MultiChoice<>(id, classNamesModel,
                OClassCollectionTextChoiceProvider.INSTANCE);
        choice.getSettings()
                .setWidth("100%")
                .setCloseOnSelect(true)
                .setTheme(BOOTSTRAP_SELECT2_THEME)
                .setContainerCssClass("link-filter-class-choice");
        choice.add(new AjaxFormSubmitBehavior(getForm(), "change") {});
        return choice;
    }

    private Component createODocumentChooseComponent(String id, IModel<Collection<ODocument>> documentsModel) {
        Select2MultiChoice<ODocument> choice = new Select2MultiChoice<>(id, documentsModel,
                new ODocumentTextChoiceProvider(classNamesModel));
        choice.getSettings()
                .setWidth("100%")
                .setCloseOnSelect(true)
                .setTheme(BOOTSTRAP_SELECT2_THEME)
                .setContainerCssClass("link-filter-class-choice");
        choice.add(new AjaxFormSubmitBehavior(getForm(), "change") {});
        return choice;
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        OProperty property = getPropertyModel().getObject();
        if (property != null) {
            OType type = property.getType();
            return type == OType.LINKSET ? FilterCriteriaType.LINKSET : FilterCriteriaType.LINKLIST;
        }
        return FilterCriteriaType.LINKLIST;
    }

    @Override
    protected void clearInputs() {
        classNamesModel.getObject().clear();
        getFilterModel().getObject().clear();
    }
}
