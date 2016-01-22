package org.orienteer.core.component.widget;

import java.util.Collection;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

/**
 * Abstract widget for all widgets that use free HTML/JS for display
 *
 * @param <T> the type of main data object linked to this widget
 */
public abstract class AbstractHtmlJsPaneWidget<T> extends AbstractWidget<T> {
	
	public static final String WIDGET_OCLASS_NAME = "HtmlJsWidget";
	
	public AbstractHtmlJsPaneWidget(String id, IModel<T> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		add(new Label("chart", new PropertyModel<String>(this, "html")).setEscapeModelStrings(false));
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.file_picture_o);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.htmljs");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		ODocument doc = getWidgetDocument();
		Collection<String> resources = doc.field("resources");
		if(resources!=null) {
			for(String resource : resources) {
				String lowercase = resource.toLowerCase();
				if(lowercase.endsWith(".css")) {
					response.render(CssHeaderItem.forUrl(resource));
				}
				else if(lowercase.endsWith(".js")) {
					response.render(JavaScriptHeaderItem.forUrl(resource));
				}
			}
		}
		String script = doc.field("script");
		if(!Strings.isEmpty(script)) {
			response.render(OnDomReadyHeaderItem.forScript(interpolateScript(script)));
		}
	}
	
	public String getHtml() {
		String html = getWidgetDocument().field("html");
		if(!Strings.isEmpty(html)) {
			html = interpolateHtml(html);
		}
		return html;
	}
	
	protected String interpolateScript(String script) {
		return interpolate(script);
	}

	protected String interpolateHtml(String html) {
		return interpolate(html);
	}
	
	protected String interpolate(String content) {
		return content;
	}

}
