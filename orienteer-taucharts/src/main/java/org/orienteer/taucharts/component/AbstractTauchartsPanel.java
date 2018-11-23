package org.orienteer.taucharts.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.orienteer.core.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import ru.ydn.wicket.wicketorientdb.model.ODocumentWrapperModel;

/**
 * Panel for display chart from <a href="https://www.taucharts.com/">https://www.taucharts.com/</a>
 *
 */
public abstract class AbstractTauchartsPanel extends Panel{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractTauchartsPanel.class);
	
	private static final WebjarsJavaScriptResourceReference TAUCHARTS_JS = 
			new WebjarsJavaScriptResourceReference("/webjars/taucharts/build/production/tauCharts.min.js");
	private static final WebjarsJavaScriptResourceReference D3_JS = new WebjarsJavaScriptResourceReference("/webjars/d3/d3.min.js");
	
	private static final WebjarsCssResourceReference TAUCHARTS_CSS =
			new WebjarsCssResourceReference("/webjars/taucharts/build/production/tauCharts.min.css");

	private IModel<TauchartsConfig> configModel;
	
	
	public AbstractTauchartsPanel(String id, TauchartsConfig config) {
		this(id, null, config);		
	}

	public AbstractTauchartsPanel(String id,IModel<ODocument> assignedDoc, TauchartsConfig config) {
		super(id,assignedDoc);
		this.configModel = new ODocumentWrapperModel<>(config);
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		tag.append("style", "height:"+configModel.getObject().getMinHeight()+"px;", " ");
		super.onComponentTag(tag);
	}
	
	protected String getCustomSql() {
		return configModel.getObject().getQuery();
	}
	
	protected String getSql() {
		String customSql = getCustomSql();
		return Strings.isEmpty(customSql)?getDefaultSql():customSql;
	}
	
	protected abstract String getDefaultSql();
	
	@Override
	public void renderHead(IHeaderResponse response) {
	  response.render(JavaScriptReferenceHeaderItem.forReference(D3_JS));
	  response.render(JavaScriptReferenceHeaderItem.forReference(TAUCHARTS_JS));
	  response.render(CssReferenceHeaderItem.forReference(TAUCHARTS_CSS));
	  
		String jsonData=null;
		String restUrl=null;
		TauchartsConfig config = configModel.getObject();
		if (config.isUsingRest()){
			restUrl = "/orientdb/query/db/sql/"+UrlEncoder.PATH_INSTANCE.encode(getSql(), "UTF-8")+"/-1?rnd="+Math.random();
		}else{
			List<ODocument> testData = new OSQLSynchQuery<ODocument>(getSql()).run(getDefaultModelObject()!=null?((ODocument)getDefaultModelObject()).toMap():null) ;
			jsonData = "[";
			for ( ODocument object : testData) {
				jsonData+=object.toJSON()+",";	
			}
			jsonData+="]";
		}

	  	TextTemplate template = new PackageTextTemplate(AbstractTauchartsPanel.class, "taucharts.tmpl.js");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("componentId", getMarkupId());
		
		params.put("rest", config.isUsingRest());
		params.put("data",CommonUtils.escapeAndWrapAsJavaScriptString(jsonData));
		params.put("url",restUrl);
		String postProcess = config.getDataPostProcessing();
		params.put("postProcess", Strings.isEmpty(postProcess)?"return data;":postProcess);
		

		params.put("type", CommonUtils.escapeStringForJSON(config.getType()));
		params.put("x", new JSONArray(config.getX()).toString());
		params.put("xLabel", CommonUtils.escapeStringForJSON(config.getxLabel().getObject()));
		params.put("y", new JSONArray(config.getY()).toString());
		params.put("yLabel", CommonUtils.escapeStringForJSON(config.getyLabel().getObject()));
		params.put("colorBy", CommonUtils.escapeStringForJSON(config.getColorBy()));
		params.put("config", CommonUtils.escapeStringForJSON(config.getConfig()));
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
			LOG.error("Can't close a template resource", e);
		}
	}
	
	
}
