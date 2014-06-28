package ru.ydn.orienteer.web;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.StructureTable;
import ru.ydn.orienteer.components.properties.MetaPanel;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.model.DocumentNameModel;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.common.thread.OPollerThread;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/doc/#{rid}/#{mode}")
public class DocumentPage extends AbstractDocumentPage {

	private IModel<DisplayMode> displayMode = DisplayMode.VIEW.asModel();
	
	public DocumentPage(IModel<ODocument> model) {
		super(model);
	}

	public DocumentPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) displayMode.setObject(mode);
	}

	@Override
	public void initialize() {
		super.initialize();
		StructureTable<OProperty> properties = new StructureTable<OProperty>("properties", 
				new PropertyModel<List<? extends OProperty>>(getDocumentModel(), "schemaClass.properties()")) {

					@Override
					protected IModel<?> getLabelModel(IModel<OProperty> rowModel) {
						return new PropertyModel<String>(rowModel, "name");
					}

					@Override
					protected Component getValueComponent(String id,
							IModel<OProperty> rowModel) {
						return new MetaPanel<Object>(id, getDocumentModel(), rowModel, displayMode);
					}
		};
		
		add(properties);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new DocumentNameModel(getDocumentModel());
	}
	
	
	
	

}
