package org.jboss.ejb3.examples.ch06;

public class FileTransferException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FileTransferException() {
		super();
	}
	
	public FileTransferException(String message, Throwable cause) {
		super(message, cause);
	}

}
