package org.orienteer.camel.behavior;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IWrapModel;
import org.orienteer.camel.component.OIntegrationConfig;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

/**
 * Behavior for {@link OIntegrationConfig#stop(org.orienteer.core.method.IMethodEnvironmentData)} OMethod
 *
 */
public class OIntegrationConfigStopBehavior extends Behavior{

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
				OIntegrationConfig config = new OIntegrationConfig(docModel.getObject());
				CamelContext context = config.getOrMakeContext(component);
				ServiceStatus status = context.getStatus();
				if (status.isStopped()){
					component.setEnabled(false);
				}else{
					component.setEnabled(true);
				}
			}
	}


}
