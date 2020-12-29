package org.orienteer.core.hook;

import com.orientechnologies.orient.core.db.record.ORecordElement.STATUS;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentEntry;
import com.orientechnologies.orient.core.record.impl.ODocumentInternal;

import java.io.Serializable;
import java.util.*;

import org.joor.Reflect;

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
	@FunctionalInterface
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
		getOrCreateCallbacksHolder(doc).registerCallback(type, callback);
	}

	@Override
	public RESULT onTrigger(TYPE iType, ORecord iRecord) {
		if (iRecord instanceof ODocument) {
			ODocument doc = (ODocument) iRecord;
			CallbacksHolder callbacks = getCallbacksHolder(doc);

			if (callbacks != null && !callbacks.isEmpty() && executeCallbacks(iType, doc, callbacks)) {
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
	
	private static CallbacksHolder getOrCreateCallbacksHolder(ODocument doc) {
		CallbacksHolder ret = getCallbacksHolder(doc);
		if(ret==null) {
			ret = new CallbacksHolder();
			doc.field(CALLBACKS_FIELD, ret, OType.CUSTOM);
		}
		return ret;
	}
	
	private static CallbacksHolder getCallbacksHolder(ODocument doc) {
		if (ODocumentInternal.rawContainsField(doc, CALLBACKS_FIELD)) {
			Object value = ODocumentInternal.rawEntry(doc, CALLBACKS_FIELD).value;
			if(value instanceof CallbacksHolder) 
				return (CallbacksHolder)value;
		} else if(!doc.getInternalStatus().equals(STATUS.NOT_LOADED) && doc.hasProperty(CALLBACKS_FIELD)) {
			return doc.field(CALLBACKS_FIELD, CallbacksHolder.class);
		}
		return null;
	}

	private boolean executeCallbacks(TYPE iType, ODocument doc, CallbacksHolder callbacks) {
		boolean docWasChanged = callbacks.call(iType, doc);
		if (callbacks.isEmpty()) {
			if(doc.isTrackingChanges()) doc.undo(CALLBACKS_FIELD);
			else {
				ODocumentEntry entry = ODocumentInternal.rawEntry(doc, CALLBACKS_FIELD);
				if(entry!=null && entry.exists()) {
					entry.setExists(false);
					entry.value = null;
					Reflect.on(doc).set("fieldSize", ODocumentInternal.rawEntries(doc).size());
				}
			}
		}
		return docWasChanged;
	}
}
