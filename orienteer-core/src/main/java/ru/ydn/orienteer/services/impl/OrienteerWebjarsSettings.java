package ru.ydn.orienteer.services.impl;

import java.util.regex.Pattern;

import org.apache.wicket.util.time.Duration;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.agilecoders.wicket.webjars.settings.WebjarsSettings;

public class OrienteerWebjarsSettings extends WebjarsSettings
{

	@Inject(optional=true)
	public void setReadFromCacheTimeout(@Named("webjars.readFromCacheTimeout") String readFromCacheTimeout) {
		readFromCacheTimeout(Duration.valueOf(readFromCacheTimeout));
	}

	@Inject(optional=true)
	public void setUseCdnResources(@Named("webjars.useCdnResources") boolean useCdnResources) {
		useCdnResources(useCdnResources);
	}

	@Inject(optional=true)
	public void setCdnUrl(@Named("webjars.cdnUrl") String cdnUrl) {
		cdnUrl(cdnUrl);
	}
	
}
