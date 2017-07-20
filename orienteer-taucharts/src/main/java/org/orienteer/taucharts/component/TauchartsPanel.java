package org.orienteer.taucharts.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * 
 * Panel for display chart from {@linkplain https://www.taucharts.com/}
 *
 */
public class TauchartsPanel extends Panel{

	private static final long serialVersionUID = 1L;
	
	private static final WebjarsJavaScriptResourceReference TAUCHARTS_JS = new WebjarsJavaScriptResourceReference("/webjars/github-com-TargetProcess-tauCharts/current/build/production/tauCharts.min.js");
	private static final WebjarsJavaScriptResourceReference D3_JS = new WebjarsJavaScriptResourceReference("/webjars/d3/current/d3.min.js");
	
	private static final WebjarsCssResourceReference TAUCHARTS_CSS = new WebjarsCssResourceReference("/webjars/github-com-TargetProcess-tauCharts/current/build/production/tauCharts.min.css");

	private TauchartsConfig config;
	
	
	public TauchartsPanel(String id, TauchartsConfig config) {
		this(id, null, config);		
	}

	public TauchartsPanel(String id,IModel<ODocument> assignedDoc, TauchartsConfig config) {
		super(id,assignedDoc);
		this.config = config;

	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
	  response.render(JavaScriptReferenceHeaderItem.forReference(D3_JS));
	  response.render(JavaScriptReferenceHeaderItem.forReference(TAUCHARTS_JS));
	  response.render(CssReferenceHeaderItem.forReference(TAUCHARTS_CSS));
	  
		String jsonData=null;
		String jsImpl = null;
		String restUrl=null;
		if (config.isUsingRest()){
			jsImpl = "taucharts.rest.impl.js";
			restUrl = "/orientdb/query/db/sql/"+UrlEncoder.PATH_INSTANCE.encode(config.getQuery(), "UTF-8")+"/-1?rnd="+Math.random();
		}else{
			jsImpl = "taucharts.impl.js";
			List<ODocument> testData = new OSQLSynchQuery<ODocument>(config.getQuery()).run(getDefaultModelObject()!=null?((ODocument)getDefaultModelObject()).toMap():null) ;
			jsonData = "[";
			for ( ODocument object : testData) {
				jsonData+=object.toJSON()+",";	
			}
			jsonData+="]";
		}

	  	TextTemplate template = new PackageTextTemplate(TauchartsPanel.class, jsImpl);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("componentId", getMarkupId());
		
		params.put("data",jsonData);
		params.put("url",restUrl);
		

		params.put("type", config.getType());
		params.put("x", config.getX());
		params.put("xLabel", config.getxLabel().getObject());
		params.put("y", config.getY());
		params.put("yLabel", config.getyLabel().getObject());
		params.put("colorBy", config.getColorBy());
		String pluginStr ="[";
		for ( String object : config.getPlugins()) {
			pluginStr+="tauCharts.api.plugins.get('"+object+"')(),";	
		}	
		pluginStr+="]";
		params.put("plugins", pluginStr);
		template.interpolate(params);
		response.render(OnDomReadyHeaderItem.forScript(template.asString()));
		try {
			template.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
