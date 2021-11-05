package org.orienteer.core.dao;

import net.bytebuddy.asm.Advice;

public class TestDAOMethodHandler{
	
	public static final Integer RETURN = Integer.valueOf(9999);
	
	@Advice.OnMethodExit
	public static void onReturn(@Advice.Return(readOnly = false) Integer ret) {
		ret = RETURN;
	}


}
