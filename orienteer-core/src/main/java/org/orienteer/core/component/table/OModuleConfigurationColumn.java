package org.orienteer.core.component.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfiguration;
import org.orienteer.core.boot.loader.util.artifact.OModuleConfigurationField;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OModuleConfigurationMetaPanel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * @author Vitaliy Gonchar
 * Column for {@link OModuleConfiguration}
 */
public class OModuleConfigurationColumn extends AbstractModeMetaColumn<OModuleConfiguration, DisplayMode, OModuleConfigurationField, String> {

    public OModuleConfigurationColumn(IModel<OModuleConfigurationField> criteryModel,
                                      IModel<DisplayMode> modeModel) {
        super(criteryModel, modeModel);
    }

    @Override
    protected <V> AbstractMetaPanel<OModuleConfiguration, OModuleConfigurationField, V> newMetaPanel(
            String id, IModel<OModuleConfigurationField> criteryModel, IModel<OModuleConfiguration> rowModel) {
        return new OModuleConfigurationMetaPanel<V>(id, getModeModel(), rowModel, criteryModel);
    }

    @Override
    protected IModel<String> newLabelModel() {
        OModuleConfigurationField critery = getCriteryModel().getObject();
        IModel<String> label = Model.of("No name");
        switch (critery) {
            case GROUP:
                label = new ResourceModel("widget.modules.group");
                break;
            case ARTIFACT:
                label = new ResourceModel("widget.modules.artifact");
                break;
            case VERSION:
                label = new ResourceModel("widget.modules.version");
                break;
            case DESCRIPTION:
                label = new ResourceModel("widget.modules.description");
                break;
            case DOWNLOADED:
                label = new ResourceModel("widget.modules.downloaded");
                break;
            case LOAD:
                label = new ResourceModel("widget.modules.load");
                break;
            case TRUSTED:
                label = new ResourceModel("widget.modules.trusted");
                break;
        }
        return label;
    }
}
