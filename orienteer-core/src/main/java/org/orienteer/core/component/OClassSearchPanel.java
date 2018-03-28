package org.orienteer.core.component;

import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.widget.support.jquery.JQueryDashboardSupport;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.List;

/**
 * Panel for search documents in classes
 */
public class OClassSearchPanel extends GenericPanel<String> {

    public final static Ordering<OClass> CLASSES_ORDERING = Ordering.natural().nullsFirst().onResultOf(input -> new OClassNamingModel(input).getObject());

    @Inject
    private IOClassIntrospector oClassIntrospector;

    private WebMarkupContainer resultsContainer;
    private IModel<OClass> selectedClassModel;

    private final SerializableSupplier<List<OClass>> classesGetter;

    public OClassSearchPanel(String id) {
        this(id, Model.of());
    }

    public OClassSearchPanel(String id, IModel<String> model) {
        this(id, model, () -> CLASSES_ORDERING.sortedCopy(OrienteerWebSession.get().getDatabase().getMetadata().getSchema().getClasses()));
    }

    public OClassSearchPanel(String id, IModel<String> model, SerializableSupplier<List<OClass>> classesGetter) {
        super(id, model);
        this.classesGetter = classesGetter;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        selectedClassModel = new OClassModel(classesGetter.get().get(0));

        Form<String> form = new Form<>("form", getModel());
        TextField<String> field = new TextField<>("query", getModel());
        field.add(AttributeModifier.replace("placeholder", new ResourceModel("page.search.placeholder").getObject()));
        form.add(field);
        form.add(createSearchButton("search"));
        form.add(createTabsPanel("tabs"));
        form.add(resultsContainer = createResultsContainer("resultsContainer"));
        add(form);
        resultsContainer.add(createEmptyLabel("resultsLabel"));
        prepareResults();
    }

    public List<OClass> getClasses() {
        return classesGetter.get();
    }

    private void prepareResults() {
        prepareResults(selectedClassModel.getObject());
    }

    private void prepareResults(OClass oClass) {
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        GenericTablePanel<ODocument> tablePanel = createTablePanel("results", oClass, modeModel);
        OrienteerDataTable<ODocument, String> table =  tablePanel.getDataTable();
        table.addCommand(new EditODocumentsCommand(table, modeModel, oClass));
        table.addCommand(new SaveODocumentsCommand(table, modeModel));
        resultsContainer.addOrReplace(tablePanel);
    }

    private AjaxButton createSearchButton(String id) {
        return new AjaxButton(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                target.add(resultsContainer);
            }
        };
    }

    private TabsPanel<OClass> createTabsPanel(String id) {
        return new TabsPanel<OClass>(id, selectedClassModel, new PropertyModel<>(this, "classes")) {
            @Override
            public void onTabClick(AjaxRequestTarget target) {
                prepareResults();
                target.add(resultsContainer);
            }
        };
    }

    private WebMarkupContainer createResultsContainer(String id) {
        WebMarkupContainer container = new WebMarkupContainer(id);
        container.setOutputMarkupId(true);
        return container;
    }

    private GenericTablePanel<ODocument> createTablePanel(String id, OClass oClass, IModel<DisplayMode> modeModel) {
        OQueryDataProvider<ODocument> provider = oClassIntrospector.getDataProviderForGenericSearch(oClass, getModel());
        oClassIntrospector.defineDefaultSorting(provider, oClass);
        return new GenericTablePanel<ODocument>(id, oClassIntrospector.getColumnsFor(oClass, false, modeModel), provider, 20) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(provider.size() > 0);
            }
        };
    }

    private Label createEmptyLabel(String id) {
        return new Label(id, new ResourceModel("page.search.emptyLabel")) {
            @Override
            @SuppressWarnings("unchecked")
            protected void onConfigure() {
                super.onConfigure();
                GenericTablePanel<ODocument> panel = (GenericTablePanel<ODocument>) resultsContainer.get("results");
                setVisible(panel.getDataTable().getDataProvider().size() == 0);
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(JQueryDashboardSupport.JQUERY_UI_JS)));
    }
}
