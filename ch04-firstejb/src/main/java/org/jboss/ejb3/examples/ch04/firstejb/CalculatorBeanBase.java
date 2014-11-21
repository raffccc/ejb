package org.jboss.ejb3.examples.ch04.firstejb;

/**
 * Common base to contains the logic and extend it to add our metadata
 * that will define the SLSB
 */
public class CalculatorBeanBase implements CalculatorCommonBusiness {
	
	/*
	 * (non-Javadoc)
	 * @see ejb.cap4.CalculatorCommonBusiness#add(int[])
	 */
	@Override
	public int add(final int... arguments) {
		int result = 0;
		
		for (final int arg : arguments) {
			result += arg;
		}
		
		return result;
	}

}
