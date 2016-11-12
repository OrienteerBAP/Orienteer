package org.orienteer.devutils;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.devutils.component.OQueryModelResultsPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketconsole.IScriptResultRenderer;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

/**
 * {@link IScriptResultRenderer} for rendering table for ODocuments results 
 */
public class ODBScriptResultRenderer implements IScriptResultRenderer{

	@Override
	public Component render(String id, IModel<?> dataModel) {
		if(dataModel instanceof OQueryModel) {
			OQueryModel<ODocument> queryModel = (OQueryModel<ODocument>) dataModel;
			OClass oClass = queryModel.probeOClass(20);
			if(oClass!=null) {
				return new OQueryModelResultsPanel(id, queryModel);
			}
		}
		return null;
	}

}
