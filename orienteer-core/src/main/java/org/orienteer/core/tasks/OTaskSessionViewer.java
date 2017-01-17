package org.orienteer.core.tasks;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Viewer class for exiting {@link OTaskSession}
 */
public class OTaskSessionViewer {

	ODocument sessionDoc;
	String sessionId;
	
	//old session
	public OTaskSessionViewer(ODocument sessionDoc) {
		assert(sessionDoc!=null);
		this.sessionDoc = sessionDoc;
	}
	
	public void detachUpdate(){
		if (isDetached()){
			sessionDoc.field(OTaskSession.Field.STATUS.fieldName(), OTaskSession.Status.DETACHED);
			sessionDoc.save();
		}
	}
	
	private boolean isDetached() {
		if(OTaskSession.Status.RUNNING.name().equals(getField(OTaskSession.Field.STATUS))){
			return (!OTaskSession.getSessions().containsKey(getId())); 
		}
		return false;
	}
	
	public boolean isStoppable(){
		Object isStoppable  = getField(OTaskSession.Field.IS_STOPPABLE);
		Object status  = getField(OTaskSession.Field.STATUS);
		if (isStoppable != null && status!=null){
			if(OTaskSession.Status.RUNNING.name().equals(status)){
				return (Boolean)isStoppable;
			}
		}
		return false;
	}	
	
	public void stopSession() throws Exception{
		OTaskSession.getCallbacks().get(getId()).stop();
	}
	
	private String getId() {
		if(sessionId==null){
			sessionId=sessionDoc.getIdentity().toString();
		}
		return sessionId;
	}
	
	private Object getField(OTaskSession.Field field) {
		return sessionDoc.field(field.fieldName());
	}
	
}
