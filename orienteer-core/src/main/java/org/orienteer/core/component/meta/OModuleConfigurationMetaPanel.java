package org.orienteer.core.component.meta;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfigurationField;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.BooleanViewPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Meta panel for {@link OModuleConfiguration}
 * @param <V> type of value
 */
public class OModuleConfigurationMetaPanel<V> extends AbstractComplexModeMetaPanel<OModuleConfiguration, DisplayMode, OModuleConfigurationField, V> {

    private static final Logger LOG = LoggerFactory.getLogger(OModuleConfigurationMetaPanel.class);

    private static final String GROUP       = "widget.modules.group";
    private static final String ARTIFACT    = "widget.modules.artifact";
    private static final String VERSION     = "widget.modules.version";
    private static final String REPOSITORY  = "widget.modules.repository";
    private static final String DESCRIPTION = "widget.modules.description";
    private static final String FILE        = "widget.modules.file";
    private static final String LOAD        = "widget.modules.load";
    private static final String TRUSTED     = "widget.modules.trusted";

    public OModuleConfigurationMetaPanel(String id, IModel<DisplayMode> modeModel,
                                         IModel<OModuleConfiguration> entityModel, IModel<OModuleConfigurationField> criteryModel) {
        super(id, modeModel, entityModel, criteryModel);
    }


    @Override
    @SuppressWarnings("unchecked")
    protected V getValue(OModuleConfiguration moduleConfiguration, OModuleConfigurationField critery) {
        V value = null;
        switch (critery) {
            case GROUP:
                value = (V) moduleConfiguration.getArtifact().getGroupId();
                break;
            case ARTIFACT:
                value = (V) moduleConfiguration.getArtifact().getArtifactId();
                break;
            case VERSION:
                value = (V) moduleConfiguration.getArtifact().getVersion();
                break;
            case DESCRIPTION:
                value = (V) moduleConfiguration.getArtifact().getDescription();
                break;
            case DOWNLOADED:
                value = (V) Boolean.valueOf(moduleConfiguration.isDownloaded());
                break;
            case LOAD:
                value = (V) Boolean.valueOf(moduleConfiguration.isLoad());
                break;
            case TRUSTED:
                value = (V) Boolean.valueOf(moduleConfiguration.isTrusted());
                break;
        }
        return value;
    }

    @Override
    protected void setValue(OModuleConfiguration moduleConfiguration, OModuleConfigurationField critery, V value) {
        switch (critery) {
            case GROUP:
                moduleConfiguration.getArtifact().setGroupId((String) value);
                break;
            case ARTIFACT:
                moduleConfiguration.getArtifact().setArtifactId((String) value);
                break;
            case VERSION:
                moduleConfiguration.getArtifact().setVersion((String) value);
                break;
            case REPOSITORY:
                moduleConfiguration.getArtifact().setRepository((String) value);
                break;
            case DESCRIPTION:
                moduleConfiguration.getArtifact().setDescription((String) value);
                break;
            case LOAD:
                moduleConfiguration.setLoad((Boolean) value);
                break;
            case TRUSTED:
                moduleConfiguration.setTrusted((Boolean) value);
                break;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Component resolveComponent(String id, DisplayMode mode, OModuleConfigurationField critery) {
        Component result = null;
        IModel<V> model = getModel();
        if (DisplayMode.EDIT.equals(mode)) {
            switch (critery) {
                case GROUP:
                    result = new TextField<>(id, model);
                    break;
                case ARTIFACT:
                    result = new TextField<>(id, model);
                    break;
                case VERSION:
                    result = new TextField<>(id, model);
                    break;
                case REPOSITORY:
                    result = new TextField<>(id, model);
                    break;
                case DESCRIPTION:
                    result = new TextArea<>(id, model);
                    break;
                case DOWNLOADED:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
                case LOAD:
                    result = new BooleanEditPanel(id, (IModel<Boolean>) model);
                    break;
                case TRUSTED:
                    result = new BooleanEditPanel(id, (IModel<Boolean>) model);
                    break;
            }
        } else {
            switch (critery) {
                case GROUP:
                    result = new Label(id, model);
                    break;
                case ARTIFACT:
                    result = new Label(id, model);
                    break;
                case VERSION:
                    result = new Label(id, model);
                    break;
                case REPOSITORY:
                    result = new Label(id, model);
                    break;
                case DESCRIPTION:
                    result = new MultiLineLabel(id, model);
                    break;
                case DOWNLOADED:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
                case LOAD:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
                case TRUSTED:
                    result = new BooleanViewPanel(id, (IModel<Boolean>) model);
                    break;
            }
        }
        return result;
    }

    @Override
    protected IModel<String> newLabelModel() {
        IModel<String> label = Model.of("");
        switch (getPropertyObject()) {
            case GROUP:
                label = new ResourceModel(GROUP);
                break;
            case ARTIFACT:
                label = new ResourceModel(ARTIFACT);
                break;
            case VERSION:
                label = new ResourceModel(VERSION);
                break;
            case REPOSITORY:
                label = new ResourceModel(REPOSITORY);
                break;
            case DESCRIPTION:
                label = new ResourceModel(DESCRIPTION);
                break;
            case FILE:
                label = new ResourceModel(FILE);
                break;
            case LOAD:
                label = new ResourceModel(LOAD);
                break;
            case TRUSTED:
                label = new ResourceModel(TRUSTED);
                break;
        }
        return label;
    }
}
