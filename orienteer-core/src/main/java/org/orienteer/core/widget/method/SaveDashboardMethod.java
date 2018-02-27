package org.orienteer.core.widget.method;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.methods.CommandWrapperMethod;
import org.orienteer.core.widget.command.SilentSaveDashboardCommand;

import com.orientechnologies.orient.core.record.impl.ODocument;

@OMethod(order=3,filters={
		@OFilter(fClass = PlaceFilter.class, fData = "DASHBOARD_SETTINGS"),
})
public class SaveDashboardMethod extends CommandWrapperMethod{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Command<?> getCommand() {
		return new SilentSaveDashboardCommand(getId(),(IModel<ODocument>) getEnvData().getDisplayObjectModel());
	}
}