package org.orienteer.metrics;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.time.Time;
import org.orienteer.core.MountPath;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

/**
 * Resource to share metrics 
 */
@MountPath("/metrics")
public class OMetricsResource extends AbstractResource {

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		ResourceResponse response = new ResourceResponse();
        response.setLastModified(Time.now());
//        response.setStatusCode(HttpServlet);
        response.setContentType(TextFormat.CONTENT_TYPE_004);
        response.disableCaching();
        if (response.dataNeedsToBeWritten(attributes)) {
        	
        	PageParameters params = attributes.getParameters();
        	Set<String> includedMetrics = new HashSet<>();
        	params.getValues("name").forEach((sv) -> {if(!sv.isEmpty()) includedMetrics.add(sv.toString()); });
        	
            response.setWriteCallback(createWriteCallback(CollectorRegistry.defaultRegistry, includedMetrics));
        }
        return response;
	}
	
	private WriteCallback createWriteCallback(CollectorRegistry registry, Set<String> metrics) {
        return new WriteCallback() {
            @Override
            public void writeData(IResource.Attributes attributes) throws IOException {
				try(OutputStreamWriter writer = new OutputStreamWriter(attributes.getResponse().getOutputStream(), "UTF8")) {
					TextFormat.write004(writer, registry.filteredMetricFamilySamples(metrics));
					writer.flush();
				}
            }
        };
    }

}
