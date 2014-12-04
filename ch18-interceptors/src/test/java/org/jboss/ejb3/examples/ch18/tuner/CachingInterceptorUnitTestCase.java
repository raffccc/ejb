package org.jboss.ejb3.examples.ch18.tuner;

import java.security.Identity;
import java.security.Principal;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.interceptor.InvocationContext;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class CachingInterceptorUnitTestCase {
	
	private static final Logger log = Logger.getLogger(CachingInterceptorUnitTestCase.class.getName());

	private static String NAME_PRINCIPAL = "Mock User";
	
	private static final Principal PRINCIPAL = new Principal() {
		@Override
		public String getName() {
			return NAME_PRINCIPAL;
		}
	};
	
	private CachingAuditor interceptor;
	
	@Before
	public void createInterceptor() {
		interceptor = new CachingAuditor();
		interceptor.beanContext = new SessionContext() {
			
			private UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException("Not supported in mock implementation");
			
			@Override
			public void setRollbackOnly() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public Object lookup(String arg0) throws IllegalArgumentException {
				throw UNSUPPORTED;
			}
			
			@Override
			public boolean isCallerInRole(String arg0) {
				throw UNSUPPORTED;
			}
			
			@Override
			public boolean isCallerInRole(Identity arg0) {
				throw UNSUPPORTED;
			}
			
			@Override
			public UserTransaction getUserTransaction() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public TimerService getTimerService() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public boolean getRollbackOnly() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public Properties getEnvironment() {
				throw UNSUPPORTED;
			}
			
			@Override
			public EJBLocalHome getEJBLocalHome() {
				throw UNSUPPORTED;
			}
			
			@Override
			public EJBHome getEJBHome() {
				throw UNSUPPORTED;
			}
			
			@Override
			public Map<String, Object> getContextData() {
				throw UNSUPPORTED;
			}
			
			@Override
			public Principal getCallerPrincipal() {
				return PRINCIPAL;
			}
			
			@Override
			public Identity getCallerIdentity() {
				throw UNSUPPORTED;
			}
			
			@Override
			public boolean wasCancelCalled() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public MessageContext getMessageContext() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public Class getInvokedBusinessInterface() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public EJBObject getEJBObject() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
				throw UNSUPPORTED;
			}
			
			@Override
			public <T> T getBusinessObject(Class<T> arg0) throws IllegalStateException {
				throw UNSUPPORTED;
			}
		};
	}
	
	@Test
	public void testCache() throws Exception {
		TestCase.assertEquals("Cache should start empty", 0, CachingAuditor.getInvocations().size());
		final InvocationContext invocation = new MockInvocationContext(TunerLocalBusiness.class.getMethods()[0], new Object[] {1});
		interceptor.audit(invocation);
		TestCase.assertEquals("Cache should have the first invocation", 1, CachingAuditor.getInvocations().size());
		
		final AuditedInvocation audit = CachingAuditor.getInvocations().get(0);
		TestCase.assertEquals("Invocation cached was not the one that was invoked", invocation, audit.getContext());
		TestCase.assertEquals("Invocation did not store the caller as expected", PRINCIPAL, audit.getCaller());
	}
}
