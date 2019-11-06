package org.orienteer.core.service;

import com.google.inject.ImplementedBy;
import org.apache.wicket.Page;
import static org.orienteer.core.module.PerspectivesModule.OPerspective;

/**
 * Perspective service for customize each page depends on given perspective
 */
@ImplementedBy(DefaultPerspectiveService.class)
public interface IPerspectiveService {

    String getCSS(OPerspective perspective, Page page);
    String getJS(OPerspective perspective, Page page);
}
