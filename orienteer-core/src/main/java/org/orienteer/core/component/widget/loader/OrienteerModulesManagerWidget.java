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
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.orienteer.core.boot.loader.util.artifact.OModuleField;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OModuleColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.OrienteerReloadPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import java.io.Serializable;
import java.util.List;

/**
 * Widget to manage Orienteers modules
 */
@Widget(domain="schema", tab="artifacts", id="artifacts-manager", autoEnable=true)
public class OrienteerModulesManagerWidget extends AbstractWidget<OModule> {

    private static final String RELOAD_ORIENTEER = "reload.title";

    public OrienteerModulesManagerWidget(String id, IModel<OModule> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        Form form = new Form("form");
        Label feedback = new Label("feedback");
        feedback.setOutputMarkupPlaceholderTag(true);
        feedback.setVisible(false);
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OModule, String>> columns = getColumns(modeModel);
        AbstractOModuleProvider installedModulesProvider = new AbstractOModuleProvider() {

            @Override
            protected List<OModule> getModules() {
                return OrienteerClassLoaderUtil.getMetadataModules();
            }
        };
        AbstractOModuleProvider availableModulesProvider = new AbstractOModuleProvider() {
            @Override
            protected List<OModule> getModules() {
                return OrienteerClassLoaderUtil.getOrienteerModulesFromServer();
            }
        };
        IOModulesUpdater updater = getUpdater(Lists.<IOModulesUpdateListener>newArrayList(installedModulesProvider, availableModulesProvider));
        final OrienteerDataTable<OModule, String> modulesTable =
                new OrienteerDataTable<>("modulesTable", columns, installedModulesProvider, 20);
        modulesTable.addCommand(new AddModuleCommand(modulesTable, new OModulesModalWindowPage(availableModulesProvider), updater));

        modulesTable.addCommand(new EditCommand<>(modulesTable, modeModel));
        modulesTable.addCommand(new SaveOModuleCommand(modulesTable, updater, modeModel, feedback));
        modulesTable.addCommand(new DeleteOModuleCommand(modulesTable, updater));
        modulesTable.addCommand(new AjaxCommand<OModule>("reloadOrienteer", new ResourceModel(RELOAD_ORIENTEER)) {
        	@Override
        	public void onClick(AjaxRequestTarget target) {
        		setResponsePage(new OrienteerReloadPage());
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

    private List<IColumn<OModule, String>> getColumns(IModel<DisplayMode> modeModel) {
        List<IColumn<OModule, String>> columns = Lists.newArrayList();
        columns.add(new CheckBoxColumn<OModule, OModule, String>(new OArtifactMetadataConverter()));
        columns.add(new OModuleColumn(OModuleField.GROUP.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.ARTIFACT.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.VERSION.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.DESCRIPTION.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.LOAD.asModel(), modeModel));
        columns.add(new OModuleColumn(OModuleField.TRUSTED.asModel(), modeModel));
        return columns;
    }

    @Override
    protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.archive);
    }

    @Override
    protected IModel<String> getDefaultTitleModel() {
        return new ResourceModel("widget.modules.manager.title");
    }

    /**
     * Converter
     */
    public static class OArtifactMetadataConverter extends Converter<OModule, OModule> implements Serializable {

        @Override
        protected OModule doForward(OModule oModule) {
            return oModule;
        }

        @Override
        protected OModule doBackward(OModule oModule) {
            return oModule;
        }
    }

    private IOModulesUpdater getUpdater(final List<IOModulesUpdateListener> listeners) {
        return new IOModulesUpdater() {
            @Override
            public void notifyAboutNewModules() {
                for (IOModulesUpdateListener listener : listeners) {
                    listener.updateModules();
                }
            }
        };
    }
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }
}
