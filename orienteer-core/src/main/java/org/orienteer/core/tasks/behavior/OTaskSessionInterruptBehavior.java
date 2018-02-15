package org.orienteer.core.tasks.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IWrapModel;
import org.orienteer.core.tasks.OTaskSession;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
/**
 * 
 * Behavior for {@link OTaskSession} interrupt OMethod 
 *
 */
public class OTaskSessionInterruptBehavior extends Behavior{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void onConfigure(Component component) {
		super.onConfigure(component);
			ODocumentModel docModel=null;
			if(component.getDefaultModel() instanceof ODocumentModel){
				docModel = (ODocumentModel) component.getDefaultModel();
			}else if(component.getDefaultModel() instanceof IWrapModel){
				docModel = (ODocumentModel) ((IWrapModel<?>) component.getDefaultModel()).getWrappedModel();
			}
			if (docModel!=null && docModel.getObject()!=null){
				component.setEnabled(new OTaskSession(docModel.getObject()).isInterruptable());
			}
	}

}
