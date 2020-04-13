package org.orienteer.core.hook;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * OrientDB {@link ORecordHook} which allows to bind runtime callback to a document.
 * Important: callbacks can preserve serialization of a document (for example newly created doc)
 */
public class CallbackHook implements ORecordHook {

	private static final String CALLBACKS_FIELD = "__callbacks__";
	
	private static final Set<TYPE> PRESERVE_IN_THREAD = new HashSet<TYPE>(Arrays.asList(TYPE.BEFORE_CREATE, TYPE.BEFORE_READ, TYPE.BEFORE_UPDATE, TYPE.BEFORE_DELETE)); 
	
	private static final ThreadLocal<CallbacksHolder> PRESERVED = new ThreadLocal<CallbackHook.CallbacksHolder>(); 
	
	/**
	 * Callback to be executed on event
	 */
	public static interface ICallback extends Serializable {
		/**
		 * Called when required event occur
		 * @param iType type of an event
		 * @param doc {@link ODocument}
		 * @return true if document was changed, false - if not
		 */
		public boolean call(TYPE iType, ODocument doc);
	}
	
	private static class CallbacksHolder implements Serializable {
		private final Map<TYPE, List<ICallback>> callbacksMap = new HashMap<ORecordHook.TYPE, List<ICallback>>();
		public void registerCallback(TYPE type, ICallback callback) {
			List<ICallback> list = callbacksMap.get(type);
			if(list==null) {
				list = new LinkedList<CallbackHook.ICallback>();
				callbacksMap.put(type, list);
			}
			list.add(callback);
		}
		
		public boolean call(TYPE type, ODocument doc) {
			List<ICallback> callbacks = callbacksMap.remove(type);
			boolean ret = false;
			if(callbacks!=null) {
				for (ICallback iCallback : callbacks) {
					ret= iCallback.call(type, doc) || ret;
				}
			}
			return ret;
		}
		
		public boolean contains(TYPE type) {
			return callbacksMap.containsKey(type);
		}
		
		private void optimize() {
			if(callbacksMap.isEmpty()) return;
			Iterator<Map.Entry<TYPE, List<ICallback>>> it = callbacksMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<TYPE, List<ICallback>> entry = it.next();
				List<ICallback> list = entry.getValue();
				if(list==null || list.isEmpty()) it.remove();
			}
		}
		
		public boolean isEmpty() {
			optimize();
			return callbacksMap.isEmpty();
		}
	}
	
	public static void registerCallback(ODocument doc, TYPE type, ICallback callback) {
		CallbacksHolder ret = (CallbacksHolder) doc.field(CALLBACKS_FIELD);
		if(ret==null) {
			ret = new CallbacksHolder();
			//Field type should be CUSTOM: to allow callback be serialized and still preserved in a doc
			doc.field(CALLBACKS_FIELD, ret, OType.CUSTOM);
		}
		ret.registerCallback(type, callback);
	}

	@Override
	public RESULT onTrigger(TYPE iType, ORecord iRecord) {
		if(!(iRecord instanceof ODocument)) return RESULT.RECORD_NOT_CHANGED;
		ODocument doc = (ODocument) iRecord;
		CallbacksHolder callbacks=null;
		if(doc.containsField(CALLBACKS_FIELD)) callbacks = (CallbacksHolder) doc.field(CALLBACKS_FIELD);
		else callbacks = PRESERVED.get();
		if(callbacks==null) return RESULT.RECORD_NOT_CHANGED; 
		boolean docWasChanged = callbacks.call(iType, doc);
		PRESERVED.remove();
		if(PRESERVE_IN_THREAD.contains(iType)) {
			doc.removeField(CALLBACKS_FIELD);
			PRESERVED.set(callbacks);
		}
		if(callbacks.isEmpty()) doc.removeField(CALLBACKS_FIELD);
		return docWasChanged?RESULT.RECORD_CHANGED:RESULT.RECORD_NOT_CHANGED;
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
	}

	@Override
	public void onUnregister() {
		
	}
	

}
