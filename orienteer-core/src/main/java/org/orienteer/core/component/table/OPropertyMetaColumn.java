package org.orienteer.core.component.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.meta.AbstractMetaPanel;
import org.orienteer.core.component.meta.OIndexMetaPanel;
import org.orienteer.core.component.meta.OPropertyMetaPanel;
import org.orienteer.core.component.property.DisplayMode;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link AbstractModeMetaColumn} for {@link OProperty}es
 */
public class OPropertyMetaColumn  extends AbstractModeMetaColumn<OProperty, DisplayMode, String, String>
{
	public OPropertyMetaColumn(CustomAttribute custom, IModel<DisplayMode> modeModel)
	{
		this(custom.getName(), modeModel);
	}
	
	public OPropertyMetaColumn(String critery, IModel<DisplayMode> modeModel)
	{
		this(critery, critery, modeModel);
	}
	
	public OPropertyMetaColumn(String sortParam, String critery, IModel<DisplayMode> modeModel)
	{
		super(sortParam, Model.of(critery), modeModel);
	}

	@Override
	protected <V> AbstractMetaPanel<OProperty, String, V> newMetaPanel(
			String componentId, IModel<String> criteryModel,
			IModel<OProperty> rowModel) {
		return new OPropertyMetaPanel<V>(componentId, getModeModel(), rowModel, criteryModel);
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("property", getCriteryModel());
	}
}
