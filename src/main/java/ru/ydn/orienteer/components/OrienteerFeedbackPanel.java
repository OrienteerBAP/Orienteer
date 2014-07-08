package ru.ydn.orienteer.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class OrienteerFeedbackPanel extends FeedbackPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class UniqueMessageFilter implements IFeedbackMessageFilter
	  {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		List<FeedbackMessage> messages = new ArrayList<FeedbackMessage>();
	 
	    public void clearMessages()
	    {
	      messages.clear();
	    }
	 
	    @Override
	    public boolean accept(FeedbackMessage currentMessage)
	    {
	      // too bad that FeedbackMessage doesnt have an equals implementation
	      for(FeedbackMessage message: messages)
	        if(message.getMessage().toString().equals(currentMessage.getMessage().toString()))
	          return false;
	      messages.add(currentMessage);
	      return true;
	    }
	  }
	
	public OrienteerFeedbackPanel(String id)
	{
		super(id, new UniqueMessageFilter());
		setOutputMarkupPlaceholderTag(true);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message) {
		switch (message.getLevel())
		{
			case FeedbackMessage.SUCCESS:
				return "alert alert-success";
			case FeedbackMessage.WARNING:
				return "alert alert-warning";
			case FeedbackMessage.ERROR:
			case FeedbackMessage.FATAL:
				return "alert alert-danger";
			case FeedbackMessage.UNDEFINED:
			case FeedbackMessage.DEBUG:
			case FeedbackMessage.INFO:
			default:
				return "alert alert-info";
		}
	}
	
	@Override
    protected void onBeforeRender()
    {
      super.onBeforeRender();
      ((UniqueMessageFilter)getFilter()).clearMessages();
    }

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if(event.getPayload() instanceof AjaxRequestHandler)
		{
			((AjaxRequestHandler)event.getPayload()).add(this);
		}
	}

}
