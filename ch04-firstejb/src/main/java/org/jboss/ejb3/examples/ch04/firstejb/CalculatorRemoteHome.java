package org.jboss.ejb3.examples.ch04.firstejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * EJB 2.x Remote Home of the CalculatorEJB
 */
public interface CalculatorRemoteHome extends EJBHome {

	/**
	 * Returns a reference to a remote component view of the CalculatorEJB 
	 */
	CalculatorRemote create() throws CreateException, RemoteException;
	
}
