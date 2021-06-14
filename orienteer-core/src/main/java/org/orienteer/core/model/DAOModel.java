package org.orienteer.core.model;

import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.dao.DAO;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IModel} for a DAO Entities
 *
 * @param <T> interface for a DAO
 */
public class DAOModel<T> implements IModel<T>, IObjectClassAwareModel<T>{
	private static final long serialVersionUID = 1L;
	
	private T object;
	private boolean needToReload=false;
	
	public DAOModel(Class<T> clazz) {
		this(DAO.create(clazz));
	}

	public DAOModel(T object) {
		this.object = object;
		needToReload=false;
	}
	

	@Override
	public T getObject() {
		try {
			if( object != null && needToReload) {
				DAO.asWrapper(object).reload();
				needToReload = false;
			}
			return object;
		} catch (ORecordNotFoundException e) {
			return null;
		}
	}

	@Override
	public void setObject(T object) {
		this.object = object;
		needToReload = false;
	}

	@Override
	public void detach() {
		T ret = getObject();
		if (ret != null && !DAO.asWrapper(object).getDocument().getIdentity().isNew()) {
			needToReload = true;
		}
		if (object instanceof IDetachable)
		{
			((IDetachable)object).detach();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getObjectClass()
	{
		return object != null ? (Class<T>) object.getClass() : null;
	}
	
	public IModel<ODocument> asODocumentModel() {
		return new IModel<ODocument>() {

			private static final long serialVersionUID = 1L;

			@Override
			public ODocument getObject() {
				return DAO.asDocument(DAOModel.this.getObject());
			}
			
			@Override
			public void setObject(ODocument object) {
				DAO.asWrapper(DAOModel.this.getObject()).fromStream(object);
			}
		};
	}
	
	public static <E> IModel<ODocument> asODocumentModel(final IModel<E> model) {
		if(model==null) return null;
		else if(model instanceof DAOModel) return ((DAOModel<E>)model).asODocumentModel();
		else return new IModel<ODocument>() {

			@Override
			public ODocument getObject() {
				return DAO.asDocument(model.getObject());
			}
			
			@Override
			public void setObject(ODocument object) {
				DAO.asWrapper(model.getObject()).fromStream(object);
			}
			
			@Override
			public void detach() {
				model.detach();
			}
		};
	}
	
	public static <T> IModel<T> of(Class<? extends T> clazz, ODocument doc) {
		return new DAOModel<T>(doc==null?DAO.create(clazz):DAO.provide(clazz, doc));
	}
	
	public static <T> IModel<T> of(ODocument doc) {
		String daoClassName = CustomAttribute.DAO_CLASS.getValue(doc.getSchemaClass());
		return of(WicketObjects.resolveClass(daoClassName), doc);
	}
	
	public static <T> IModel<T> of(T object) {
		return new DAOModel<T>(object);
	}
	
}
