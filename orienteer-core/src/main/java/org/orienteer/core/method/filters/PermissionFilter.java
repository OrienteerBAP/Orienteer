package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

/**
 * Filter by user permissions
 */
public class PermissionFilter implements IMethodFilter{

	/*
	public enum Permission{
		NONE(ORole.PERMISSION_NONE),
		CREATE(ORole.PERMISSION_CREATE),
		READ(ORole.PERMISSION_READ),
		UPDATE(ORole.PERMISSION_UPDATE),
		DELETE(ORole.PERMISSION_DELETE),
		EXECUTE(ORole.PERMISSION_EXECUTE),
		ALL(ORole.PERMISSION_ALL);
		
		private int permissionBit;
		public int permissionBit(){ return permissionBit;}
		private Permission(int permissionBit){this.permissionBit = permissionBit;}
	}
	*/
	//private Permission curPermission;
	private OrientPermission orientPermission;
	
	@Override
	public IMethodFilter setFilterData(String filterData) {
		//curPermission = Permission.valueOf(filterData);
		orientPermission = OrientPermission.valueOf(filterData);
		return this;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (orientPermission!=null){
			Object obj = dataObject.getDisplayObjectModel().getObject();
			if(obj instanceof ODocument){
				return OSecurityHelper.isAllowed((ODocument)obj, orientPermission);
			}else if(obj instanceof OClass){
				return OSecurityHelper.isAllowed((OClass)obj, orientPermission);
			}
		}
		return true;
	}
}
