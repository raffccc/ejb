package org.jboss.ejb3.examples.testsupport.txwrap;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TaskExecutionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TaskExecutionException(final Throwable cause) {
		super(cause);
	}
	
	public TaskExecutionException(String message) {
		super(message);
	}

}
