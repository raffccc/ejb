package org.jboss.ejb3.examples.ch06;

public interface FileTransferCommonBusiness {

	void mkdir(String directory) throws IllegalStateException;
	
	void cd(String directory) throws IllegalStateException;
	
	/**
	 * Returns the name of the current directory
	 */
	String pwd() throws IllegalStateException;
	
	void disconnect();
	
	void connect() throws IllegalStateException;
	
}
