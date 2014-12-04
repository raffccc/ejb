package org.jboss.ejb3.examples.ch17.transactions.api;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InsufficientBalanceException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InsufficientBalanceException(final String message) {
		super(message);
	}

}
