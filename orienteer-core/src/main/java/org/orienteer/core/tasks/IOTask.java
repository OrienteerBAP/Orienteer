package org.orienteer.core.tasks;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Base task for tasks
 *
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOTask.CLASS_NAME, 
			isAbstract = true,
            displayable = {"name", "description"})
public interface  IOTask extends IODocumentWrapper {
	public static final Logger LOG = LoggerFactory.getLogger(IOTask.class);
	public static final String CLASS_NAME = "OTask";
	public static final CustomAttribute TASK_JAVA_CLASS_ATTRIBUTE = CustomAttribute.create("orienteer.taskclass", OType.STRING, null, true, true);

	public String getName();
	
	public String getDescription();
	
	@DAOField(defaultValue = "true")
	public boolean isAutodeleteSessions();
	public IOTask setAutodeleteSessions(boolean value);
	
	@DAOField(linkedClass = OTaskSessionRuntime.TASK_SESSION_CLASS)
	public List<ODocument> getSessions();
	
	public OTaskSessionRuntime startNewSession();
}
