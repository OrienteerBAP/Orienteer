package org.orienteer.devutils;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.orienteer.devutils.component.OQueryModelResultsPanel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import ru.ydn.wicket.wicketconsole.IScriptResultRenderer;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * {@link IScriptResultRenderer} for rendering table for ODocuments results 
 */
public class ODBScriptResultRenderer implements IScriptResultRenderer{

	@Override
	public Component render(String id, IModel<?> dataModel) {
		if(dataModel == null) return null;
		else if(dataModel instanceof OQueryModel) {
			OQueryModel<ODocument> queryModel = (OQueryModel<ODocument>) dataModel;
			OClass oClass = queryModel.getSchemaClass();
			if(oClass!=null) {
				return new OQueryModelResultsPanel(id, queryModel);
			}
		} else {
			// Trying to find ODocument related stuff
			Object value = dataModel.getObject();
			if(value == null) return null;
			Class<?> valueClass = value.getClass();
			if(valueClass.isArray()) {
				Class<?> arrayClass = valueClass.getComponentType();
				if(!arrayClass.isPrimitive()) {
					value = Arrays.asList((Object[])value);
					if(arrayClass != null && OIdentifiable.class.isAssignableFrom(arrayClass)) {
						return new MultiLineLabel(id, serializeODocuments((Collection<OIdentifiable>)value));
					}
				}
			}
			if(value instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>)value;
				if(!collection.isEmpty() && collection.iterator().next() instanceof OIdentifiable) {
					//TODO: add more suitable component for visualization of result set
					return new MultiLineLabel(id, serializeODocuments((Collection<OIdentifiable>)collection));
				}
			} else if(value instanceof OResultSet) {
				String result = ((OResultSet)value).stream().map(OResult::toJSON).collect(Collectors.joining("\n"));
				return new MultiLineLabel(id, result);
			}
		}
		return null;
	}
	
	private String serializeODocuments(Collection<? extends OIdentifiable> collection) {
		StringBuilder sb = new StringBuilder();
		for(OIdentifiable id : collection) {
			sb.append(id.getRecord().toJSON()).append('\n');
		}
		return sb.toString();
	}

}
