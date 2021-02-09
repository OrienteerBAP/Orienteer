package org.orienteer.logger.server.model;

import java.util.Date;
import java.util.List;

import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOHandler;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;
import org.orienteer.core.dao.handler.extra.SudoMethodHandler;
import org.orienteer.logger.server.OLoggerModule;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * DAO interface for OLogger Server functionality
 */
@ProvidedBy(DAOProvider.class)
@DAOHandler(SudoMethodHandler.class)
public interface IOLoggerDAO {
	
	public static final IOLoggerDAO INSTANCE = DAO.dao(IOLoggerDAO.class);

	@Query("select from "+IOLoggerEventModel.CLASS_NAME+" where correlationId = :correlationId")
	public List<IOLoggerEventModel> getEventsByCorrelationId(String correlationId);
	
	@Query("select from "+IOLoggerEventDispatcherModel.CLASS_NAME+" where alias = :alias")
	public IOLoggerEventDispatcherModel getOLoggerEventDispatcher(String alias);
	
	@Query("select from "+IOLoggerEventFilteredDispatcherModel.CLASS_NAME+" where alias = :alias")
	public IOLoggerEventFilteredDispatcherModel getOLoggerEventFilteredDispatcher(String alias);
	
	@Query("select from "+IOLoggerEventMailDispatcherModel.CLASS_NAME+" where alias = :alias")
	public IOLoggerEventMailDispatcherModel getOLoggerEventMailDispatcher(String alias);
	
	@Query("select from "+IOCorrelationIdGeneratorModel.CLASS_NAME+" where alias = :alias")
	public IOCorrelationIdGeneratorModel getOCorrelationIdGenerator(String alias);
	
	@Query("select from "+OLoggerModule.ILoggerModuleConfiguration.CLASS_NAME+" where name = '"+OLoggerModule.NAME+"'")
	public OLoggerModule.ILoggerModuleConfiguration getModule(ODatabaseDocument db);
	
	public default IOLoggerEventModel storeOLoggerEvent(String eventJson) {
		IOLoggerEventModel event = DAO.create(IOLoggerEventModel.class);
		event.getDocument().fromJSON(eventJson);
		event.save();
        return event;
    }
}
