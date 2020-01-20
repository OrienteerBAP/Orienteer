package org.orienteer.core.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.Tika;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.Url.StringMode;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.AbstractResource.WriteCallback;
import org.apache.wicket.request.resource.CharSequenceResource;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.orienteer.core.MountPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import net.coobird.thumbnailator.Thumbnails;

/**
 * Test resource to echo content url to verify
 */
@MountPath("/api/echo/${rid}/${field}")
public class EchoUrlResource extends CharSequenceResource {

	public EchoUrlResource() {
		super("text/plain");
	}
	
	@Override
	protected CharSequence getData(Attributes attributes) {
		PageParameters params = attributes.getParameters();
        String ridStr = "#"+params.get("rid").toOptionalString();
        ORID orid = ORecordId.isA(ridStr) ? new ORecordId(ridStr) : null;
        String field = params.get("field").toString();
        boolean full = params.get("full").toBoolean();
        return OContentShareResource.urlFor(orid.getRecord(), field, "text/plain", full);
	}
	
}
