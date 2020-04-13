package org.orienteer.core.hook;

import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.io.Serializable;
import java.util.*;

/**
 * OrientDB {@link ORecordHook} which allows to bind runtime callback to a document.
 * Important: callbacks can preserve serialization of a document (for example newly created doc)
 */
public class CallbackHook implements ORecordHook {

	private static final String CALLBACKS_FIELD = "__callbacks__";
	
//	private static final Set<TYPE> PRESERVE_IN_THREAD = new HashSet<TYPE>(Arrays.asList(TYPE.BEFORE_CREATE, TYPE.BEFORE_READ, TYPE.BEFORE_UPDATE, TYPE.BEFORE_DELETE));
//	private static final ThreadLocal<CallbacksHolder> PRESERVED = new ThreadLocal<>();
	
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

		private final Map<TYPE, List<ICallback>> callbacksMap;

		public CallbacksHolder() {
			callbacksMap = new HashMap<>();
		}

		public void registerCallback(TYPE type, ICallback callback) {
			List<ICallback> list = callbacksMap.computeIfAbsent(type, k -> new LinkedList<>());
			list.add(callback);
		}
		
		public boolean call(TYPE type, ODocument doc) {
			List<ICallback> callbacks = callbacksMap.remove(type);
			boolean ret = false;
			if (callbacks != null) {
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
			callbacksMap.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
		}
		
		public boolean isEmpty() {
			optimize();
			return callbacksMap.isEmpty();
		}
	}
	
	public static void registerCallback(ODocument doc, TYPE type, ICallback callback) {
		CallbacksHolder ret = doc.field(CALLBACKS_FIELD, CallbacksHolder.class);
		if (ret == null) {
			ret = new CallbacksHolder();
			//Field type should be CUSTOM: to allow callback be serialized and still preserved in a doc
			doc.field(CALLBACKS_FIELD, ret, OType.CUSTOM);
		}
		ret.registerCallback(type, callback);
	}

	@Override
	public RESULT onTrigger(TYPE iType, ORecord iRecord) {
		if (iRecord instanceof ODocument) {
			ODocument doc = (ODocument) iRecord;
			CallbacksHolder callbacks = getCallbacksHolder(doc);

			if (callbacks != null && executeCallbacks(iType, doc, callbacks)) {
				return RESULT.RECORD_CHANGED;
			}
		}
		return RESULT.RECORD_NOT_CHANGED;
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
	}

	@Override
	public void onUnregister() {
		
	}
	
	private CallbacksHolder getCallbacksHolder(ODocument doc) {
		CallbacksHolder callbacks = null;
		if (doc.containsField(CALLBACKS_FIELD)) {
			callbacks = doc.field(CALLBACKS_FIELD, CallbacksHolder.class);
		}
		return callbacks;
	}

	private boolean executeCallbacks(TYPE iType, ODocument doc, CallbacksHolder callbacks) {
		boolean docWasChanged = callbacks.call(iType, doc);
		if (callbacks.isEmpty()) {
			doc.removeField(CALLBACKS_FIELD);
		}
		return docWasChanged;
	}
}
