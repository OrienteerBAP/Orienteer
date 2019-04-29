package org.orienteer.pages.wicket.mapper;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.string.StringValue;

/**
 * {@link IPageParametersEncoder} for transparent add/remove parameters
 */
public class TransparentParameterPageEncoder extends PageParametersEncoder {

    private final String parameter;

    public TransparentParameterPageEncoder(String parameter) {
        super();
        this.parameter = parameter;
    }

    @Override
    public PageParameters decodePageParameters(Url url) {
        PageParameters ret = super.decodePageParameters(url);
        return ret != null ? ret : new PageParameters();
    }

    @Override
    public Url encodePageParameters(PageParameters pageParameters) {
        StringValue sv = pageParameters.get(parameter);

        if (!sv.isEmpty()) {
            pageParameters.remove(parameter);
        }

        Url ret = super.encodePageParameters(pageParameters);

        if (!sv.isEmpty()) {
            pageParameters.add(parameter, sv.toString());
        }
        return ret;
    }
}
