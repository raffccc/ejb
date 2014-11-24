package org.jboss.ejb3.examples.ch04;

import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorBeanBase;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorLocal;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorLocalBusiness;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorLocalHome;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorRemote;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorRemoteBusiness;
import org.jboss.ejb3.examples.ch04.firstejb.CalculatorRemoteHome;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for the CalculatorEJB, testing many views 
 */
@RunWith(Arquillian.class)
public class MultiViewCalculatorIntegrationTestCase {

	private static final Logger log = Logger.getLogger(MultiViewCalculatorIntegrationTestCase.class.getName());

	/*
	 * EJB 2.x stuff
	 */
	
	/**
	 * The JNDI Naming Context
	 */
	private static EJBContext namingContext;
	
	/**
	 * The EJB 2.x local component view of the CalculatorEJB
	 */
	private static CalculatorLocal calcLocal;
	
	/**
	 * The EJB 2.x remote component view of the CalculatorEJB
	 */
	private static CalculatorRemote calcRemote;
	
	/**
	 * JNDI Name of the Local Home Reference
	 */
	private static final String JNDI_NAME_CALC_LOCAL_HOME =
			"java:module/ManyViewCalculatorBean!org.jboss.ejb3.examples.ch04.firstejb.CalculatorLocalHome";
	
	/**
	 * JNDI Name of the Remote Home Reference
	 */
	private static final String JNDI_NAME_CALC_REMOTE_HOME =
			"java:module/ManyViewCalculatorBean!org.jboss.ejb3.examples.ch04.firstejb.CalculatorRemoteHome";
	
	/**
	 * EJB 3.x Local Business view
	 */
	@EJB(mappedName="java:module/ManyViewCalculatorBean!org.jboss.ejb3.examples.ch04.firstejb.CalculatorLocalBusiness")
	private CalculatorLocalBusiness calcLocalBusiness;

	/**
	 * EJB 3.x Remote Business view
	 */
	@EJB(mappedName="java:module/ManyViewCalculatorBean!org.jboss.ejb3.examples.ch04.firstejb.CalculatorRemoteBusiness")
	private CalculatorRemoteBusiness calcRemoteBusiness;

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
	public void before() throws Throwable {
		//Create Assertion Delegate
		assertionDelegate = new CalculatorAssertionDelegate();
		
		namingContext = calcLocalBusiness.getContext();
		
		final Object calcLocalHomeReference = namingContext.lookup(JNDI_NAME_CALC_LOCAL_HOME);
		final CalculatorLocalHome calcLocalHome = (CalculatorLocalHome) calcLocalHomeReference;
		calcLocal = calcLocalHome.create();
		
		final Object calcRemoteReference = namingContext.lookup(JNDI_NAME_CALC_REMOTE_HOME);
		final CalculatorRemoteHome calcRemoteHome = (CalculatorRemoteHome) calcRemoteReference;
		calcRemote = calcRemoteHome.create();
	}
	
	/**
	 * Ensures that the CalculatorEJB adds as expected,
	 * using the EJB 3.x local business view
	 */
	@Test
	public void testAdditionUsingLocalBusinessReference() throws Throwable {
		//Teste
		log.info("Testing local business reference...");
		assertionDelegate.assertAdditionSucceeds(calcLocalBusiness);
	}

	/**
	 * Ensures that the CalculatorEJB adds as expected,
	 * using the EJB 3.x remote business view
	 */
	@Test
	public void testAdditionUsingRemoteBusinessReference() throws Throwable {
		//Teste
		log.info("Testing remote business reference...");
		assertionDelegate.assertAdditionSucceeds(calcRemoteBusiness);
	}
	
	/**
	 * Ensures that the CalculatorEJB adds as expected,
	 * using the EJB 2.x local view
	 */
	@Test
	public void testAdditionUsingLocalReference() throws Throwable {
		//Teste
		log.info("Testing local reference...");
		assertionDelegate.assertAdditionSucceeds(calcLocal);
	}
	
	/**
	 * Ensures that the CalculatorEJB adds as expected,
	 * using the EJB 2.x remote view
	 */
	@Test
	public void testAdditionUsingRemoteReference() throws Throwable {
		//Teste
		log.info("Testing remote reference...");
		assertionDelegate.assertAdditionSucceeds(calcRemote);
	}
	
}