package org.orienteer.rproxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.SharedResources;
import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.string.interpolator.VariableInterpolator;
import org.orienteer.core.OrienteerWebApplication;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request.Builder;
import ru.ydn.wicket.wicketorientdb.LazyAuthorizationRequestCycleListener;
import ru.ydn.wicket.wicketorientdb.rest.ReverseProxyResource;

/**
 * {@link ReverseProxyResource} configured by ORProxyEndPoint
 */
public final class ORProxyResource extends ReverseProxyResource {
	
	private static final long serialVersionUID = 1L;
	private String baseUrl;
	private String username;
	private String password;
	private Map<String, String> cookies;
	private Map<String, String> headers;
	private List<String> protectedParameters;
	private Boolean loggingEnabled;
	
	private IORProxyExtension extension;
	
	private ORProxyResource(IORProxyEndPoint endPoint) {
		this.baseUrl = endPoint.getBaseUrl();
		this.username = endPoint.getUsername();
		this.password = endPoint.getPassword();
		this.cookies = new HashMap<String, String>();
		if(endPoint.getCookies()!=null) cookies.putAll(endPoint.getCookies());
		this.headers = new HashMap<String, String>();
		if(endPoint.getHeaders()!=null) cookies.putAll(endPoint.getHeaders());
		this.protectedParameters = endPoint.getProtectedParameters();
		this.loggingEnabled = endPoint.isLoggingEnabled();
		Class<? extends IORProxyExtension> extension = endPoint.getExtensionClass();
		if(extension!=null) {
			try {
				this.extension = OrienteerWebApplication.get().getServiceInstance(extension);
				this.extension.init(endPoint, this);
			} catch (Exception e) {
			} 
		}
	}
	
	@Override
	protected HttpUrl getBaseUrl(Attributes attributes) {
		return HttpUrl.get(interpolate(baseUrl, attributes));
	}
	
	@Override
	protected void onMapUrl(Attributes attributes, HttpUrl.Builder builder) {
		if(protectedParameters!=null) {
			for (String parameter : protectedParameters) {
				builder.removeAllQueryParameters(parameter);
			}
		}
		if(extension!=null) extension.onMapUrl(attributes, builder);
	}
	
	@Override
	protected void onMapHeaders(Attributes attributes, Headers.Builder builder) {
		for (Map.Entry<String, String> headerItem : headers.entrySet()) {
			builder.add(headerItem.getKey(), interpolate(headerItem.getValue(), attributes));
		}
		if(!cookies.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> cookiesItem : cookies.entrySet()) {
				sb.append(cookiesItem.getKey()).append('=').append(cookiesItem.getValue()).append("; ");
			}
			builder.add("Cookie", sb.toString());
		}
		if(!Strings.isEmpty(username) && !Strings.isEmpty(password))
			builder.add(LazyAuthorizationRequestCycleListener.AUTHORIZATION_HEADER, Credentials.basic(username, password));
		if(extension!=null) extension.onMapHeaders(attributes, builder);
	}
	
	@Override
	protected void onMapRequest(Attributes attributes, Builder builder) {
		if(extension!=null) extension.onMapRequest(attributes, builder);
	}
	
	@Override
	protected boolean isDebugLoggingEnabled(Attributes attributes) {
		return Boolean.TRUE.equals(loggingEnabled) || super.isDebugLoggingEnabled(attributes);
	}
	
	private String interpolate(final String template, final Attributes attributes) {
		if(template==null || !template.contains("${")) return template;
		return new VariableInterpolator(template) {
			
			@Override
			protected String getValue(String variableName) {
				if(variableName==null) return null;
				PageParameters pageParameters = attributes.getParameters();
				if(pageParameters.getPosition(variableName)>=0) {
					return pageParameters.get(variableName).toString();
				}
				IRequestParameters requestParamters = attributes.getRequest().getRequestParameters();
				StringValue sv = requestParamters.getParameterValue(variableName);
				if(!sv.isNull()) return sv.toString();
				try {
					int index = Integer.parseInt(variableName);
					return pageParameters.get(index).toString();
				} catch (NumberFormatException e) {
					//It's not number and it's OK
				}
				return null;
			}
		}.toString();
	}
	
	public static boolean mount(IORProxyEndPoint endPoint) {
		return mount(OrienteerWebApplication.lookupApplication(), endPoint);
	}
	
	public static boolean mount(OrienteerWebApplication app, IORProxyEndPoint endPoint) {
		return mount(app, endPoint, true);
	}

	public static boolean mount(OrienteerWebApplication app, IORProxyEndPoint endPoint, boolean remount) {
		SharedResources sharedResources = app.getSharedResources();
		if(sharedResources.get(endPoint.getSharedResourceName())!=null) {
			if(!remount) return false;
			unmount(app, endPoint);
		}
		app.getSharedResources().add(endPoint.getSharedResourceName(), new ORProxyResource(endPoint));
	    app.mountResource(endPoint.getMountPath(), new SharedResourceReference(endPoint.getSharedResourceName()));
	    return true;
	}
	
	public static boolean unmount(IORProxyEndPoint endPoint) {
		return unmount(OrienteerWebApplication.lookupApplication(), endPoint);
	}
	
	public static boolean unmount(OrienteerWebApplication app, IORProxyEndPoint endPoint) {
		SharedResources sharedResources = app.getSharedResources();
		ResourceReference reference = sharedResources.get(endPoint.getSharedResourceName());
		if(reference!=null) sharedResources.remove(new ResourceReference.Key(reference));
		app.unmount(endPoint.getMountPath());
		return true;
	}
}
