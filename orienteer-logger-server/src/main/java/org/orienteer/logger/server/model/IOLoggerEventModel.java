package org.orienteer.logger.server.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOHandler;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.handler.InvocationChain;

/**
 * Wrapper for logger event
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOLoggerEventModel.CLASS_NAME,
			nameProperty = "dateTime",
			displayable = {"application", "nodeId", "correlationId", "dateTime", "hostName", "summary"})
public interface IOLoggerEventModel extends IODocumentWrapper {

    public static final String CLASS_NAME = "OLoggerEvent";
    
    public static final String PROP_REMOTE_ADDRESS = "remoteAddress";
    public static final String PROP_HOST_NAME = "hostName";
    public static final String PROP_USERNAME = "username";
    public static final String PROP_SEEDCLASS = "seedClass";
    public static final String PROP_CLIENT_URL = "clientUrl";
    
    /**
     * {@link IMethodHandler} to check that source contains link to a ODocument
     */
    public static class InvokeCheckSource implements IMethodHandler<ODocumentWrapper> {

		@Override
		public Optional<Object> handle(ODocumentWrapper target, Object proxy, Method method, Object[] args,
				InvocationChain<ODocumentWrapper> chain) throws Throwable {
			Optional<Object> ret = chain.handle(target, proxy, method, args);
			((IOLoggerEventModel) proxy).checkForSourceDoc();
			return ret;
		}
    	
    }

    public String getEventId();
    public IOLoggerEventModel setEventId(String id);

    public String getApplication();
    public IOLoggerEventModel setApplication(String application);

    public String getNodeId();
    public IOLoggerEventModel setNodeId(String nodeId);

    public String getCorrelationId();
    public IOLoggerEventModel setCorrelationId(String correlationId);

    @DAOField(type=OType.DATETIME)
    public Date getDateTime();
    public IOLoggerEventModel setDateTime(Date datetime);
    
    public default Instant getDateTimeAsInstant() {
    	Date date = getDateTime();
    	return date!=null?date.toInstant():null;
    }
    
    public default IOLoggerEventModel setDateTimeAsInstant(Instant instant) {
    	return setDateTime(instant!=null?Date.from(instant):null);
    }

    public String getRemoteAddress();
    public IOLoggerEventModel setRemoteAddress(String address);

    public String getHostName();
    public IOLoggerEventModel setHostName(String hostName);

    public String getUsername();
    public IOLoggerEventModel setUsername(String username);

    public String getClientUrl();
    public IOLoggerEventModel setClientUrl(String clientUrl);

    @DAOField(visualization = UIVisualizersRegistry.VISUALIZER_RESTRICTED_WIDTH)
    public String getSummary();

    @DAOField(visualization = UIVisualizersRegistry.VISUALIZER_CODE, uiReadOnly = true)
    public String getMessage();
    public IOLoggerEventModel setMessage(String message);

    public String getSeedClass();
    public IOLoggerEventModel setSeedClass(String seedClass);
    
    public String getSource();
    @DAOHandler(InvokeCheckSource.class)
    public IOLoggerEventModel setSource(String source);
    
    @DAOField(type=OType.LINK)
    public ODocument getSourceDoc();
    public IOLoggerEventModel setSourceDoc(OIdentifiable source);
    
    public default void checkForSourceDoc() {
    	String source = getSource();
    	if(source!=null && ORecordId.isA(source)) {
    		setSourceDoc(new ORecordId(source));
    	}
    }
}
