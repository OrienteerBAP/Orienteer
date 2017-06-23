package org.orienteer.core.component.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.boot.loader.util.artifact.OArtifact;
import org.orienteer.core.boot.loader.util.artifact.OArtifactField;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OArtifactMetaPanel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * Column for {@link OArtifact}
 */
public class OArtifactColumn extends AbstractModeMetaColumn<OArtifact, DisplayMode, OArtifactField, String> {

    public OArtifactColumn(IModel<OArtifactField> criteryModel,
                                      IModel<DisplayMode> modeModel) {
        super(criteryModel, modeModel);
    }

    @Override
    protected <V> AbstractMetaPanel<OArtifact, OArtifactField, V> newMetaPanel(
            String id, IModel<OArtifactField> criteryModel, IModel<OArtifact> rowModel) {
        return new OArtifactMetaPanel<V>(id, getModeModel(), rowModel, criteryModel);
    }

    @Override
    protected IModel<String> newLabelModel() {
        OArtifactField critery = getCriteryModel().getObject();
        IModel<String> label = Model.of("No name");
        switch (critery) {
            case GROUP:
                label = new ResourceModel("widget.artifacts.group");
                break;
            case ARTIFACT:
                label = new ResourceModel("widget.artifacts.artifact");
                break;
            case VERSION:
                label = new ResourceModel("widget.artifacts.version");
                break;
            case DESCRIPTION:
                label = new ResourceModel("widget.artifacts.description");
                break;
            case DOWNLOADED:
                label = new ResourceModel("widget.artifacts.downloaded");
                break;
            case LOAD:
                label = new ResourceModel("widget.artifacts.load");
                break;
            case TRUSTED:
                label = new ResourceModel("widget.artifacts.trusted");
                break;
        }
        return label;
    }
}
