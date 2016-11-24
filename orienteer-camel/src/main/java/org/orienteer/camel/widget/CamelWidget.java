package org.orienteer.camel.widget;

import java.io.IOException;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Bytes;
import org.orienteer.core.component.FAIcon;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.Widget;

import com.orientechnologies.orient.core.record.impl.ODocument;

@Widget(domain="schema", tab="integration", id="camel", order=60, autoEnable=true)
public class CamelWidget extends AbstractWidget<Void>{
    private FileUploadField fileUpload;

	public CamelWidget(String id, IModel<Void> model, IModel<ODocument> widgetDocumentModel) {
		super(id, model, widgetDocumentModel);
        Form<?> form = new Form<Void>("form"){
        	@Override
        	protected void onSubmit() {

	   			final FileUpload uploadedFile = fileUpload.getFileUpload();
	   			if (uploadedFile != null) {
	   				try {
		   				CamelContext context = new DefaultCamelContext();
						RoutesDefinition routes = context.loadRoutesDefinition(uploadedFile.getInputStream());
			    		context.addRouteDefinitions(routes.getRoutes());
			    		context.start();
						Thread.sleep(3000);
						context.stop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	   			}
        	}
        };
        form.setMultiPart(true);
        form.setMaxSize(Bytes.kilobytes(100));
    	form.add(fileUpload = new FileUploadField("fileUpload"));

        add(form);
	}

	@Override
	protected FAIcon newIcon(String id) {
        return new FAIcon(id, FAIconType.bars);
	}

	@Override
	protected IModel<String> getDefaultTitleModel() {
		return new ResourceModel("integration.camel");
	}

}
