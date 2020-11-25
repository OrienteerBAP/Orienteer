package org.orienteer.rproxy;

import java.util.function.Consumer;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IOrienteerModule} for 'orienteer-rproxy' module
 */
public class ORProxyModule extends AbstractOrienteerModule{

	protected ORProxyModule() {
		super("orienteer-rproxy", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		DAO.describe(helper, IORProxyEndPoint.class);
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		app.getOrientDbSettings().addORecordHooks(ORProxyHook.class);
		iterateOverEndPounts(db, endPoint -> ORProxyResource.mount(app, endPoint));
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		app.getOrientDbSettings().removeORecordHooks(ORProxyHook.class);
		iterateOverEndPounts(db, endPoint -> ORProxyResource.unmount(app, endPoint));
	}
	
	protected void iterateOverEndPounts(ODatabaseSession db, Consumer<IORProxyEndPoint> consumer) {
		db.query("select from ORProxyEndPoint").forEachRemaining(r -> {
			if(r.isRecord()) {
				IORProxyEndPoint endPoint = DAO.provide(IORProxyEndPoint.class, (ODocument) r.getRecord().get());
				consumer.accept(endPoint);
			}
		});
	}
	
}
