package org.orienteer.pages;

import org.apache.wicket.core.request.handler.IPageClassRequestHandler;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.string.StringValue;

import com.orientechnologies.orient.core.id.ORID;

/**
 * {@link IPageParametersEncoder} for transparent adding page Id
 */
public class OPageParametersEncoder extends PageParametersEncoder {

	public static final String PAGE_IDENTITY = "__orienteerPageId__";
	
	private final ORID pageIdentity;
	
	public OPageParametersEncoder(ORID pageIdentity) {
		this.pageIdentity = pageIdentity;
	}
	@Override
	public PageParameters decodePageParameters(Url url) {
		PageParameters ret =  super.decodePageParameters(url);
		if(ret==null) ret = new PageParameters();
		ret.add(PAGE_IDENTITY, pageIdentity);
		return ret;
	}

	@Override
	public Url encodePageParameters(PageParameters pageParameters) {
		StringValue sv = pageParameters.get(PAGE_IDENTITY);
		if(!sv.isEmpty()) {
			pageParameters.remove(PAGE_IDENTITY);
		}
		Url ret = super.encodePageParameters(pageParameters);
		if(!sv.isEmpty()) {
			pageParameters.add(PAGE_IDENTITY, sv.toString());
		}
		return ret;
	}
	
	public static boolean matchHandler(ORID pageIdentity, IPageClassRequestHandler requestHandler) {
		PageParameters params = null;
		if(requestHandler instanceof IPageRequestHandler) {
			IRequestablePage page = ((IPageRequestHandler)requestHandler).getPage();
			if(page!=null) params = page.getPageParameters();
		}
		if(params==null) params = requestHandler.getPageParameters();
		StringValue sv = params!=null?params.get(PAGE_IDENTITY):null;
		return sv!=null && pageIdentity!=null?pageIdentity.toString().equals(sv.toOptionalString()):false;
	}
	
}