package org.orienteer.birt.component.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.model.IModel;
import org.eclipse.birt.report.engine.api.EngineException;
import org.orienteer.birt.component.widget.AbstractBirtWidget;
import org.orienteer.core.component.property.BinaryEditPanel;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Object for BIRT report configuration. Based on ODocument
 *
 */
public class BirtReportODocumentConfig implements IBirtReportConfig{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, Object> parameters;
	private Set<Object> visibleParameters;
	private boolean useLocalDB;
	private IModel<ODocument> configDocModel;
	

	public BirtReportODocumentConfig(IModel<ODocument> configDocModel,Map<String,Object> additionalParameters) throws EngineException {
    	ODocument modelObject = configDocModel.getObject();
		byte[] reportData = modelObject.field(AbstractBirtWidget.REPORT_FIELD_NAME);
    	if (reportData==null || reportData.length==0){
    		throw new EngineException("Configure report first");
    	}
		Map<String,Object> parameters = modelObject.field(AbstractBirtWidget.PARAMETERS_FIELD_NAME);
		if (additionalParameters!=null){
			parameters.putAll(additionalParameters);
		}
		Boolean isUseLocalDB = modelObject.field(AbstractBirtWidget.USE_LOCAL_BASE_FIELD_NAME);
		if (isUseLocalDB == null){
			isUseLocalDB = false;
		}
		Set<Object> visibleParameters = modelObject.field(AbstractBirtWidget.VISIBLE_PARAMETERS_FIELD_NAME);
		
		this.configDocModel = configDocModel;
		this.parameters = parameters;
		this.visibleParameters = visibleParameters;
		
		
		this.useLocalDB = isUseLocalDB;
	}
	
	public InputStream getReportDataStream() {
		return new ByteArrayInputStream((byte[]) configDocModel.getObject().field(AbstractBirtWidget.REPORT_FIELD_NAME));
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public boolean isUseLocalDB() {
		return useLocalDB;
	}

	public Set<Object> getVisibleParameters() {
		return visibleParameters;
	}

	@Override
	public String getOutName() {
    	ODocument modelObject = configDocModel.getObject();
		String filename = modelObject.field(AbstractBirtWidget.REPORT_FIELD_NAME+BinaryEditPanel.FILENAME_SUFFIX);
		filename=filename.replaceFirst("\\.[^\\.]*$", "");

		return filename;
	}
	
	

}
