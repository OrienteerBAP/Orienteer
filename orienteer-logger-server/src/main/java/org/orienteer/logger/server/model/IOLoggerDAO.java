package org.orienteer.logger.server.model;

import java.util.Date;
import java.util.List;

import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.logger.server.OLoggerModule;
import org.orienteer.transponder.annotation.Query;
import org.orienteer.transponder.annotation.common.Sudo;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * DAO interface for OLogger Server functionality
 */
@ProvidedBy(DAOProvider.class)
public interface IOLoggerDAO {
	
	public static final IOLoggerDAO INSTANCE = DAO.dao(IOLoggerDAO.class);

	@Sudo
	@Query("select from "+IOLoggerEventModel.CLASS_NAME+" where correlationId = :correlationId")
	public List<IOLoggerEventModel> getEventsByCorrelationId(String correlationId);
	
	@Sudo
	@Query("select from "+IOLoggerEventDispatcherModel.CLASS_NAME+" where alias = :alias")
	public IOLoggerEventDispatcherModel getOLoggerEventDispatcher(String alias);
	
	@Sudo
	@Query("select from "+IOLoggerEventFilteredDispatcherModel.CLASS_NAME+" where alias = :alias")
	public IOLoggerEventFilteredDispatcherModel getOLoggerEventFilteredDispatcher(String alias);
	
	@Sudo
	@Query("select from "+IOLoggerEventMailDispatcherModel.CLASS_NAME+" where alias = :alias")
	public IOLoggerEventMailDispatcherModel getOLoggerEventMailDispatcher(String alias);
	
	@Sudo
	@Query("select from "+IOCorrelationIdGeneratorModel.CLASS_NAME+" where alias = :alias")
	public IOCorrelationIdGeneratorModel getOCorrelationIdGenerator(String alias);
	
	@Sudo
	@Query("select from "+OLoggerModule.ILoggerModuleConfiguration.CLASS_NAME+" where name = '"+OLoggerModule.NAME+"'")
	public OLoggerModule.ILoggerModuleConfiguration getModule(ODatabaseDocument db);
	
	@Sudo
	public default IOLoggerEventModel storeOLoggerEvent(String eventJson) {
		IOLoggerEventModel event = DAO.create(IOLoggerEventModel.class);
		event.getDocument().fromJSON(eventJson);
		event.save();
        return event;
    }
}
