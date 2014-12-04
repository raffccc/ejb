package org.jboss.ejb3.examples.ch18.tuner;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.interceptor.InvocationContext;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class Channel2RestrictorUnitTestCase {
	
	private static final Method METHOD_GET_CHANNEL = TunerLocalBusiness.class.getMethods()[0];
	
	private Channel2Restrictor interceptor;
	
	@Before
	public void createInterceptor() {
		interceptor = new Channel2Restrictor();
	}
	
	@Test(expected = Channel2ClosedException.class)
	public void requestToChannel2Blocked() throws Exception {
		Channel2AccessPolicy.setChannel2Permitted(false);
		
		final InvocationContext invocation = new MockInvocationContext(METHOD_GET_CHANNEL, new Object[] {2});
		interceptor.checkAccessibility(invocation);
	}
	
	@Test
	public void requestToChannel2NotBlocked() {
		Channel2AccessPolicy.setChannel2Permitted(true);
		
		final InvocationContext invocation = new MockInvocationContext(METHOD_GET_CHANNEL, new Object[] {2});
		try {
			interceptor.checkAccessibility(invocation);
		} catch (Exception e) {
			TestCase.fail("Should not have been blocked with: " + e);
		}
	}
	
	@Test
	public void requestToChannel1NeverBlocked() {
		Channel2AccessPolicy.setChannel2Permitted(false);
		
		final InvocationContext invocation = new MockInvocationContext(METHOD_GET_CHANNEL, new Object[] {1});
		try {
			interceptor.checkAccessibility(invocation);
		} catch (Exception e) {
			TestCase.fail("Should not have been blocked with: " + e);
		}
	}


}
