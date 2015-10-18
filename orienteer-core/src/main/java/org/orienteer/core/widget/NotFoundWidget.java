package org.orienteer.core.widget;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.module.OWidgetsModule;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Dummy widget to show if widget with required type id was not found
 *
 * @param <T> the type of a main object for a dashboard
 */
public class NotFoundWidget<T> extends AbstractWidget<T> {
	
	public NotFoundWidget(String id, IModel<T> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		add(new Label("error", new StringResourceModel("widget.error.notfound", widgetDocumentModel)
										.setParameters(widgetDocumentModel.getObject().field(OWidgetsModule.OPROPERTY_TYPE_ID))));
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.exclamation_circle);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("widget.error");
	}

}
