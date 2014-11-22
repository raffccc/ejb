package org.jboss.ejb3.examples.ch04;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jboss.ejb3.examples.ch04.firstejb.CalculatorCommonBusiness;

/**
 * Contains functions to assert that implementations
 * of {@link CalculatorCommonBusiness} are working
 * as expected
 */
public class CalculatorAssertionDelegate {

	private static final Logger log = Logger.getLogger(CalculatorAssertionDelegate.class.getName());
	
	/**
	 * Uses the supplied Calculator instance to test the addition algorithm
	 */
	void assertAdditionSucceeds(final CalculatorCommonBusiness calc) {
		final int[] arguments = new int[] { 2,3,5 };
		final int expectedSum = 10;
		
		//Add
		final int actualSum = calc.add(arguments); //Real EJB Invocation!
		
		//Test
		TestCase.assertEquals("Addition did not return the expected result", expectedSum, actualSum);
		
		//Log
		final StringBuffer sb = new StringBuffer();
		sb.append("Obtained expected result, ");
		sb.append(actualSum);
		sb.append(", from arguments: ");
		for (final int arg : arguments) {
			sb.append(arg);
			sb.append(" ");
		}
		log.info(sb.toString());
	}
	
}