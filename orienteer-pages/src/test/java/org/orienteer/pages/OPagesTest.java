package org.orienteer.pages;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;
import org.orienteer.pages.web.EmbeddedWebPage;
import org.orienteer.pages.web.FullWebPage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class OPagesTest {

	@Inject
	private OrienteerTester tester;
	
	@Test
	@Sudo
	public void testPageRender() {
		ODatabaseDocument db = tester.getDatabase();
		List<ODocument> docs = db.query(new OSQLSynchQuery<>("select from OPage where path = ?"), "/testcase/");
		ODocument pageDoc = docs!=null && !docs.isEmpty()?docs.get(0):null;
		if(pageDoc==null) {
			pageDoc = new ODocument("OPage");
			pageDoc.field("path", "/testcase/");
			pageDoc.field("content", "TEST");
		}
		pageDoc.field("embedded", false);
		pageDoc.save();
		tester.executeUrl("testcase/");
		tester.assertRenderedPage(FullWebPage.class);
		pageDoc.field("embedded", true);
		pageDoc.save();
		tester.executeUrl("testcase/");
		tester.assertRenderedPage(EmbeddedWebPage.class);
	}
}
