package org.orienteer.core.component.widget.document;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.widget.AbstractCalculatedDocumentsWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

/**
 * Widget for calculated document
 */
@Widget(id="calculated-documents", domain="document", order=20, oClass = AbstractCalculatedDocumentsWidget.WIDGET_OCLASS_NAME, autoEnable=false)
public class CalculatedDocumentsWidget extends AbstractCalculatedDocumentsWidget<ODocument> {

	public CalculatedDocumentsWidget(String id, IModel<ODocument> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
	}
	
	@Override
	protected OQueryDataProvider<ODocument> newDataProvider(String sql) {
		return super.newDataProvider(sql)
						.setParameter("doc", getModel());
	}
}
