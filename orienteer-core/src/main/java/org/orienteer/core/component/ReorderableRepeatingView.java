package org.orienteer.core.component;


import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

/**
 * {@link RepeatingView} implementation with ability to reorder components
 */
public class ReorderableRepeatingView extends RepeatingView{

	public ReorderableRepeatingView(String id, IModel<?> model) {
		super(id, model);
	}

	public ReorderableRepeatingView(String id) {
		super(id);
	}
	
	private Comparator<Component> comparator;
	private List<String> orderedIds;
	
	@Override
	public Iterator<? extends Component> renderIterator() {
		if(comparator!=null){
			return iterator(comparator);
		} else if(orderedIds!=null) {
			Stream<Component> stream1 = orderedIds.stream().map(id->get(id)).filter(Objects::nonNull);
			Stream<Component> stream2 = stream().filter(component->!orderedIds.contains(component.getId()));
			return Stream.concat(stream1, stream2).iterator();
		} else {
			return iterator();
		}
	}
	
	public ReorderableRepeatingView setComparator(Comparator<Component> comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public Comparator<Component> getComparator() {
		return comparator;
	}
	
	public ReorderableRepeatingView setComponentOrderByIds(List<String> orderedIds) {
		this.orderedIds = orderedIds;
		return this;
	}
	
	public List<String> getComponentOrderByIds() {
		return orderedIds;
	}
	

}
