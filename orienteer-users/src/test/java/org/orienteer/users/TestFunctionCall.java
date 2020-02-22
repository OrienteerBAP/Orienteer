package org.orienteer.users;

import com.orientechnologies.orient.core.metadata.function.OFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.users.module.OrienteerUsersModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

@RunWith(OrienteerTestRunner.class)
public class TestFunctionCall {


  @Test
  public void testCallFunction() {
    DBClosure.sudoConsumer(db -> {
      OFunction function = db.getMetadata().getFunctionLibrary().getFunction(OrienteerUsersModule.FUN_REMOVE_RESTORE_ID);

      function.execute("1", "test", "1000");
    });
  }

}
