package org.orienteer.core.component.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.BooleanViewPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OClassViewPanel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.proto.OPropertyPrototyper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.collate.OCollate;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OCompositeIndexDefinition;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.index.OIndexes;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OSQLEngine;

/**
 * Meta panel for {@link OIndex}
 *
 * @param <V> type of a value
 */
public class OIndexMetaPanel<V> extends AbstractComplexModeMetaPanel<OIndex<?>, DisplayMode, String, V> implements IDisplayModeAware
{
	private static final List<String> INDEX_TYPES;
	private static final List<String> ALGORITHMS = new ArrayList<>(OIndexes.getIndexEngines());
	
	static
	{
		INDEX_TYPES = new ArrayList<String>();
		for (OClass.INDEX_TYPE type : OClass.INDEX_TYPE.values())
		{
			INDEX_TYPES.add(type.name());
		}
	}
	
	public OIndexMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OIndex<?>> entityModel, IModel<String> propertyModel,
			IModel<V> valueModel)
	{
		super(id, modeModel, entityModel, propertyModel, valueModel);
	}

	public OIndexMetaPanel(String id, IModel<DisplayMode> modeModel,
			IModel<OIndex<?>> entityModel, IModel<String> criteryModel)
	{
		super(id, modeModel, entityModel, criteryModel);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected V getValue(OIndex<?> entity, String critery) {
		if(OIndexPrototyper.DEF_COLLATE.equals(critery))
		{
			OIndexDefinition definition = entity.getDefinition();
			if(definition instanceof OCompositeIndexDefinition) return (V)"composite";
			OCollate collate = definition.getCollate();
			return (V)(collate!=null?collate.getName():null);
		}
		else
		{
			return (V) PropertyResolver.getValue(critery, entity);
		}
	}

	@Override
	protected void setValue(OIndex<?> entity, String critery, V value) {
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		db.commit();
		try
		{
			if(OIndexPrototyper.DEF_COLLATE.equals(critery))
			{
				if(value!=null)
				{
					String collate = value.toString();
					entity.getDefinition().setCollate(OSQLEngine.getCollate(collate));
				}
				else
				{
					entity.getDefinition().setCollate(null);
				}
			}
			else
			{
				PropertyResolver.setValue(critery, entity, value, null);
			}
		} finally
		{
			db.begin();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Component resolveComponent(String id, DisplayMode mode,
			String critery) {
		if(DisplayMode.VIEW.equals(mode))
		{
			if(OIndexPrototyper.DEF_CLASS_NAME.equals(critery))
			{
				return new OClassViewPanel(id, new OClassModel((IModel<String>)getModel()));
			}
			else if(OIndexPrototyper.DEF_NULLS_IGNORED.equals(critery))
			{
				return new BooleanViewPanel(id, (IModel<Boolean>)getModel());
			}
			//Default component for view
			return new Label(id, getModel());
		}
		else if(DisplayMode.EDIT.equals(mode))
		{
			boolean isProto = getEntityObject() instanceof IPrototype<?>;
			if(OIndexPrototyper.NAME.equals(critery) && isProto)
			{
				return new TextField<V>(id, getModel()).setType(String.class).setRequired(true);
			}
			else if(OIndexPrototyper.TYPE.equals(critery) && isProto)
			{
				return new DropDownChoice<String>(id, (IModel<String>)getModel(), INDEX_TYPES).setRequired(true);
			}
			else if(OIndexPrototyper.ALGORITHM.equals(critery) && isProto)
			{
				return new DropDownChoice<String>(id, (IModel<String>)getModel(), ALGORITHMS).setNullValid(true);
			}
			else if(OIndexPrototyper.DEF_COLLATE.equals(critery))
			{
				OIndex<?> index = getEntityObject();
				if(!(index.getDefinition() instanceof OCompositeIndexDefinition)
						&& (!isProto || index.getDefinition().getFields().size()==1))
				{
					return new DropDownChoice<String>(id, (IModel<String>)getModel(), Lists.newArrayList(OSQLEngine.getCollateNames())).setNullValid(true);
				}
			}
			else if(OIndexPrototyper.DEF_NULLS_IGNORED.equals(critery))
			{
				return new CheckBox(id, (IModel<Boolean>)getModel());
			}
			else if(OIndexPrototyper.METADATA.equals(critery) && isProto)
			{
				return new TextArea<>(id, getModel()).setType(ODocument.class);
			}
			//Default component for edit is view
			return resolveComponent(id, DisplayMode.VIEW, critery);
		}
		else return null;
	}

	@Override
	protected IModel<String> newLabelModel() {
		return new SimpleNamingModel<String>("index", getPropertyModel());
	}

}
