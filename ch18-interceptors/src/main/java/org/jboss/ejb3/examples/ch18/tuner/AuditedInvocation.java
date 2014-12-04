package org.jboss.ejb3.examples.ch18.tuner;

import java.security.Principal;

import javax.interceptor.InvocationContext;

/**
 * Encapsulates the auditable properties behind an invocation 
 */
public class AuditedInvocation {

	private final InvocationContext context;
	
	private final Principal caller;
	
	public AuditedInvocation(final InvocationContext context, final Principal caller) {
		assert context != null : "context must be specified";
		assert caller != null : "caller must be specified";
		this.context = context;
		this.caller = caller;
	}
	
	public InvocationContext getContext() {
		return context;
	}
	
	public Principal getCaller() {
		return caller;
	}
	
}
