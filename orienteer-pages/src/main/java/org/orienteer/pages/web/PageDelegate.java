package org.orienteer.pages.web;

import com.orientechnologies.common.concur.resource.OPartitionedObjectPool;
import com.orientechnologies.orient.core.command.script.OScriptManager;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.DefaultMarkupCacheKeyProvider;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.resource.AbstractStringResourceStream;
import org.apache.wicket.util.resource.IFixedLocationResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.pages.module.PagesModule;
import org.orienteer.pages.wicket.mapper.OPageParametersEncoder;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Delegate for functions which commonly used in wrapped pages
 */
public class PageDelegate implements IMarkupResourceStreamProvider, IMarkupCacheKeyProvider, IDetachable, IClusterable{
	
	private static final IMarkupResourceStreamProvider DEFAULT_MARKUP_PROVIDER = new DefaultMarkupResourceStreamProvider();
	private static final IMarkupCacheKeyProvider DEFAULT_MARKUP_CACHKEY_PROVIDER = new DefaultMarkupCacheKeyProvider();
	
	private static class OPageResourceStream extends AbstractStringResourceStream implements IFixedLocationResourceStream {

		private String content;
		private String location;
		
		public OPageResourceStream(ODocument page, String wrapTag) {
			super("text/html");
			content = (String) page.field(PagesModule.OPROPERTY_CONTENT);
			if(!Strings.isEmpty(wrapTag)) content = "<"+wrapTag+">"+content+"</"+wrapTag+">";
			location="OPage"+page.getIdentity()+"?v"+page.getVersion();
		}
		@Override
		public String locationAsString() {
			return location;
		}

		@Override
		protected String getString() {
			return content;
		}
		
	}
	
	private final WebPage page;
	private final ODocumentModel pageDocumentModel;
	
	public PageDelegate(WebPage page, PageParameters parameters) {
		this(page, parameters.get(OPageParametersEncoder.PAGE_IDENTITY).toString(), parameters.get("rid").toOptionalString());
	}
	
	public PageDelegate(WebPage page, String pageOrid, String docOrid) {
		this(page, new ORecordId(pageOrid), Strings.isEmpty(docOrid)?null:new ORecordId(docOrid));
	}
	
	public PageDelegate(WebPage page, ORID pageOrid, ORID docOrid) {
		this.page = page;
		this.pageDocumentModel = new ODocumentModel(pageOrid);
		ODocument doc = (ODocument)(docOrid!=null?docOrid.getRecord():pageDocumentModel.getObject().field(PagesModule.OPROPERTY_DOCUMENT));
		if(doc!=null) page.setDefaultModel(new ODocumentModel(doc));
		String script = pageDocumentModel.getObject().field(PagesModule.OPROPERTY_SCRIPT);
		if(!Strings.isEmpty(script)) {
			OrientDB context = OrienteerWebApplication.lookupApplication().getOrientDbSettings().getContext();
			OScriptManager scriptManager = OrientDBInternal.extract(context).getScriptManager();
			ODatabaseDocumentInternal db = OrienteerWebSession.get().getDatabaseDocumentInternal();
			final OPartitionedObjectPool.PoolEntry<ScriptEngine> entry = 
					scriptManager.acquireDatabaseEngine(db.getName(), "javascript");
			final ScriptEngine scriptEngine = entry.object;
			Bindings binding = null;
		    try {
				binding = scriptManager.bind(scriptEngine, scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE),
												 db, null, null);
				binding.put("page", page);
				binding.put("pageDoc", pageDocumentModel.getObject());
				binding.put("doc", doc);
				try {
					scriptEngine.eval(script);
				} catch (ScriptException e) {
					scriptManager.throwErrorMessage(e, script);
				}
			} finally {
				if (scriptManager != null && binding != null) {
					scriptManager.unbind(scriptEngine, binding, null, null);
					scriptManager.releaseDatabaseEngine("javascript", db.getName(), entry);
				}
			}
		}
	}

	@Override
	public void detach() {
		pageDocumentModel.detach();
	}
	
	public ODocumentModel getPageDocumentModel() {
		return pageDocumentModel;
	}

	@Override
	public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
		ODocument pageDoc = pageDocumentModel.getObject();
		return DEFAULT_MARKUP_CACHKEY_PROVIDER.getCacheKey(container, containerClass)+"_"+pageDoc.getIdentity().toString()+"_v"+pageDoc.getVersion();
	}

	@Override
	public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass) {
		return getMarkupResourceStream(container, containerClass, null);
	}
	
	public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass, String wrapTag) {
		if(container.getClass().equals(containerClass)) {
			return new OPageResourceStream(pageDocumentModel.getObject(), wrapTag);
		} else {
			return DEFAULT_MARKUP_PROVIDER.getMarkupResourceStream(container, containerClass);
		}
	}
	
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(pageDocumentModel, "title");
	}
	
}
