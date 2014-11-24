package org.jboss.ejb3.examples.ch04.firstejb;

import javax.ejb.EJBContext;

public interface CalculatorCommonBusiness {

	/**
	 * Adds all arguments
	 * @return The sum of all arguments
	 */
	int add(int... arguments);
	
	EJBContext getContext();
	
}
