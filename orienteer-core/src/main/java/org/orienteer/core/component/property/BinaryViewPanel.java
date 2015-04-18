package org.orienteer.core.component.property;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.orienteer.core.service.IOClassIntrospector;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class BinaryViewPanel<V> extends GenericPanel<V> {
	
	private IModel<ODocument> documentModel;
	private IModel<OProperty> propertyModel;
	private IModel<V> valueModel;
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	@SuppressWarnings("unchecked")
	public BinaryViewPanel(String id, IModel<ODocument> docModel, IModel<OProperty> propModel, IModel<V> valueModel) {
		super(id, valueModel);
		this.documentModel = docModel;
		this.propertyModel = propModel;
		add(new ResourceLink<byte[]>("data", new AbstractResource() {
			
			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
			     ResourceResponse resourceResponse = new ResourceResponse();
			     resourceResponse.setContentType("application/octet-stream");
			     String filename = oClassIntrospector.getDocumentName(documentModel.getObject());
			     filename += "."+propertyModel.getObject().getName()+".bin";
			     resourceResponse.setFileName(filename);
			     resourceResponse.setWriteCallback(new WriteCallback()
			     {
			        @Override
			        public void writeData(Attributes attributes) throws IOException
			        {
			             OutputStream outputStream = attributes.getResponse().getOutputStream();
			             outputStream.write((byte[])BinaryViewPanel.this.getModelObject());
			        }
			     });
			     return resourceResponse;
			}
		}));
	}
	
}
