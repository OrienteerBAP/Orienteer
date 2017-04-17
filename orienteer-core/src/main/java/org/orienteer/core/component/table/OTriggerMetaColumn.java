package org.orienteer.core.component.table;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.meta.OTriggerMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.model.OTriggerModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * {@link AbstractModeMetaColumn} for {@link OTriggerModel}s
 */
public class OTriggerMetaColumn extends AbstractModeMetaColumn<OTriggerModel, DisplayMode, String, String>
{

    public OTriggerMetaColumn(String criteryModel, IModel<DisplayMode> modeModel) {
        this(criteryModel, criteryModel, modeModel);
    }

    public OTriggerMetaColumn(String sortParam, String critery, IModel<DisplayMode> modeModel)
    {
        super(sortParam, Model.of(critery), modeModel);
    }

    @Override
    protected <V> AbstractMetaPanel<OTriggerModel, String, V> newMetaPanel(String componentId, IModel<String> criteryModel, IModel<OTriggerModel> rowModel) {
        return new OTriggerMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
    }

    @Override
    protected IModel<String> newLabelModel() {
        return new SimpleNamingModel<String>("trigger", getCriteryModel());
    }
}
