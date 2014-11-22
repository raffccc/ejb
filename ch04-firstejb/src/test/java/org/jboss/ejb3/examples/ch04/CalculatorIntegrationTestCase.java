package org.jboss.ejb3.examples.ch04;

import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorBeanBase;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorLocalBusiness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for the CalculatorEJB exposing one
 * business view
 */
@RunWith(Arquillian.class)
public class CalculatorIntegrationTestCase {

	private static final Logger log = Logger.getLogger(CalculatorIntegrationTestCase.class.getName());
	
	/**
	 * The EJB 3.x local business view of the CalculatorEJB
	 */
	 @EJB(mappedName="java:app/firstejb/SimpleCalculatorBean")
	private static CalculatorLocalBusiness calcLocalBusiness;
	
	/**
	 * Delegate for ensuring that the obtained Calculators are working as expected
	 */
	private static CalculatorAssertionDelegate assertionDelegate;
	
	/**
	 * Define the deployment
	 */
	@Deployment
	public static JavaArchive createDeployment() throws MalformedURLException {
		final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "firstejb.jar")
				.addPackage(CalculatorBeanBase.class.getPackage())
				.addClass(CalculatorAssertionDelegate.class);
		
		/*
		 * If "true" is specified, acts as a shorthand for toString(Formatter) where the 
		 * Formatters.VERBOSE is leveraged. Otherwise the Formatters.SIMPLE will be used 
		 * (equivalent to toString()).
		 */
		log.info(archive.toString(true));
		return archive;
	}
	
	/**
	 * Run once before any tests
	 */
	@Before
	public void beforeClass() throws Throwable {
		//Create Assertion Delegate
		assertionDelegate = new CalculatorAssertionDelegate();
	}
	
	@Test
	public void testAddditionUsingBusinessReference() throws Throwable {
		//Test
		log.info("Testing EJB via business reference...");
		assertionDelegate.assertAdditionSucceeds(calcLocalBusiness);
	}
	
}