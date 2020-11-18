package org.orienteer.core.component.widget.document.security;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractDeleteCommand;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.command.AbstractSaveCommand;
import org.orienteer.core.component.property.BooleanEditPanel;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.behavior.DisableIfDocumentNotSavedBehavior;
import ru.ydn.wicket.wicketorientdb.behavior.SecurityBehavior;
import ru.ydn.wicket.wicketorientdb.model.EnumNamingModel;
import ru.ydn.wicket.wicketorientdb.model.JavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.*;

/**
 * Widget to configure security on ORole
 */
@Widget(id="role-security", domain="document", tab="security", order=30, autoEnable=true, selector="ORole")
public class ORoleSecurityWidget extends AbstractWidget<ODocument> {
	
	private class SecurityRightsColumn extends AbstractColumn<String, String>
	{
		private final OrientPermission permission;
		public SecurityRightsColumn(OrientPermission permission)
		{
			super(new EnumNamingModel<OrientPermission>(permission));
			this.permission = permission;
		}

		@Override
		public void populateItem(Item<ICellPopulator<String>> cellItem,
				String componentId, IModel<String> rowModel) {
			cellItem.add(new BooleanEditPanel(componentId, getSecurityRightsModel(rowModel)));
		}
		
		protected IModel<Boolean> getSecurityRightsModel(final IModel<String> rowModel)
		{
			return new AbstractCheckBoxModel() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void unselect() {
					ORole oRole = roleModel.getObject();
					oRole.revoke(rowModel.getObject(), permission.getPermissionFlag());
					oRole.save();
				}
				
				@SuppressWarnings("deprecation")
				@Override
				public void select() {
					ORole oRole = roleModel.getObject();
					oRole.grant(rowModel.getObject(), permission.getPermissionFlag());
					oRole.save();
				}
				
				@SuppressWarnings("deprecation")
				@Override
				public boolean isSelected() {
					ORole oRole = roleModel.getObject();
					return oRole.allow(rowModel.getObject(), permission.getPermissionFlag());
				}
				
			};
		}
	}
	
	private IModel<ORole> roleModel = new LoadableDetachableModel<ORole>() {

		@Override
		protected ORole load() {
			return getDatabaseSession().getMetadata().getSecurity().getRole(ORoleSecurityWidget.this.getModelObject());
		}
	};
	
	public ORoleSecurityWidget(String id, IModel<ODocument> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

		List<IColumn<String, String>> sColumns = new ArrayList<IColumn<String, String>>();
		sColumns.add(new CheckBoxColumn<String, String, String>(Converter.<String>identity()));
		sColumns.add(new PropertyColumn<String, String>(new ResourceModel("orule"), ""));
		sColumns.add(new SecurityRightsColumn(OrientPermission.EXECUTE));
		sColumns.add(new SecurityRightsColumn(OrientPermission.CREATE));
		sColumns.add(new SecurityRightsColumn(OrientPermission.READ));
		sColumns.add(new SecurityRightsColumn(OrientPermission.UPDATE));
		sColumns.add(new SecurityRightsColumn(OrientPermission.DELETE));
		
		JavaSortableDataProvider<String, String> provider = new JavaSortableDataProvider<String, String>(new PropertyModel<Collection<String>>(this, "ruleSet"));
		GenericTablePanel<String> tablePanel = new GenericTablePanel<String>("tablePanel", sColumns, provider ,20);
		tablePanel.add(new SecurityBehavior(model, Component.ENABLE, OrientPermission.UPDATE));
		OrienteerDataTable<String, String> sTable = tablePanel.getDataTable();
		sTable.addCommand(new AbstractSaveCommand<String>(sTable, null));

		sTable.addCommand(new AbstractModalWindowCommand<String>(sTable.newCommandId(), "command.add") {
			{
				setIcon(FAIconType.plus_circle);
				setBootstrapType(BootstrapType.SUCCESS);
			}

			@Override
			protected void initializeContent(ModalWindow modal) {
				modal.setTitle(new ResourceModel("command.add.widget"));
				modal.setContent(new AddRuleDialog(modal) {

					@Override
					protected void onRuleEntered(AjaxRequestTarget target,
					                             ORule.ResourceGeneric resource, String specific) {
						String rule;
						if(resource==null) rule=specific;
						else {
							rule = resource.getLegacyName();
							if(specific!=null) rule+="."+specific;
						}
						ODocument doc = ORoleSecurityWidget.this.getModelObject();
						Map<String, Byte> rules = doc.field("rules");
						if(rules==null) {
							rules = new HashMap<String, Byte>();
							doc.field("rules", rules);
						}
						rules.put(rule, Byte.valueOf((byte)0));
						doc.save();
						modal.close(target);
						sendActionPerformed();
					}

				});
				modal.setAutoSize(true);
				modal.setMinimalWidth(300);
			}
		});
		sTable.addCommand(new AbstractDeleteCommand<String>(sTable) {

			@Override
			protected void perfromSingleAction(AjaxRequestTarget target, String object) {
				ODocument role = ORoleSecurityWidget.this.getModelObject();
				Map<String, Object> rules = role.field("rules");
				rules.remove(object);
				role.save();
			}
		});
		add(tablePanel);
		add(DisableIfDocumentNotSavedBehavior.INSTANCE, UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Collection<String> getRuleSet() {
		ODocument role = getModelObject();
		Map<String, Object> rules = role.field("rules");
		return rules!=null?rules.keySet():Collections.EMPTY_SET;
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
	
	@Override
	public void detachModels() {
		super.detachModels();
		roleModel.detach();
	}

}
