package org.orienteer.core.component.property;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * {@link GenericPanel} to view {@link Boolean} parameters
 */
public class BooleanViewPanel extends GenericPanel<Boolean>
{
	private static final long serialVersionUID = 1L;
	private Boolean defaultValue;
	private boolean hideIfTrue = false;
	private boolean hideIfFalse = false;

	public BooleanViewPanel(String id, IModel<Boolean> model) {
		super(id, model);
		initialize();
	}

	public BooleanViewPanel(String id) {
		super(id);
		initialize();
	}
	
	protected void initialize()
	{
		add(new WebMarkupContainer("icon", getModel())
		{
			boolean effectiveValue;
			
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.append("class", effectiveValue?"fa-check-circle text-success":"fa-times-circle text-danger", " ");
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				Boolean value = getModelObject();
				if(value==null) value = defaultValue;
				boolean visibility = false;
				if(value!=null)
				{
					effectiveValue = value;
					visibility = effectiveValue?!hideIfTrue:!hideIfFalse;
				}
				setVisible(visibility);
			}
		});
	}

	public Boolean getDefaultValue() {
		return defaultValue;
	}

	public BooleanViewPanel setDefaultValue(Boolean defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public boolean isHideIfTrue() {
		return hideIfTrue;
	}

	public BooleanViewPanel setHideIfTrue(boolean hideIfTrue) {
		this.hideIfTrue = hideIfTrue;
		return this;
	}

	public boolean isHideIfFalse() {
		return hideIfFalse;
	}

	public BooleanViewPanel setHideIfFalse(boolean hideIfFalse) {
		this.hideIfFalse = hideIfFalse;
		return this;
	}
	
}
