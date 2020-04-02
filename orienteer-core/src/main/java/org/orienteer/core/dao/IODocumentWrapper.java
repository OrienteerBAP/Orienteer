package org.orienteer.core.dao;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.OrienteerWebApplication;

import java.io.Serializable;

/**
 * Interface which mirror methods of {@link ODocumentWrapper} 
 */
public interface IODocumentWrapper extends Serializable {

	static <T extends IODocumentWrapper> T get(Class<T> wrapperClass) {
		return OrienteerWebApplication.lookupApplication().getServiceInstance(wrapperClass);
	}

	static <T extends IODocumentWrapper> T get(Class<T> wrapperClass, ODocument document) {
		T wrapper = get(wrapperClass);
		wrapper.fromStream(document);
		return wrapper;
	}

	void fromStream(final ODocument iDocument);
	ODocument toStream();
	<R extends IODocumentWrapper> R load();
	<R extends IODocumentWrapper> R load(final String iFetchPlan);
	<R extends IODocumentWrapper> R load(final String iFetchPlan, final boolean iIgnoreCache);
	<R extends IODocumentWrapper> R load(final String iFetchPlan, final boolean iIgnoreCache, final boolean loadTombstone);
	<R extends IODocumentWrapper> R reload();
	<R extends IODocumentWrapper> R reload(final String iFetchPlan);
	<R extends IODocumentWrapper> R reload(final String iFetchPlan, final boolean iIgnoreCache);
	<R extends IODocumentWrapper> R save();
	<R extends IODocumentWrapper> R save(final String iClusterName);
	ODocument getDocument();
}