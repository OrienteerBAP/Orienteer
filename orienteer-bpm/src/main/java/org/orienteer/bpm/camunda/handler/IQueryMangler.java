package org.orienteer.bpm.camunda.handler;

import com.github.raymanrt.orientqb.query.Query;
import com.google.common.base.Function;

/**
 * Interface to mangle {@link Query}ies 
 */
public interface IQueryMangler extends Function<Query, Query>{

}
