package org.orienteer.core.component.widget.loader;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.internal.InternalOModuleManager;
import org.orienteer.core.boot.loader.internal.artifact.OArtifact;
import org.orienteer.core.boot.loader.internal.artifact.OArtifactField;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OArtifactColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.converter.IdentityConverter;
import ru.ydn.wicket.wicketorientdb.model.AbstractListModel;
import ru.ydn.wicket.wicketorientdb.model.JavaSortableDataProvider;

import java.util.Collection;
import java.util.List;

/**
 * Widget to manage Orienteers modules
 */
@Widget(domain="schema", tab="artifacts", id="artifacts-manager", autoEnable=true)
public class OrienteerArtifactsManagerWidget extends AbstractWidget<OArtifact> {
	
	private static final Logger LOG = LoggerFactory.getLogger(OrienteerArtifactsManagerWidget.class);

    private static final String RELOAD_ORIENTEER = "reload.title";


    public OrienteerArtifactsManagerWidget(String id, IModel<OArtifact> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        Form form = new Form("form");
        Label feedback = new Label("feedback");
        feedback.setOutputMarkupPlaceholderTag(true);
        feedback.setVisible(false);
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OArtifact, String>> columns = getColumns(modeModel);
        IModel<List<OArtifact>> installedModulesModel = new AbstractListModel<OArtifact>() {

			@Override
			protected Collection<OArtifact> getData() {
				return InternalOModuleManager.get().getOArtifactsMetadataAsList();
			}
		};
		
		ISortableDataProvider<OArtifact, String> installedModulesProvider = new JavaSortableDataProvider<>(installedModulesModel);
        
        IModel<List<OArtifact>> availableModulesModel = new AbstractListModel<OArtifact>() {
        	List<OArtifact> downloadedModules;

			@Override
			protected Collection<OArtifact> getData() {
				if(downloadedModules==null || downloadedModules.isEmpty()) {
					try {
						downloadedModules = InternalOModuleManager.get().getOrienteerModules();
					} catch (Exception e) {
						LOG.error("It's not possible to download modules file from the internet", e);
						error(e.getMessage());
					}
				}
				return downloadedModules;
			}
		};
        ISortableDataProvider<OArtifact, String> availableModulesProvider = new JavaSortableDataProvider<>(availableModulesModel);
        final OrienteerDataTable<OArtifact, String> modulesTable =
                new OrienteerDataTable<>("oArtifactsTable", columns, installedModulesProvider, 20);
        modulesTable.addCommand(new AddOArtifactCommand(modulesTable, new OArtifactsModalWindowPage(availableModulesProvider)));
        modulesTable.addCommand(new EditOArtifactsCommand(modulesTable, modeModel));
        modulesTable.addCommand(new SaveOArtifactCommand(modulesTable, modeModel, feedback));
        modulesTable.addCommand(new DeleteOArtifactCommand(modulesTable));
        modulesTable.addCommand(new ReloadOrienteerCommand(modulesTable, new ResourceModel(RELOAD_ORIENTEER)));
        form.add(modulesTable);
        form.add(feedback);
        add(form);
    }

    private List<IColumn<OArtifact, String>> getColumns(IModel<DisplayMode> modeModel) {
        List<IColumn<OArtifact, String>> columns = Lists.newArrayList();
        columns.add(new CheckBoxColumn<>(new IdentityConverter<>()));
        columns.add(new OArtifactColumn(OArtifactField.GROUP.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.ARTIFACT.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.VERSION.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.DESCRIPTION.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.LOAD.asModel(), modeModel));
        columns.add(new OArtifactColumn(OArtifactField.TRUSTED.asModel(), modeModel));
        return columns;
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.archive);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.artifacts.manager.title");
    }

    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }
}
