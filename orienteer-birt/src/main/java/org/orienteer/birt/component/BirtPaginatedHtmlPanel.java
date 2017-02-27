package org.orienteer.birt.component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;

public class BirtPaginatedHtmlPanel extends Panel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BirtPaginatedHtmlPanel(String id,InputStream report) {
		this(id,report,new HashMap<String,Object>());
	}
	
	public BirtPaginatedHtmlPanel(String id,InputStream report,Map<String, Object> parameters) {
		super(id);
		final BirtHtmlPanel birtPanel = new BirtHtmlPanel("report",report,parameters);
		birtPanel.setOutputMarkupId(true);
		add(birtPanel);
		AjaxPagingNavigator pager = new AjaxPagingNavigator("pages", birtPanel) {
			private static final long serialVersionUID = 1L;

			@Override
		    protected void onAjaxEvent(AjaxRequestTarget target) {
		        target.add(birtPanel);
		        target.add(this);
		    }
		};
		pager.setOutputMarkupId(true);
		add(pager);
	}

}
