package org.orienteer.birt.component.widget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.birt.component.BirtPaginatedHtmlPanel;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class AbstractBirtWidget<T> extends AbstractWidget<T>{


	public AbstractBirtWidget(String id, IModel<T> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		/*
		 * parameters
		 * report byte[]
		 * */
		
		byte[] reportData = widgetDocumentModel.getObject().field("report");
		final InputStream reportStream = new ByteArrayInputStream(reportData);
		AjaxLazyLoadPanel panel = new AjaxLazyLoadPanel("report")
		{
		  @Override
		  public Component getLazyLoadComponent(String id)
		  {
		       return new BirtPaginatedHtmlPanel(id,reportStream,new HashMap<String,Object>());
		  }
		};
		add(panel);	
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.table);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.birt");
	}
}
