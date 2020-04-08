package org.orienteer.core.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.metadata.schema.OType;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass("DAOAllTypesTestClass")
public interface IDAOAllTypesTestClass {
	  /**
	   * BOOLEAN("Boolean", 0, Boolean.class, new Class<?>[] { Number.class }),
	   */
	public Boolean getBoolean();
	public void setBoolean(Boolean val);
	
	public boolean getBooleanPrimitive();
	public void setBooleanPrimitive(boolean val);
	
	@DAOField(notNull = true)
	public Boolean getBooleanDeclared();
	public void setBooleanDeclared(Boolean val);

	/**
	 * INTEGER("Integer", 1, Integer.class, new Class<?>[] { Number.class }),
	 */
	
	public Integer getInteger();
	public void setInteger(Integer val);

	/**
	 * SHORT("Short", 2, Short.class, new Class<?>[] { Number.class }),
	 */
	
	public Short getShort();
	public void setShort(Short val); 

	/**
	 * LONG("Long", 3, Long.class, new Class<?>[] { Number.class, }),
	 */
	
	public Long getLong();
	public void setLong(Long val);

	/**
	 * FLOAT("Float", 4, Float.class, new Class<?>[] { Number.class }),
	 */
	
	public Float getFloat();
	public void setFloat(Float val);

	/**
	 * DOUBLE("Double", 5, Double.class, new Class<?>[] { Number.class }),
	 */
	
	public Double getDouble();
	public void setDouble(Double val);

	/**
	 * DATETIME("Datetime", 6, Date.class, new Class<?>[] { Date.class, Number.class }),
	 */
	
	public Date getDateTime();
	public void setDateTime(Date val);

	/**
	 * STRING("String", 7, String.class, new Class<?>[] { Enum.class }),
	 */
	
	public String getString();
	public void setString(String val);

	/**
	 * BINARY("Binary", 8, byte[].class, new Class<?>[] { byte[].class }),
	 */
	
	public byte[] getBinary();
	public void setBinary(byte[] val);

	/**
	 * EMBEDDED("Embedded", 9, Object.class, new Class<?>[] { ODocumentSerializable.class, OSerializableStream.class }),
	 */
	@DAOField(embedded = true)
	public IDAODummyClass getEmbedded();
	@DAOField(embedded = true)
	public void setEmbedded(IDAODummyClass val);

	/**
	 * EMBEDDEDLIST("EmbeddedList", 10, List.class, new Class<?>[] { List.class, OMultiCollectionIterator.class }),
	 */
	@DAOField(embedded = true)
	public List<IDAODummyClass> getEmbeddedList();
	@DAOField(embedded = true)
	public void setEmbeddedList(List<IDAODummyClass> val);
	
	public List<String> getEmbeddedStringList();
	public void setEmbeddedStringList(List<String> val);

	/**
	 * EMBEDDEDSET("EmbeddedSet", 11, Set.class, new Class<?>[] { Set.class }),
	 */

	@DAOField(embedded = true)
	public Set<IDAODummyClass> getEmbeddedSet();
	@DAOField(embedded = true)
	public void setEmbeddedSet(Set<IDAODummyClass> val);
	
	public Set<String> getEmbeddedStringSet();
	public void setEmbeddedStringSet(Set<String> val);
	/**
	 * EMBEDDEDMAP("EmbeddedMap", 12, Map.class, new Class<?>[] { Map.class }),
	 */
	@DAOField(embedded = true)
	public Map<String, IDAODummyClass> getEmbeddedMap();
	@DAOField(embedded = true)
	public void setEmbeddedMap(Map<String, IDAODummyClass> val);
	
	public Map<String, String> getEmbeddedStringMap();
	public void setEmbeddedStringMap(Map<String, String> val);

	/**
	 * LINK("Link", 13, OIdentifiable.class, new Class<?>[] { OIdentifiable.class, ORID.class }),
	 */
	
	public IDAODummyClass getLink();
	public void setLink(IDAODummyClass val);

	/**
	 * LINKLIST("LinkList", 14, List.class, new Class<?>[] { List.class }),
	 */
	public List<IDAODummyClass> getLinkList();
	public void setLinkList(List<IDAODummyClass> val);

	/**
	 * LINKSET("LinkSet", 15, Set.class, new Class<?>[] { Set.class }),
	 */
	public Set<IDAODummyClass> getLinkSet();
	public void setLinkSet(Set<IDAODummyClass> val);

	/**
	 * LINKMAP("LinkMap", 16, Map.class, new Class<?>[] { Map.class }),
	 */
	
	public Map<String, IDAODummyClass> getLinkMap();
	public void setLinkMap(Map<String, IDAODummyClass> val);

	/**
	 * BYTE("Byte", 17, Byte.class, new Class<?>[] { Number.class }),
	 */
	
	public Byte getByte();
	public void setByte(Byte val);

	/**
	 * TRANSIENT("Transient", 18, null, new Class<?>[] {}),
	 */
	
	@DAOField(type = OType.TRANSIENT)
	public Object getTransient();
	@DAOField(type = OType.TRANSIENT)
	public void setTransient(Object val);

	/**
	 * DATE("Date", 19, Date.class, new Class<?>[] { Number.class }),
	 */
	
	@DAOField(type = OType.DATE)
	public Date getDate();
	@DAOField(type = OType.DATE)
	public void setDate(Date val);

	/**
	 * CUSTOM("Custom", 20, OSerializableStream.class, new Class<?>[] { OSerializableStream.class, Serializable.class }),
	 */
	public Serializable getCustom();
	public void setCustom(Serializable val);

	/**
	 * DECIMAL("Decimal", 21, BigDecimal.class, new Class<?>[] { BigDecimal.class, Number.class }),
	 */
	
	public BigDecimal getDecimal();
	public void setDecimal(BigDecimal val);

	/**
	 * LINKBAG("LinkBag", 22, ORidBag.class, new Class<?>[] { ORidBag.class }),
	 */
	
	@DAOField(linkedClass = "IDAODummyClass")
	public ORidBag getLinkBag();
	@DAOField(linkedClass = "IDAODummyClass")
	public void setLinkBag(ORidBag val);

	/**
	 * ANY("Any", 23, null, new Class<?>[] {});
	 */
	@DAOField(type = OType.ANY)
	public Object getAny();
	@DAOField(type = OType.ANY)
	public void setAny(Object val);
}
