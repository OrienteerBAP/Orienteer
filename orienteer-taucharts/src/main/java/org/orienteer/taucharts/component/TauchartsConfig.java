package org.orienteer.taucharts.component;

import java.io.Serializable;
import java.util.ArrayList;
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
	private String x;
	private IModel<String> xLabel;
	private String y;
	private IModel<String> yLabel;
	private String colorBy;
	private List<String> plugins;
	private String query;
	private boolean usingRest;
	

	public TauchartsConfig(String type ,String x,String y,String colorBy,List<String> plugins,String query,String xLabel,String yLabel,Boolean usingRest) {
		this.type = type;
		this.x=x;
		this.y=y;
		this.xLabel=new SimpleNamingModel<String>(Strings.isEmpty(xLabel)?(x):xLabel);
		this.yLabel=new SimpleNamingModel<String>(Strings.isEmpty(yLabel)?(y):yLabel);
		this.colorBy=colorBy;
		if (plugins==null){
			this.plugins = new ArrayList<String>();
		}else{
			this.plugins=plugins;
		}
		this.query=query;	
		this.usingRest = usingRest!=null?usingRest:false;
	}


	public String getType() {
		return type;
	}


	public String getX() {
		return x;
	}


	public IModel<String> getxLabel() {
		return xLabel;
	}


	public String getY() {
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
}
