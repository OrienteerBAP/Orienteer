package org.orienteer.core.component.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OModule;
import org.orienteer.core.boot.loader.util.artifact.OModuleField;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OModuleMetaPanel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * @author Vitaliy Gonchar
 * Column for {@link OModule}
 */
public class OModuleColumn extends AbstractModeMetaColumn<OModule, DisplayMode, OModuleField, String> {
    public OModuleColumn(IModel<OModuleField> criteryModel, IModel<DisplayMode> modeModel) {
        super(criteryModel, modeModel);
    }

    @Override
    protected <V> AbstractMetaPanel<OModule, OModuleField, V> newMetaPanel(
            String id, IModel<OModuleField> criteryModel, IModel<OModule> rowModel) {
        return new OModuleMetaPanel<V>(id, getModeModel(), rowModel, criteryModel);
    }

    @Override
    protected IModel<String> newLabelModel() {
        OModuleField critery = getCriteryModel().getObject();
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
