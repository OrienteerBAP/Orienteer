package org.orienteer.core.util;

import org.orienteer.core.OrienteerWebSession;

import com.orientechnologies.orient.core.record.impl.ODocument;

import lombok.experimental.UtilityClass;

/**
 * Collection of SQL functions from Orienteer. Prefix is 'o', so to invoke user() sql function ouser() should be used
 */
@UtilityClass
public class OSQLFunctions {

	public ODocument user() {
		OrienteerWebSession session = OrienteerWebSession.get();
		return session!=null?session.getUserAsODocument():null;
	}
}
