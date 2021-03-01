package org.orienteer.core.dao.dm;

import java.util.HashSet;
import java.util.Set;

import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;

/**
 * Interface helper to simplify work with ORestricted 
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = OSecurityShared.RESTRICTED_CLASSNAME)
public interface IORestricted {
	
	@DAOField("_allow")
	public Set<OIdentifiable> getAllowAll();
	@DAOField("_allow")
	public IORestricted setAllowAll(Set<OIdentifiable> identifiables);
	
	public default IORestricted addToAllowAll(OIdentifiable identifiable) {
		return setAllowAll(addSafely(getAllowAll(), identifiable));
	}
	
	public default IORestricted remoteFromAllowAll(OIdentifiable identifiable) {
		return setAllowAll(removeSafely(getAllowAll(), identifiable));
	}
	
	@DAOField("_allowRead")
	public Set<OIdentifiable> getAllowRead();
	@DAOField("_allowRead")
	public IORestricted setAllowRead(Set<OIdentifiable> identifiables);
	
	public default IORestricted addToAllowRead(OIdentifiable identifiable) {
		return setAllowRead(addSafely(getAllowRead(), identifiable));
	}
	
	public default IORestricted remoteFromAllowRead(OIdentifiable identifiable) {
		return setAllowRead(removeSafely(getAllowRead(), identifiable));
	}
	
	@DAOField("_allowUpdate")
	public Set<OIdentifiable> getAllowUpdate();
	@DAOField("_allowUpdate")
	public IORestricted setAllowUpdate(Set<OIdentifiable> identifiables);
	
	public default IORestricted addToAllowUpdate(OIdentifiable identifiable) {
		return setAllowUpdate(addSafely(getAllowUpdate(), identifiable));
	}
	
	public default IORestricted remoteFromAllowUpdate(OIdentifiable identifiable) {
		return setAllowUpdate(removeSafely(getAllowUpdate(), identifiable));
	}
	
	@DAOField("_allowDelete")
	public Set<OIdentifiable> getAllowDelete();
	@DAOField("_allowDelete")
	public IORestricted setAllowDelete(Set<OIdentifiable> identifiables);
	
	public default IORestricted addToAllowDelete(OIdentifiable identifiable) {
		return setAllowDelete(addSafely(getAllowDelete(), identifiable));
	}
	
	public default IORestricted remoteFromAllowDelete(OIdentifiable identifiable) {
		return setAllowDelete(removeSafely(getAllowDelete(), identifiable));
	}
	
	public static Set<OIdentifiable> addSafely(Set<OIdentifiable> set, OIdentifiable identifiable) {
		if(set==null) set = new HashSet<OIdentifiable>();
		set.add(identifiable);
		return set;
	}
	
	public static Set<OIdentifiable> removeSafely(Set<OIdentifiable> set, OIdentifiable identifiable) {
		if(set==null) return set;
		set.remove(identifiable);
		return set;
	}
}
