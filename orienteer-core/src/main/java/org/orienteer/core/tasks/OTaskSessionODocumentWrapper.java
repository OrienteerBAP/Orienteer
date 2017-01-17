package org.orienteer.core.tasks;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Wrapper class for exiting {@link OTaskSession} document in DB
 */
public class OTaskSessionODocumentWrapper extends ODocumentWrapper {

	String sessionId;
	
	//old session
	public OTaskSessionODocumentWrapper(ODocument sessionDoc) {
		super(sessionDoc);
	}
	
	public void detachUpdate(){
		if (isDetached()){
			document.field(OTaskSession.Field.STATUS.fieldName(), OTaskSession.Status.DETACHED);
			document.save();
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
			sessionId=getDocument().getIdentity().toString();
		}
		return sessionId;
	}
	
	private Object getField(OTaskSession.Field field) {
		return document.field(field.fieldName());
	}
	
}
