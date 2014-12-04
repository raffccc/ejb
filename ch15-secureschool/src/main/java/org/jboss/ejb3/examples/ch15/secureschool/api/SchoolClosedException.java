package org.jboss.ejb3.examples.ch15.secureschool.api;

import javax.ejb.EJBAccessException;

public class SchoolClosedException extends EJBAccessException {

	private static final long serialVersionUID = -8206672433162786872L;
	
	private SchoolClosedException(final String message) {
		super(message);
	}
	
	public static SchoolClosedException newInstance(final String message) throws IllegalArgumentException {
		if (message == null) {
			throw new IllegalArgumentException("Message must be specified");
		}
		
		return new SchoolClosedException(message);
	}

}
