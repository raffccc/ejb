package org.jboss.ejb3.examples.testsupport.txwrap;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ForcedTestException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private static final String MESSAGE = "Intentional Test Exception";
	
	public ForcedTestException() {
		super(MESSAGE);
	}

}
