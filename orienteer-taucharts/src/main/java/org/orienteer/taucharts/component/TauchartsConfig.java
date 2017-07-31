package org.orienteer.taucharts.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * 
 * Config for {@link TauchartsPanel}
 *
 */
public class TauchartsConfig implements Serializable{
	private static final long serialVersionUID = 1L;
	private String type;
	private Collection<String> x;
	private IModel<String> xLabel;
	private Collection<String> y;
	private IModel<String> yLabel;
	private String colorBy;
	private List<String> plugins;
	private String query;
	private boolean usingRest;
	private String config;
	

	public TauchartsConfig(String type, Collection<String> x, Collection<String> y, String colorBy,List<String> plugins,String query,String xLabel,String yLabel,Boolean usingRest, String config) {
		this.type = type;
		this.x=x;
		this.y=y;
		this.xLabel=new SimpleNamingModel<String>(Strings.isEmpty(xLabel)?(x!=null && !x.isEmpty() ? x.iterator().next():"x"):xLabel);
		this.yLabel=new SimpleNamingModel<String>(Strings.isEmpty(yLabel)?(y!=null && !y.isEmpty() ? y.iterator().next():"y"):yLabel);
		this.colorBy=colorBy;
		if (plugins==null){
			this.plugins = new ArrayList<String>();
		}else{
			this.plugins=plugins;
		}
		this.query=query;	
		this.usingRest = usingRest!=null?usingRest:false;
		this.config = config;
	}


	public String getType() {
		return type;
	}


	public Collection<String> getX() {
		return x;
	}


	public IModel<String> getxLabel() {
		return xLabel;
	}


	public Collection<String> getY() {
		return y;
	}


	public IModel<String> getyLabel() {
		return yLabel;
	}


	public String getColorBy() {
		return colorBy;
	}


	public List<String> getPlugins() {
		return plugins;
	}


	public String getQuery() {
		return query;
	}


	public boolean isUsingRest() {
		return usingRest;
	}			
	
	public String getConfig() {
		return config;
	}
}
