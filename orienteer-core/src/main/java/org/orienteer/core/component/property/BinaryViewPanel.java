package org.orienteer.core.component.property;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.service.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link GenericPanel} to view binary parameters (download)
 *
 * @param <V> the type of the panel's model object
 */
public class BinaryViewPanel<V> extends GenericPanel<V> {
	
	private ResourceLink<byte[]> dataLink;
	private IModel<String> nameModel;
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	@SuppressWarnings("unchecked")
	public BinaryViewPanel(String id, final IModel<ODocument> docModel, final IModel<OProperty> propModel, IModel<V> valueModel) {
		super(id, valueModel);
		
		nameModel = new LoadableDetachableModel<String>() {

			@Override
			protected String load() {
				String filename = docModel.getObject().field(propModel.getObject().getName()+BinaryEditPanel.FILENAME_SUFFIX, String.class);
				if(Strings.isEmpty(filename)){
					filename = oClassIntrospector.getDocumentName(docModel.getObject());
					filename += "."+propModel.getObject().getName()+".bin";
				}
				return filename;
			}
			
			@Override
			public void detach() {
				super.detach();
				docModel.detach();
				propModel.detach();
			}
		};
		
		initialize();
	}
	
	public BinaryViewPanel(String id, IModel<String> nameModel, IModel<V> valueModel) {
		super(id, valueModel);
		this.nameModel = nameModel;
		initialize();
	}
	
	protected void initialize() {
		add((dataLink = new ResourceLink<byte[]>("data", new AbstractResource() {
			
			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
			     ResourceResponse resourceResponse = new ResourceResponse();
			     resourceResponse.setContentType("application/octet-stream");
			     String filename = nameModel.getObject();
			     resourceResponse.setFileName(filename);
			     resourceResponse.setWriteCallback(new WriteCallback()
			     {
			        @Override
			        public void writeData(Attributes attributes) throws IOException
			        {
			        	 byte[] data = (byte[])BinaryViewPanel.this.getModelObject();
			        	 if(data!=null) {
			        		 OutputStream outputStream = attributes.getResponse().getOutputStream();
			        		 outputStream.write((byte[])BinaryViewPanel.this.getModelObject());
			        	 }
			        }
			     });
			     return resourceResponse;
			}
		})).setBody(nameModel));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		byte[] data = (byte[])getModelObject();
		dataLink.setVisibilityAllowed(data!=null && data.length>0);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		nameModel.detach();
	}
	
}
