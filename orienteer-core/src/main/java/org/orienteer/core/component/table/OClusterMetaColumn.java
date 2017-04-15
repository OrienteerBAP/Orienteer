package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.storage.OCluster;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OClusterMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * {@link AbstractModeMetaColumn} for {@link OCluster}s
 */
public class OClusterMetaColumn extends AbstractModeMetaColumn<OCluster, DisplayMode, String, String>{

    public OClusterMetaColumn(String critery, IModel<DisplayMode> modeModel)
    {
        this(critery, critery, modeModel);
    }

    public OClusterMetaColumn(String sortProperty, String criteryModel, IModel<DisplayMode> modeModel) {
        super(sortProperty, Model.of(criteryModel), modeModel);
    }

    @Override
    protected <V> AbstractMetaPanel<OCluster, String, V> newMetaPanel(String componentId, IModel<String> criteryModel, IModel<OCluster> rowModel) {
        return new OClusterMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new SimpleNamingModel<String>("cluster."+ getCriteryModel().getObject().toLowerCase());
    }

}
