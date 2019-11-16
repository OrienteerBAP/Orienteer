package org.orienteer.tours.component;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.BookmarkablePageLinkCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.web.HomePage;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command to start a new tour 
 */
@OMethod(titleKey = "command.starttour",
icon = FAIconType.question_circle_o,
filters = {
           @OFilter(fClass = PlaceFilter.class, fData = "DASHBOARD_SETTINGS"),
   }
)
public class StartTourCommand extends Command<ODocument> {

	public StartTourCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.starttour");
	}
	
	@Override
	protected AbstractLink newLink(String id) {
		return new ExternalLink(id, "javascript:otour.start()");
	}

	@Override
	public void onClick() {
		//NOP
	}
}
