package org.jboss.ejb3.examples.testsupport.txwrap;

import java.util.concurrent.Callable;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@Local(TxWrappingLocalBusiness.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TxWrappingBean implements TxWrappingLocalBusiness {

	@Override
	public void wrapInTx(Callable<?>... tasks) throws IllegalArgumentException,	TaskExecutionException {
		if (tasks == null) {
			throw new IllegalArgumentException("task must be specified");
		}
		
		for (final Callable<?> task : tasks) {
			this.wrapInTx(task);
		}
	}

	@Override
	public <T> T wrapInTx(Callable<T> task) throws IllegalArgumentException, TaskExecutionException {
		try {
			return task.call();
		} catch (final Throwable e) {
			throw new TaskExecutionException(e);
		}
	}

}
