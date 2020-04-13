package org.orienteer.pages;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;
import org.orienteer.pages.web.EmbeddedWebPage;
import org.orienteer.pages.web.FullWebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class OPagesTest {

	private static final Logger LOG = LoggerFactory.getLogger(OPagesTest.class);

	@Inject
	private OrienteerTester tester;
	
	@Test
	@Sudo
	public void testPageRender() {
		ODatabaseDocument db = tester.getDatabase();
		OResultSet result = db.query("select from OPage where path = ?", "/testcase/");
		ODocument pageDoc = result.hasNext() ? (ODocument) result.next().getElement().orElse(null) : null;
		if (pageDoc == null) {
			pageDoc = new ODocument("OPage");
			pageDoc.field("path", "/testcase/");
			pageDoc.field("content", "TEST");
		}
		pageDoc.field("embedded", false);
		pageDoc.save();



		db.getHooks().keySet().forEach(hook -> LOG.info("[1] Registered hook: {}", hook));

		IOrientDbSettings settings = OrienteerWebApplication.lookupApplication().getOrientDbSettings();

		ODatabaseSession db2 = settings.getContext().cachedPool(settings.getDbName(), settings.getAdminUserName(), settings.getAdminPassword()).acquire();

		db2.getHooks().keySet().forEach(hook -> LOG.info("[2] Registered hook: {}", hook));


		tester.executeUrl("testcase/");
		tester.assertRenderedPage(FullWebPage.class);
		pageDoc.field("embedded", true);
		pageDoc.save();
		tester.executeUrl("testcase/");
		tester.assertRenderedPage(EmbeddedWebPage.class);
	}
}
