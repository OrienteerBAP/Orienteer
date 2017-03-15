package org.orienteer.birt.component.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.birt.report.engine.api.EngineException;

/**
 * Config for BIRT report based on simple filename
 *
 */
public class BirtReportFileConfig implements IBirtReportConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String reportFileName;
	private Map<String, Object> parameters;
	private Set<Object> visibleParameters;
	private boolean isUseLocalDB = false;
	
	
	public BirtReportFileConfig(String reportFileName,boolean isUseLocalDB) throws EngineException  {
		this(reportFileName);
		this.isUseLocalDB = isUseLocalDB;
		
	}
	public BirtReportFileConfig(String reportFileName) throws EngineException  {
		File testfile = new File(reportFileName);
		if (!testfile.exists()){
			throw new EngineException("Cannot open BIRT report file "+reportFileName+"("+testfile.getAbsolutePath()+")");
		}
		this.reportFileName = reportFileName;
		parameters = new HashMap<String, Object>();
		visibleParameters = new HashSet<Object>();
	}

	@Override
	public InputStream getReportDataStream() {
		try {
			FileInputStream stream = new FileInputStream(reportFileName);
			return stream;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public boolean isUseLocalDB() {
		return isUseLocalDB;
	}

	@Override
	public Set<Object> getVisibleParameters() {
		return visibleParameters;
	}

}
