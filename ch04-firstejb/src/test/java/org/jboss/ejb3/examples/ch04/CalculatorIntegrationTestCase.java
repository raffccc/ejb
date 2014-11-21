package org.jboss.ejb3.examples.ch04;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.jboss.ejb3.examples.ch04.firstejb.CalculatorCommonBusiness;
import org.jboss.ejb3.examples.ch04.firstejb.NoInterfaceViewCalculatorBean;
import org.junit.BeforeClass;
import org.junit.Test;

public class CalculatorIntegrationTestCase {

	/**
	 * The JNDI Naming Context
	 */
	private static Context namingContext;
	
	/**
	 * The EJB 3.1 no-interface view of the CalculatorEJB
	 */
	private static NoInterfaceViewCalculatorBean calc;
	
	/**
	 * JNDI Name of the no-interface view
	 */
	private static final String JNDI_NAME_CALC =
			"java:global/ejb/cap4/SimpleCalculatorBean";
	
	//Invoked by JUnit before any tests run
	@BeforeClass
	public static void obtainProxyReferences() throws Throwable {
		//Create the naming context, using jndi.properties on the classpath
		namingContext = new InitialContext();
		
		//Obtain the EJB 3.1 Business Reference
		calc = (NoInterfaceViewCalculatorBean) namingContext.lookup(JNDI_NAME_CALC);
	}
	
	@Test
	public void testAddditionUsingBusinessReference() throws Throwable {
		this.assertAdditionSucceeds(calc);
	}
	
	private void assertAdditionSucceeds(CalculatorCommonBusiness calc) {
		//Initialize
		final int[] arguments = new int[] { 2,3,5 };
		final int expectedSum = 10;
		
		//Add
		final int actualSum = calc.add(arguments); //Real EJB Invocation!
		
		//Test
		TestCase.assertEquals("Addition did not return the expected result", expectedSum, actualSum);
	}
}
