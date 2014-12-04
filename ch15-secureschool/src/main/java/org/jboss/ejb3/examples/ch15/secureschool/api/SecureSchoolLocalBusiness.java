package org.jboss.ejb3.examples.ch15.secureschool.api;

public interface SecureSchoolLocalBusiness {
	
	void open();
	
	void close();
	
	void openFrontDoor() throws SchoolClosedException;
	
	void openServiceDoor();
	
	boolean isOpen();

}
