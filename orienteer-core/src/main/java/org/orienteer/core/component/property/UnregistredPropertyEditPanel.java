/**
 * 
 */
package org.orienteer.core.component.property;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.sun.mail.handlers.image_gif;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

/**
 * To making fields in classes by fields in documents 
 */
public class UnregistredPropertyEditPanel extends GenericPanel<OProperty> {

	public UnregistredPropertyEditPanel(String id, IModel<OProperty> model) {
		super(id, model);
		add(makeRealizeButton());
	}
	
	protected Component makeRealizeButton() {
		return new AjaxLink("realize") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
				boolean isActiveTransaction = db.getTransaction().isActive();
				if(isActiveTransaction) db.commit();
				((IPrototype<?>)UnregistredPropertyEditPanel.this.getModel().getObject()).realizePrototype();
				setVisibilityAllowed(false);
				target.add(this);
				String message = getLocalizer().getString("widget.document.unregistered.properties.success", this).replace("\"", "\\\"");
				info(message);
			}
		};
	}
	
	public void setPropertyComponent(Component propertyComponent){
		addOrReplace(propertyComponent);
	}
	
	public String getPropertyComponentId() {
		return "propertyComponent";
	}
	
	
}
