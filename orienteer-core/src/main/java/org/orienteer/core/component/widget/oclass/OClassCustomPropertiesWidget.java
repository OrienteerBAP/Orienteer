package org.orienteer.core.component.widget.oclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
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
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.component.BootstrapSize;
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
import org.orienteer.core.component.table.AbstractModeMetaColumn;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IWidgetType;
import org.orienteer.core.widget.Widget;
import org.orienteer.core.widget.command.modal.AddWidgetDialog;

import ru.ydn.wicket.wicketorientdb.behavior.DisableIfPrototypeBehavior;
import ru.ydn.wicket.wicketorientdb.components.RootForm;
import ru.ydn.wicket.wicketorientdb.components.TransactionlessForm;
import ru.ydn.wicket.wicketorientdb.model.OClassCustomModel;

import com.google.common.base.Enums;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Widget to show custom properties of an {@link OClass}
 */
@Widget(id="class-custom", domain="class", tab="configuration", order=30, autoEnable=true)
public class OClassCustomPropertiesWidget extends AbstractModeAwareWidget<OClass> {
	
	private static final Predicate<String> IS_NOT_SYSTEM = new Predicate<String>() {

		@Override
		public boolean apply(String input) {
			return CustomAttributes.fromString(input)==null;
		}
	};

	private OrienteerStructureTable<OClass, String> structureTable;
	private boolean hideSystem = true;
	
	public OClassCustomPropertiesWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		
		Form<OClass> form = new TransactionlessForm<OClass>("form", model);
		structureTable = new OrienteerStructureTable<OClass, String>("table", model, new PropertyModel<List<? extends String>>(this, "custom")) {

			@Override
			protected Component getValueComponent(String id,
					IModel<String> rowModel) {
				return new AbstractModeMetaPanel<OClass, DisplayMode, String, String>(id, getModeModel(), OClassCustomPropertiesWidget.this.getModel(), rowModel) {

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
						return new OClassCustomModel(getEntityModel(), getPropertyModel());
					}
				};
				
			}
		};
		structureTable.addCommand(new AbstractModalWindowCommand<OClass>(new ResourceModel("command.add.custom"), structureTable) {

			{
				setIcon(FAIconType.plus_circle);
				setBootstrapType(BootstrapType.SUCCESS);
			}
			@Override
			protected void initializeContent(final ModalWindow modal) {
				modal.setTitle(new ResourceModel("command.add.custom"));
				modal.setContent(new Fragment(modal.getContentId(), "addDialog", OClassCustomPropertiesWidget.this) {
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
								OClass oClass = OClassCustomPropertiesWidget.this.getModelObject();
								boolean isTransactionActive = getDatabase().getTransaction().isActive();
								if(isTransactionActive) getDatabase().commit();
								try {
									oClass.setCustom(key, value);
								} finally {
									if(isTransactionActive) getDatabase().begin();
								}
								modal.close(target);
								target.add(OClassCustomPropertiesWidget.this);
							}
						});
						add(form);
					}
				});
				modal.setAutoSize(true);
				modal.setMinimalWidth(300);
			}
		});
		structureTable.addCommand(new EditSchemaCommand<OClass>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OClass>(structureTable, getModeModel()));
		structureTable.addCommand(new AjaxCommand<OClass>(new StringResourceModel("command.showhide.system.${}", new PropertyModel<Boolean>(this, "hideSystem")), structureTable) {

			{
				setBootstrapType(BootstrapType.WARNING);
			}
			@Override
			public void onClick(AjaxRequestTarget target) {
				hideSystem=!hideSystem;
				target.add(OClassCustomPropertiesWidget.this);
			}
		});
		form.add(structureTable);
		add(form);
		add(DisableIfPrototypeBehavior.INSTANCE);
	}
	
	public boolean isHideSystem() {
		return hideSystem;
	}

	public void setHideSystem(boolean hideSystem) {
		this.hideSystem = hideSystem;
	}

	public List<String> getCustom() {
		OClass oClass = getModelObject();
		Collection<String> keys = oClass.getCustomKeys();
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
	protected IModel<String> getTitleModel() {
		return new ResourceModel("class.custom");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
