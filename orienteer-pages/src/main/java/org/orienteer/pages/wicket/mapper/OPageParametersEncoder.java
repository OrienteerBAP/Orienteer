package org.orienteer.pages.wicket.mapper;

import com.orientechnologies.orient.core.id.ORID;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * {@link IPageParametersEncoder} for transparent adding page Id
 */
public class OPageParametersEncoder extends TransparentParameterPageEncoder {

	public static final String PAGE_IDENTITY = "__orienteerPageId__";
	
	private final ORID pageIdentity;
	
	public OPageParametersEncoder(ORID pageIdentity) {
		super(PAGE_IDENTITY);
		this.pageIdentity = pageIdentity;
	}

	@Override
	public PageParameters decodePageParameters(Url url) {
		PageParameters ret =  super.decodePageParameters(url);
		ret.add(PAGE_IDENTITY, pageIdentity.toString());
		return ret;
	}
}