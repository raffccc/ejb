package org.jboss.ejb3.examples.ch04;

import junit.framework.TestCase;

import org.jboss.ejb3.examples.ch04.firstejb.CalculatorCommonBusiness;
import org.jboss.ejb3.examples.ch04.firstejb.NoInterfaceViewCalculatorBean;
import org.junit.Test;

public class CalculatorUnitTestCase {
	
	/**
	 * Ensures that the CalculatorEJB adds as expected
	 */
	@Test
	public void testAddition() {
		//Initialize. In this scope, this is not an EJB 
		final CalculatorCommonBusiness calc = new NoInterfaceViewCalculatorBean();
		final int expectedSum = 2+3+5;
		
		//Add
		final int actualSum = calc.add(2,3,5);
		
		//Test
		TestCase.assertEquals("Addition did not return the expected result", expectedSum, actualSum);
	}

}
