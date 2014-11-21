package org.jboss.ejb3.examples.ch04.firstejb;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * EJB 2.x Local Home of the CalculatorEJB
 */
public interface CalculatorLocalHome extends EJBLocalHome {

	/**
	 * Returns a reference to a local component view of the CalculatorEJB 
	 */
	CalculatorLocal create() throws CreateException;
	
}
