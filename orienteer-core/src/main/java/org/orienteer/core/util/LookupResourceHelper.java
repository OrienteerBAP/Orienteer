package org.orienteer.core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.service.OrienteerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to help in lookuping of startup properties files.
 * Plugable architecture. Please see {@link OrienteerModule} for lookups order specification
 */
public class LookupResourceHelper {
	
	private final static Logger LOG = LoggerFactory.getLogger(LookupResourceHelper.class);
	
	/**
	 * Interface for classes that can find a resource by specified identifier
	 */
	public static interface IResourceLookuper {
		/**
		 * Lookup file according to lookuper politics
		 * @param identifier identifier to use for lookup
		 * @return file or null of file was not found
		 */
		public URL lookup(String identifier);
	}
	
	private LookupResourceHelper() {
		
	}
	
	/**
	 * Lookup file specified by system property 
	 */
	public static class SystemPropertyFileLookuper implements IResourceLookuper {

		public static final SystemPropertyFileLookuper INSTANCE = new SystemPropertyFileLookuper();
		
		@Override
		public URL lookup(String identifier) {
			String path = System.getProperty(identifier);
			if(!Strings.isEmpty(path))
			{
				File file = new File(path);
				return convertFileToURL(file);
			}
			else return null;
		}
	}
	
	/**
	 * Lookup classpath resource specified by system property 
	 */
	public static class SystemPropertyResourceLookuper implements IResourceLookuper {

		public static final SystemPropertyResourceLookuper INSTANCE = new SystemPropertyResourceLookuper();
		
		@Override
		public URL lookup(String identifier) {
			return Thread.currentThread().getContextClassLoader().getResource(identifier);
		}
	}
	
	/**
	 * Lookup property URL by system property 
	 */
	public static class SystemPropertyURLLookuper implements IResourceLookuper {

		public static final SystemPropertyURLLookuper INSTANCE = new SystemPropertyURLLookuper();
		
		@Override
		public URL lookup(String identifier) {
			String path = System.getProperty(identifier);
			if(!Strings.isEmpty(path))
			{
				try {
					return new URL(path);
				} catch (MalformedURLException e) {
					LOG.debug(String.format("URL '%s' is malformed. Specified by system property '%s'", path, identifier), e);
					return null;
				}
			}
			else return null;
		}
	}
	
	/**
	 * Lookup file by looking up from start directory. By default start directory is current one. 
	 */
	public static class UpDirectoriesFileLookuper implements IResourceLookuper {
		
		public static final UpDirectoriesFileLookuper INSTANCE = new UpDirectoriesFileLookuper();
		
		private File startDir = new File("").getAbsoluteFile();
		
		public UpDirectoriesFileLookuper() {
		}
		
		public UpDirectoriesFileLookuper(File startDir) {
			this.startDir = startDir;
		}
		
		@Override
		public URL lookup(String identifier) {
			File dir = startDir;
			File file = new File(identifier);
			while(!file.exists() && dir!=null)
			{
				dir = dir.getParentFile();
				file = new File(dir, identifier);
			}
			return convertFileToURL(file);
		}
	}
	
	/**
	 * Lookup file in specified directory 
	 */
	public static class DirFileLookuper implements IResourceLookuper {
		
		public static final DirFileLookuper CURRENT_DIR_INSTANCE = new DirFileLookuper();
		public static final DirFileLookuper CONFIG_DIR_INSTANCE = new DirFileLookuper(System.getProperty("user.home")+File.separatorChar+".orienteer");
		
		private File dir = new File("").getAbsoluteFile();
		
		public DirFileLookuper() {
		}
		
		public DirFileLookuper(File startDir) {
			this.dir = startDir;
		}
		
		public DirFileLookuper(String path) {
			this.dir = new File(path);
		}
		
		@Override
		public URL lookup(String identifier) {
			File file = new File(dir, identifier);
			return convertFileToURL(file);
		}
	}
	
	/**
	 * Stacked lookuper
	 */
	public static class StackedResourceLookuper implements IResourceLookuper {
		
		private final IResourceLookuper[] lookupers;
		
		public StackedResourceLookuper(IResourceLookuper... lookupers) {
			this.lookupers = lookupers;
		}
		
		@Override
		public URL lookup(String identifier) {
			URL ret;
			for(IResourceLookuper lookuper:lookupers)
			{
				ret = lookuper.lookup(identifier);
				if(ret!=null) return ret;
			}
			return null;
		}
		
	}
	
	/**
	 * Utility method to convert file to URL
	 * @param file local for
	 * @return URL of a provided file
	 */
	public static URL convertFileToURL(File file)
	{
		if(file==null || !file.exists()) return null;
		if(!file.isAbsolute()) file = file.getAbsoluteFile();
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			LOG.debug(String.format("File '%s' can't be converted to URL", file), e);
			return null;
		}
	}
}
