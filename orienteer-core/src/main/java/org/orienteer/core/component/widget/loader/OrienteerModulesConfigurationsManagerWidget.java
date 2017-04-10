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
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfigurationField;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.*;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OModuleConfigurationColumn;
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
public class OrienteerModulesConfigurationsManagerWidget extends AbstractWidget<OModuleConfiguration> {

    private static final String RELOAD_ORIENTEER = "reload.title";

    public OrienteerModulesConfigurationsManagerWidget(String id, IModel<OModuleConfiguration> model, IModel<ODocument> widgetDocumentModel) {
        super(id, model, widgetDocumentModel);
        Form form = new Form("form");
        Label feedback = new Label("feedback");
        feedback.setOutputMarkupPlaceholderTag(true);
        feedback.setVisible(false);
        IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
        List<IColumn<OModuleConfiguration, String>> columns = getColumns(modeModel);
        AbstractOModulesConfigurationsProvider installedModulesProvider = new AbstractOModulesConfigurationsProvider() {

            @Override
            protected List<OModuleConfiguration> getModulesConfigurations() {
                return OrienteerClassLoaderUtil.getOModulesConfigurationsMetadataAsList();
            }
        };
        AbstractOModulesConfigurationsProvider availableModulesProvider = new AbstractOModulesConfigurationsProvider() {
            @Override
            protected List<OModuleConfiguration> getModulesConfigurations() {
                return OrienteerClassLoaderUtil.getOrienteerModulesConfigurationsFromServer();
            }
        };
        IOModulesConfigurationsUpdater updater = getUpdater(Lists.<IOModulesUpdateListener>newArrayList(installedModulesProvider, availableModulesProvider));
        final OrienteerDataTable<OModuleConfiguration, String> modulesTable =
                new OrienteerDataTable<>("modulesConfigurationsTable", columns, installedModulesProvider, 20);
        modulesTable.addCommand(new AddOModuleConfigurationCommand(modulesTable, new OModulesModalWindowPage(availableModulesProvider), updater));

        modulesTable.addCommand(new EditCommand<>(modulesTable, modeModel));
        modulesTable.addCommand(new SaveOModuleConfigurationCommand(modulesTable, updater, modeModel, feedback));
        modulesTable.addCommand(new DeleteOModuleConfigurationCommand(modulesTable, updater));
        modulesTable.addCommand(new AjaxCommand<OModuleConfiguration>("reloadOrienteer", new ResourceModel(RELOAD_ORIENTEER)) {
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

    private List<IColumn<OModuleConfiguration, String>> getColumns(IModel<DisplayMode> modeModel) {
        List<IColumn<OModuleConfiguration, String>> columns = Lists.newArrayList();
        columns.add(new CheckBoxColumn<OModuleConfiguration, OModuleConfiguration, String>(new OModulesConfigurationsConverter()));
        columns.add(new OModuleConfigurationColumn(OModuleConfigurationField.GROUP.asModel(), modeModel));
        columns.add(new OModuleConfigurationColumn(OModuleConfigurationField.ARTIFACT.asModel(), modeModel));
        columns.add(new OModuleConfigurationColumn(OModuleConfigurationField.VERSION.asModel(), modeModel));
        columns.add(new OModuleConfigurationColumn(OModuleConfigurationField.DESCRIPTION.asModel(), modeModel));
        columns.add(new OModuleConfigurationColumn(OModuleConfigurationField.LOAD.asModel(), modeModel));
        columns.add(new OModuleConfigurationColumn(OModuleConfigurationField.TRUSTED.asModel(), modeModel));
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
    public static class OModulesConfigurationsConverter extends Converter<OModuleConfiguration, OModuleConfiguration> implements Serializable {

        @Override
        protected OModuleConfiguration doForward(OModuleConfiguration moduleConfiguration) {
            return moduleConfiguration;
        }

        @Override
        protected OModuleConfiguration doBackward(OModuleConfiguration moduleConfiguration) {
            return moduleConfiguration;
        }
    }

    private IOModulesConfigurationsUpdater getUpdater(final List<IOModulesUpdateListener> listeners) {
        return new IOModulesConfigurationsUpdater() {
            @Override
            public void notifyAboutNewModules() {
                for (IOModulesUpdateListener listener : listeners) {
                    listener.updateModulesConfigurations();
                }
            }
        };
    }
    
    @Override
    protected String getWidgetStyleClass() {
    	return "strict";
    }
}
