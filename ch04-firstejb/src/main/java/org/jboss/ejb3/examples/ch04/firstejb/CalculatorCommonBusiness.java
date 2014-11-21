package org.jboss.ejb3.examples.ch04.firstejb;

public interface CalculatorCommonBusiness {

	/**
	 * Adds all arguments
	 * @return The sum of all arguments
	 */
	int add(int... arguments);
	
}
