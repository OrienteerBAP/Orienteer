package org.orienteer.devutils.web;

import org.orienteer.core.MountPath;
import org.orienteer.core.web.AbstractWidgetPage;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Page for devtools
 */
@MountPath(value="/tools")
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
@RequiredOrientResource(value = OSecurityHelper.FEATURE, specific = "devutils")
public class ToolsPage extends AbstractWidgetPage<Void> {

	@Override
	public String getDomain() {
		return "tools";
	}
	
	@Override
		protected boolean switchToDefaultTab() {
			return super.switchToDefaultTab()?true:selectTab("console");
		}

}
