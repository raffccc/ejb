package org.jboss.ejb3.examples.ch18.tuner;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.interceptor.InvocationContext;

public class Channel2Restrictor {

	private static final Logger log = Logger.getLogger(Channel2Restrictor.class.getName());

	private static final String METHOD_NAME_GET_CHANNEL = TunerLocalBusiness.class.getMethods()[0].getName();

	public Object checkAccessibility(final InvocationContext context) throws Exception {
		assert context != null : "Context was not specified";

		if (isRequestForChannel2(context) && !Channel2AccessPolicy.isChannel2Permitted()) {
			throw Channel2ClosedException.INSTANCE;
		}

		return context.proceed();
	}

	private boolean isRequestForChannel2(InvocationContext context) {
		assert context != null : "Context was not specified";

		final Method targetMethod = context.getMethod();

		final String targetMethodName = targetMethod.getName();
		if (targetMethodName.equals(METHOD_NAME_GET_CHANNEL)) {
			log.info("This is a request for channel content: " + context);
			return (Integer)context.getParameters()[0] == 2;
		}	
		return false;
	}

}
