package org.orienteer.core.component.widget;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IOClassIntrospector;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.filter.AbstractFilteredDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

/**
 * Widget for {@link OTask}
 *
 */
@Widget(domain="document",selector=OTask.TASK_CLASS, id=OTaskWidget.WIDGET_TYPE_ID, order=20, autoEnable=true)
public class OTaskWidget extends AbstractWidget<ODocument>{

	public static final String WIDGET_TYPE_ID = "task";
	private static final long serialVersionUID = 1L;

	@Inject
	private IOClassIntrospector oClassIntrospector;


	public OTaskWidget(String id, IModel<ODocument> model, final IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);

		IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
		OClass taskSessionClass = getModelObject().getDatabase().getMetadata().getSchema().getClass(OTaskSessionRuntime.TASK_SESSION_CLASS);
		AbstractFilteredDataProvider<ODocument> provider = new OQueryDataProvider<ODocument>("select from "+ITaskSession.TASK_SESSION_CLASS+" where "+
				ITaskSession.Field.TASK_LINK.fieldName()+"="+getModelObject().getIdentity());
		oClassIntrospector.defineDefaultSorting(provider, taskSessionClass);
		GenericTablePanel<ODocument> tablePanel =
				new GenericTablePanel<>("tablePanel", oClassIntrospector.getColumnsFor(taskSessionClass, true, modeModel), provider, 20);
		OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();

		
		add(tablePanel);
		add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
	}
	

	
	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("task.title");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}