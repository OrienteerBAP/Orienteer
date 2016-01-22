package org.orienteer.core.component.widget.oclass;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractSaveCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.property.LinkViewPanel;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.behavior.DisableIfPrototypeBehavior;
import ru.ydn.wicket.wicketorientdb.model.EnumNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show and modify security settings of a {@link OClass}
 */
@Widget(id="class-security", domain="class", tab="security", order=30, autoEnable=true)
public class OClassSecurityWidget extends AbstractWidget<OClass> {
	
	private class SecurityRightsColumn extends AbstractColumn<ORole, String>
	{
		private final OrientPermission permission;
		public SecurityRightsColumn(OrientPermission permission)
		{
			super(new EnumNamingModel<OrientPermission>(permission));
			this.permission = permission;
		}

		@Override
		public void populateItem(Item<ICellPopulator<ORole>> cellItem,
				String componentId, IModel<ORole> rowModel) {
			cellItem.add(new BooleanEditPanel(componentId, getSecurityRightsModel(rowModel)));
		}
		
		protected IModel<Boolean> getSecurityRightsModel(final IModel<ORole> rowModel)
		{
			return new AbstractCheckBoxModel() {
				
				@Override
				public void unselect() {
					ORole oRole = rowModel.getObject();
					oRole.revoke(ORule.ResourceGeneric.CLASS, getSecurityResourceSpecific(), permission.getPermissionFlag());
                    oRole.revoke(ORule.ResourceGeneric.CLUSTER, getSecurityResourceSpecific(), permission.getPermissionFlag());
					oRole.save();
				}
				
				@Override
				public void select() {
					ORole oRole = rowModel.getObject();
					oRole.grant(ORule.ResourceGeneric.CLASS, getSecurityResourceSpecific(), permission.getPermissionFlag());
                    oRole.grant(ORule.ResourceGeneric.CLUSTER, getSecurityResourceSpecific(), permission.getPermissionFlag());
					oRole.save();
				}
				
				@Override
				public boolean isSelected() {
					ORole oRole = rowModel.getObject();
					return oRole.allow(ORule.ResourceGeneric.CLASS, getSecurityResourceSpecific(), permission.getPermissionFlag());
				}
				
				private String getSecurityResourceSpecific()
				{
					return OClassSecurityWidget.this.getModelObject().getName();
				}
			};
		}
	}
	
	public OClassSecurityWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<OClass> sForm = new Form<OClass>("form");
		OSecurityHelper.secureComponent(sForm, OSecurityHelper.requireOClass("ORole", Component.ENABLE, OrientPermission.UPDATE));
		
		List<IColumn<ORole, String>> sColumns = new ArrayList<IColumn<ORole,String>>();
		OClass oRoleClass = OrientDbWebSession.get().getDatabase().getMetadata().getSchema().getClass("ORole");
		sColumns.add(new AbstractColumn<ORole, String>(new OClassNamingModel(oRoleClass), "name") {

			@Override
			public void populateItem(Item<ICellPopulator<ORole>> cellItem,
					String componentId, IModel<ORole> rowModel) {
				cellItem.add(new LinkViewPanel(componentId, new PropertyModel<ODocument>(rowModel, "document")));
			}
		});
		sColumns.add(new SecurityRightsColumn(OrientPermission.CREATE));
		sColumns.add(new SecurityRightsColumn(OrientPermission.READ));
		sColumns.add(new SecurityRightsColumn(OrientPermission.UPDATE));
		sColumns.add(new SecurityRightsColumn(OrientPermission.DELETE));
		
		OQueryDataProvider<ORole> sProvider = new OQueryDataProvider<ORole>("select from ORole", ORole.class);
		sProvider.setSort("name", SortOrder.ASCENDING);
		OrienteerDataTable<ORole, String> sTable = new OrienteerDataTable<ORole, String>("security", sColumns, sProvider ,20);
		Command<ORole> saveCommand = new AbstractSaveCommand<ORole>(sTable, null);
		sTable.addCommand(saveCommand);
		sTable.setCaptionModel(new ResourceModel("class.security"));
		sForm.add(sTable);
		add(sForm);
		add(DisableIfPrototypeBehavior.INSTANCE, UpdateOnActionPerformedEventBehavior.INSTANCE);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.shield);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("class.security");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
