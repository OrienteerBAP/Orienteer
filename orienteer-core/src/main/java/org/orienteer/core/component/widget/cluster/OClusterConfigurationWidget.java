package org.orienteer.core.component.widget.cluster;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OCluster;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.EditSchemaCommand;
import org.orienteer.core.component.command.SaveSchemaCommand;
import org.orienteer.core.component.meta.OClusterMetaPanel;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.widget.AbstractModeAwareWidget;
import org.orienteer.core.widget.Widget;

/**
 * Widget to show and modify {@link OCluster} configuration
 */
@Widget(id="cluster-configuration", domain="cluster", tab="configuration", autoEnable=true)
public class OClusterConfigurationWidget extends AbstractModeAwareWidget<OCluster> {

	private OrienteerStructureTable<OCluster, String> structureTable;

	public OClusterConfigurationWidget(String id, IModel<OCluster> model,
                                       IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		Form<OCluster> form = new Form<OCluster>("form");
		structureTable  = new OrienteerStructureTable<OCluster, String>("attributes", getModel(), OClusterMetaPanel.OCLUSTER_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OClusterMetaPanel<Object>(id, getModeModel(), this.getModel(), rowModel);
			}
			
		};
		structureTable.addCommand(new EditSchemaCommand<OCluster>(structureTable, getModeModel()));
		structureTable.addCommand(new SaveSchemaCommand<OCluster>(structureTable, getModeModel()));
		
		form.add(structureTable);
		add(form);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("cluster.configuration");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
