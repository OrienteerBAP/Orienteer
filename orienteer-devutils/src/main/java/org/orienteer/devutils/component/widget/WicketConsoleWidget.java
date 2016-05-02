package org.orienteer.devutils.component.widget;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketconsole.WicketConsolePanel;

/**
 * Widget to show wicket-console on tools dashboard page
 */
@Widget(id="wicket-console", domain="tools", tab = "console", autoEnable=true)
public class WicketConsoleWidget extends AbstractWidget<Void> {

	public WicketConsoleWidget(String id, IModel<Void> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		add(new WicketConsolePanel("console"));
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.terminal);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.wicketconsole");
	}

}
