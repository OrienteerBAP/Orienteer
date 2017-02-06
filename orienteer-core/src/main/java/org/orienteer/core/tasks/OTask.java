package org.orienteer.core.tasks;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass.ATTRIBUTES;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Base task for tasks
 *
 */
public abstract class OTask extends ODocumentWrapper {
	private static final Logger LOG = LoggerFactory.getLogger(OTask.class);
	public static final String TASK_CLASS = "OTask";
	public static final CustomAttribute TASK_JAVA_CLASS_ATTRIBUTE = CustomAttribute.create("orienteer.taskclass", OType.STRING, null, true, true);

	/**
	 * data fields
	 */
	public enum Field{
		NAME("name"),
		DESCRIPTION("description"),
		SESSIONS("sessions"),
		AUTODELETE_SESSIONS("autodeleteSessions");
		
		private String fieldName;
		public String fieldName(){ return fieldName;}
		private Field(String fieldName){	this.fieldName = fieldName;	}
	}
	
	
	public OTask(ODocument oTask) {
		super(oTask);
	}
	
	
	public static final OTask makeFromODocument(ODocument oTask){
		try {
			Class<?> myClass = Class.forName((String) TASK_JAVA_CLASS_ATTRIBUTE.getValue(oTask.getSchemaClass()));
	
			Constructor<?> constructor = myClass.getConstructor(ODocument.class);
	
			Object result = constructor.newInstance(oTask);
			return (OTask) result;
		} catch (Exception e) {
			LOG.error("Can't create task from ODocument", e);
		}
		return null;
	}
	
	//////////////////////////////////////////////////////////////////////
	protected Object getField(Field field) {
		return getDocument().field(field.fieldName());
	}
	//////////////////////////////////////////////////////////////////////

	
	public abstract OTaskSessionRuntime startNewSession();
	
}
