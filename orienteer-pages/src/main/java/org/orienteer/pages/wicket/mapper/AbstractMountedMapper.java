package org.orienteer.pages.wicket.mapper;

import org.apache.wicket.core.request.handler.IPageClassRequestHandler;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.function.Supplier;

/**
 * Abstract mounted mapper which maps request only if request is for this mapper.
 * Check if request is for this mapper by parameters
 */
public abstract class AbstractMountedMapper extends MountedMapper {

    public AbstractMountedMapper(String mountPath, Class<? extends IRequestablePage> pageClass) {
        super(mountPath, pageClass);
    }

    public AbstractMountedMapper(String mountPath, Supplier<Class<? extends IRequestablePage>> pageClassProvider) {
        super(mountPath, pageClassProvider);
    }

    public AbstractMountedMapper(String mountPath, Class<? extends IRequestablePage> pageClass, IPageParametersEncoder pageParametersEncoder) {
        super(mountPath, pageClass, pageParametersEncoder);
    }

    public AbstractMountedMapper(String mountPath, Supplier<Class<? extends IRequestablePage>> pageClassProvider, IPageParametersEncoder pageParametersEncoder) {
        super(mountPath, pageClassProvider, pageParametersEncoder);
    }

    /**
     * Check if given parameters are good for this mapper
     * @param parameters page parameters
     * @return true if mapper can handle this request with given parameters
     */
    protected abstract boolean isMatch(PageParameters parameters);

    /**
     * Ensure that request is for this mapper
     * @param requestHandler request handler for handle request
     * @return url if this mapper can handle this request
     */
    @Override
    public Url mapHandler(IRequestHandler requestHandler) {
        if (requestHandler instanceof IPageClassRequestHandler) {
            IPageClassRequestHandler handler = (IPageClassRequestHandler) requestHandler;
            PageParameters params = null;
            if (handler instanceof IPageRequestHandler) {
                IRequestablePage page = ((IPageRequestHandler)handler).getPage();
                if (page != null) {
                    params = page.getPageParameters();
                }
            }

            if (params == null) {
                params = handler.getPageParameters();
            }
            return isMatch(params) ? super.mapHandler(requestHandler) : null;
        } else {
            return super.mapHandler(requestHandler);
        }
    }
}
