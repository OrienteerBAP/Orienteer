package org.orienteer.core.component.command;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.DataTableCommandsToolbar;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.schema.OIndexPage;
import org.orienteer.core.web.schema.OIndexPage;

import ru.ydn.wicket.wicketorientdb.model.OIndexModel;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResources;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * {@link Command} to create an {@link OIndex} for a selected set of properties
 */
@RequiredOrientResources({
	@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.CREATE),
	@RequiredOrientResource(value=OSecurityHelper.CLUSTER, specific="internal", permissions=OrientPermission.CREATE)
})
public class CreateOIndexFromOPropertiesCommand extends
		AbstractCheckBoxEnabledCommand<OProperty>
{
	private IModel<OClass> classModel;

	public CreateOIndexFromOPropertiesCommand(OrienteerDataTable<OProperty, ?> table, IModel<OClass> classModel)
	{
		super(new ResourceModel("command.create.index"), table);
		this.classModel = classModel;
	}
	
	

	@Override
	protected void onInstantiation() {
		super.onInstantiation();
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
			setResponsePage(new OIndexPage(new OIndexModel(OIndexPrototyper.newPrototype(oClass.getName(), fields))).setModeObject(DisplayMode.EDIT));
		}
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(classModel!=null) classModel.detach();
	}
	
	

}
