package org.orienteer.taucharts.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.crypt.StringUtils;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.taucharts.component.widget.AbstractTauchartsWidget;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

/**
 * 
 * Config for {@link AbstractTauchartsPanel}
 *
 */
public class TauchartsConfig extends ODocumentWrapper{
	private static final long serialVersionUID = 1L;
	
	public TauchartsConfig(ODocument widgetConfig) {
		super(widgetConfig);
	}

	public String getType() {
		return document.field(AbstractTauchartsWidget.TYPE_PROPERTY_NAME+".alias");
	}


	public Collection<String> getX() {
		return document.field(AbstractTauchartsWidget.X_PROPERTY_NAME);
	}




	public Collection<String> getY() {
		return document.field(AbstractTauchartsWidget.Y_PROPERTY_NAME);
	}

	public IModel<String> getxLabel() {
		return getLabelModel(AbstractTauchartsWidget.X_LABEL_PROPERTY_NAME, getX(), "tauchart.x");
	}

	public IModel<String> getyLabel() {
		return getLabelModel(AbstractTauchartsWidget.Y_LABEL_PROPERTY_NAME, getY(), "tauchart.y");
	}
	
	private IModel<String> getLabelModel(String labelProperty, Collection<String> fields, String defaultLabel) {
		String customLabel = document.field(labelProperty);
		if(!Strings.isEmpty(customLabel)) return new SimpleNamingModel<>(customLabel);
		else if(fields!=null && fields.size()>0) return Model.of(fields.iterator().next());
		else return new SimpleNamingModel<>(defaultLabel);
	}
	
	public int getMinHeight() {
		return CommonUtils.defaultIfNull(document.field(OWidgetsModule.OPROPERTY_SIZE_Y), 320);
	}


	public String getColorBy() {
		return document.field(AbstractTauchartsWidget.COLOR_PROPERTY_NAME);
	}


	public List<String> getPlugins() {
		Set<ODocument> plugins = document.field(AbstractTauchartsWidget.PLUGINS_PROPERTY_NAME);
		List<String> ret = new ArrayList<>();
		if(plugins!=null) ret.addAll(Collections2.transform(plugins, d -> d.field("alias")));
		return ret;
	}


	public String getQuery() {
		return document.field(AbstractTauchartsWidget.QUERY_PROPERTY_NAME);
	}
	
	public String getDataPostProcessing() {
		return document.field(AbstractTauchartsWidget.DATA_POST_PROCESSING_PROPERTY_NAME);
	}


	public boolean isUsingRest() {
		return document.field(AbstractTauchartsWidget.USING_REST_PROPERTY_NAME);
	}			
	
	public String getConfig() {
		return document.field(AbstractTauchartsWidget.CONFIG_PROPERTY_NAME);
	}
}
