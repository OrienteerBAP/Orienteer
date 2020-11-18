package org.orienteer.devutils.component.widget;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.structuretable.OrienteerStructureTable;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Widget to show wicket-console on tools dashboard page
 */
@Widget(id="devutils-monitoring", domain="tools", tab = "monitoring", autoEnable=true)
public class MonitoringWidget extends AbstractWidget<Void> {
	
	private final Map<String, IModel<?>> monitoringParams = new HashMap<>();

	
	private void registerAll() {
		register("monitoring.configurationType", () -> OrienteerWebApplication.get().getConfigurationType());
		register("monitoring.activeThreads", Thread::activeCount);
	}
	
	private void register(String key, SerializableSupplier<?> supplier) {
		monitoringParams.put(key, LambdaModel.of(supplier));
	}

	public MonitoringWidget(String id, IModel<Void> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
		registerAll();
		Form<Void> form = new Form<Void>("form", getModel());
		List<String> paramsNames = new ArrayList<>(monitoringParams.keySet());
		OrienteerStructureTable<Void, String> propertiesStructureTable = new OrienteerStructureTable<Void, String>("params", getModel(), paramsNames){

			@Override
			protected Component getValueComponent(String id,
					IModel<String> rowModel) {
				return new MultiLineLabel(id, monitoringParams.get(rowModel.getObject()));
			}
			
			@Override
			protected IModel<?> getLabelModel(Component resolvedComponent, IModel<String> rowModel) {
				return new SimpleNamingModel<>(rowModel);
			}
		};
		form.add(propertiesStructureTable);
		add(form);
	}

	@Override
	protected FAIcon newIcon(String id) {
		return new FAIcon(id, FAIconType.line_chart);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("widget.monitoring");
	}
	
	@Override
	protected String getWidgetStyleClass() {
		return "strict";
	}

}
