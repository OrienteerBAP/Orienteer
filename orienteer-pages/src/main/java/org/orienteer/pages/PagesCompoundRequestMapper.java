package org.orienteer.pages;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.CompoundRequestMapper;
import org.orienteer.pages.module.PagesModule;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Main {@link IRequestMapper} for Orienteer Pages
 */
public class PagesCompoundRequestMapper extends CompoundRequestMapper {

	public PagesCompoundRequestMapper() {
		super();
		initialPagesLoad();
	}
	
	protected void initialPagesLoad() {
		new DBClosure<Boolean>() {

			@Override
			protected Boolean execute(ODatabaseDocument db) {
				List<ODocument> pages = db.query(new OSQLSynchQuery<ODocument>("select from "+PagesModule.OCLASS_PAGE));
				for (ODocument pageDoc : pages) {
					if(pageDoc.field(PagesModule.OPROPERTY_PATH)!=null) add(pageDoc);
				}
				return true;
			}
		}.execute();
	}
	


	public PagesCompoundRequestMapper add(OIdentifiable pageDocId) {
		super.add(new PagesMountedMapper(pageDocId));
		return this;
	}

	public PagesCompoundRequestMapper remove(OIdentifiable pageDocId) {
		for(IRequestMapper mapper : this) {
			if(mapper instanceof PagesMountedMapper 
					&& ((PagesMountedMapper)mapper).isServing(pageDocId)) {
				remove(mapper);
				break;
			}
		}
		return this;
	}
	
}
