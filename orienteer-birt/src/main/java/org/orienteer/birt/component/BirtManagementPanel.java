package org.orienteer.birt.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;

public class BirtManagementPanel extends Panel{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String PAGER_NAME = "paginator";
	//private static final String BUTTONS_NAME = "buttons";
	
	public BirtManagementPanel(String id,final AbstractBirtReportPanel reportPanel) {
		super(id);
		AjaxPagingNavigator pager = new AjaxPagingNavigator(PAGER_NAME, reportPanel) {
			private static final long serialVersionUID = 1L;

			@Override
		    protected void onAjaxEvent(AjaxRequestTarget target) {
		        target.add(reportPanel);
		        target.add(this);
		    }
		};
		pager.setOutputMarkupId(true);

		add(pager);
		add(new ResourceLink<>("HTML", new HtmlBirtResource(reportPanel)));
		add(new ResourceLink<>("PDF", new PDFBirtResource(reportPanel)));
		add(new ResourceLink<>("Excel", new ExcelBirtResource(reportPanel)));
		
		//add(new Label)
		//add(new ResourceLink<>("parameter", new ExcelBirtResource(reportPanel)));
		
	}
}
