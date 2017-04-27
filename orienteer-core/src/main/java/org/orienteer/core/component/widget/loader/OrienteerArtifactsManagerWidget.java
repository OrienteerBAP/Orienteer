package org.orienteer.core.component.widget.loader;

import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerFilter;
import org.orienteer.core.boot.loader.OrienteerClassLoader;
import org.orienteer.core.boot.loader.util.OrienteerClassLoaderUtil;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactField;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OArtifactColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.ReloadOrienteerPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import java.io.Serializable;
import java.util.List;

/**
 * Widget to manage Orienteers modules
 */
@Widget(domain="schema", tab="artifacts", id="artifacts-manager", autoEnable=true)
public class OrienteerArtifactsManagerWidget extends AbstractWidget<OArtifact> {

    private static final String RELOAD_ORIENTEER = "reload.title";

    public OrienteerArtifactsManagerWidget(String id, IModel<OArtifact> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        Form form = new Form("form");
        Label feedback = new Label("feedback");
        feedback.setOutputMarkupPlaceholderTag(true);
        feedback.setVisible(false);
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OArtifact, String>> columns = getColumns(modeModel);
        AbstractOArtifactsProvider installedModulesProvider = new AbstractOArtifactsProvider() {

            @Override
            protected List<OArtifact> getOArtifacts() {
                return OrienteerClassLoaderUtil.getOoArtifactsMetadataAsList();
            }
        };
        AbstractOArtifactsProvider availableModulesProvider = new AbstractOArtifactsProvider() {
            @Override
            protected List<OArtifact> getOArtifacts() {
                return OrienteerClassLoaderUtil.getOrienteerArtifactsFromServer();
            }
        };
        IOArtifactsUpdater updater = getUpdater(Lists.<IOArtifactsUpdateListener>newArrayList(installedModulesProvider, availableModulesProvider));
        final OrienteerDataTable<OArtifact, String> modulesTable =
                new OrienteerDataTable<>("oArtifactsTable", columns, installedModulesProvider, 20);
        modulesTable.addCommand(new AddOArtifactCommand(modulesTable, new OArtifactsModalWindowPage(availableModulesProvider), updater));

        modulesTable.addCommand(new EditCommand<>(modulesTable, modeModel));
        modulesTable.addCommand(new SaveOArtifactCommand(modulesTable, updater, modeModel, feedback));
        modulesTable.addCommand(new DeleteOArtifactCommand(modulesTable, updater));
        modulesTable.addCommand(new AjaxCommand<OArtifact>("reloadOrienteer", new ResourceModel(RELOAD_ORIENTEER)) {
        	@Override
        	public void onClick(AjaxRequestTarget target) {
        	    setResponsePage(new ReloadOrienteerPage());
                OrienteerClassLoader.useDefaultClassLoaderProperties();
        		OrienteerFilter.reloadOrienteer();
        	}
        }.setIcon(FAIconType.refresh)
         .setBootstrapType(BootstrapType.PRIMARY)
         .setBootstrapType(BootstrapType.WARNING)
         .setChangingDisplayMode(true));
        form.add(modulesTable);
        form.add(feedback);
        add(form);
    }

    private List<IColumn<OArtifact, String>> getColumns(IModel<DisplayMode> modeModel) {
        List<IColumn<OArtifact, String>> columns = Lists.newArrayList();
        columns.add(new CheckBoxColumn<OArtifact, OArtifact, String>(new OArtifactsConverter()));
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

    /**
     * Converter
     */
    public static class OArtifactsConverter extends Converter<OArtifact, OArtifact> implements Serializable {

        @Override
        protected OArtifact doForward(OArtifact oArtifact) {
            return oArtifact;
        }

        @Override
        protected OArtifact doBackward(OArtifact oArtifact) {
            return oArtifact;
        }
    }

    private IOArtifactsUpdater getUpdater(final List<IOArtifactsUpdateListener> listeners) {
        return new IOArtifactsUpdater() {
            @Override
            public void notifyAboutNewModules() {
                for (IOArtifactsUpdateListener listener : listeners) {
                    listener.updateOArtifacts();
                }
            }
        };
    }
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }
}
