package org.orienteer.core.service.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbSettings;

/**
 * Guice enabled {@link IOrientDbSettings}
 */
public class GuiceOrientDbSettings extends OrientDbSettings
{

	@Inject
	@Override
	public void setDBUrl(@Named("orientdb.url") String url) {
		super.setDBUrl(url);
	}


	@Inject(optional=true)
	@Override
	public void setGuestUserName(@Named("orientdb.guest.username") String userName) {
		super.setGuestUserName(userName);
	}

	@Inject(optional=true)
	@Override
	public void setGuestPassword(@Named("orientdb.guest.password")String password) {
		super.setGuestPassword(password);
	}

	@Inject(optional=true)
	@Override
	public void setAdminUserName(@Named("orientdb.admin.username") String userName) {
		super.setAdminUserName(userName);
	}

	@Inject(optional=true)
	@Override
	public void setAdminPassword(@Named("orientdb.admin.password")String password) {
		super.setAdminPassword(password);
	}
	
	@Inject(optional=true)
	@Deprecated
    public void setDBUserName(@Named("orientdb.db.username") String userName) {
		setGuestUserName(userName);
	}

    @Inject(optional=true)
    @Deprecated
    public void setDBUserPassword(@Named("orientdb.db.password")String password) {
    	setGuestPassword(password);
    }

    @Inject(optional=true)
    @Deprecated
    public void setDBInstallatorUserName(@Named("orientdb.db.installator.username") String userName) {
    	setAdminUserName(userName);
    }

    @Inject(optional=true)
    @Deprecated
    public void setDBInstallatorUserPassword(@Named("orientdb.db.installator.password")String password) {
    	setAdminPassword(password);
    }


	@Inject(optional=true)
	@Override
	public void setOrientDBRestApiUrl(@Named("orientdb.rest.url") String orientDbRestApiUrl) {
		super.setOrientDBRestApiUrl(orientDbRestApiUrl);
	}
	
}
