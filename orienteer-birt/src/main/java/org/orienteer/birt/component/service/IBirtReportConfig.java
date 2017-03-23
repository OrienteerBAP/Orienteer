package org.orienteer.birt.component.service;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Interface for all BIRT report configs
 *
 */
public interface IBirtReportConfig extends Serializable {
	public InputStream getReportDataStream();
	
	public Map<String, Object> getParameters();
	
	public boolean isUseLocalDB();

	public Set<Object> getVisibleParameters();
}
