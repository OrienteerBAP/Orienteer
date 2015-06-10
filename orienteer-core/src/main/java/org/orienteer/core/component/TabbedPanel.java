package org.orienteer.core.component;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

/**
 * Bootstrap and AJAX enabled {@link org.apache.wicket.extensions.markup.html.tabs.TabbedPanel}
 *
 * @param <T> The type of panel to be used for this component's tabs. Just use {@link ITab} if you
 *            have no special needs here.
 */
public class TabbedPanel<T extends ITab> extends org.apache.wicket.extensions.markup.html.tabs.TabbedPanel<T> {

	public TabbedPanel(String id, List<T> tabs, IModel<Integer> model) {
		super(id, tabs, model);
	}

	public TabbedPanel(String id, List<T> tabs) {
		super(id, tabs);
	}
	
	@Override
	protected String getSelectedTabCssClass() {
		return "active";
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(TabsPanel.TABDROP_CSS));
		response.render(JavaScriptHeaderItem.forReference(TabsPanel.TABDROP_JS));
		response.render(OnDomReadyHeaderItem.forScript("$('#"+getMarkupId()+" > ul').tabdrop({text: '<i class=\"glyphicon glyphicon-align-justify\"></i>'});"));
	}
	
	@Override
	protected WebMarkupContainer newLink(String linkId, final int index) {
			return new AjaxLink<Void>(linkId)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setSelectedTab(index);
					target.add(TabbedPanel.this);
				}
			};
	}
	
}
