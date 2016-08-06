package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.db.DbEntity;

import com.google.common.base.Converter;

/**
 * ID value converter for non uniq {@link DbEntity}es, to make it uniq among all BPMEntities 
 */
public class NonUniqIdConverter extends Converter<Object, Object> {
	
	private final String prefix;
	
	public NonUniqIdConverter(String prefix) {
		this.prefix = prefix;
	}

	@Override
	protected Object doForward(Object a) {
		if(a==null || a.toString().startsWith(prefix)) return a;
		else return prefix+a;
	}

	@Override
	protected Object doBackward(Object b) {
		if(b==null || !b.toString().startsWith(prefix)) return b;
		else return b.toString().substring(prefix.length());
	}

}
