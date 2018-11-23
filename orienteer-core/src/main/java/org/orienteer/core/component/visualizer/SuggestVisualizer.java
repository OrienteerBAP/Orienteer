package org.orienteer.core.component.visualizer;

import java.util.Collection;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.OClassMetaPanel;
import org.orienteer.core.component.property.DisplayMode;

import org.orienteer.core.util.ODocumentChoiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.select2.*;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IVisualizer} to show links as tags control
 */
public class SuggestVisualizer extends AbstractSimpleVisualizer {
	
	public static final String NAME = "suggest";
	
	private static final Logger LOG = LoggerFactory.getLogger(SuggestVisualizer.class);
	
    public SuggestVisualizer() {
        super(NAME, false, OType.LINK, OType.LINKLIST, OType.LINKSET);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Component createComponent(String id, DisplayMode mode,
                                         IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        if (DisplayMode.EDIT.equals(mode)) {
            OProperty property = propertyModel.getObject();
            OClass oClass = property.getLinkedClass();
            if(oClass!=null) {
	            AbstractSelect2Choice<?, ?> choice = property.getType().isMultiValue() ?
	            		new Select2MultiChoice<ODocument>(id, (IModel<Collection<ODocument>>) valueModel, new ODocumentChoiceProvider(oClass))
	            		: new Select2Choice<ODocument>(id, (IModel<ODocument>) valueModel, new ODocumentChoiceProvider(oClass));
	            choice.getSettings()
							.setWidth("100%")
							.setCloseOnSelect(true)
							.setTheme(OClassMetaPanel.BOOTSTRAP_SELECT2_THEME);
				return choice;
            } else {
            	LOG.warn("Property '"+property.getFullName()+"' doesn't have linked class specified.");
            }
        }
        
        return null;
    }

}