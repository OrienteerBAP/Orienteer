package org.orienteer.core.tasks;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Avoid many spellchecks and template warnings
 *
 */
public class OTaskSessionImpl extends OTaskSession<OTaskSessionImpl>{
	
	public OTaskSessionImpl(ODocument doc){
		super(doc);
	}
}
