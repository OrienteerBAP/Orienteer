package ru.ydn.orienteer.web.schema;

import java.util.Arrays;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Enums;
import com.google.common.base.Functions;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.ATTRIBUTES;
import com.orientechnologies.orient.core.metadata.schema.OClassImpl;

import ru.ydn.orienteer.components.StructureTable;
import ru.ydn.orienteer.schema.SchemaHelper;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.model.FunctionModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;

@MountPath("/class/${className}")
public class ViewClassPage extends OrienteerBasePage<OClass> {
	private static 	OClass.ATTRIBUTES[] ATTRS_TO_VIEW = {OClass.ATTRIBUTES.NAME, OClass.ATTRIBUTES.SHORTNAME, OClass.ATTRIBUTES.SUPERCLASS, OClass.ATTRIBUTES.OVERSIZE, OClass.ATTRIBUTES.STRICTMODE, OClass.ATTRIBUTES.ABSTRACT, OClass.ATTRIBUTES.CLUSTERSELECTION, OClass.ATTRIBUTES.CUSTOM };	
	public ViewClassPage(IModel<OClass> model) {
		super(model);
	}

	public ViewClassPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected IModel<OClass> resolveByPageParameters(
			PageParameters pageParameters) {
		String className = pageParameters.get("className").toOptionalString();
		return Strings.isEmpty(className)?null:new OClassModel(className);
	}

	@Override
	public void initialize() {
		super.initialize();
		add(new StructureTable<OClass.ATTRIBUTES>("properties", Arrays.asList(ATTRS_TO_VIEW)) {

			
			@Override
			protected IModel<?> getLabelModel(IModel<ATTRIBUTES> rowModel) {
				return new FunctionModel<ATTRIBUTES, String>(rowModel, SchemaHelper.BUITIFY_NAME_FUNCTION);
			}

			@Override
			protected Component getValueComponent(String id, final IModel<OClass.ATTRIBUTES> rowModel) {
				return new Label(id, new LoadableDetachableModel<Object>() {

					@Override
					protected Object load() {
						return ViewClassPage.this.getModelObject().get(rowModel.getObject());
					}
				});
			}
		});
	}

	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}
	
}
