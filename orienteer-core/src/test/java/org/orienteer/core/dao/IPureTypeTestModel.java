package org.orienteer.core.dao;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;

import java.util.List;

@ProvidedBy(ODocumentWrapperProvider.class)
public interface IPureTypeTestModel extends IODocumentWrapper {

  static IPureTypeTestModel get() {
    return OrienteerWebApplication.lookupApplication().getServiceInstance(IPureTypeTestModel.class);
  }

  int getInteger();
  void setInteger(int integer);

  short getShort();
  void setShort(short value);

  long getLong();
  void setLong(long value);

  float getFloat();
  void setFloat(float value);

  double getDouble();
  void setDouble(double value);

  byte getByte();
  void setByte(byte value);


  @Query("select from TestModel")
  List<ODocument> listModels();
}
