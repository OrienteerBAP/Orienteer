package org.orienteer.core.dao;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;

import static org.junit.Assert.assertEquals;

@RunWith(OrienteerTestRunner.class)
public class GetterAndSetterTest {

  @Before
  @Sudo
  public void init() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    OSchemaHelper helper = OSchemaHelper.bind(db);

    helper.oClass("TestModel")
      .oProperty("integer", OType.INTEGER)
      .oProperty("short", OType.SHORT)
      .oProperty("long", OType.LONG)
      .oProperty("float", OType.FLOAT)
      .oProperty("double", OType.DOUBLE)
      .oProperty("byte", OType.BYTE);
  }

  @After
  @Sudo
  public void destroy() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    db.getMetadata().getSchema().dropClass("TestModel");
  }

  @Test
  @Sudo
  public void testGetterAndSetterForNumberFields() {
    IPureTypeTestModel model = IPureTypeTestModel.get();
    model.fromStream(new ODocument("TestModel"));

    assertEquals(0, model.getInteger());
    model.setInteger(1);
    assertEquals(1, model.getInteger());

    assertEquals(0, model.getShort());
    model.setShort((short) 2);
    assertEquals(2, model.getShort());

    assertEquals(0, model.getLong());
    model.setLong(3);
    assertEquals(3, model.getLong());

    assertEquals(0, model.getFloat(), 0);
    model.setFloat(4);
    assertEquals(4, model.getFloat(), 0);

    assertEquals(0, model.getDouble(), 0);
    model.setDouble(5);
    assertEquals(5, model.getDouble(), 0);

    assertEquals(0, model.getByte(), 0);
    model.setByte((byte) 6);
    assertEquals(6, model.getByte());
  }

}
