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
import org.orienteer.core.behavior.UpdateOnActionPerformedEventBehavior;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.sun.mail.handlers.image_gif;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

/**
 * @author Asm
 *
 */
public class UnregistredPropertyEditPanel<T> extends GenericPanel<T> {

	IModel<T> propertyModel;
	IModel<?> reloadableModel;
	Component successMessage;
	Component reloadableComponent;
	
	public UnregistredPropertyEditPanel(String id, IModel<T> model, Component propertyComponent) {
		super(id, model);
		//add(new Label("propertyComponent","cool property component"));
		//add(propertyComponent);
		propertyModel = model;
		propertyComponent.setMarkupId(id+":propertyComponent");
	}

	public UnregistredPropertyEditPanel(String id, IModel<T> model) {
		super(id, model);
		propertyModel = model;
		add(makeRealizeButton());
		add(successMessage = makeSuccessMessage());
	}
	
	protected Component makeRealizeButton() {
		return new AjaxLink("realize") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
				boolean isActiveTransaction = db.getTransaction().isActive();
				if(isActiveTransaction) db.commit();

				((IPrototype<?>)propertyModel.getObject()).realizePrototype();
				setVisibilityAllowed(false);
				successMessage.setVisibilityAllowed(true);
				target.add(this,successMessage);
				//reloadableModel.detach();
				//if (reloadableComponent!=null) target.add(reloadableComponent);
				//reloadableComponent.
				//getModel().getObject()
				//inputDocument.setObject(null);
				//target.add(LinkEditPanel.this);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				//setVisible(inputDocument.getObject()!=null);
			}
		};
	}
	/*
	protected Component makeRealizeButton1() {
		return new Link("realize") {

			@Override
			public void onClick() {
				ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
				boolean isActiveTransaction = db.getTransaction().isActive();
				if(isActiveTransaction) db.commit();

				((IPrototype<?>)propertyModel.getObject()).realizePrototype();
				reloadableModel.detach();
				//setVisibilityAllowed(false);
				//if (reloadableComponent!=null) target.add(reloadableComponent);
				//getModel().getObject()
				//inputDocument.setObject(null);
				//target.add(LinkEditPanel.this);
			}
		};
	}
	*/
	protected Component makeSuccessMessage() {
		return new Label("successMessage","Field added").setOutputMarkupId(true).setVisibilityAllowed(false);
	}
	
	public UnregistredPropertyEditPanel(String id) {
		super(id);
	}
	
	public void setPropertyComponent(Component propertyComponent){
		addOrReplace(propertyComponent);
	}
	
	public void setReloadableComponent(Component reloadableComponent) {
		this.reloadableComponent = reloadableComponent;
	}
	
	public void setReloadableModel(IModel<?> reloadableModel) {
		this.reloadableModel = reloadableModel;
	}
	
	public String getPropertyComponentId() {
		return "propertyComponent";
	}
	
	
}
