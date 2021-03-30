package org.orienteer.core.dao;

import java.io.Serializable;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Interface which mirror methods of {@link ODocumentWrapper} 
 */
public interface IODocumentWrapper extends Serializable {
	
	public void fromStream(final ODocument iDocument);
	public ODocument toStream();
	public <R extends IODocumentWrapper> R load(final String iFetchPlan, final boolean iIgnoreCache);
	public <R extends IODocumentWrapper> R reload();
	public <R extends IODocumentWrapper> R reload(final String iFetchPlan);
	public <R extends IODocumentWrapper> R reload(final String iFetchPlan, final boolean iIgnoreCache);
	public <R extends IODocumentWrapper> R save();
	public <R extends IODocumentWrapper> R save(final String iClusterName);
	public ODocument getDocument();
}