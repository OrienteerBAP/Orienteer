package org.orienteer.core.component.command.modal;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.OClassSearchPanel;
import org.orienteer.core.component.command.AbstractCheckBoxEnabledCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;

import java.util.List;

/**
 * Modal window for selecting an {@link ODocument}
 */
public abstract class SelectDialogPanel extends GenericPanel<String> {

	protected ModalWindow modal;

	public SelectDialogPanel(String id, final ModalWindow modal, IModel<OClass> initialClass, boolean isMultiValue) {
		super(id, Model.of(""));
		this.modal = modal;
		this.modal.setMinimalHeight(400);

		add(createSearchPanel("searchPanel", initialClass, isMultiValue));
	}

	private OClassSearchPanel createSearchPanel(String id, IModel<OClass> initialClass, boolean isMultiValue) {
		OClassSearchPanel searchPanel = new OClassSearchPanel(id, Model.of()) {
			@Override
			protected void onPrepareResults(OrienteerDataTable<ODocument, String> table, OClass oClass, IModel<DisplayMode> modeModel) {
				table.addCommand(createSelectCommand(table));
				if (isMultiValue) {
					table.addCommand(createSelectAndSearchCommand(table, this));
				}
			}

			@Override
			protected List<IColumn<ODocument, String>> getColumnsFor(OClass oClass, IModel<DisplayMode> modeModel) {
				return oClassIntrospector.getColumnsFor(oClass, true, modeModel);
			}
		};
		searchPanel.setSelectedClassModel(initialClass);
		return searchPanel;
	}

	private Command<ODocument> createSelectCommand(OrienteerDataTable<ODocument, String> table) {
		return new AbstractCheckBoxEnabledCommand<ODocument>(new ResourceModel("command.select"), table) {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				setBootstrapType(BootstrapType.SUCCESS);
				setIcon(FAIconType.hand_o_right);
				setAutoNotify(false);
			}

			@Override
			protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
				if(onSelect(target, objects, false)) modal.close(target);
			}
		};
	}

	private Command<ODocument> createSelectAndSearchCommand(OrienteerDataTable<ODocument, String> table, OClassSearchPanel searchPanel) {
		return new AbstractCheckBoxEnabledCommand<ODocument>(new ResourceModel("command.selectAndSearchMode"), table) {
			@Override
			protected void onInitialize() {
				super.onInitialize();
				setBootstrapType(BootstrapType.SUCCESS);
				setIcon(FAIconType.hand_o_right);
				setAutoNotify(false);
			}

			@Override
			protected void performMultiAction(AjaxRequestTarget target, List<ODocument> objects) {
				if (onSelect(target, objects, true)) {
					TextField<String> field = searchPanel.getQueryField();
					resetSelection();
					target.add(getTable());
					target.focusComponent(field);
					target.appendJavaScript(String.format("$('#%s').select()", field.getMarkupId()));
				}
			}
		};
	}

	protected abstract boolean onSelect(AjaxRequestTarget target, List<ODocument> objects, boolean selectMore);
}
