package org.orienteer.core.web.schema;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
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
public class SchemaPage extends AbstractWidgetPage<Void> {

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
