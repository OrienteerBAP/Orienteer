package org.orienteer.birt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.resource.UrlResourceStream;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.resource.FileSystemResourceReference;
import org.apache.wicket.util.io.IClusterable;
import org.eclipse.birt.report.engine.api.CachedImage;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.ImageSize;
import org.eclipse.birt.report.engine.util.FileUtil;

/**
 * Holder for image. We extends CachedImage just as a most suitable holder 
 */
public class BirtImage extends CachedImage implements IClusterable {
	
	/**
	 * Serializable implementation of ImageSize. Required for wicket storing, 
	 */
	public static class BirtImageSize extends ImageSize implements IClusterable {

		public BirtImageSize(String u, float w, float h) {
			super(u, w, h);
		}
		
		public BirtImageSize(ImageSize imageSize) {
			super(imageSize.getUnit(), imageSize.getWidth(), imageSize.getHeight());
		}
		
	}
	
	private int source;
	private byte[] data;
	

	public BirtImage(IImage image) {
		setID(image.getID());
		setImageMap(image.getImageMap());
		setImageSize(image.getImageSize());
		setMIMEType(image.getMimeType());
		setSource(image.getSource());
		setData(image.getImageData());
	}
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public IResource toResource() {
		try {
			if(source == IImage.URL_IMAGE) {
				return new ResourceStreamResource(new UrlResourceStream(new URL(getID())));
			} else if (source == IImage.FILE_IMAGE) {
				return new FileSystemResource( Paths.get(FileUtil.getURI(getID())));
			} else {
				return new ByteArrayResource(getMIMEType(), data);
			}
		} catch (MalformedURLException e) {
			throw new WicketRuntimeException("Can't transform to resource", e);
		}
	}

	@Override
	public void setImageSize(ImageSize size) {
		//Required to make size serializable
		super.setImageSize(size==null || size instanceof BirtImageSize?size:new BirtImageSize(size));
	}
	

}
