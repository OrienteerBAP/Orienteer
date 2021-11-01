package org.orienteer.core.dao.dm;

import java.util.HashSet;
import java.util.Set;

import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.IODocumentWrapper;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;

/**
 * Interface helper to simplify work with ORestricted 
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = OSecurityShared.RESTRICTED_CLASSNAME)
public interface IORestricted extends IODocumentWrapper {
	
	@EntityProperty("_allow")
	public Set<OIdentifiable> getAllowAll();
	@EntityProperty("_allow")
	public IORestricted setAllowAll(Set<OIdentifiable> identifiables);
	
	public default IORestricted addToAllowAll(OIdentifiable identifiable) {
		return setAllowAll(addSafely(getAllowAll(), identifiable));
	}
	
	public default IORestricted remoteFromAllowAll(OIdentifiable identifiable) {
		return setAllowAll(removeSafely(getAllowAll(), identifiable));
	}
	
	@EntityProperty("_allowRead")
	public Set<OIdentifiable> getAllowRead();
	@EntityProperty("_allowRead")
	public IORestricted setAllowRead(Set<OIdentifiable> identifiables);
	
	public default IORestricted addToAllowRead(OIdentifiable identifiable) {
		return setAllowRead(addSafely(getAllowRead(), identifiable));
	}
	
	public default IORestricted remoteFromAllowRead(OIdentifiable identifiable) {
		return setAllowRead(removeSafely(getAllowRead(), identifiable));
	}
	
	@EntityProperty("_allowUpdate")
	public Set<OIdentifiable> getAllowUpdate();
	@EntityProperty("_allowUpdate")
	public IORestricted setAllowUpdate(Set<OIdentifiable> identifiables);
	
	public default IORestricted addToAllowUpdate(OIdentifiable identifiable) {
		return setAllowUpdate(addSafely(getAllowUpdate(), identifiable));
	}
	
	public default IORestricted remoteFromAllowUpdate(OIdentifiable identifiable) {
		return setAllowUpdate(removeSafely(getAllowUpdate(), identifiable));
	}
	
	@EntityProperty("_allowDelete")
	public Set<OIdentifiable> getAllowDelete();
	@EntityProperty("_allowDelete")
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
