package org.orienteer.core.component.widget;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.core.tasks.OTaskSessionImpl;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * Widget for {@link TaskManagerModule}
 *
 */
@Widget(domain="document",selector=OTaskSession.TASK_SESSION_CLASS, id=OTaskSessionWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
public class OTaskSessionWidget extends AbstractWidget<ODocument>{

	public static final String WIDGET_TYPE_ID = "taskSession";
	private static final long serialVersionUID = 1L;
	//private static final Logger LOG = LoggerFactory.getLogger(OTaskSessionWidget.class);
	private Form<?> form;
	
	public static final List<String> TASK_DATA_LIST = new ArrayList<String>();
	static
	{
		//TASK_DATA_LIST.add("name");
	}	


	public OTaskSessionWidget(String id, IModel<ODocument> model, final IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

		form = new Form<Void>("form");
        
        OrienteerStructureTable<ODocument, String> structuredTable = new OrienteerStructureTable<ODocument, String>("table", model, TASK_DATA_LIST) {
			private static final long serialVersionUID = 1L;
			@Override
			protected Component getValueComponent(String id, IModel<String> rowModel) {
				return new Label(id,new PropertyModel<>(getModel(), rowModel.getObject()));
			}
			@Override
			protected IModel<?> getLabelModel(Component resolvedComponent, IModel<String> rowModel) {
				return new SimpleNamingModel<String>("integration."+rowModel.getObject());
			}
		};
			
		
		form.add(structuredTable);
		structuredTable.addCommand(makeStopButton());
		
		form.setOutputMarkupId(true);

		add(form);
	}
	
	
	private Command<ODocument> makeStopButton() {
		
		return new Command<ODocument>("stop","task.session.command.stop") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			protected void onInitialize() {
				super.onInitialize();
				setIcon(FAIconType.stop);
				setBootstrapType(BootstrapType.DANGER);
				setChangingDisplayMode(true);
				OTaskSessionImpl taskSession = new OTaskSessionImpl(OTaskSessionWidget.this.getModelObject());
				taskSession.detachUpdate();
				setEnabled(taskSession.isStoppable());
			}
			@Override
			public void onClick() {
				OTaskSessionImpl taskSession = new OTaskSessionImpl(OTaskSessionWidget.this.getModelObject());
				try {
					taskSession.getCallback().stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setEnabled(false);
			}
		};
	}	
	
	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("task.session.title");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
