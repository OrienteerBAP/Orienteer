package org.orienteer.birt.component;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.eclipse.birt.report.engine.api.EngineException;
import org.orienteer.birt.component.resources.ExcelBirtResource;
import org.orienteer.birt.component.resources.HtmlBirtResource;
import org.orienteer.birt.component.resources.PDFBirtResource;
import org.orienteer.birt.component.service.BirtReportParameterDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Panel for runtime birt report manage  
 *
 */
public class BirtManagementPanel extends Panel{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(BirtManagementPanel.class);
	
	private static final String PAGER_NAME = "paginator";
	//private static final String BUTTONS_NAME = "buttons";
	
	public BirtManagementPanel(String id,final AbstractBirtReportPanel reportPanel) {
		super(id);
		final AjaxPagingNavigator pager = new AjaxPagingNavigator(PAGER_NAME, reportPanel) {
			private static final long serialVersionUID = 1L;

			@Override
		    protected void onAjaxEvent(AjaxRequestTarget target) {
		        target.add(reportPanel);
		        target.add(this);
		    }
		};

		add(pager);
		add(new ResourceLink<>("HTML", new HtmlBirtResource(reportPanel)));
		add(new ResourceLink<>("PDF", new PDFBirtResource(reportPanel)));
		add(new ResourceLink<>("Excel", new ExcelBirtResource(reportPanel)));
		
		add(new ParamsListView("params",reportPanel.getParametersDefenitions(),reportPanel,pager));
		add(new ParamsListView("hiddenParams",reportPanel.getHiddenParametersDefinitions(),reportPanel,pager));
		
	}
	////////////////////////////////////////////////////////////////////////	
	private class ParamsListView extends ListView<BirtReportParameterDefinition>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private AbstractBirtReportPanel reportPanel; 
		private Component pager; 
		public ParamsListView(String id, List<BirtReportParameterDefinition> list,AbstractBirtReportPanel reportPanel,Component pager) {
			super(id, list);
			this.reportPanel = reportPanel;
			this.pager = pager;
		}

		@Override
		protected void populateItem(ListItem<BirtReportParameterDefinition> item) {
			String name = item.getModelObject().getName();
			item.add(new Label("parameterName",name));
			String defaultValue = item.getModelObject().getDefaultValue();
			Object value = reportPanel.getParameter(name);
			if (Strings.isNullOrEmpty((String) value)){
				reportPanel.setParameter(name, defaultValue);
			}
			item.add(new TextField<>("parameterInput",new PropertyModel<>(reportPanel,"config.parameters["+name+"]"))
					.add(new AjaxFormComponentUpdatingBehavior("change"){

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							try {
								reportPanel.setCurrentPage(0);
								reportPanel.updateReportCache();
						        target.add(reportPanel);
						        target.add(pager);
							} catch (EngineException e) {
								String message = e.getMessage();
								error("Cannot update report cache:"+message);
								LOG.error("Can't update report cache", e);
							}
						}
						
			}));
		}
	}
	////////////////////////////////////////////////////////////////////////	
	
}
