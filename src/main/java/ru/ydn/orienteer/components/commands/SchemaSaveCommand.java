package ru.ydn.orienteer.components.commands;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class SchemaSaveCommand<T> extends SimpleSaveCommand<T>
{

	public SchemaSaveCommand(OrienteerStructureTable<T, ?> structureTable,
			IModel<DisplayMode> displayModeModel)
	{
		super(structureTable, displayModeModel);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		try
		{
			super.onClick(target);
		}
		finally
		{
			getDatabase().getMetadata().reload();
		}
	}
	
	

}
