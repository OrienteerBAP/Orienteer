package org.orienteer.rproxy;

import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.io.IClusterable;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Interface for ReverseProxy extensions
 */
public interface IORProxyExtension extends IClusterable {
	
	public default void init(IORProxyEndPoint iorProxyEndPoint, ORProxyResource orProxyResource) {}
	public default void onMapUrl(Attributes attributes, HttpUrl.Builder builder) {}
	public default void onMapHeaders(Attributes attributes, Headers.Builder builder) {}
	public default void onMapRequest(Attributes attributes, Request.Builder builder) {}
}
