package org.jboss.ejb3.examples.testsupport.txwrap;

import java.util.concurrent.Callable;

/**
 * Wraps arbitrary {@link Callable} tasks inside of a new Tx
 */
public interface TxWrappingLocalBusiness {
	
	<T> T wrapInTx(Callable<T> tasks) throws IllegalArgumentException, TaskExecutionException;
	
	void wrapInTx(Callable<?>... tasks) throws IllegalArgumentException, TaskExecutionException;

}
