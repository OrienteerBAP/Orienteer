package org.orienteer.camel.behavior;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IWrapModel;
import org.orienteer.camel.tasks.IOIntegrationConfig;
import org.orienteer.core.dao.DAO;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

/**
 * Behavior for {@link IOIntegrationConfig#stop(org.orienteer.core.method.IMethodContext)} OMethod
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
				IOIntegrationConfig config = DAO.provide(IOIntegrationConfig.class, docModel.getObject());
				CamelContext context = config.getOrMakeContext();
				ServiceStatus status = context.getStatus();
				if (status.isStopped()){
					component.setEnabled(false);
				}else{
					component.setEnabled(true);
				}
			}
	}


}
