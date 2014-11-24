package org.jboss.ejb3.examples.ch05.encryption;

import javax.ejb.ApplicationException;

@ApplicationException
public class EncryptionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public EncryptionException() {
		super();
	}
	
	public EncryptionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public EncryptionException(String message) {
		super(message);
	}
	
	public EncryptionException(Throwable cause) {
		super(cause);
	}

}
