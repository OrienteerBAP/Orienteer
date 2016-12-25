package org.orienteer.core.component.widget.document;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.DeleteODocumentCommand;
import org.orienteer.core.component.command.EditODocumentsCommand;
import org.orienteer.core.component.command.ExportCommand;
import org.orienteer.core.component.command.SaveODocumentsCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.widget.AbstractCalculatedDocumentsWidget;
import org.orienteer.core.component.widget.AbstractHtmlJsPaneWidget;
import org.orienteer.core.model.ODocumentNameModel;
import org.orienteer.core.service.impl.OClassIntrospector;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
						.setParameter("doc", getModel())
						.setContextVariable("doc", getModel());
	}

}
