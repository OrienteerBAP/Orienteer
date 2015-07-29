package org.orienteer.core.component.widget.oclass;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.command.ShowHideParentsCommand;
import org.orienteer.core.component.widget.AbstractIndexesWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.OIndexiesDataProvider;

/**
 * Widget to show and modify {@link OIndex}ies of an {@link OClass}
 */
@Widget(id="class-indexies", domain="class", tab="configuration", order=20, autoEnable=true)
public class OClassIndexiesWidget extends AbstractIndexesWidget<OClass> {
	
	private IModel<Boolean> showParentIndexesModel = Model.<Boolean>of(true);
	
	public OClassIndexiesWidget(String id, IModel<OClass> model,
			IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		iTable.addCommand(new ShowHideParentsCommand<OIndex<?>>(getModel(), iTable, showParentIndexesModel));
	}

	@Override
	protected String getCaptionResourceKey() {
		return "class.indexies";
	}

	@Override
	protected OIndexiesDataProvider getIndexDataProvider() {
		return new OIndexiesDataProvider(getModel(), showParentIndexesModel);
	}
}
