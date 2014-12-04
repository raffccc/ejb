package org.jboss.ejb3.examples.ch18.tuner;

import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

public class MockInvocationContext implements InvocationContext {

	private static final String MSG_UNSUPPORTED = "Not supported in mock implementation";
	
	private final Method method;
	
	private final Object[] params;
	
	MockInvocationContext(final Method method, final Object[] params) {
		assert method != null : "method must be specified";
		assert params != null : "params must be specified";
		this.method = method;
		this.params = params;
	}
	
	@Override
	public Map<String, Object> getContextData() {
		throw new UnsupportedOperationException(MSG_UNSUPPORTED);
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public Object[] getParameters() {
		return params;
	}

	@Override
	public Object getTarget() {
		throw new UnsupportedOperationException(MSG_UNSUPPORTED);
	}

	@Override
	public Object getTimer() {
		throw new UnsupportedOperationException(MSG_UNSUPPORTED);
	}

	@Override
	public Object proceed() throws Exception {
		return null;
	}

	@Override
	public void setParameters(Object[] arg0) {
		throw new UnsupportedOperationException(MSG_UNSUPPORTED);

	}

}
