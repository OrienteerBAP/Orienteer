package ru.ydn.orienteer.web;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.commands.EditCommand;
import ru.ydn.orienteer.components.commands.SaveCommand;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.MetaPanel;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.model.DocumentNameModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

@MountPath("/doc/#{rid}/#{mode}")
public class DocumentPage extends AbstractDocumentPage {

	private OrienteerStructureTable<OProperty> propertiesStructureTable;
	
	private IModel<DisplayMode> displayMode = DisplayMode.VIEW.asModel();
	
	public DocumentPage(ODocument doc)
	{
		this(new ODocumentModel(doc));
	}
	
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
		Form<ODocument> form = new Form<ODocument>("form", getModel());
		propertiesStructureTable = new OrienteerStructureTable<OProperty>("properties", 
				new PropertyModel<List<? extends OProperty>>(getDocumentModel(), "schemaClass.properties()")) {

					@Override
					protected IModel<?> getLabelModel(IModel<OProperty> rowModel) {
						return new OPropertyNamingModel(rowModel);
					}

					@Override
					protected Component getValueComponent(String id,
							IModel<OProperty> rowModel) {
						return new MetaPanel<Object>(id, getDocumentModel(), rowModel, displayMode);
					}
		};
		
		form.add(propertiesStructureTable);
		add(form);
	}
	
	public DisplayMode getDisplayMode()
	{
		return displayMode.getObject();
	}
	
	public DocumentPage setDisplayMode(DisplayMode displayMode)
	{
		this.displayMode.setObject(displayMode);
		return this;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		propertiesStructureTable.addCommand(new EditCommand(propertiesStructureTable.getCommandsToolbar(), displayMode).setBootstrapType(BootstrapType.PRIMARY));
		propertiesStructureTable.addCommand(new SaveCommand(propertiesStructureTable.getCommandsToolbar(), displayMode, getModel()).setBootstrapType(BootstrapType.PRIMARY));
	}

	@Override
	public IModel<String> getTitleModel() {
		return new DocumentNameModel(getDocumentModel());
	}
	
	
	
	

}
