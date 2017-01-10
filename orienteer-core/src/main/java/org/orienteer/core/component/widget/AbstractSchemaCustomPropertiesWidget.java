package org.orienteer.core.component.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.AbstractModeMetaPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.component.widget.oclass.OClassCustomPropertiesWidget;
import org.orienteer.core.widget.AbstractModeAwareWidget;

import ru.ydn.wicket.wicketorientdb.behavior.DisableIfPrototypeBehavior;
import ru.ydn.wicket.wicketorientdb.components.RootForm;
import ru.ydn.wicket.wicketorientdb.components.TransactionlessForm;
import ru.ydn.wicket.wicketorientdb.model.OClassCustomModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Abstract widget for custom properties modification
 *
 * @param <T> the type of schema object
 */
public abstract class AbstractSchemaCustomPropertiesWidget<T> extends AbstractModeAwareWidget<T> {
	
	private static final Predicate<String> IS_NOT_SYSTEM = new Predicate<String>() {

		@Override
		public boolean apply(String input) {
			return CustomAttribute.getIfExists(input)==null;
		}
	};

	private OrienteerStructureTable<T, String> structureTable;
	private boolean hideSystem = true;
	
	public AbstractSchemaCustomPropertiesWidget(String id, IModel<T> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		Form<T> form = new TransactionlessForm<T>("form", model);
		structureTable = new OrienteerStructureTable<T, String>("table", model, new PropertyModel<List<String>>(this, "custom")) {

			@Override
			protected Component getValueComponent(String id,
					IModel<String> rowModel) {
				return new AbstractModeMetaPanel<T, DisplayMode, String, String>(id, getModeModel(), AbstractSchemaCustomPropertiesWidget.this.getModel(), rowModel) {

					@Override
					protected Component resolveComponent(String id,
							DisplayMode mode, String critery) {
						if(DisplayMode.EDIT.equals(mode)) {
							return new TextArea<String>(id, getValueModel());
						} else {
							return new MultiLineLabel(id, getValueModel());
						}
					}

					@Override
					protected IModel<String> newLabelModel() {
						return getPropertyModel();
					}

					@Override
					protected IModel<String> resolveValueModel() {
						return createCustomModel(getEntityModel(), getPropertyModel());
					}
				};
				
			}
		};
		structureTable.addCommand(new AbstractModalWindowCommand<T>(new ResourceModel("command.add.custom"), structureTable) {

			{
				setIcon(FAIconType.plus_circle);
				setBootstrapType(BootstrapType.SUCCESS);
				OSecurityHelper.secureComponent(this, OSecurityHelper.requireResource(ResourceGeneric.SCHEMA, null, OrientPermission.UPDATE));
				OSecurityHelper.secureComponent(this, OSecurityHelper.requireResource(ResourceGeneric.CLUSTER, "internal", OrientPermission.UPDATE));
			}
			@Override
			protected void initializeContent(final ModalWindow modal) {
				modal.setTitle(new ResourceModel("command.add.custom"));
				modal.setContent(new Fragment(modal.getContentId(), "addDialog", AbstractSchemaCustomPropertiesWidget.this) {
					{
						final IModel<String> keyModel = Model.<String>of();
						final IModel<String> valueModel = Model.<String>of();
						Form<Object> form = new RootForm<Object>("form");
						form.add(new TextField<String>("key", keyModel, String.class).setRequired(true));
						form.add(new TextArea<String>("value", valueModel).setType(String.class).setRequired(true));
						form.add(new AjaxButton("submit", form) {
							@Override
							protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
								String key = keyModel.getObject();
								String value = valueModel.getObject();
								boolean isTransactionActive = getDatabase().getTransaction().isActive();
								if(isTransactionActive) getDatabase().commit();
								try {
									addCustom(key, value);
								} finally {
									if(isTransactionActive) getDatabase().begin();
								}
								modal.close(target);
								target.add(AbstractSchemaCustomPropertiesWidget.this);
							}
						});
						add(form);
					}
				});
				modal.setAutoSize(true);
				modal.setMinimalWidth(300);
			}
		});
		structureTable.addCommand(new EditSchemaCommand<T>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<T>(structureTable, getModeModel()));
		structureTable.addCommand(new AjaxCommand<T>(new StringResourceModel("command.showhide.system.${}", new PropertyModel<Boolean>(this, "hideSystem")), structureTable) {

			{
				setBootstrapType(BootstrapType.WARNING);
			}
			@Override
			public void onClick(AjaxRequestTarget target) {
				hideSystem=!hideSystem;
				target.add(AbstractSchemaCustomPropertiesWidget.this);
			}
		});
		form.add(structureTable);
		add(form);
		add(DisableIfPrototypeBehavior.INSTANCE, UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
	}
	
	public boolean isHideSystem() {
		return hideSystem;
	}

	public void setHideSystem(boolean hideSystem) {
		this.hideSystem = hideSystem;
	}
	
	protected abstract Collection<String> getOriginalCustomKeys();
	protected abstract void addCustom(String key, String value);
	protected abstract IModel<String> createCustomModel(IModel<T> schemaObjectModel, IModel<String> customPropertyModel);

	public List<String> getCustom() {
		Collection<String> keys = getOriginalCustomKeys();
		if(keys==null || keys.isEmpty()) return Collections.EMPTY_LIST;
		if(hideSystem) {
			keys = Collections2.filter(keys, IS_NOT_SYSTEM);
		}
		List<String> ret = new ArrayList<String>(keys);
		Collections.sort(ret);
		return ret;
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("class.custom");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}
}

