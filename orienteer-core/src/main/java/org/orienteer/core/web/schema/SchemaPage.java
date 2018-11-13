package org.orienteer.core.web.schema;

import org.orienteer.core.MountPath;
import org.orienteer.core.web.AbstractWidgetPage;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

/**
 * Page to show schema
 */
@MountPath(value="/schema", alt={"/classes"})
@RequiredOrientResource(value = OSecurityHelper.SCHEMA, permissions=OrientPermission.READ)
@RequiredOrientResource(value = OSecurityHelper.FEATURE, specific=SchemaPage.SCHEMA_FEATURE, permissions=OrientPermission.READ)
public class SchemaPage extends AbstractWidgetPage<Void> {
	
	public final static String SCHEMA_FEATURE="schema";

	@Override
	public String getDomain() {
		return "schema";
	}
	
	@Override
	protected boolean switchToDefaultTab() {
		if(super.switchToDefaultTab()) return true;
		else {
			return selectTab("classes");
		}
	}

}
