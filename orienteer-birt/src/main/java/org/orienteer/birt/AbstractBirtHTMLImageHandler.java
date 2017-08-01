package org.orienteer.birt;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.FileSystemResourceReference;
import org.apache.wicket.util.io.IClusterable;
import org.eclipse.birt.report.engine.api.CachedImage;
import org.eclipse.birt.report.engine.api.IHTMLImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wicket specific implementation of {@link IHTMLImageHandler} 
 */
public abstract class AbstractBirtHTMLImageHandler implements IHTMLImageHandler, IClusterable {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractBirtHTMLImageHandler.class);
	
	private Map<String, BirtImage> imageMap = new HashMap<>();
	
	@Override
	public String onDesignImage(IImage image, IReportContext context) {
		return handleImage(image, context);
	}

	@Override
	public String onDocImage(IImage image, IReportContext context) {
		return handleImage(image, context);
	}

	@Override
	public String onFileImage(IImage image, IReportContext context) {
		return handleImage(image, context);
	}

	@Override
	public String onURLImage(IImage image, IReportContext context) {
		return handleImage(image, context);
	}
	
	@Override
	public String onCustomImage(IImage image, IReportContext context) {
		return handleImage(image, context);
	}
	
	protected String handleImage(IImage image, IReportContext context) {
		BirtImage birtImage = getCachedImage(image.getID(), image.getSource(), context);
		if(birtImage==null) {
			birtImage = new BirtImage(image);
			if(birtImage.getURL()==null) {
				birtImage.setURL(urlFor(birtImage.getID(), birtImage));
			}
			imageMap.put(image.getID(), birtImage);
		}
		return birtImage.getURL();
	}
	
	protected abstract String urlFor(String id, BirtImage image);
	
	public BirtImage getBirtImage(String id) {
		return imageMap.get(id);
	}
	
	public IResource getBirtImageAsResource(String id) {
		BirtImage image = imageMap.get(id);
		return image!=null?image.toResource():null;
	}

	@Override
	public BirtImage getCachedImage(String id, int type, IReportContext context) {
		//No cache - default behavior for server renderer
		return null;
	}

	@Override
	public BirtImage addCachedImage(String id, int type, IImage image, IReportContext context) {
		//No cache - default behavior for server renderer
		return null;
		
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public String onDesignImage(IImage image, Object context) {
		throw new UnsupportedOperationException("Method onDesignImage(IImage image, Object context) is not supported");
	}

	@Override
	@SuppressWarnings("deprecation")
	public String onDocImage(IImage image, Object context) {
		throw new UnsupportedOperationException("Method onDocImage(IImage image, Object context) is not supported");
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public String onFileImage(IImage image, Object context) {
		throw new UnsupportedOperationException("Method onFileImage(IImage image, Object context) is not supported");
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public String onURLImage(IImage image, Object context) {
		throw new UnsupportedOperationException("Method onURLImage(IImage image, Object context) is not supported");
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public String onCustomImage(IImage image, Object context) {
		throw new UnsupportedOperationException("Method onCustomImage(IImage image, Object context) is not supported");
	}
}
