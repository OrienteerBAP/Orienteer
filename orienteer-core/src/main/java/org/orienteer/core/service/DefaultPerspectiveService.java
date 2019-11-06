package org.orienteer.core.service;

import com.google.inject.Singleton;
import org.apache.wicket.Page;
import org.orienteer.core.module.PerspectivesModule;

/**
 * Default implementation of {@link IPerspectiveService}
 */
@Singleton
public class DefaultPerspectiveService implements IPerspectiveService {
    @Override
    public String getCSS(PerspectivesModule.OPerspective perspective, Page page) {
        return null;
    }

    @Override
    public String getJS(PerspectivesModule.OPerspective perspective, Page page) {
        return null;
    }
}
