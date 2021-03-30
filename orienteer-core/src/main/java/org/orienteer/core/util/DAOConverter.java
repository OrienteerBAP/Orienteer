package org.orienteer.core.util;

import org.orienteer.core.dao.DAO;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Converter} to convert ODocument to DAO and back
 * @param <D> main interface for a DAO to convert to
 */
public class DAOConverter<D> extends Converter<ODocument, D> {
	
	private final Class<? extends D> daoInterface;
	private final Class<?>[] additionalInterfaces;
	
	public DAOConverter(Class<? extends D> daoInterface) {
		this.daoInterface = daoInterface;
		this.additionalInterfaces = new Class<?>[0];
	}
	
	public DAOConverter(Class<? extends D> daoInterface, Class<?>... additionalInterfaces) {
		this.daoInterface = daoInterface;
		this.additionalInterfaces = additionalInterfaces;
	}

	@Override
	protected D doForward(ODocument a) {
		return DAO.provide(daoInterface, a, additionalInterfaces);
	}

	@Override
	protected ODocument doBackward(D b) {
		return DAO.asDocument(b);
	}
	
	public static <D> Converter<? super ODocument, D> instance(Class<? extends D> daoInterface) {
		return new DAOConverter<D>(daoInterface);
	}

}
