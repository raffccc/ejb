package org.jboss.ejb3.examples.ch18.tuner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import junit.framework.TestCase;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InterceptorIntegrationTest {
	
	private static final Logger log = Logger.getLogger(InterceptorIntegrationTest.class.getName());

	@EJB(mappedName="java:global/tuner/TunerBean!org.jboss.ejb3.examples.ch18.tuner.TunerLocalBusiness")
	private TunerLocalBusiness bean;
	
	@Deployment
	public static JavaArchive createDeployment() {
		final JavaArchive deployment = ShrinkWrap.create(JavaArchive.class, "tuner.jar")
				.addPackage(AuditedInvocation.class.getPackage());
		
		log.info(deployment.toString(true));
		return deployment;
	}
	
	@After
	public void clearInvocationsAfterTest() {
		//Clean up
		CachingAuditor.clearInTesting();
	}
	
	@Test
	public void testCachingInterception() throws IOException {
		TestCase.assertEquals("No invocations should have yet been intercepted", 0, CachingAuditor.getInvocations().size());
		
		final int channel = 1;
		final InputStream content = bean.getChannel(channel);
		TestCase.assertEquals("Did not obtain expected response", channel, content.read());
		
		TestCase.assertEquals("The invocation should have been intercepted", 1, CachingAuditor.getInvocations().size());
	}
	
	@Test(expected = Channel2ClosedException.class)
	public void testChannel2Restricted() throws Throwable {
		Channel2AccessPolicy.setChannel2Permitted(false);
		try {
			try {
				bean.getChannel(2);
			} catch (final EJBException e) {
				throw e.getCause();
			} 
		} catch (final UndeclaredThrowableException ute) {
			throw ute.getCause();
		}
		TestCase.fail("Request should've been blocked");
	}
	
	@Test
	public void testChannel2Allowed() throws Throwable {
		Channel2AccessPolicy.setChannel2Permitted(true);
		final int channel = 2;
		final InputStream stream = bean.getChannel(channel);
		TestCase.assertEquals("Unexpected content obtained from channel " + channel, channel, stream.read());
	}
	
}
