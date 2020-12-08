package org.orienteer.rproxy;

import java.util.List;
import java.util.Map;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import com.google.inject.ProvidedBy;

/**
 * DAO class for ORProxyEndPoint which holds configuration for reverse proxy end points
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IORProxyEndPoint.CLASS_NAME, nameProperty = "name")
public interface IORProxyEndPoint {
	
	public static final String CLASS_NAME = "ORProxyEndPoint";
	
	public String getName();
	public void setName(String name);
	
	public String getMountPath();
	public void setMountPath(String mountPath);
	
	public String getBaseUrl();
	public void setBaseUrl(String baseUrl);
	
	public Boolean isLoggingEnabled();
	public void setLoggingEnabled(Boolean loggingEnabled);
	
	public String getUsername();
	public void setUsername(String username);
	
	@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_PASSWORD)
	public String getPassword();
	public void setPassword(String password);
	
	public Map<String, String> getCookies();
	public void setCookies(Map<String, String> cookies);
	
	public Map<String, String> getHeaders();
	public void setHeaders(Map<String, String> headers);
	
	public List<String> getProtectedParameters();
	public void setProtectedParameters(List<String> protectedParameters);
	
	public String getExtensionClassName();
	public void setExtensionClassName(String className);
	
	public default String getSharedResourceName() {
		return "ORProxy"+DAO.asWrapper(this).getDocument().getIdentity();
	}
	
	public default Class<? extends IORProxyExtension> getExtensionClass() {
		String className = getExtensionClassName();
		if(!Strings.isEmpty(className)) {
			try {
				Class<?> clazz = Class.forName(className);
				if(IORProxyExtension.class.isAssignableFrom(clazz)) return (Class<? extends IORProxyExtension>) clazz;
			} catch (ClassNotFoundException e) {
				// It's OK
			}
		}
		return null;
	}
}
