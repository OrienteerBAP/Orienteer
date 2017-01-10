package org.orienteer.core.component.property;

import org.apache.tika.Tika;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ByteArrayResource;

/**
 * {@link GenericPanel} to view binary images
 *
 * @param <V> the type of the panel's model object
 */
public class ImageViewPanel<V> extends GenericPanel<V> {

	public ImageViewPanel(String id, IModel<V> valueModel) {
		super(id, valueModel);

		byte[] imageBytes = (byte[]) getModelObject();
		if (imageBytes != null) {
			String mimeType = new Tika().detect(imageBytes);
			ByteArrayResource byteArrayResource = new ByteArrayResource(mimeType, imageBytes);
			add(new Image("image", byteArrayResource));
		} else {
			add(new EmptyPanel("image"));
		}
	}
}
