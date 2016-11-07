package org.orienteer.core.component.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.property.DisplayMode;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link AbstractModeMetaColumn} for {@link OClass}es
 */
public class OClassMetaColumn  extends AbstractModeMetaColumn<OClass, DisplayMode, String, String>
{
	public OClassMetaColumn(CustomAttribute custom, IModel<DisplayMode> modeModel)
	{
		this(custom.getName(), modeModel);
	}
	
	public OClassMetaColumn(String critery, IModel<DisplayMode> modeModel)
	{
		this(critery, critery, modeModel);
	}
	
	public OClassMetaColumn(String sortParam, String critery, IModel<DisplayMode> modeModel)
	{
		super(sortParam, Model.of(critery), modeModel);
	}

	@Override
	protected <V> AbstractMetaPanel<OClass, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OClass> rowModel) {
		return new OClassMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("class", getCriteryModel());
	}

}
