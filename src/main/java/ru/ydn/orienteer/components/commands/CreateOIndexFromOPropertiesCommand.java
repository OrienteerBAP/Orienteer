package ru.ydn.orienteer.components.commands;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.table.DataTableCommandsToolbar;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.web.schema.OIndexPage;
import ru.ydn.wicket.wicketorientdb.model.OIndexModel;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;

@RequiredOrientResource(value = ODatabaseSecurityResources.SCHEMA, permissions=OrientPermission.CREATE)
public class CreateOIndexFromOPropertiesCommand extends
		AbstractCheckBoxEnabledCommand<OProperty>
{
	private IModel<OClass> classModel;
	public CreateOIndexFromOPropertiesCommand(DataTableCommandsToolbar<OProperty> toolbar, IModel<OClass> classModel)
	{
		super(new ResourceModel("command.create.index"), toolbar);
		this.classModel = classModel;
	}

	public CreateOIndexFromOPropertiesCommand(OrienteerDataTable<OProperty, ?> table, IModel<OClass> classModel)
	{
		super(new ResourceModel("command.create.index"), table);
		this.classModel = classModel;
	}
	
	

	@Override
	protected void onInitialize() {
		super.onInitialize();
		setIcon(FAIconType.plus);
		setBootstrapType(BootstrapType.SUCCESS);
	}

	@Override
	protected void performMultiAction(AjaxRequestTarget target, List<OProperty> objects) {
		if(objects==null || objects.size()==0)
		{
			error(OrienteerWebApplication.get().getResourceSettings().getLocalizer().getString("errors.checkbox.empty", this));
			return;
		}
		else
		{
			List<String> fields = Lists.newArrayList(Lists.transform(objects, new Function<OProperty, String>() {

				@Override
				public String apply(OProperty input) {
					return input.getName();
				}
			}));
			OClass oClass = classModel!=null?classModel.getObject():null;
			if(oClass==null) oClass = objects.get(0).getOwnerClass();
			setResponsePage(new OIndexPage(new OIndexModel(OIndexPrototyper.newPrototype(oClass.getName(), fields))).setDisplayMode(DisplayMode.EDIT));
		}
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(classModel!=null) classModel.detach();
	}
	
	

}
