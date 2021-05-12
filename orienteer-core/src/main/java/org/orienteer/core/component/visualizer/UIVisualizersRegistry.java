package org.orienteer.core.component.visualizer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.lang.Args;
import org.orienteer.core.component.TextBreakPanel;
import org.orienteer.core.component.property.LinksPropertyDataTablePanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registry for {@link IVisualizer}s
 */
public class UIVisualizersRegistry
{
	public static final String VISUALIZER_DEFAULT = DefaultVisualizer.NAME;
	public static final String VISUALIZER_TEXTAREA = "textarea";
	public static final String VISUALIZER_TABLE = "table";
	public static final String VISUALIZER_LISTBOX = ListboxVisualizer.NAME;
	public static final String VISUALIZER_PASSWORD = PasswordVisualizer.NAME;
	public static final String VISUALIZER_HTML = HTMLVisualizer.NAME;
	public static final String VISUALIZER_URL_LINK = UrlLinkVisualizer.NAME;
	public static final String VISUALIZER_LOCALIZATION = LocalizationVisualizer.NAME;
	public static final String VISUALIZER_IMAGE = ImageVisualizer.NAME;
	public static final String VISUALIZER_SUGGEST = SuggestVisualizer.NAME;
	public static final String VISUALIZER_CODE = CodeVisualizer.NAME;
	public static final String VISUALIZER_JAVASCRIPT = JavaScriptCodeVisualizer.NAME;
	public static final String VISUALIZER_SQL = SqlCodeVisualizer.NAME;
	public static final String VISUALIZER_HEX = HexVisualizer.NAME;
	public static final String VISUALIZER_RESTRICTED_WIDTH = "restricted-width";

	private Table<OType, String, IVisualizer> registryTable = HashBasedTable.create();
	
	public UIVisualizersRegistry()
	{
		registerUIComponentFactory(DefaultVisualizer.INSTANCE);
		registerUIComponentFactory(new SimpleVisualizer("textarea", MultiLineLabel.class, TextArea.class, OType.STRING));
		registerUIComponentFactory(new SimpleVisualizer("table", true, LinksPropertyDataTablePanel.class, 
																	   LinksPropertyDataTablePanel.class,
																	   OType.LINKLIST, 
																	   OType.LINKSET, 
																	   OType.LINKBAG));
		registerUIComponentFactory(new SimpleVisualizer(VISUALIZER_RESTRICTED_WIDTH, TextBreakPanel.class, TextField.class, OType.STRING));
		registerUIComponentFactory(new ListboxVisualizer());
		registerUIComponentFactory(new PasswordVisualizer());
		registerUIComponentFactory(new HTMLVisualizer());
		registerUIComponentFactory(new UrlLinkVisualizer());
		registerUIComponentFactory(new LocalizationVisualizer());
		registerUIComponentFactory(new ImageVisualizer());
		registerUIComponentFactory(new SuggestVisualizer());
		registerUIComponentFactory(new CodeVisualizer());
		registerUIComponentFactory(new JavaScriptCodeVisualizer());
		registerUIComponentFactory(new SqlCodeVisualizer());
		registerUIComponentFactory(new HexVisualizer());
		registerUIComponentFactory(new LinksAsEmbeddedVisualizer());
	}
	
	public Table<OType, String, IVisualizer> getRegistryTable()
	{
		return registryTable;
	}
	
	public void registerUIComponentFactory(IVisualizer visualizer)
	{
		for(OType oType : visualizer.getSupportedTypes())
		{
			registryTable.put(oType, visualizer.getName(), visualizer);
		}
	}

	public void unregisterUIComponentFactory(List<OType> types, String name) {
		types.forEach(t -> registryTable.remove(t, name));
	}

	public IVisualizer getComponentFactory(OType oType, String componentName)
	{
		Args.notNull(oType, "oType");
		Args.notNull(componentName, "componentName");
		return registryTable.get(oType, componentName);
	}
	
	public List<String> getComponentsOptions(OType oType)
	{
		List<String> ret = new ArrayList<String>();
		if(oType!=null)
		{
			ret.addAll(registryTable.row(oType).keySet());
		}
		else
		{
			ret.addAll(registryTable.columnKeySet());
		}
		Collections.sort(ret);
		return ret;
	}
	
}
