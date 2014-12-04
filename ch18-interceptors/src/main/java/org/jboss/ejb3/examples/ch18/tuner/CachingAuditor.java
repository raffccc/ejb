package org.jboss.ejb3.examples.ch18.tuner;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * Aspect which keeps a cache of all intercepted
 * invocations in a globally accessible cache 
 */
public class CachingAuditor {

	private final Logger log = Logger.getLogger(CachingAuditor.class.getName());

	/*
	 * Thread safe because this member is shared by all interceptor instances, which are
	 * linked to bean instances.
	 */
	private static final List<AuditedInvocation> invocations = new CopyOnWriteArrayList<>();

	@Resource
	SessionContext beanContext;

	@AroundInvoke
	public Object audit(final InvocationContext invocationContext) throws Exception {
		assert invocationContext != null : "Context was not specified";

		Principal caller;
		try {
			caller = beanContext.getCallerPrincipal();
		} catch (final NullPointerException e) {
			caller = new Principal() {
				@Override
				public String getName() {
					return "Unauthenticated Caller";
				}
			};
		}

		final AuditedInvocation audit = new AuditedInvocation(invocationContext, caller);
		invocations.add(audit);

		try {
			log.info("Intercepted " + invocationContext);
			return invocationContext.proceed();
		} finally {
			log.info("Done with " + invocationContext);
		}
	}

	public static List<AuditedInvocation> getInvocations() {
		return Collections.unmodifiableList(invocations);
	}
	
	static void clearInTesting() {
		invocations.clear();
	}

}
